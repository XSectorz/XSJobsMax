package net.xsapi.panat.xsjobsmax.core;

import net.xsapi.panat.xsjobsmax.config.messages;
import net.xsapi.panat.xsjobsmax.player.xsPlayer;
import net.xsapi.panat.xsjobsmax.utils.MessagesUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class jobsSkillHandler {

    public static boolean requireChecker(jobsLevel jL,xsPlayer xPlayer) {

        int playerHave = 0;

        for(String req : jL.getRequiredList()) {

            String reqType = req.split(":")[0];
            int amt = Integer.parseInt(req.split(":")[1]);

            if(reqType.equalsIgnoreCase("MONEY")) {
                playerHave = (int) core.getEconomy().getBalance(xPlayer.getPlayer());
            } else if(reqType.equalsIgnoreCase("SC_POINT")) {
                playerHave = core.getSCPoint().look(xPlayer.getPlayer().getUniqueId());
            }

            if(playerHave < amt) {
                return false;
            }
        }

        return true;
    }

    public static List<String> decodeSkill(String type, int amt) {

        if(messages.customConfig.get("skills."+type) == null) {
            return new ArrayList<>(Arrays.asList(MessagesUtils.messages("skill_unknow")));
        }

        ArrayList<String> lores = new ArrayList<String>(messages.customConfig.getStringList("skills."+type));
        ArrayList<String> loreReture = new ArrayList<String>();

        for(String lore : lores) {
            lore = MessagesUtils.replaceColor(lore);
            loreReture.add(lore.replace("{amount}",amt+""));
        }

        return loreReture;

    }

    public static String decodeRequired(xsPlayer player, String type, int amt) {

        int playerHave = 0;

        String format = "";

        if(type.equalsIgnoreCase("MONEY")) {
            playerHave = (int) core.getEconomy().getBalance(player.getPlayer());
        } else if(type.equalsIgnoreCase("SC_POINT")) {
            playerHave = core.getSCPoint().look(player.getPlayer().getUniqueId());
        }

        if(playerHave >= amt) {
            format = messages.customConfig.getString("upgrades.upgrade_required_success");
        } else {
            format = messages.customConfig.getString("upgrades.upgrade_required_needed");
        }

        format = format.replace("%have%",playerHave+"");
        format = format.replace("%required%",amt+"");
        if(messages.customConfig.get("required."+type) != null) {
            format = format.replace("%name%",messages.customConfig.getString("required."+type));
        }
        format = MessagesUtils.replaceColor(format);

        return format;

    }

}