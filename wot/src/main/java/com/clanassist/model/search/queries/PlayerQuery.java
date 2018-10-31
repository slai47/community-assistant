package com.clanassist.model.search.queries;

import com.clanassist.model.search.enums.PlayerSearchType;
import com.utilities.Utils;
import com.utilities.search.Query;

import java.util.List;

/**
 * Created by Obsidian47 on 3/2/14.
 */
public class PlayerQuery extends Query {

    private String webAddress; //remove
    private String applicationIdString;//remove
    private PlayerSearchType type;
    private int account_id;
    private String search;
    private String language;
    private List<Integer> tankNumbers;//remove

    private String name; //This is used for the ClanPlayerWN8sTask

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(webAddress);
        sb.append(type.getUrl());
        sb.append(applicationIdString);
        //add needed vars
        if (type == PlayerSearchType.SEARCH) {
            sb.append("&search=" + search);
            sb.append("&limit=50");
        } else {
            sb.append("&account_id=" + account_id);
        }
        if (type == PlayerSearchType.VEHICLES) {
            sb.append("&fields=all,clan,tank_id,max_xp,max_frags,mark_of_mastery,stronghold_defense,stronghold_skirmish");
        }
        if (tankNumbers != null) {
            sb.append("&tank_id=");
            int i = 0;
            for (Integer num : tankNumbers) {
                sb.append(num);
                i++;
                if (i < tankNumbers.size())
                    sb.append(",");
            }
        }
        sb.append("&language=" + language);
        return Utils.validateUrl(sb.toString());
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public PlayerSearchType getType() {
        return type;
    }

    public void setType(PlayerSearchType type) {
        this.type = type;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
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

    public void setTankNumbers(List<Integer> tankNumbers) {
        this.tankNumbers = tankNumbers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
