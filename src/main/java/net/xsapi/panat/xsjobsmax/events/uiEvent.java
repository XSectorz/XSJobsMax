package net.xsapi.panat.xsjobsmax.events;

import net.xsapi.panat.xsjobsmax.config.config;
import net.xsapi.panat.xsjobsmax.core.core;
import net.xsapi.panat.xsjobsmax.core.jobsSkill;
import net.xsapi.panat.xsjobsmax.core.jobsSkillHandler;
import net.xsapi.panat.xsjobsmax.gui.jobsMaxUI;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import net.xsapi.panat.xsjobsmax.player.xsSkill;
import net.xsapi.panat.xsjobsmax.utils.ConfigUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Arrays;


public class uiEvent implements Listener {

    public ArrayList<Integer> slotPrev = new ArrayList<>(Arrays.asList(1,10,19,28,37));

    @EventHandler
    public void onClickUI(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        xsPlayer xsPlayerData = core.getXSPlayer().get(p.getUniqueId());

        if(e.getView().getTitle().startsWith(ConfigUtils.getString("gui.title"))) {
            if(e.getSlot() == 50) {
                if(e.getCurrentItem() != null) {
                    if(xsPlayerData.getPageOpen()*5 > xsPlayerData.getSkillMenuID().size() ) {
                        e.setCancelled(true);
                        return;
                    }
                    xsPlayerData.setPageOpen(xsPlayerData.getPageOpen()+1);
                    jobsMaxUI.openUI(p);
                }
            } else if(e.getSlot() == 48) {
                if(xsPlayerData.getPageOpen() > 1) {
                    xsPlayerData.setPageOpen(xsPlayerData.getPageOpen()-1);
                    jobsMaxUI.openUI(p);
                } else {
                    e.setCancelled(true);
                    return;
                }
            } else if(e.getSlot() == 49) {
                p.closeInventory();
            } else if((e.getSlot()+1)%9 == 0 && e.getSlot() != 53) {
                if(e.getCurrentItem() != null) {
                    if((((e.getSlot()+1)/9)-1)*xsPlayerData.getPageOpen() >= xsPlayerData.getSkillMenuID().size()) {
                        e.setCancelled(true);
                        return;
                    }
                    String jobType = xsPlayerData.getSkillMenuID().get((((e.getSlot()+1)/9)-1)*xsPlayerData.getPageOpen());
                        //p.sendMessage(jobType);
                    xsSkill jobSkillPlayer = xsPlayerData.getSkillList().get(jobType);
                    jobsSkill jobSkill = core.getJobsSkillsList().get(jobType);
                    if(jobSkillPlayer.getPage()*6 >= jobSkill.getMaxLevel()) {
                        e.setCancelled(true);
                        return;
                    }
                    jobSkillPlayer.setPage(jobSkillPlayer.getPage()+1);
                    jobsMaxUI.openUI(xsPlayerData.getPlayer());
                }
            } else if(slotPrev.contains(e.getSlot())) {
                if(e.getCurrentItem() != null) {
                    if(slotPrev.indexOf(e.getSlot())*xsPlayerData.getPageOpen() >= xsPlayerData.getSkillMenuID().size()) {
                        e.setCancelled(true);
                        return;
                    }

                    String jobType = xsPlayerData.getSkillMenuID().get(slotPrev.indexOf(e.getSlot())*xsPlayerData.getPageOpen());
                       // p.sendMessage(jobType);
                    xsSkill jobSkillPlayer = xsPlayerData.getSkillList().get(jobType);

                    if(jobSkillPlayer.getPage() <= 1) {
                        e.setCancelled(true);
                        return;
                    }
                    jobSkillPlayer.setPage(jobSkillPlayer.getPage()-1);
                    jobsMaxUI.openUI(xsPlayerData.getPlayer());
                }
            } else if(e.getSlot() >= 2 && e.getSlot() <= 43) {

                if((e.getSlot()/9)*xsPlayerData.getPageOpen() < xsPlayerData.getSkillMenuID().size()) {
                    String jobType = xsPlayerData.getSkillMenuID().get((e.getSlot()/9)*xsPlayerData.getPageOpen());
                    jobsSkill jobSkill = core.getJobsSkillsList().get(jobType);
                    xsSkill playerSkill = xsPlayerData.getSkillList().get(jobType);

                    int levelClicked = (((playerSkill.getPage()-1)*6)+((e.getSlot()%9)-2));

                    if((levelClicked+1) <= playerSkill.getLevel()) {
                        if(ConfigUtils.getBoolean("options.click.sound.upgrade_already.enable")) {
                            p.playSound(p.getLocation(), Sound.valueOf(ConfigUtils.getString("options.click.sound.upgrade_already.sound")),1.0f,1.0f);
                        }
                    } else if(((levelClicked+1) > playerSkill.getLevel() && (levelClicked+1) != playerSkill.getLevel()+1) || e.getCurrentItem() == null) {
                        if(ConfigUtils.getBoolean("options.click.sound.upgrade_out_of_index.enable")) {
                            p.playSound(p.getLocation(), Sound.valueOf(ConfigUtils.getString("options.click.sound.upgrade_out_of_index.sound")),1.0f,1.0f);
                        }
                    } else {

                        if(jobsSkillHandler.requireChecker(jobSkill.getJobsLevels().get(levelClicked), xsPlayerData,false)) {
                            if(ConfigUtils.getBoolean("options.click.sound.upgrade_ready.enable")) {
                                p.playSound(p.getLocation(), Sound.valueOf(ConfigUtils.getString("options.click.sound.upgrade_ready.sound")),1.0f,1.0f);
                            }
                            jobsSkillHandler.requireChecker(jobSkill.getJobsLevels().get(levelClicked), xsPlayerData,true);
                            playerSkill.setLevel(playerSkill.getLevel()+1);
                            xsPlayerData.updateSkillStats();
                            jobsMaxUI.openUI(xsPlayerData.getPlayer());
                        } else {
                            if(ConfigUtils.getBoolean("options.click.sound.upgrade_require_more.enable")) {
                                p.playSound(p.getLocation(), Sound.valueOf(ConfigUtils.getString("options.click.sound.upgrade_require_more.sound")),1.0f,1.0f);
                            }
                        }
                    }

                    //p.sendMessage(jobSkill.getNameID()+"-"+levelClicked);
                }
            }

            e.setCancelled(true);
        }
    }

}