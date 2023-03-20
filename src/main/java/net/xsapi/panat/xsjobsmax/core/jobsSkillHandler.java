package net.xsapi.panat.xsjobsmax.core;

import net.xsapi.panat.xsjobsmax.config.ability;
import net.xsapi.panat.xsjobsmax.config.items;
import net.xsapi.panat.xsjobsmax.config.messages;
import net.xsapi.panat.xsjobsmax.config.skills;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import net.xsapi.panat.xsjobsmax.utils.MessagesUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

public class jobsSkillHandler {

    public static boolean requireChecker(jobsLevel jL,xsPlayer xPlayer,boolean isTake) {

        int playerHave = 0;

        for(String req : jL.getRequiredList()) {

            String reqType = req.split(":")[0];
            int amt = Integer.parseInt(req.split(":")[1]);
            String itemsType = "";

            if(reqType.split(";").length >= 2) {
                itemsType = reqType.split(";")[1];
                reqType = reqType.split(";")[0];
            }

            if(reqType.equalsIgnoreCase("MONEY")) {
                playerHave = (int) core.getEconomy().getBalance(xPlayer.getPlayer());
                if(isTake) {
                    core.getEconomy().withdrawPlayer(xPlayer.getPlayer(),(double) amt);
                }
            } else if(reqType.equalsIgnoreCase("SC_POINT")) {
                playerHave = core.getSCPoint().look(xPlayer.getPlayer().getUniqueId());
                if(isTake) {
                    core.getSCPoint().take(xPlayer.getPlayer().getUniqueId(),amt);
                }
            } else if(reqType.equalsIgnoreCase("ITEM")) {
                ItemStack it = items.customConfig.getItemStack("items."+itemsType);
                playerHave = decodeHaveRequiredItems(it,xPlayer.getPlayer());

                if (isTake) {

                    int itemsToRemove = amt;

                    for(ItemStack invItem : xPlayer.getPlayer().getInventory().getContents()) {
                        if (invItem != null) {
                            if (invItem.getType().equals(it.getType())) {
                                if (it.hasItemMeta()) {
                                    if (!invItem.hasItemMeta()) {
                                        continue;
                                    }
                                    if(invItem.getItemMeta().hasCustomModelData()) {
                                        if (invItem.getItemMeta().getCustomModelData() != it.getItemMeta().getCustomModelData()) {
                                            continue;
                                        }
                                    }
                                }
                                int preAmount = invItem.getAmount();
                                int newAmount = Math.max(0, preAmount - itemsToRemove);
                                itemsToRemove = Math.max(0, itemsToRemove - preAmount);
                                invItem.setAmount(newAmount);
                                if(itemsToRemove == 0) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if(playerHave < amt) {
                return false;
            }

        }

        return true;
    }

    public static boolean checkIsValidPlayer(UUID uuid) {
        File userFile = new File(core.getPlugin().getDataFolder() + "/players",uuid+".yml");
        if(userFile.exists()) {
            return true;
        }
        return false;
    }

    public static File getValidPlayerFile(UUID uuid) {
        return new File(core.getPlugin().getDataFolder() + "/players",uuid+".yml");
    }

    public static List<String> decodeSkill(String type, int amt) {

        if(messages.customConfig.get("skills."+type) == null) {
            return new ArrayList<>(Arrays.asList(MessagesUtils.messages("skill_unknow")));
        }

        ArrayList<String> lores = new ArrayList<String>(messages.customConfig.getStringList("skills."+type));
        ArrayList<String> loreReture = new ArrayList<String>();

        for(String lore : lores) {
            lore = MessagesUtils.replaceColor(lore);
            lore = lore.replace("{amount}",amt+"");
            loreReture.add(lore);
        }

        return loreReture;

    }

    public static List<String> decodeSkill(String type, int amt,String amtType) {

        if(messages.customConfig.get("skills."+type) == null) {
            return new ArrayList<>(Arrays.asList(MessagesUtils.messages("skill_unknow")));
        }

        ArrayList<String> lores = new ArrayList<String>(messages.customConfig.getStringList("skills."+type));
        ArrayList<String> loreReture = new ArrayList<String>();

        for(String lore : lores) {
            lore = MessagesUtils.replaceColor(lore);

            if(!type.equalsIgnoreCase("MIGHTY") &&
                    !type.equalsIgnoreCase("AGILITY") &&
                    !type.equalsIgnoreCase("TOUGHNESS")) {


                int chance = 0;
                chance = (amt*ability.customConfig.getInt("ability."+type+".multiple_chance"));

                if(chance > 100) {
                    chance = 100;
                }

                String skill_format_type = "";
                int amount = 0;

                if(type.equalsIgnoreCase("BLOOD_RUST")) {
                    skill_format_type = "multiple_drain_hp";
                } else if(type.equalsIgnoreCase("MIGHTY_MINER")) {
                    for(String effect : ability.customConfig.getStringList("ability.MIGHTY_MINER.effect_list")) {
                        String effect_type = effect.split(":")[0];
                        int amplifier = Integer.parseInt(effect.split(":")[1]);
                        int timer = Integer.parseInt(effect.split(":")[2]);

                        lore = lore.replace("{" + effect_type + "_POWER}",amplifier+"");
                        lore = lore.replace("{" + effect_type + "_TIMER}",timer+"");
                    }
                    int cooldown_timer = ability.customConfig.getInt("ability.MIGHTY_MINER.cooldowns");

                    cooldown_timer = cooldown_timer-((amt-1)*ability.customConfig.getInt("ability.MIGHTY_MINER.multiple_cooldown_reduction"));

                    lore = lore.replace("{MIGHTY_MINER_COOLDOWN}",cooldown_timer+"");
                } else if(type.equalsIgnoreCase("I_AM_WITCH")) {
                    skill_format_type = "multiple_take_less";
                } else if(type.equalsIgnoreCase("GOOD_FARMER")) {
                    int cooldown_timer = ability.customConfig.getInt("ability.GOOD_FARMER.cooldowns");
                    cooldown_timer = cooldown_timer-((amt-1)*ability.customConfig.getInt("ability.GOOD_FARMER.multiple_cooldown_reduction"));

                    double ability_timer = ability.customConfig.getDouble("ability.GOOD_FARMER.skill_timer");

                    ability_timer = ability_timer+((amt-1)* ability.customConfig.getDouble("ability.GOOD_FARMER.multiple_timer"));

                    lore = lore.replace("{GOOD_FARMER_COOLDOWN}",cooldown_timer+"");
                    lore = lore.replace("{GOOD_FARMER_TIMER}",ability_timer+"");
                }
                amount = (amt*ability.customConfig.getInt("ability."+type+"."+skill_format_type));

                lore = lore.replace("{chance}",
                        chance+"");
                lore = lore.replace("{amount}",amount+"");
                lore = lore.replace("{level}",amtType);
            }

            loreReture.add(lore);
        }

        return loreReture;

    }

    public static String decodeRequired(xsPlayer player, String type, int amt) {

        int playerHave = 0;

        String format = "";
        String itemsType = "";

        if(type.split(";").length >= 2) {
            itemsType = type.split(";")[1];
            type = type.split(";")[0];
        }

        if(type.equalsIgnoreCase("MONEY")) {
            playerHave = (int) core.getEconomy().getBalance(player.getPlayer());
        } else if(type.equalsIgnoreCase("SC_POINT")) {
            playerHave = core.getSCPoint().look(player.getPlayer().getUniqueId());
        } else if(type.equalsIgnoreCase("ITEM")) {
            playerHave = 0;

            if(items.customConfig.get("items."+itemsType) == null) {
                return MessagesUtils.messages("required_items_null").replace("%name%",itemsType);
            }

            ItemStack it = items.customConfig.getItemStack("items."+itemsType);

            if(it.hasItemMeta()) {
              if(it.getItemMeta().hasDisplayName()) {
                  itemsType = it.getItemMeta().getDisplayName();
              } else {
                  itemsType = it.getType().toString();
              }
            } else {
                itemsType = it.getType().toString();
            }
            playerHave = decodeHaveRequiredItems(it,player.getPlayer());
        }

        if(playerHave >= amt) {
            format = messages.customConfig.getString("upgrades.upgrade_required_success");
        } else {
            format = messages.customConfig.getString("upgrades.upgrade_required_needed");
        }

        format = format.replace("%have%",playerHave+"");
        format = format.replace("%required%",amt+"");
        if(messages.customConfig.get("required."+type) != null) {
            format = format.replace("%name%",messages.customConfig.getString("required."+type));
        } else if(type.equalsIgnoreCase("ITEM")) {
            format = format.replace("%name%",itemsType);
        }
        format = MessagesUtils.replaceColor(format);

        return format;

    }

    public static int decodeHaveRequiredItems(ItemStack itemStack, Player p) {

        int have = 0;
        for(ItemStack invItem : p.getInventory().getContents()) {
            if (invItem != null) {
                if (invItem.getType().equals(itemStack.getType())) {
                    if (itemStack.hasItemMeta()) {
                        if (!invItem.hasItemMeta()) {
                            continue;
                        }
                        if(invItem.getItemMeta().hasCustomModelData()) {
                            if (invItem.getItemMeta().getCustomModelData() != itemStack.getItemMeta().getCustomModelData()) {
                                continue;
                            }
                        }
                    }
                    have += invItem.getAmount();
                }
            }
        }


        return have;
    }

    public static void getAbilityList(xsPlayer xPlayer,String skill) {

        int level_temp = 0;

        for(String level : skills.customConfig.getConfigurationSection("skills."+skill).getKeys(false)) {

            level_temp += 1;

            if(level_temp > xPlayer.getSkillList().get(skill).getLevel()) {
                break;
            } else {
                for(String ability : skills.customConfig.getStringList("skills."+skill+"."+level+".ability")) {
                    String type = ability.split(":")[0];
                    int amt = Integer.parseInt(ability.split(":")[1]);

                    if(xPlayer.getAbility().get(type) != null) {
                        if(type.equalsIgnoreCase("MIGHTY") || type.equalsIgnoreCase("AGILITY")
                        || type.equalsIgnoreCase("TOUGHNESS")) {
                            xPlayer.getAbility().put(type,xPlayer.getAbility().get(type)+amt);
                        } else {
                            xPlayer.getAbility().put(type,amt);
                        }
                    } else {
                        xPlayer.getAbility().put(type,amt);
                    }

                }
            }

        }

    }

}