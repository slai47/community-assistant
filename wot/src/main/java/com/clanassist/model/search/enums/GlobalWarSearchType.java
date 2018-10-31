package com.clanassist.model.search.enums;

/**
 * Created by Harrison on 5/13/14.
 */
public enum GlobalWarSearchType {

    BATTLES("/wot/globalmap/clanbattles/?"),
    PROVINCE("/wot/globalwar/provinces/?"),
    TOURNAMENT("/wot/globalwar/tournaments/?"),
    MAPS("/wot/globalwar/maps/?");

    private String url;

    GlobalWarSearchType(String str) {
        url = str;
    }

    public String getUrl() {
        return url;
    }
}
