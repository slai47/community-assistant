package com.clanassist.model.enums;

/**
 * Created by Obsidian47 on 3/23/14.
 */
public enum Server {

    US(".com", "2acfbd91939b7bd250094257551d1f28", "na", "na"),
    EU(".eu", "c8a524994b28774872d47ba545addd62", "eu", "eu"),
    RU(".ru", "ced111c38f0ae5047a2d284c5dce0ee1", "ru", "ru"),
    SEA(".asia", "6f8068566313adfe4579741390bc8a94", "sea", "asia");
//    KOREA(".kr", "");

    private String suffix;
    private String applicationId;
    private String serverName;
    private String ClanToolsServerName;

    Server(String suffix, String appId, String noobmeterName, String clanToolsServerName) {
        this.suffix = suffix;
        applicationId = appId;
        this.serverName = noobmeterName;
        this.ClanToolsServerName = clanToolsServerName;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getServerName() {
        return serverName;
    }

    public String getClanToolsServerName() {
        return ClanToolsServerName;
    }
}
