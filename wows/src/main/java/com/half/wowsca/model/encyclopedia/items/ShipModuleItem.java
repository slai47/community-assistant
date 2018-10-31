package com.half.wowsca.model.encyclopedia.items;

import org.json.JSONObject;

/**
 * Created by slai4 on 5/12/2016.
 */
public class ShipModuleItem {

    private long id;
    private String name;
    private String type;
    private long price_xp;
    private long price_credits;
    private boolean isDefault;

    public static ShipModuleItem parse(JSONObject json){
        if(json != null) {
            ShipModuleItem item = new ShipModuleItem();
            item.setId(json.optLong("module_id"));
            item.setName(json.optString("name"));
            item.setType(json.optString("type"));
            item.setPrice_credits(json.optLong("price_credit"));
            item.setPrice_xp(json.optLong("price_xp"));
            item.setDefault(json.optBoolean("is_default"));
            return item;
        }
        return null;
    }

    @Override
    public String toString() {
        return "ShipModuleItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", price_xp=" + price_xp +
                ", price_credits=" + price_credits +
                ", isDefault=" + isDefault +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getPrice_xp() {
        return price_xp;
    }

    public void setPrice_xp(long price_xp) {
        this.price_xp = price_xp;
    }

    public long getPrice_credits() {
        return price_credits;
    }

    public void setPrice_credits(long price_credits) {
        this.price_credits = price_credits;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
