package net.xsapi.panat.xsjobsmax.player;

import net.xsapi.panat.xsjobsmax.config.config;
import net.xsapi.panat.xsjobsmax.core.core;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class xsPlayer {

    private Player player;
    private int pageOpen = 1;
    private File userFile;
    private FileConfiguration userConfig;
    private HashMap<String,xsSkill> skillList = new HashMap<>();
    private ArrayList<String> skillMenuID = new ArrayList<>();

    public xsPlayer(Player p) {
        this.player = p;
        this.pageOpen = 1;
        userFile = new File(core.getPlugin().getDataFolder() + "/players",p.getUniqueId()+".yml");
        userConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(userFile);

        core.getXSPlayer().put(player.getUniqueId(),this);

        createUserFile();
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

        checkSkill();
        loadSkill();

        try {
            userConfig.save(userFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSkill() {
        for (String skill : config.customConfig.getConfigurationSection("gui.skill").getKeys(false)) {
           xsSkill skillID = new xsSkill(skill,userConfig.getInt("skills."+skill+".level"));
            skillList.put(skill,skillID);
        }
    }

    public FileConfiguration getUserConfig() {
        return userConfig;
    }

    public void checkSkill() {
        for (String skill : config.customConfig.getConfigurationSection("gui.skill").getKeys(false)) {
            if(userConfig.get("skills."+skill) == null) {
                userConfig.set("skills."+skill+".level", 0);
            }
        }
    }

    public void saveUser() {

        core.getXSPlayer().remove(player.getUniqueId());

        try {
            getUserConfig().save(userFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}