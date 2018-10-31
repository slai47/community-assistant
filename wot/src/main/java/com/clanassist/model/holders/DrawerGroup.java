package com.clanassist.model.holders;

import com.clanassist.model.enums.DrawerType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harrison on 8/4/2014.
 */
public class DrawerGroup {

    private List<DrawerChild> children;
    private DrawerType type;
    private String title;

    public DrawerGroup() {
        children = new ArrayList<DrawerChild>();
    }

    public List<DrawerChild> getChildren() {
        return children;
    }

    public void setChildren(List<DrawerChild> children) {
        this.children = children;
    }

    public DrawerType getType() {
        return type;
    }

    public void setType(DrawerType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
