package net.xsapi.panat.xsjobsmax.player;

import net.xsapi.panat.xsjobsmax.config.config;
import net.xsapi.panat.xsjobsmax.core.core;
import net.xsapi.panat.xsjobsmax.core.jobsSkillHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.xsapi.panat.xsjobsmax.core.core.getTABLE;
import static net.xsapi.panat.xsjobsmax.core.core.getUsingSQL;

public class xsPlayer {

    private Player player;
    private int pageOpen = 1;
    private File userFile;
    private FileConfiguration userConfig;
    private HashMap<String,xsSkill> skillList = new HashMap<>();
    private ArrayList<String> skillMenuID = new ArrayList<>();

    private HashMap<String,Integer> ability = new HashMap<String, Integer>();
    private HashMap<String,Long> cooldowns = new HashMap<String,Long>();

    private HashMap<String,Long> activated_ability = new HashMap<String,Long>();

    public xsPlayer(Player p) {
        this.player = p;
        this.pageOpen = 1;

        if(getUsingSQL()) {
            createUserSQL();
            normalStats();
            checkSkill();
            loadSQLUserData();
        } else {
            userFile = new File(core.getPlugin().getDataFolder() + "/players",p.getUniqueId()+".yml");
            userConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(userFile);
            createUserFile();
        }

        core.getXSPlayer().put(player.getUniqueId(),this);
    }

    public HashMap<String, Integer> getAbility() {
        return ability;
    }

    public HashMap<String,Long> getCooldowns() {
        return cooldowns;
    }
    public HashMap<String,Long> getActivated_ability() {
        return activated_ability;
    }

    public ArrayList<String> getSkillMenuID() {
        return skillMenuID;
    }

    public void setSkillMenuID(ArrayList<String> skillMenuID) {
        this.skillMenuID = skillMenuID;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPageOpen() {
        return pageOpen;
    }

    public void setPageOpen(int pageOpen) {
        this.pageOpen = pageOpen;
    }

    public HashMap<String,xsSkill> getSkillList() {
        return skillList;
    }

    public void setSkillList(HashMap<String,xsSkill> skillList) {
        this.skillList = skillList;
    }

    public void createUserFile() {
        YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(this.userFile);
        if (!userFile.exists()) {
            userConfig.set("AccoutName", player.getName());
        }

        normalStats();
        checkSkill();
        loadSkill();

        try {
            userConfig.save(userFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createUserSQL() {
        try {
            Connection connection = DriverManager.getConnection(core.getPlugin().getJDBC_URL(),core.getPlugin().getUSER(),core.getPlugin().getPASS());

            String checkPlayerQuery = "SELECT EXISTS(SELECT * FROM " + getTABLE() + " WHERE player = ?) AS exist";
            PreparedStatement preparedStatement = connection.prepareStatement(checkPlayerQuery);
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                boolean exists = resultSet.getBoolean("exist");

                if (!exists) {
                    String insertQuery = "INSERT INTO " + getTABLE() + " (uuid, player, hunter, miner, fisher, farmer, digger, alchemist) "
                            + "VALUES (?, ?, 0, 0, 0, 0, 0, 0)";

                    PreparedStatement insertPreparedStatement = connection.prepareStatement(insertQuery);
                    insertPreparedStatement.setString(1, String.valueOf(player.getUniqueId()));
                    insertPreparedStatement.setString(2, player.getName());
                    insertPreparedStatement.executeUpdate();
                    insertPreparedStatement.close();
                } else {
                    Bukkit.getConsoleSender().sendMessage("Player : " + player.getName() + " already exists");
                }
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadSQLUserData() {
        try {
            Connection connection = DriverManager.getConnection(core.getPlugin().getJDBC_URL(), core.getPlugin().getUSER(), core.getPlugin().getPASS());

            String selectQuery = "SELECT hunter, miner, fisher, farmer, digger, alchemist FROM " + getTABLE() + " WHERE player = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                for (String skill : config.customConfig.getConfigurationSection("gui.skill").getKeys(false)) {
                    xsSkill skillID = new xsSkill(skill,resultSet.getInt(skill));
                    skillList.put(skill,skillID);
                    jobsSkillHandler.getAbilityList(this,skill);
                }
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void normalStats() {
        ability.put("MIGHTY",0);
        ability.put("AGILITY",0);
        ability.put("TOUGHNESS",0);
    }

    public void updateSkillStats() {
        normalStats();
        for (String skill : config.customConfig.getConfigurationSection("gui.skill").getKeys(false)) {
            jobsSkillHandler.getAbilityList(this,skill);
        }

    }

    public void loadSkill() {

        for (String skill : config.customConfig.getConfigurationSection("gui.skill").getKeys(false)) {
            xsSkill skillID = new xsSkill(skill,userConfig.getInt("skills."+skill+".level"));
            skillList.put(skill,skillID);
            jobsSkillHandler.getAbilityList(this,skill);
        }
    }

    public FileConfiguration getUserConfig() {
        return userConfig;
    }

    public void checkSkill() {
        for (String skill : config.customConfig.getConfigurationSection("gui.skill").getKeys(false)) {
            if(getUsingSQL()) {
                try {
                    Connection connection = DriverManager.getConnection(core.getPlugin().getJDBC_URL(),core.getPlugin().getUSER(),core.getPlugin().getPASS());
                    DatabaseMetaData metaData = connection.getMetaData();

                    ResultSet resultSet = metaData.getColumns(null, null, getTABLE(), skill);

                    if(!resultSet.next()) {
                        getAbility().put(skill,0);
                    }

                    resultSet.close();
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } else {
                if(userConfig.get("skills."+skill) == null) {
                    userConfig.set("skills."+skill+".level", 0);
                    getAbility().put(skill,0);
                }
            }
        }
    }

    public void saveSkillData() {

        if(getUsingSQL()) {
            try {
                Connection connection = DriverManager.getConnection(core.getPlugin().getJDBC_URL(), core.getPlugin().getUSER(), core.getPlugin().getPASS());

                String updateQuery = "UPDATE " + getTABLE() + " SET hunter = ?, miner = ?, fisher = ?, farmer = ?, digger = ?, alchemist = ? WHERE player = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);

                preparedStatement.setInt(1, skillList.get("hunter").getLevel());
                preparedStatement.setInt(2, skillList.get("miner").getLevel());
                preparedStatement.setInt(3, skillList.get("fisher").getLevel());
                preparedStatement.setInt(4, skillList.get("farmer").getLevel());
                preparedStatement.setInt(5, skillList.get("digger").getLevel());
                preparedStatement.setInt(6, skillList.get("alchemist").getLevel());
                preparedStatement.setString(7, player.getName());

                preparedStatement.executeUpdate();
                Bukkit.getConsoleSender().sendMessage("§x§f§f§c§e§2§2[XSJOBS] Saved Player: " + player.getName() + " via SQL successfully");
                preparedStatement.close();
                connection.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            for (Map.Entry<String,xsSkill> skillData : skillList.entrySet()) {
                xsSkill skill = skillData.getValue();
                String type = skillData.getKey();

                userConfig.set("skills."+type+".level", skill.getLevel());
            }
        }
    }

    public void saveUser() {

        saveSkillData();
        core.getXSPlayer().remove(player.getUniqueId());
        if(!getUsingSQL()) {
            try {
                getUserConfig().save(userFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}