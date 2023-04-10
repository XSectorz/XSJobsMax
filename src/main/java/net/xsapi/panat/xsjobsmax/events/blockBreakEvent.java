package net.xsapi.panat.xsjobsmax.events;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import net.xsapi.panat.xsjobsmax.config.ability;
import net.xsapi.panat.xsjobsmax.config.items;
import net.xsapi.panat.xsjobsmax.core.core;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import net.xsapi.panat.xsjobsmax.utils.MessagesUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class blockBreakEvent implements Listener {


    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block b = e.getBlock();

        if(!e.isCancelled()) {
            if(ability.customConfig.getStringList("ability.TREASURE_HUNTER.allowed_blocks").contains(b.getType().toString().toUpperCase())) {
                skillTriggerTreasureHunter(core.getXSPlayer().get(e.getPlayer().getUniqueId()),b);
            }
            if(ability.customConfig.getStringList("ability.MIGHTY_MINER.activated_blocks").contains(b.getType().toString().toUpperCase())) {
                skillTriggerMightyMiner(core.getXSPlayer().get(e.getPlayer().getUniqueId()));
            }

            if(ability.customConfig.getStringList("ability.GOOD_FARMER.activated_blocks").contains(b.getType().toString().toUpperCase())) {
                e.setCancelled(skillTriggerGoodFarmer(core.getXSPlayer().get(e.getPlayer().getUniqueId()),b));
            }
        }

    }

    public boolean skillTriggerGoodFarmer(xsPlayer xPlayer,Block b) {
        if(xPlayer.getAbility().get("GOOD_FARMER") != null) {

            int level = xPlayer.getAbility().get("GOOD_FARMER");

            double ability_timer = ability.customConfig.getDouble("ability.GOOD_FARMER.skill_timer");

            ability_timer = ability_timer+((level-1)* ability.customConfig.getDouble("ability.GOOD_FARMER.multiple_timer"));


            if(xPlayer.getCooldowns().containsKey("GOOD_FARMER")) {
                if(System.currentTimeMillis() - xPlayer.getCooldowns().get("GOOD_FARMER") <= 0
                && System.currentTimeMillis() - xPlayer.getActivated_ability().get("GOOD_FARMER") >= ability_timer*1000) {
                  //  Bukkit.broadcastMessage("ON COOLDOWN : " + (System.currentTimeMillis() - xPlayer.getCooldowns().get("GOOD_FARMER")));
                    return false;
                }
            }

            if(xPlayer.getActivated_ability().containsKey("GOOD_FARMER") && System.currentTimeMillis() - xPlayer.getCooldowns().get("GOOD_FARMER") <= 0) {
                //Bukkit.broadcastMessage("START SKILL TIME LEFT: " + (System.currentTimeMillis() - xPlayer.getActivated_ability().get("GOOD_FARMER")));
                if(System.currentTimeMillis() - xPlayer.getActivated_ability().get("GOOD_FARMER") >= ability_timer*1000) {
                    return false;
                }
            } else {
                //Bukkit.broadcastMessage("JUST START");
                int cooldown_timer = ability.customConfig.getInt("ability.GOOD_FARMER.cooldowns");
                cooldown_timer = cooldown_timer-((level-1)*ability.customConfig.getInt("ability.GOOD_FARMER.multiple_cooldown_reduction"));
                xPlayer.getCooldowns().put("GOOD_FARMER",(System.currentTimeMillis()+(cooldown_timer*1000))+(long)(ability_timer*1000));
                xPlayer.getActivated_ability().put("GOOD_FARMER",System.currentTimeMillis());
                //Bukkit.broadcastMessage("COOLDOWN: " + xPlayer.getCooldowns().get("GOOD_FARMER"));
            }

            if (Jobs.getPlayerManager().getJobsPlayer(xPlayer.getPlayer()) != null) {
                    List<JobProgression> jobs = Jobs.getPlayerManager().getJobsPlayer(xPlayer.getPlayer()).getJobProgression();
                    List<String> AllowedJobs = ability.customConfig.getStringList("ability.GOOD_FARMER.allowed_jobs");
                    for (JobProgression prog : jobs) {
                        if (AllowedJobs.contains(prog.getJob().getName())) {
                             Ageable ageable = (Ageable) b.getBlockData();

                            if(ageable.getAge() == ageable.getMaximumAge()) {
                                ageable.setAge(0);
                                b.setBlockData(ageable);
                                return true;
                            }
                        }
                    }
            }
        }
        return false;
    }

    public void skillTriggerMightyMiner(xsPlayer xPlayer) {
        if(xPlayer.getAbility().get("MIGHTY_MINER") != null) {
            if(xPlayer.getCooldowns().containsKey("MIGHTY_MINER")) {
                if(System.currentTimeMillis() - xPlayer.getCooldowns().get("MIGHTY_MINER") <= 0) {
                    return;
                }
            }
            int level = xPlayer.getAbility().get("MIGHTY_MINER");

            int random = (int) ((Math.random() * (100 - 0)) + 0);

            if(ability.customConfig.getInt("ability.MIGHTY_MINER.multiple_chance")*level >= random) {
                JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(xPlayer.getPlayer());

                if (jobsPlayer != null) {
                    List<JobProgression> jobs = jobsPlayer.getJobProgression();
                    List<String> AllowedJobs = ability.customConfig.getStringList("ability.MIGHTY_MINER.allowed_jobs");
                    for (JobProgression prog : jobs) {
                        if (AllowedJobs.contains(prog.getJob().getName())) {
                            int cooldown_timer = ability.customConfig.getInt("ability.MIGHTY_MINER.cooldowns");

                            cooldown_timer = cooldown_timer-((level-1)*ability.customConfig.getInt("ability.MIGHTY_MINER.multiple_cooldown_reduction"));

                            for(String effect : ability.customConfig.getStringList("ability.MIGHTY_MINER.effect_list")) {
                                String effect_type = effect.split(":")[0];
                                int amplifier = Integer.parseInt(effect.split(":")[1]);
                                int timer = Integer.parseInt(effect.split(":")[2]);

                                if(xPlayer.getPlayer().getPotionEffect(PotionEffectType.getByName(effect_type)) != null) {
                                    int newAmplifier = amplifier+xPlayer.getPlayer().getPotionEffect(PotionEffectType.getByName(effect_type)).getAmplifier();
                                    xPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.getByName(effect_type),timer*20,newAmplifier));
                                } else {
                                    xPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.getByName(effect_type),timer*20,amplifier-1));
                                }

                                xPlayer.getCooldowns().put("MIGHTY_MINER",System.currentTimeMillis()+(cooldown_timer*1000));
                            }
                        }
                    }
                }
            }
        }
    }

    public void skillTriggerTreasureHunter(xsPlayer xPlayer,Block b) {
        if(xPlayer.getAbility().get("TREASURE_HUNTER") != null) {
            int level = xPlayer.getAbility().get("TREASURE_HUNTER");

            int random = (int) ((Math.random() * (100 - 0)) + 0);

            Bukkit.broadcastMessage("COME1 -> " + ability.customConfig.getInt("ability.TREASURE_HUNTER.multiple_chance")*level);

            if(ability.customConfig.getInt("ability.TREASURE_HUNTER.multiple_chance")*level >= random) {
                CoreProtectAPI CoreProtect = core.getCoreProtectAPI();
                Bukkit.broadcastMessage("COME2");
                if (CoreProtect != null){
                    List<String[]> lookup = CoreProtect.blockLookup(b, 604800);
                    if (lookup.isEmpty()) {
                        Player p = xPlayer.getPlayer();
                        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(xPlayer.getPlayer());

                        if (jobsPlayer != null) {
                            List<JobProgression> jobs = jobsPlayer.getJobProgression();
                            List<String> AllowedJobs = ability.customConfig.getStringList("ability.TREASURE_HUNTER.allowed_jobs");
                            for (JobProgression prog : jobs) {
                                if(AllowedJobs.contains(prog.getJob().getName())) {
                                    p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG,1,1);
                                    List<String> dropL = ability.customConfig.getStringList("ability.TREASURE_HUNTER.drops_list");
                                    int randomDrop = (int) ((Math.random() * (dropL.size() - 0)) + 0);

                                    String type = dropL.get(randomDrop).split(":")[0];
                                    int amount = Integer.parseInt(dropL.get(randomDrop).split(":")[1]);
                                    String name = "";

                                    if(type.equalsIgnoreCase("MONEY")) {
                                        core.getEconomy().depositPlayer(p,amount);
                                    } else if(type.equalsIgnoreCase("SC_POINT")) {
                                        core.getSCPoint().give(p.getUniqueId(),amount);
                                    } else {
                                        String itemName = type.split(";")[1];
                                        type = "ITEM";
                                        if(items.customConfig.get("items."+itemName) == null) {
                                            p.sendMessage(MessagesUtils.messages("item_null"));
                                            return;
                                        }

                                        ItemStack it = items.customConfig.getItemStack("items."+itemName);
                                        name = it.getType().toString();
                                        amount = it.getAmount();
                                        if(it.hasItemMeta()) {
                                            if(it.getItemMeta().hasDisplayName()) {
                                                name = it.getItemMeta().getDisplayName();
                                            }
                                        }

                                        p.getInventory().addItem(it);
                                    }
                                    p.sendMessage(MessagesUtils.messages("treasure_item_get_activated")
                                            .replace("%type%",MessagesUtils.messages("placeholder."+type)
                                                    .replace("%item_name%",name)
                                                    .replace("%amount%",amount+"")));
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}