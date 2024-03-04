package net.xsapi.panat.xsjobsmax.events;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import net.xsapi.panat.xsjobsmax.config.ability;
import net.xsapi.panat.xsjobsmax.core.core;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class etDamageEvent implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {

        double damage = e.getDamage();

        if(e.getDamager() instanceof Player) {

            xsPlayer xPlayer = core.getXSPlayer().get(((Player) e.getDamager()).getPlayer().getUniqueId());
            if(xPlayer == null)  {
                return;
            }
            int mighty = xPlayer.getAbility().get("MIGHTY");
            damage = damage + (mighty/5);

        } else if(e.getDamager() instanceof Arrow) {

            if(((Arrow) e.getDamager()).getShooter() instanceof  Player) {
                Player shooter = (Player) ((Arrow) e.getDamager()).getShooter();

                if(shooter != null) {
                    xsPlayer xPlayer = core.getXSPlayer().get(shooter.getUniqueId());
                    if(xPlayer == null)  {
                        return;
                    }
                    int mighty = xPlayer.getAbility().get("MIGHTY");

                    damage = damage + (mighty/5);
                }
            }
        }

        if(e.getEntity() instanceof Player) {

            if(core.getXSPlayer().containsKey(((Player) e.getEntity()).getPlayer().getUniqueId())) {
                xsPlayer xPlayer = core.getXSPlayer().get(((Player) e.getEntity()).getPlayer().getUniqueId());
                if(xPlayer == null)  {
                    return;
                }
                int toughness = xPlayer.getAbility().get("TOUGHNESS");

                damage = damage - (((damage*((double) (toughness/10)/10)/100)));
            }
        }
        e.setDamage(damage);
    }

    @EventHandler
    public void onDamageMagic(EntityDamageEvent e) {
        if(!e.isCancelled()) {
            if (e.getEntity() instanceof Player) { //ตรวจสอบว่า entity ที่โดนดาเมจเป็น player
                Player player = (Player) e.getEntity();
                if (e.getCause() == EntityDamageEvent.DamageCause.POISON || e.getCause() == EntityDamageEvent.DamageCause.MAGIC) { //ตรวจสอบว่าเป็นพิษหรือวิทเทอร์
                    skillTriggerBrewer(core.getXSPlayer().get(player.getUniqueId()),e.getDamage());
                }
            }
        }
    }

    public void skillTriggerBrewer(xsPlayer xPlayer,double damage) {
        if(xPlayer.getAbility().get("I_AM_WITCH") != null) {
            JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(xPlayer.getPlayer());

            if (jobsPlayer != null) {
                List<JobProgression> jobs = jobsPlayer.getJobProgression();
                List<String> AllowedJobs = ability.customConfig.getStringList("ability.I_AM_WITCH.allowed_jobs");
                for (JobProgression prog : jobs) {
                    if (AllowedJobs.contains(prog.getJob().getName())) {
                        double amount = xPlayer.getAbility().get("I_AM_WITCH") * ability.customConfig.getDouble("ability.I_AM_WITCH.multiple_take_less");
                        amount = ((damage*(100-amount))/100);
                        xPlayer.getPlayer().setHealth(xPlayer.getPlayer().getHealth()-amount);
                    }
                }
            }
        }
    }

}