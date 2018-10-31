package com.clanassist.tools;

import android.content.Context;

import com.clanassist.CAApp;
import com.clanassist.model.enums.Server;
import com.utilities.logging.Dlog;

/**
 * Created by Harrison on 9/16/2014.
 */
public class WebsiteUrlBuilder {

    public static String getClanToolsAddress(Context ctx, boolean isPlayer, int accountId) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://www.clantools.us/servers/");
        Server saved = CAApp.getServerType(ctx);
        if (isPlayer) {
            sb.append(saved.getClanToolsServerName() + "/");
            sb.append("players?id=" + accountId);
        } else {
            sb.append(saved.getClanToolsServerName() + "/");
            sb.append("clans?id=" + accountId);
        }
        Dlog.d("WebsiteUrlBuilder", "" + sb.toString());
        return sb.toString();
    }

    public static String getWoTLabsAddress(Context ctx, boolean isPlayer, String playerName, String clanAbbr) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://www.wotlabs.net/");
        Server saved = CAApp.getServerType(ctx);
        if (isPlayer) {
            sb.append(saved.getServerName() + "/");
            sb.append("player/");
            sb.append(playerName);
        } else {
            sb.append(saved.getServerName() + "/");
            sb.append("clan/");
            sb.append(clanAbbr);
        }
        return sb.toString();
    }

    public static String getWotStatsAddress(Context ctx, boolean isPlayer, String playerName, String clanAbbr) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://www.wotstats.org/");
        Server saved = CAApp.getServerType(ctx);
        if (isPlayer) {
            sb.append("stats/");
            sb.append(saved.getServerName() + "/");
            sb.append(playerName + "/");
        } else {
            sb.append("clan/");
            sb.append(saved.getServerName() + "/");
            sb.append(clanAbbr);
        }
        return sb.toString();
    }

}
