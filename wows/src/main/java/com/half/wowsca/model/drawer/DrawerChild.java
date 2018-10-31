package com.half.wowsca.model.drawer;


import com.half.wowsca.model.enums.DrawerType;
import com.half.wowsca.model.enums.Server;

/**
 * Created by Harrison on 8/4/2014.
 */
public class DrawerChild {

    private String title;
    private long id;
    private DrawerType type;
    private boolean isSearch;
    private Server server;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DrawerType getType() {
        return type;
    }

    public void setType(DrawerType type) {
        this.type = type;
    }

    public boolean isSearch() {
        return isSearch;
    }

    public void setSearch(boolean isSearch) {
        this.isSearch = isSearch;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
