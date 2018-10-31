package com.half.wowsca.model.encyclopedia;

import org.json.JSONObject;

/**
 * Created by slai4 on 4/25/2016.
 */
public abstract class CAItem {

    private long id;
    private String name;
    private String image;

    public abstract void parse(JSONObject jsonObject);

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
