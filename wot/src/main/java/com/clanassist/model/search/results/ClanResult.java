package com.clanassist.model.search.results;

import com.clanassist.model.clan.Clan;
import com.clanassist.model.player.PlayerClan;
import com.clanassist.model.search.queries.ClanQuery;
import com.utilities.search.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Obsidian47 on 3/6/14.
 */
public class ClanResult extends Result<ClanQuery> {

    private List<ClanQuery> queries;
    //for searches
    private List<Clan> clans;

    private Clan details;

    private PlayerClan playerClanInfo;

    public ClanResult() {
        queries = new ArrayList<ClanQuery>();
        clans = new ArrayList<Clan>();
        playerClanInfo = new PlayerClan();
    }

    public List<ClanQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<ClanQuery> queries) {
        this.queries = queries;
    }

    public List<Clan> getClans() {
        return clans;
    }

    public void setClans(List<Clan> clans) {
        this.clans = clans;
    }

    public Clan getDetails() {
        return details;
    }

    public void setDetails(Clan details) {
        this.details = details;
    }

    public PlayerClan getPlayerClanInfo() {
        return playerClanInfo;
    }

    public void setPlayerClanInfo(PlayerClan playerClanInfo) {
        this.playerClanInfo = playerClanInfo;
    }
}
