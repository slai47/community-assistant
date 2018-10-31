package com.clanassist.model.search.queries;

import com.clanassist.model.player.Player;
import com.clanassist.model.search.enums.PlayerSearchType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harrison on 8/5/2014.
 */
public class ClanPlayerWN8sTaskQuery {

    private List<Player> players;
    private String language;
    private PlayerSearchType type;
    private String applicationIdString;
    private String webAddress;
    private List<Integer> tankNumbers;

    /**
     * Creates all of the player queries to fire off to the Player parser to be consumed
     *
     * @return
     */
    public List<PlayerQuery> getUrls() {
        List<PlayerQuery> listOfUrls = new ArrayList<PlayerQuery>();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            PlayerQuery query = new PlayerQuery();
            query.setAccount_id(p.getId());
            query.setApplicationIdString(applicationIdString);
            query.setLanguage(language);
            query.setType(type);
            query.setWebAddress(webAddress);
            query.setTankNumbers(tankNumbers);
            listOfUrls.add(query);
        }
        return listOfUrls;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public PlayerSearchType getType() {
        return type;
    }

    public void setType(PlayerSearchType type) {
        this.type = type;
    }

    public String getApplicationIdString() {
        return applicationIdString;
    }

    public void setApplicationIdString(String applicationIdString) {
        this.applicationIdString = applicationIdString;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public List<Integer> getTankNumbers() {
        return tankNumbers;
    }

    public void setTankNumbers(List<Integer> tankNumbers) {
        this.tankNumbers = tankNumbers;
    }
}
