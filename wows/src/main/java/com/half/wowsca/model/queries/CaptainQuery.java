package com.half.wowsca.model.queries;

import com.half.wowsca.model.enums.Server;

/**
 * Created by slai4 on 9/15/2015.
 */
public class CaptainQuery {

    private String name;
    private long id;
    private Server server;
    private String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
