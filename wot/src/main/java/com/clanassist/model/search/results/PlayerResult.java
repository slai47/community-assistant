package com.clanassist.model.search.results;

import com.clanassist.model.player.Badges;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.search.queries.PlayerQuery;
import com.utilities.search.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Obsidian47 on 3/6/14.
 */
public class PlayerResult extends Result<PlayerQuery> {

    private List<PlayerQuery> queries;

    private Player player;
    private List<Player> players;

    public PlayerResult() {
        players = new ArrayList<Player>();
        queries = new ArrayList<PlayerQuery>();
    }

    @Override
    public String toString() {
        return "PlayerResult{" +
                ", player=" + player +
                '}';
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<PlayerQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<PlayerQuery> queries) {
        this.queries = queries;
    }
}
