package net.xsapi.panat.xsjobsmax.config;

import org.bukkit.Bukkit;

public class configloader {

    public configloader() {

        Bukkit.getLogger().info("§aLoad config.yml....");
        new config().loadConfigu();
        new messages().loadConfigu();
        new skills().loadConfigu();
        new items().loadConfigu();
    }

}