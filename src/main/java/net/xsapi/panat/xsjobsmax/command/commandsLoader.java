package net.xsapi.panat.xsjobsmax.command;

import net.xsapi.panat.xsjobsmax.core.core;

public class commandsLoader {

    public commandsLoader() {
        core.getPlugin().getCommand("xsjobs").setExecutor(new commands());
    }

}