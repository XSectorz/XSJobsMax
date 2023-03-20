package net.xsapi.panat.xsjobsmax.events;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import net.xsapi.panat.xsjobsmax.config.ability;
import net.xsapi.panat.xsjobsmax.config.items;
import net.xsapi.panat.xsjobsmax.core.core;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import net.xsapi.panat.xsjobsmax.utils.MessagesUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class fishEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFish(PlayerFishEvent e) {
        if(e.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            if(e.isCancelled()) {
                skillTriggerFishMaster(core.getXSPlayer().get(e.getPlayer().getUniqueId()));
            }
        }
    }

    public void skillTriggerFishMaster(xsPlayer xPlayer) {
        if(xPlayer.getAbility().get("FISH_MASTER") != null) {
            int level = xPlayer.getAbility().get("FISH_MASTER");

            int random = (int) ((Math.random() * (100 - 0)) + 0);

            if(ability.customConfig.getInt("ability.FISH_MASTER.multiple_chance")*level >= random) {
                Player p = xPlayer.getPlayer();
                JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(xPlayer.getPlayer());

                if (jobsPlayer != null) {
                    List<JobProgression> jobs = jobsPlayer.getJobProgression();
                    List<String> AllowedJobs = ability.customConfig.getStringList("ability.FISH_MASTER.allowed_jobs");
                    for (JobProgression prog : jobs) {
                        if(AllowedJobs.contains(prog.getJob().getName())) {
                            p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG,1,1);
                            List<String> dropL = ability.customConfig.getStringList("ability.FISH_MASTER.drops_list");
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
                            p.sendMessage(MessagesUtils.messages("fish_item_get_activated")
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