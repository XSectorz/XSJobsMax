package net.xsapi.panat.xsjobsmax.events;

import net.xsapi.panat.xsjobsmax.core.core;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class quitEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        core.getXSPlayer().get(p.getUniqueId()).saveUser();
    }

}