package com.half.wowsca.model.queries;

import com.half.wowsca.model.enums.Server;

import java.util.Map;

/**
 * Created by slai4 on 10/31/2015.
 */
public class ShipQuery {

    private Server server;
    private long shipId;
    private String language;

    private Map<String, Long> modules;

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public long getShipId() {
        return shipId;
    }

    public void setShipId(long shipId) {
        this.shipId = shipId;
    }

    public Map<String, Long> getModules() {
        return modules;
    }

    public void setModules(Map<String, Long> modules) {
        this.modules = modules;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
