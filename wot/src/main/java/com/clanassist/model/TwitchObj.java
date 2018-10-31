package com.clanassist.model;

/**
 * Created by slai4 on 12/3/2015.
 */
public class TwitchObj {

    private String name;
    private String url;

    private boolean live;

    private String gamePlaying;
    private String thumbnail;
    private String logo;
    private String streamName;

    public TwitchObj(String name) {
        this.name = name;
        url = "http://www.twitch.tv/" + name;
        live = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public String getGamePlaying() {
        return gamePlaying;
    }

    public void setGamePlaying(String gamePlaying) {
        this.gamePlaying = gamePlaying;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }
}
