package com.clanassist.model.player;

import com.clanassist.model.clan.Emblems;

import java.util.Calendar;

/**
 * Created by Obsidian47 on 3/2/14.
 */
public class PlayerClan {

    private String role_i18n;
    private String name;
    private String abbr;
    private int clanId;
    private String role;
    private String color;
    private Calendar since;
    private Emblems emblems;

    public String getRole_i18n() {
        return role_i18n;
    }

    public void setRole_i18n(String role_i18n) {
        this.role_i18n = role_i18n;
    }

    public int getClanId() {
        return clanId;
    }

    public void setClanId(int clanId) {
        this.clanId = clanId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Calendar getSince() {
        return since;
    }

    public void setSince(Calendar since) {
        this.since = since;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Emblems getEmblems() {
        return emblems;
    }

    public void setEmblems(Emblems emblems) {
        this.emblems = emblems;
    }
}
