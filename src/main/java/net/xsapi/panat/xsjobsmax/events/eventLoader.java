package net.xsapi.panat.xsjobsmax.events;

import net.xsapi.panat.xsjobsmax.core.core;

public class eventLoader {

    public eventLoader() {

        core.getPlugin().getServer().getPluginManager().registerEvents(new joinEvent(),core.getPlugin());
        core.getPlugin().getServer().getPluginManager().registerEvents(new quitEvent(),core.getPlugin());
        core.getPlugin().getServer().getPluginManager().registerEvents(new uiEvent(),core.getPlugin());
        core.getPlugin().getServer().getPluginManager().registerEvents(new etDamageEvent(),core.getPlugin());
        core.getPlugin().getServer().getPluginManager().registerEvents(new manaRegenEvent(),core.getPlugin());
        core.getPlugin().getServer().getPluginManager().registerEvents(new etKillEvent(),core.getPlugin());
        core.getPlugin().getServer().getPluginManager().registerEvents(new blockBreakEvent(),core.getPlugin());
        core.getPlugin().getServer().getPluginManager().registerEvents(new fishEvent(),core.getPlugin());
    }

}