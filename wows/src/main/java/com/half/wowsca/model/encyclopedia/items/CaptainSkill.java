package com.half.wowsca.model.encyclopedia.items;

import com.half.wowsca.model.encyclopedia.CAItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slai4 on 4/25/2016.
 */
public class CaptainSkill extends CAItem {

    private int tier;
    private List<String> abilities;

    @Override
    public void parse(JSONObject jsonObject) {
        if(jsonObject != null){
            setTier(jsonObject.optInt("tier"));
            setName(jsonObject.optString("name"));
            setImage(jsonObject.optString("icon"));
            JSONArray perks = jsonObject.optJSONArray("perks");
            if(perks != null) {
                setAbilities(new ArrayList<String>());
                for (int i = 0; i < perks.length(); i++) {
                    JSONObject item = perks.optJSONObject(i);
                    abilities.add(item.optString("description"));
                }
            }
        }
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }
}
