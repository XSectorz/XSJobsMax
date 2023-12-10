package net.xsapi.panat.xsjobsmax.core;

import net.milkbowl.vault.economy.Economy;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

import net.xsapi.panat.xsjobsmax.command.commandsLoader;
import net.xsapi.panat.xsjobsmax.config.config;
import net.xsapi.panat.xsjobsmax.config.configloader;
import net.xsapi.panat.xsjobsmax.config.skills;
import net.xsapi.panat.xsjobsmax.events.eventLoader;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
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
    public static CoreProtectAPI cpAPI = null;

    private static boolean usingMySQL = false;
    private static String JDBC_URL;
    private static String USER;
    private static String PASS;
    private static String DB_TABLE;
    private static String TABLE = "XSJOBSMAX_Data";

    @Override
    public void onEnable() {
        plugin = this;

        new configloader();
        new commandsLoader();
        new eventLoader();

        setUpDefault();
        if(usingMySQL) {
            sqlConnection();
        }

        loadSKill();
        reStoreData();
        APILoader();

        Bukkit.getConsoleSender().sendMessage("§x§f§f§a§c§2§f******************************");
        Bukkit.getConsoleSender().sendMessage("§x§f§f§a§c§2§f   XSAPI JobsMax v1.0     ");
        Bukkit.getConsoleSender().sendMessage("§r");
        Bukkit.getConsoleSender().sendMessage("§x§f§f§a§c§2§f  Beyond The Limit!!");
        Bukkit.getConsoleSender().sendMessage("§r");
        Bukkit.getConsoleSender().sendMessage("§x§f§f§a§c§2§f******************************");

    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§c[XSJOBS] Plugin Disabled 1.19.2!");

        for(Player p : Bukkit.getOnlinePlayers()) {
            xsPlayer xPlayer = core.getXSPlayer().get(p.getUniqueId());
            xPlayer.saveUser();
        }

    }

    public boolean setupCoreProtectAPI() {

        if (Bukkit.getPluginManager().getPlugin("CoreProtect") == null) {
            return false;
        }

        // Check that the API is enabled
        Plugin coreP = getServer().getPluginManager().getPlugin("CoreProtect");
        CoreProtectAPI CoreProtect = ((CoreProtect) coreP).getAPI();

        cpAPI = CoreProtect;

        return cpAPI != null;
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

    public boolean setupSkillAPI() {
        if (Bukkit.getPluginManager().getPlugin("AureliumSkills") != null) {
            return true;
        }
        return false;
    }

    public void APILoader() {
        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage("§x§f§f§5§8§5§8[XSJOBS] Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            Bukkit.getConsoleSender().sendMessage("§x§f§f§c§e§2§2[XSJOBS] Vault: §x§5§d§f§f§6§3Hook");
        }

        if(!setupSCCoin()) {
            Bukkit.getConsoleSender().sendMessage("§x§f§f§c§e§2§2[XSJOBS] PlayerPoint: §x§f§f§5§8§5§8Not Hook");
        } else {
            Bukkit.getConsoleSender().sendMessage("§x§f§f§c§e§2§2[XSJOBS] PlayerPoint: §x§5§d§f§f§6§3Hook");
        }

        if(!setupSkillAPI()) {
            Bukkit.getConsoleSender().sendMessage("§x§f§f§c§e§2§2[XSJOBS] AureliumSkills: §x§f§f§5§8§5§8Not Hook");
        } else {
            Bukkit.getConsoleSender().sendMessage("§x§f§f§c§e§2§2[XSJOBS] AureliumSkills: §x§5§d§f§f§6§3Hook");
        }

        if(!setupCoreProtectAPI()) {
            Bukkit.getConsoleSender().sendMessage("§x§f§f§c§e§2§2[XSJOBS] CoreProtect: §x§f§f§5§8§5§8Not Hook");
        } else {
            Bukkit.getConsoleSender().sendMessage("§x§f§f§c§e§2§2[XSJOBS] CoreProtect: §x§5§d§f§f§6§3Hook");
        }

    }

    public static Economy getEconomy() {
        return econ;
    }
    public static PlayerPointsAPI getSCPoint() {
        return ppAPI;
    }

    public static CoreProtectAPI getCoreProtectAPI() {
        return cpAPI;
    }

    public void setUpDefault() {
        this.usingMySQL = config.customConfig.getBoolean("database.enable");
    }

    public void sqlConnection() {
        String host = config.customConfig.getString("database.host");
        DB_TABLE =  config.customConfig.getString("database.dbTable");
        JDBC_URL = "jdbc:mysql://" + host +  "/" + getDB_TABLE();
        USER = config.customConfig.getString("database.user");
        PASS = config.customConfig.getString("database.password");

        try {
            Connection connection = DriverManager.getConnection(JDBC_URL,USER,PASS);

            Statement statement = connection.createStatement();

            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + getTABLE() + " ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "uuid VARCHAR(36), "
                    + "player VARCHAR(16), "
                    + "hunter INT DEFAULT 0, "
                    + "miner INT DEFAULT 0, "
                    + "fisher INT DEFAULT 0, "
                    + "farmer INT DEFAULT 0, "
                    + "digger INT DEFAULT 0, "
                    + "alchemist INT DEFAULT 0"
                    + ")";

            statement.executeUpdate(createTableQuery);
            statement.close();
            connection.close();

            Bukkit.getConsoleSender().sendMessage("§x§E§7§F§F§0§0[XSJOBS] Database : §x§6§0§F§F§0§0Connected");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§x§E§7§F§F§0§0[XSJOBS] Database : §x§C§3§0§C§2§ANot Connected");
            e.printStackTrace();
        }
    }

    public static String getTABLE() {
        return TABLE;
    }

    public static boolean getUsingSQL() {
        return usingMySQL;
    }

    public String getDB_TABLE() {
        return DB_TABLE;
    }

    public String getJDBC_URL() {
        return JDBC_URL;
    }

    public String getUSER() {
        return USER;
    }

    public String getPASS() {
        return PASS;
    }


}
