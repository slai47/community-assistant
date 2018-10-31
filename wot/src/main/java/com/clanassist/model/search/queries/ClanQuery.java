package com.clanassist.model.search.queries;

import com.clanassist.model.search.enums.ClanSearchType;
import com.utilities.Utils;
import com.utilities.search.Query;

/**
 * Created by Obsidian47 on 3/2/14.
 */
public class ClanQuery extends Query {

    private String webAddress;
    private ClanSearchType type;
    private int clanId;
    private String search;
    private String language;
    private String applicationIdString;
    private String memberId;

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(webAddress);
        sb.append(type.getUrl());
        sb.append(applicationIdString);
        //add needed vars
        if (type == ClanSearchType.DETAILS) {
            sb.append("&clan_id=" + clanId);
        } else if (type == ClanSearchType.CLAN_MEMBER) {
            sb.append("&account_id=" + memberId);
        } else
            sb.append("&search=" + search);
        sb.append("&language=" + language);
        return Utils.validateUrl(sb.toString());
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public ClanSearchType getType() {
        return type;
    }

    public void setType(ClanSearchType type) {
        this.type = type;
    }

    public int getClanId() {
        return clanId;
    }

    public void setClanId(int clanId) {
        this.clanId = clanId;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
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

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
