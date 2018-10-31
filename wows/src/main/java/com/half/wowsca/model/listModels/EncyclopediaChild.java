package com.half.wowsca.model.listModels;

import com.half.wowsca.model.enums.EncyclopediaType;

import java.util.List;

/**
 * Created by slai4 on 12/1/2015.
 */
public class EncyclopediaChild {

    private EncyclopediaType type;
    private List<Float> values;
    private List<String> titles;
    private List<String> types;
    private String title;

    public static EncyclopediaChild create(EncyclopediaType type, List<Float> val, List<String> titles, List<String> types, String title){
        EncyclopediaChild child = new EncyclopediaChild();
        child.setType(type);
        child.setValues(val);
        child.setTitle(title);
        child.setTitles(titles);
        child.setTypes(types);
        return child;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public EncyclopediaType getType() {
        return type;
    }

    public void setType(EncyclopediaType type) {
        this.type = type;
    }

    public List<Float> getValues() {
        return values;
    }

    public void setValues(List<Float> values) {
        this.values = values;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }
}
