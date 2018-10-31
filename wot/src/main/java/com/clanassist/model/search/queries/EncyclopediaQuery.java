package com.clanassist.model.search.queries;

import com.clanassist.model.search.enums.EncyclopediaType;
import com.utilities.Utils;
import com.utilities.search.Query;

/**
 * Created by Harrison on 4/4/2015.
 */
public class EncyclopediaQuery extends Query {

    private String webAddress;
    private EncyclopediaType type;
    private String language;
    private String applicationIdString;

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(webAddress);
        sb.append(type.getUrl());
        sb.append(applicationIdString);
        sb.append("&language=" + language);
        return Utils.validateUrl(sb.toString());
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public EncyclopediaType getType() {
        return type;
    }

    public void setType(EncyclopediaType type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getApplicationIdString() {
        return applicationIdString;
    }

    public void setApplicationIdString(String applicationIdString) {
        this.applicationIdString = applicationIdString;
    }
}
