package net.xsapi.panat.xsjobsmax.core;

import net.milkbowl.vault.economy.Economy;

import net.xsapi.panat.xsjobsmax.command.commandsLoader;
import net.xsapi.panat.xsjobsmax.config.configloader;
import net.xsapi.panat.xsjobsmax.config.skills;
import net.xsapi.panat.xsjobsmax.events.eventLoader;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class core extends JavaPlugin {

    public static core plugin;
    public static HashMap<UUID, xsPlayer> playerData = new HashMap<>();
    public static HashMap<String,jobsSkill> jobsSkillsList = new HashMap<>();

    public static core getPlugin() {
        return plugin;
    }

    public static HashMap<UUID, xsPlayer> getXSPlayer() {
        return playerData;
    }
    public static HashMap<String,jobsSkill> getJobsSkillsList() { return jobsSkillsList; }

    private static Economy econ = null;
    private static PlayerPointsAPI ppAPI = null;

    @Override
    public void onEnable() {
        plugin = this;

        new configloader();
        new commandsLoader();
        new eventLoader();
        loadSKill();
        reStoreData();
        APILoader();

        Bukkit.getLogger().info("§x§f§f§a§c§2§f******************************");
        Bukkit.getLogger().info("§x§f§f§a§c§2§f   XSAPI JobsMax v1.0     ");
        Bukkit.getLogger().info("§r");
        Bukkit.getLogger().info("§x§f§f§a§c§2§f  Beyond The Limit!!");
        Bukkit.getLogger().info("§r");
        Bukkit.getLogger().info("§x§f§f§a§c§2§f******************************");

    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("§c[XSJOBS] Plugin Disabled 1.19.2!");
    }

    public void reStoreData() {

        for(Player p : Bukkit.getOnlinePlayers()) {
            xsPlayer xsPlayerData = new xsPlayer(p);
        }

    }

    public void loadSKill() {
        for (String skillID : skills.customConfig.getConfigurationSection("skills").getKeys(false)) {
            jobsSkill skill = new jobsSkill(skillID,skills.customConfig.getConfigurationSection("skills."+skillID).getKeys(false).size());

            for(String ability_level : skills.customConfig.getConfigurationSection("skills."+skillID).getKeys(false)) {
                jobsLevel jL = new jobsLevel(
                        new ArrayList<String>(skills.customConfig.getStringList("skills."+skillID+"."+ability_level+".ability")),
                        new ArrayList<String>(skills.customConfig.getStringList("skills."+skillID+"."+ability_level+".required")));
                skill.getJobsLevels().add(jL);
            }

            getJobsSkillsList().put(skillID,skill);
        }
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            this.getLogger().info("§x§f§f§5§8§5§8[XSJOBS] Vault Not Found!");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public boolean setupSCCoin() {
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
            this.ppAPI = PlayerPoints.getInstance().getAPI();
        }
        if (this.ppAPI != null) {
            return true;
        } else {
            return false;
        }
    }

    public void APILoader() {
        if (!setupEconomy()) {
            Bukkit.getLogger().info("§x§f§f§5§8§5§8[XSJOBS] Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            Bukkit.getLogger().info("§x§f§f§c§e§2§2[XSJOBS] Vault: §x§5§d§f§f§6§3Hook");
        }

        if(!setupSCCoin()) {
            Bukkit.getLogger().info("§x§f§f§c§e§2§2[XSHOP] PlayerPoint: §x§f§f§5§8§5§8Not Hook");
        } else {
            Bukkit.getLogger().info("§x§f§f§c§e§2§2[XSHOP] PlayerPoint: §x§5§d§f§f§6§3Hook");
        }

    }

    public static Economy getEconomy() {
        return econ;
    }
    public static PlayerPointsAPI getSCPoint() {
        return ppAPI;
    }

}
