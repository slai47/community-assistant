package com.half.wowsca.model.encyclopedia.items;

import com.half.wowsca.model.encyclopedia.CAItem;

import org.json.JSONObject;

/**
 * Created by slai4 on 10/31/2015.
 */
public class EquipmentInfo extends CAItem {

    private String tag;
    private int price;
    private String description;
    private String type;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void parse(JSONObject jsonObject) {
        if(jsonObject != null) {
            setName(jsonObject.optString("name"));
            setPrice(jsonObject.optInt("price_credit"));
            setType(jsonObject.optString("type"));
            setImage(jsonObject.optString("image"));
            setTag(jsonObject.optString("tag"));
            setType(jsonObject.optString("type"));
            setDescription(jsonObject.optString("description"));
        }
    }
}
