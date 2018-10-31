package com.clanassist.model.search.queries;

import android.text.TextUtils;

import com.clanassist.model.search.enums.GlobalWarSearchType;
import com.utilities.Utils;
import com.utilities.search.Query;

/**
 * Created by Harrison on 5/13/14.
 */
public class GlobalWarQuery extends Query {

    private GlobalWarSearchType type;
    private String webAddress;
    private String language;
    private String applicationIdString;
    private String mapId;
    //for province search
    private String provinceId;
    private int clanId;

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(webAddress);
        sb.append(type.getUrl());
        sb.append(applicationIdString);
        if (type == GlobalWarSearchType.PROVINCE)
            sb.append("&province_id=" + provinceId);
        if (type == GlobalWarSearchType.BATTLES) {
            sb.append("&clan_id=" + clanId);
        }
        if (!TextUtils.isEmpty(mapId))
            sb.append("&map_id=" + mapId);
        sb.append("&language=" + language);

        return Utils.validateUrl(sb.toString());
    }

    public GlobalWarSearchType getType() {
        return type;
    }

    public void setType(GlobalWarSearchType type) {
        this.type = type;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
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

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public int getClanId() {
        return clanId;
    }

    public void setClanId(int clanId) {
        this.clanId = clanId;
    }
}
