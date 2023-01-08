package net.xsapi.panat.xsjobsmax.command;

import net.xsapi.panat.xsjobsmax.core.core;
import net.xsapi.panat.xsjobsmax.gui.jobsMaxUI;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import net.xsapi.panat.xsjobsmax.utils.MessagesUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String arg, String[] args) {

        if(commandSender instanceof Player) {
            Player sender = (Player) commandSender;

            if(command.getName().equalsIgnoreCase("xsjobs")) {
                if(args.length == 0) {
                    if(!sender.hasPermission("xsjobs.ui")) {
                        sender.sendMessage(MessagesUtils.messages("no_permission"));
                        return false;
                    }
                    xsPlayer xsPlayerData = core.getXSPlayer().get(sender.getUniqueId());
                    xsPlayerData.setPageOpen(1);
                    jobsMaxUI.openUI(sender);
                    return true;
                }
            }
        }

        return false;
    }

}