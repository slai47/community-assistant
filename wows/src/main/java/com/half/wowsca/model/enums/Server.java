package com.half.wowsca.model.enums;

/**
 * Created by slai4 on 9/15/2015.
 */
public enum Server {

    NA(".com", "na", "na"),
    EU(".eu", "eu", "eu"),
    RU(".ru",  "ru", "ru"),
    SEA(".asia", "asia", "asia"),
    KR(".kr", "kr", "asia");

    private final String appId = "2acfbd91939b7bd250094257551d1f28";

    private String suffix;
    private String serverName;
    private String warshipsToday;

    Server(String suffix, String serverName, String warshipsToday) {
        this.suffix = suffix;
        this.serverName = serverName;
        this.warshipsToday = warshipsToday;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getAppId() {
        return appId;
    }

    public String getServerName() {
        return serverName;
    }

    public String getWarshipsToday() {
        return warshipsToday;
    }
}
