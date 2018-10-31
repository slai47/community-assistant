package com.half.wowsca.model;

import org.json.JSONObject;

/**
 * Created by slai4 on 5/1/2016.
 */
public class CaptainPrivateInformation {

    private int gold;
    private int freeExp;
    private double credits;
    private long premiumExpiresAt;
    private int emptySlots;
    private int slots;
    private long battleTime;

    public void parse(JSONObject obj){
        gold = obj.optInt("gold");
        freeExp = obj.optInt("free_exp");
        credits = obj.optDouble("credits");
        premiumExpiresAt = obj.optLong("premium_expires_at") * 1000;
        emptySlots = obj.optInt("empty_slots");
        slots = obj.optInt("slots");
        battleTime = obj.optInt("battle_life_time");
    }


    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getFreeExp() {
        return freeExp;
    }

    public void setFreeExp(int freeExp) {
        this.freeExp = freeExp;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public long getPremiumExpiresAt() {
        return premiumExpiresAt;
    }

    public void setPremiumExpiresAt(long premiumExpiresAt) {
        this.premiumExpiresAt = premiumExpiresAt;
    }

    public int getEmptySlots() {
        return emptySlots;
    }

    public void setEmptySlots(int emptySlots) {
        this.emptySlots = emptySlots;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public long getBattleTime() {
        return battleTime;
    }

    public void setBattleTime(long battleTime) {
        this.battleTime = battleTime;
    }
}
