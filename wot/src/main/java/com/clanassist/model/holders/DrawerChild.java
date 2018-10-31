package com.clanassist.model.holders;

import com.clanassist.model.enums.DrawerType;
import com.clanassist.model.enums.Server;

/**
 * Created by Harrison on 8/4/2014.
 */
public class DrawerChild {

    private String title;
    private int id;
    private DrawerType type;
    private Server server;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DrawerType getType() {
        return type;
    }

    public void setType(DrawerType type) {
        this.type = type;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
