package com.clanassist.model.search.enums;

/**
 * Created by Obsidian47 on 3/2/14.
 */
public enum ClanSearchType {

    SEARCH("/wgn/clans/list/?"),
    DETAILS("/wgn/clans/info/?"),
    CLAN_MEMBER("/wgn/clans/membersinfo/?");


    private String url;

    ClanSearchType(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
