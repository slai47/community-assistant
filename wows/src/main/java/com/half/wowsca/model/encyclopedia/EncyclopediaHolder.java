package com.half.wowsca.model.encyclopedia;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by slai4 on 4/25/2016.
 */
public class EncyclopediaHolder<X, Y> {

    private Map<X,Y> items;

    public EncyclopediaHolder() {
        items = new HashMap<>();
    }

    public Y get(X x){
        if(items != null)
            return items.get(x);
        else
            return null;
    }

    public void put(X x, Y y){
        if(items != null)
            items.put(x, y);
    }

    public Map<X, Y> getItems() {
        return items;
    }

    public void setItems(Map<X, Y> items) {
        this.items = items;
    }
}
