package com.clanassist.model.listmodel;

/**
 * Created by Harrison on 6/20/2015.
 */
public class CPStatModel {

    private String text;
    private String subText;
    private String midText;
    private int backgroundColor;
    private int accountId;
    private String accountName;
    private float difference;

    public CPStatModel(String text, String subText, String middleText, int backgroundColor) {
        this.text = text;
        this.subText = subText;
        this.backgroundColor = backgroundColor;
        this.midText = middleText;
        difference = 0f;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getMidText() {
        return midText;
    }

    public void setMidText(String midText) {
        this.midText = midText;
    }

    public float getDifference() {
        return difference;
    }

    public void setDifference(float difference) {
        this.difference = difference;
    }
}
