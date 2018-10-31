package com.clanassist.backend.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.clanassist.CAApp;
import com.clanassist.model.player.Player;
import com.clanassist.model.search.queries.PlayerQuery;
import com.clanassist.model.search.results.PlayerResult;

/**
 * Created by Harrison on 5/23/2015.
 */
public class GetAllPlayerInfo extends AsyncTask<Integer, Integer, Player> {

    private Context ctx;

    private String baseUrl;
    private String language;
    private String server;

    private DownloadClanAsync callback;

    private boolean canceled;

    private boolean grabRecentWN8;

    public GetAllPlayerInfo(Context ctx, DownloadClanAsync callback, String baseUrl, String language, String server) {
        this.ctx = ctx;
        this.baseUrl = baseUrl;
        this.language = language;
        this.server = server;
        this.callback = callback;
    }

    @Override
    protected Player doInBackground(Integer... params) {
        int accountId = params[0];
        if (!canceled) {
            PlayerQuery details = new PlayerQuery();
            details.setAccount_id(accountId);
            details.setApplicationIdString(baseUrl);
            details.setLanguage(language);
            details.setWebAddress(server);

            GetTankerInfo info = new GetTankerInfo(ctx, false, grabRecentWN8, false, false);
            PlayerResult result = info.getPlayerResult(details);

            Player player = new Player();
            player.setId(accountId);
            if (result != null && !canceled) {
                Player p = result.getPlayer();
                if (p != null) {
                    player = p;
                }
            }
            return player;
        } else {
            Player player = new Player();
            player.setId(accountId);
            return player;
        }
    }

    @Override
    protected void onPostExecute(Player player) {
        super.onPostExecute(player);
        if(callback != null) {
            callback.playerReceived(player);
        } else {
            CAApp.getEventBus().post(player);
        }
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isGrabRecentWN8() {
        return grabRecentWN8;
    }

    public void setGrabRecentWN8(boolean grabRecentWN8) {
        this.grabRecentWN8 = grabRecentWN8;
    }
}
