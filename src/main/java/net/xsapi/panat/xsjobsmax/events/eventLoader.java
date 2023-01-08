package net.xsapi.panat.xsjobsmax.events;

import net.xsapi.panat.xsjobsmax.core.core;

public class eventLoader {

    public eventLoader() {

        core.getPlugin().getServer().getPluginManager().registerEvents(new joinEvent(),core.getPlugin());
        core.getPlugin().getServer().getPluginManager().registerEvents(new quitEvent(),core.getPlugin());
        core.getPlugin().getServer().getPluginManager().registerEvents(new uiEvent(),core.getPlugin());

    }

}