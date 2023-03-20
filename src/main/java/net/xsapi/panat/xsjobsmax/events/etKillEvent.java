package net.xsapi.panat.xsjobsmax.events;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import net.xsapi.panat.xsjobsmax.config.ability;
import net.xsapi.panat.xsjobsmax.core.core;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class etKillEvent implements Listener {

    @EventHandler
    public void onKill(EntityDeathEvent e) {

        if(e.getEntity().getKiller() instanceof Player) {
            Player killer = e.getEntity().getKiller();

            skillTrigger(core.getXSPlayer().get(killer.getUniqueId()));

        }

    }



    public void skillTrigger(xsPlayer xPlayer) {
        if(xPlayer.getAbility().get("BLOOD_RUST") != null) {
            int level = xPlayer.getAbility().get("BLOOD_RUST");

            int random = (int) ((Math.random() * (100 - 0)) + 0);

            if(ability.customConfig.getInt("ability.BLOOD_RUST.multiple_chance")*level >= random) {
                int amount = ability.customConfig.getInt("ability.BLOOD_RUST.multiple_drain_hp")*level;

                Player p = xPlayer.getPlayer();
                //p.sendMessage("DRAIN : " + amount);

                JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(xPlayer.getPlayer());

                if (jobsPlayer != null) {
                    List<JobProgression> jobs = jobsPlayer.getJobProgression();
                    List<String> AllowedJobs = ability.customConfig.getStringList("ability.BLOOD_RUST.allowed_jobs");
                    for (JobProgression prog : jobs) {
                        if(AllowedJobs.contains(prog.getJob().getName())) {
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);

                            if(p.getHealth()+amount >= p.getMaxHealth()) {
                                p.setHealth(p.getMaxHealth());
                            } else {
                                p.setHealth(p.getHealth()+amount);
                            }

                            break;
                        }
                    }
                }
            }
        }
    }
}