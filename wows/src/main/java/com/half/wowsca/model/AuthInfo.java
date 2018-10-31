package com.half.wowsca.model;

import android.content.Context;

import com.utilities.preferences.Prefs;

import java.util.Calendar;

/**
 * Created by slai4 on 5/1/2016.
 */
public class AuthInfo {

    public static final String LOGIN_ACCOUNT_ID = "login_account_id";
    public static final String LOGIN_EXPIRES = "login_expires";
    public static final String LOGIN_TOKEN = "login_token";
    public static final String LOGIN_USERNAME = "login_username";
    private String token;
    private long expires;
    private long account_id;
    private String username;
    private boolean isExpired;

    public static AuthInfo getAuthInfo(Context ctx){
        AuthInfo info = new AuthInfo();
        Prefs prefs = new Prefs(ctx);
        info.setAccount_id(prefs.getLong(LOGIN_ACCOUNT_ID, 0));
        info.setExpires(prefs.getLong(LOGIN_EXPIRES, 0));
        info.setToken(prefs.getString(LOGIN_TOKEN, ""));
        info.setUsername(prefs.getString(LOGIN_USERNAME, ""));

        Calendar calendar = Calendar.getInstance();
        info.setExpired(calendar.getTimeInMillis() > info.getExpires());

        return info;
    }

    public static void setAuthInfo(Context ctx, String token, long expireTime, long account_id, String username){
        Prefs prefs = new Prefs(ctx);
        prefs.setString(LOGIN_TOKEN, token);
        prefs.setLong(LOGIN_EXPIRES, expireTime * 1000);
        prefs.setLong(LOGIN_ACCOUNT_ID, account_id);
        prefs.setString(LOGIN_USERNAME, username);
    }

    public void save(Context ctx){
        setAuthInfo(ctx, getToken(), getExpires(), getAccount_id(), getUsername());
    }

    public static void delete(Context ctx){
        Prefs prefs = new Prefs(ctx);
        prefs.remove(LOGIN_ACCOUNT_ID);
        prefs.remove(LOGIN_EXPIRES);
        prefs.remove(LOGIN_TOKEN);
        prefs.remove(LOGIN_USERNAME);
    }

    @Override
    public String toString() {
        return "AuthInfo{" +
                "token='" + token + '\'' +
                ", expires=" + expires +
                ", account_id=" + account_id +
                ", username='" + username + '\'' +
                ", isExpired=" + isExpired +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }
}
