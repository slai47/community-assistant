package com.half.wowsca.model.result;

import com.half.wowsca.model.Captain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slai4 on 9/19/2015.
 */
public class SearchResults {

    private List<Captain> captains;

    public SearchResults() {
        this.captains = new ArrayList<Captain>();
    }

    public List<Captain> getCaptains() {
        return captains;
    }

    public void setCaptains(List<Captain> captains) {
        this.captains = captains;
    }
}
