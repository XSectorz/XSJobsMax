package net.xsapi.panat.xsjobsmax.utils;

import net.xsapi.panat.xsjobsmax.config.config;

import java.util.List;

public class ConfigUtils {

    public static String getString(String path) {
        return config.customConfig.getString(path).replace("&", "§");
    }

    public static Integer getInteger(String path) {
        return config.customConfig.getInt(path);
    }

    public static Boolean getBoolean(String path) {
        return config.customConfig.getBoolean(path);
    }

    public static List<String> getStringList(String path) {
        return config.customConfig.getStringList(path);
    }
}