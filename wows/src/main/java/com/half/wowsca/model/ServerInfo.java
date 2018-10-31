package com.half.wowsca.model;

import com.half.wowsca.model.enums.Server;

/**
 * Created by slai4 on 12/2/2015.
 */
public class ServerInfo {

    private Server server;
    private String name;
    private int players;

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }
}
