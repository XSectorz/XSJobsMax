package net.xsapi.panat.xsjobsmax.gui;

import net.xsapi.panat.xsjobsmax.config.config;
import net.xsapi.panat.xsjobsmax.config.messages;
import net.xsapi.panat.xsjobsmax.core.core;
import net.xsapi.panat.xsjobsmax.core.jobsSkill;
import net.xsapi.panat.xsjobsmax.core.jobsSkillHandler;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import net.xsapi.panat.xsjobsmax.player.xsSkill;
import net.xsapi.panat.xsjobsmax.utils.ConfigUtils;
import net.xsapi.panat.xsjobsmax.utils.ItemUtils;
import net.xsapi.panat.xsjobsmax.utils.MessagesUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class jobsMaxUI {

    public static ArrayList<Integer> blocked_index = new ArrayList<>(Arrays.asList(45,46,47,51,52,53));
    public static int close_index = 49;
    public static int next_index = 50;
    public static int prev_index = 48;
    public static ArrayList<Integer> skill_icon_index = new ArrayList<>(Arrays.asList(0,9,18,27,36));

    public static void openUI(Player p) {

        xsPlayer xsPlayerData = core.getXSPlayer().get(p.getUniqueId());

        xsPlayerData.getSkillMenuID().clear();

        Inventory inv = Bukkit.createInventory(null, 54, ConfigUtils.getString("gui.title") + " - " + xsPlayerData.getPageOpen());

        if(ConfigUtils.getBoolean("gui.sound.enable")) {
            p.playSound(p.getLocation(),Sound.valueOf(ConfigUtils.getString("gui.sound.type")),1.0f,1.0f);
        }

        for(int index : blocked_index) {
            inv.setItem(index, ItemUtils.createDecoration("barrier"));
        }


        //Skill Icon Showcase
        int startIndex = (xsPlayerData.getPageOpen()*5)-5;
        ArrayList<String> skillID = new ArrayList<>(config.customConfig.getConfigurationSection("gui.skill").getKeys(false));

        if(xsPlayerData.getPageOpen() > 1) {
            inv.setItem(prev_index, ItemUtils.createDecoration("prev_page"));
        } else {
            inv.setItem(prev_index, ItemUtils.createDecoration("barrier"));
        }

        if(startIndex+5 < skillID.size()) {
            inv.setItem(next_index, ItemUtils.createDecoration("next_page"));
        } else {
            inv.setItem(next_index, ItemUtils.createDecoration("barrier"));
        }

        for(int i = 0 ; i < 5 ; i++) {

            if(startIndex+i >= skillID.size()) {
                inv.setItem(skill_icon_index.get(i), ItemUtils.createDecoration("coming_soon"));
                inv.setItem((9*(i))+1, ItemUtils.createDecoration("skill_prev_block"));
                inv.setItem((9*(i))+8, ItemUtils.createDecoration("skill_next_block"));
                continue;
            }

            String skillName = skillID.get(startIndex+i);

            inv.setItem(skill_icon_index.get(i), ItemUtils.createItem(
                    Material.valueOf(ConfigUtils.getString("gui.skill."+ skillName +".material")),
                    1,
                    ConfigUtils.getInteger("gui.skill." + skillName + ".modelData"),
                    ConfigUtils.getString("gui.skill."+ skillName +".name"),
                    new ArrayList<String>(ConfigUtils.getStringList("gui.skill."+ skillName +".lore")),
                    false,
                    new HashMap<>()
            ));
            xsSkill playerSkill = xsPlayerData.getSkillList().get(skillName);
            jobsSkill jobSkill = core.getJobsSkillsList().get(skillName);
            xsPlayerData.getSkillMenuID().add(skillName);

            int startIndexOfSkill = (playerSkill.getPage()*6)-6;

            for(int j = 0 ; j < 6 ; j++) {

                if(startIndexOfSkill+j >= jobSkill.getMaxLevel()) {
                    continue;
                }

                ItemStack temp;
                String type = "";
                if(playerSkill.getLevel()-1 >= startIndexOfSkill+j) {
                    type = "skill_unlock";
                } else {
                    type = "skill_lock";
                }

                temp = ItemUtils.createDecoration(type);
                ItemMeta tempMeta = temp.getItemMeta();
                tempMeta.setDisplayName(tempMeta.getDisplayName().replace("<skill>",skillName)
                        .replace("<level>",(startIndexOfSkill+j+1)+""));

                ArrayList<String> lore = new ArrayList<>(Arrays.asList("",MessagesUtils.messages("skill_unlock_lore")));

                for(String lores : jobSkill.getJobsLevels().get(startIndexOfSkill+j).getAbilityList()) {

                    String abilityType = lores.split(":")[0];
                    int abilityAmt = Integer.parseInt(lores.split(":")[1]);

                    for(String jobSkillDesc : jobsSkillHandler.decodeSkill(abilityType,abilityAmt)) {
                        lore.add(jobSkillDesc);
                    }
                }

                if(xsPlayerData.getSkillList().get(skillName).getLevel() == startIndexOfSkill+j) {
                    for(String loreUpgradeType : messages.customConfig.getStringList("upgrades.can_upgrade")) {
                        if(loreUpgradeType.equalsIgnoreCase("{unlock_condition}")) {
                            for(String lores : jobSkill.getJobsLevels().get(startIndexOfSkill+j).getRequiredList()) {
                                String reqType = lores.split(":")[0];
                                int reqAmt = Integer.parseInt(lores.split(":")[1]);
                                lore.add(jobsSkillHandler.decodeRequired(xsPlayerData,reqType,reqAmt));
                            }
                            continue;
                        }
                        lore.add(MessagesUtils.replaceColor(loreUpgradeType));
                    }
                } else if(xsPlayerData.getSkillList().get(skillName).getLevel() > startIndexOfSkill+j) {
                    if(xsPlayerData.getSkillList().get(skillName).getLevel() == jobSkill.getMaxLevel()) {
                        for (String lores : messages.customConfig.getStringList("upgrades.max_upgrade")) {
                            lore.add(MessagesUtils.replaceColor(lores));
                        }
                    } else {
                        for (String lores : messages.customConfig.getStringList("upgrades.already_upgrade")) {
                            lore.add(MessagesUtils.replaceColor(lores));
                        }
                    }
                } else {
                    for (String lores : messages.customConfig.getStringList("upgrades.cant_upgrade")) {
                        lore.add(MessagesUtils.replaceColor(lores));
                    }
                }


                tempMeta.setLore(lore);
                temp.setItemMeta(tempMeta);

                inv.setItem(skill_icon_index.get(i)+(j+2), temp);

            }

            if(playerSkill.getPage() > 1) {
                inv.setItem((9*(i))+1, ItemUtils.createDecoration("skill_prev"));
            } else {
                inv.setItem((9*(i))+1, ItemUtils.createDecoration("skill_prev_block"));
            }

            if(startIndexOfSkill+6 < jobSkill.getMaxLevel()) {
                inv.setItem((9*(i))+8, ItemUtils.createDecoration("skill_next"));
            } else {
                inv.setItem((9*(i))+8, ItemUtils.createDecoration("skill_next_block"));
            }


        }

        inv.setItem(close_index, ItemUtils.createDecoration("exit"));

        p.openInventory(inv);
    }

}