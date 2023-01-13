package net.xsapi.panat.xsjobsmax.command;

import net.xsapi.panat.xsjobsmax.config.items;
import net.xsapi.panat.xsjobsmax.core.core;
import net.xsapi.panat.xsjobsmax.core.jobsSkillHandler;
import net.xsapi.panat.xsjobsmax.gui.jobsMaxUI;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import net.xsapi.panat.xsjobsmax.utils.MessagesUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import net.xsapi.panat.xsjobsmax.core.jobsSkill;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String arg, String[] args) {

        if(commandSender instanceof Player) {
            Player sender = (Player) commandSender;

            if(command.getName().equalsIgnoreCase("xsjobs")) {
                if(args.length == 0) {
                    if(!sender.hasPermission("xsjobs.ui")) {
                        sender.sendMessage(MessagesUtils.messages("no_permission"));
                        return false;
                    }
                    xsPlayer xsPlayerData = core.getXSPlayer().get(sender.getUniqueId());
                    xsPlayerData.setPageOpen(1);
                    jobsMaxUI.openUI(sender);

                    for(Map.Entry<String,Integer> data : xsPlayerData.getAbility().entrySet()) {
                        sender.sendMessage(data.getKey() + " : " + data.getValue());
                    }

                    return true;
                } else if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("help")) {
                        if(!sender.hasPermission("xsjobs.help")) {
                            sender.sendMessage(MessagesUtils.messages("no_permission"));
                            return false;
                        }
                        sender.sendMessage(MessagesUtils.messages("commands_help"));
                        sender.sendMessage(MessagesUtils.messages("commands_open"));
                        if(sender.hasPermission("xsapi.developer")) {
                            sender.sendMessage(MessagesUtils.messages("commands_set_level"));
                            sender.sendMessage(MessagesUtils.messages("commands_save_items"));
                            sender.sendMessage(MessagesUtils.messages("commands_get_items"));
                            sender.sendMessage(MessagesUtils.messages("commands_remove_items"));
                        }
                        return true;
                    }
                } else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("getitems")) {
                        if(!sender.hasPermission("xsapi.developer")) {
                            sender.sendMessage(MessagesUtils.messages("no_permission"));
                            return false;
                        }

                        String name = args[1].toString();

                        if(items.customConfig.get("items."+name) == null) {
                            sender.sendMessage(MessagesUtils.messages("get_items_null"));
                            return false;
                        }

                        ItemStack it = items.customConfig.getItemStack("items."+name);
                        sender.sendMessage(MessagesUtils.messages("get_items_success"));
                        sender.getInventory().addItem(it);
                        return true;
                    } else if(args[0].equalsIgnoreCase("removeitems")) {

                        if(!sender.hasPermission("xsapi.developer")) {
                            sender.sendMessage(MessagesUtils.messages("no_permission"));
                            return false;
                        }

                        String name = args[1].toString();

                        if(items.customConfig.get("items."+name) == null) {
                            sender.sendMessage(MessagesUtils.messages("get_items_null"));
                            return false;
                        }

                        File itemFile = new File(core.getPlugin().getDataFolder(),"items.yml");
                        YamlConfiguration itemsConfigs = YamlConfiguration.loadConfiguration(itemFile);

                        itemsConfigs.set("items."+name,null);

                        try {
                            itemsConfigs.save(itemFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        items.customConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(itemFile);
                        try {
                            items.customConfig.save(itemFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        sender.sendMessage(MessagesUtils.messages("remove_items_success"));
                        return true;
                    } else if(args[0].equalsIgnoreCase("saveitems")) {
                        if(!sender.hasPermission("xsapi.developer")) {
                            sender.sendMessage(MessagesUtils.messages("no_permission"));
                            return false;
                        }

                        String name = args[1].toString();

                        if(sender.getInventory().getItemInMainHand().getType() == Material.AIR) {
                            sender.sendMessage(MessagesUtils.messages("save_items_hand_empty"));
                            return false;
                        }

                        if(items.customConfig.get("items."+name) != null) {
                            sender.sendMessage(MessagesUtils.messages("saved_items_null"));
                            return false;
                        }

                        File itemFile = new File(core.getPlugin().getDataFolder(),"items.yml");
                        YamlConfiguration itemsConfigs = YamlConfiguration.loadConfiguration(itemFile);

                        itemsConfigs.set("items."+name,sender.getInventory().getItemInMainHand());

                        try {
                            itemsConfigs.save(itemFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        items.customConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(itemFile);
                        try {
                            items.customConfig.save(itemFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        sender.sendMessage(MessagesUtils.messages("save_items_success"));
                        return true;
                    }
                } else if (args.length == 4) {
                    if(args[0].equalsIgnoreCase("setlevel")) {
                        if(!sender.hasPermission("xsapi.developer")) {
                            sender.sendMessage(MessagesUtils.messages("no_permission"));
                            return false;
                        }
                        Player target = Bukkit.getPlayer(args[1].toString());
                        OfflinePlayer playerOff = null;
                        boolean isplayerOff = false;
                        if(target == null) {
                            playerOff = Bukkit.getOfflinePlayer(args[1].toString());
                            isplayerOff = true;
                        }

                        if(target != null || jobsSkillHandler.checkIsValidPlayer(playerOff.getUniqueId())) {
                            String skill = args[2].toString();

                            int lvl = 0;
                            try {
                                lvl = Integer.parseInt(args[3].toString());
                            } catch (NumberFormatException nfe) {
                                sender.sendMessage(MessagesUtils.messages("NaN_error"));
                                return false;
                            }

                            if(lvl < 0) {
                                sender.sendMessage(MessagesUtils.messages("negative_error"));
                                return false;
                            }

                            jobsSkill jS = core.getJobsSkillsList().get(skill);

                            if(jS == null) {
                                sender.sendMessage(MessagesUtils.messages("skill_null"));
                                return false;
                            }

                            if(lvl > jS.getMaxLevel()) {
                                sender.sendMessage(MessagesUtils.messages("too_high_error"));
                                return false;
                            }


                            if(isplayerOff) {
                                File userFile = jobsSkillHandler.getValidPlayerFile(playerOff.getUniqueId());
                                FileConfiguration userConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(userFile);

                                userConfig.set("skills."+skill+".level",lvl);
                                try {
                                    userConfig.save(userFile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                xsPlayer xPlayer = core.getXSPlayer().get(target.getUniqueId());

                                xPlayer.getSkillList().get(skill).setLevel(lvl);
                                jobsSkillHandler.getAbilityList(xPlayer,skill);
                            }

                            sender.sendMessage(MessagesUtils.messages("set_skill_success").replace("<player>",args[1].toString())
                                    .replace("<skill>",skill).replace("<lvl>",lvl+""));

                            return true;
                        } else {
                            sender.sendMessage(MessagesUtils.messages("player_null"));
                            return false;
                        }
                    }
                }
            }
        }


        return false;
    }

}