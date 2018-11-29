package com.half.wowsca.model.encyclopedia.items;

import androidx.core.util.Pair;

import com.half.wowsca.model.encyclopedia.CAItem;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by slai4 on 4/25/2016.
 */
public class ExteriorItem extends CAItem {

    private String description;
    private String type;
    private Map<String, Pair<String, Float>> coef;


    @Override
    public void parse(JSONObject object) {
        if(object != null){
            setId(object.optLong("consumable_id"));
            setName(object.optString("name"));
            setDescription(object.optString("description"));
            setType(object.optString("type"));
            setImage(object.optString("image"));

            JSONObject coef = object.optJSONObject("profile");
            if(coef != null){
                setCoef(new HashMap<String, Pair<String, Float>>());
                Iterator<String> iter = coef.keys();
                while (iter.hasNext()){
                    String key = iter.next();
                    JSONObject factor = coef.optJSONObject(key);
                    double amount = factor.optDouble("value");
                    String description = factor.optString("description");
                    getCoef().put(key, new Pair<>(description, (float) amount));
                }
            }
        }
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

    public Map<String, Pair<String, Float>> getCoef() {
        return coef;
    }

    public void setCoef(Map<String, Pair<String, Float>> coef) {
        this.coef = coef;
    }
}
