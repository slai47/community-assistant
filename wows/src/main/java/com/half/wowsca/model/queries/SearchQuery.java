package com.half.wowsca.model.queries;

import com.half.wowsca.model.enums.Server;

/**
 * Created by slai4 on 9/19/2015.
 */
public class SearchQuery {

    private Server server;
    private String search;
    private String language;

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
