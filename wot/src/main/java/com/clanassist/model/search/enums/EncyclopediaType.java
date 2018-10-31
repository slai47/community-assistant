package com.clanassist.model.search.enums;

/**
 * Created by Harrison on 4/4/2015.
 */
public enum EncyclopediaType {

    MAPS("/wot/encyclopedia/arenas/?");

    private String url;

    EncyclopediaType(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
