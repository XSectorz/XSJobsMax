package net.xsapi.panat.xsjobsmax.config;

import net.xsapi.panat.xsjobsmax.core.core;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ability {
    public static File customConfigFile;

    public static FileConfiguration customConfig;

    public FileConfiguration getConfig() {
        return customConfig;
    }

    public void loadConfigu() {
        customConfigFile = new File(core.getPlugin().getDataFolder(), "ability.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            core.getPlugin().saveResource("ability.yml", false);
        }
        customConfig = (FileConfiguration) new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        customConfigFile = new File(core.getPlugin().getDataFolder(), "ability.yml");
        try {
            customConfig.options().copyDefaults(true);
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        customConfigFile = new File(core.getPlugin().getDataFolder(), "ability.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            core.getPlugin().saveResource("ability.yml", false);
        } else {
            customConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(customConfigFile);
            try {
                customConfig.save(customConfigFile);
                core.getPlugin().reloadConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}