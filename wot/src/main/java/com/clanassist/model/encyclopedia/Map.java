package com.clanassist.model.encyclopedia;

/**
 * Created by Harrison on 4/4/2015.
 */
public class Map {

    private String name_i18n;
    private String description;
    private String id;

    private String gridUrl;
    private String screenUrl;

    public String getName_i18n() {
        return name_i18n;
    }

    public void setName_i18n(String name_i18n) {
        this.name_i18n = name_i18n;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGridUrl() {
        return gridUrl;
    }

    public void setGridUrl(String gridUrl) {
        this.gridUrl = gridUrl;
    }

    public String getScreenUrl() {
        return screenUrl;
    }

    public void setScreenUrl(String screenUrl) {
        this.screenUrl = screenUrl;
    }
}
