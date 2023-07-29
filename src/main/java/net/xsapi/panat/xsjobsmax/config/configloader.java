package net.xsapi.panat.xsjobsmax.config;

import org.bukkit.Bukkit;

public class configloader {

    public configloader() {

        Bukkit.getConsoleSender().sendMessage("§aLoad config.yml....");
        new config().loadConfigu();
        new messages().loadConfigu();
        new skills().loadConfigu();
        new items().loadConfigu();
        new ability().loadConfigu();
    }

}