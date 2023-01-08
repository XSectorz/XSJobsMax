package net.xsapi.panat.xsjobsmax.utils;

import net.xsapi.panat.xsjobsmax.config.messages;

public class MessagesUtils {

    public static String messages(String path) {

        return messages.customConfig.getString(path).replace("<prefix>", messages.customConfig.getString("prefix"))
                .replace("&", "§");
    }

    public static String replaceColor(String str) {
        return str.replace("&", "§");
    }

}