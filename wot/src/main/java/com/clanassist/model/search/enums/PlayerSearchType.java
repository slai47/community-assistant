package com.clanassist.model.search.enums;

/**
 * Created by Obsidian47 on 3/2/14.
 */
public enum PlayerSearchType {

    SEARCH("/wot/account/list/?"),
    DETAILS("/wot/account/info/?"),
    TANKS("/wot/account/tanks/?"),
    VEHICLES("/wot/tanks/stats/?");

    private String url;

    PlayerSearchType(String str) {
        url = str;
    }

    public String getUrl() {
        return url;
    }
}
