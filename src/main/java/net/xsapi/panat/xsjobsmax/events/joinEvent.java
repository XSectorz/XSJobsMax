package net.xsapi.panat.xsjobsmax.events;

import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class joinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        xsPlayer playerData = new xsPlayer(p);
    }

}