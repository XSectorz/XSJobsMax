package net.xsapi.panat.xsjobsmax.events;

import com.archyx.aureliumskills.api.event.ManaRegenerateEvent;
import net.xsapi.panat.xsjobsmax.core.core;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class manaRegenEvent implements Listener {

    @EventHandler
    public void manaRegen(ManaRegenerateEvent e) {
        xsPlayer xsPlayerData = core.getXSPlayer().get(e.getPlayer().getUniqueId());

        int agility = xsPlayerData.getAbility().get("AGILITY");

        e.setAmount(e.getAmount()+(e.getAmount()*(agility/10)/100));
    }

}