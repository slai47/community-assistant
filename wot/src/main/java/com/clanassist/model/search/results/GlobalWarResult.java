package com.clanassist.model.search.results;

import com.clanassist.model.clan.Battle;
import com.clanassist.model.clan.globalwar.Campaign;
import com.clanassist.model.clan.globalwar.Province;
import com.clanassist.model.search.queries.GlobalWarQuery;
import com.utilities.search.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Harrison on 5/13/14.
 */
public class GlobalWarResult extends Result<GlobalWarQuery> {

    private List<GlobalWarQuery> queries;

    //for provinces
    private Map<String, Province> provinces;

    //for battles
    private List<Integer> clanIds;
    private Map<Integer, List<Battle>> battles;

    private List<Campaign> campaigns;

    public GlobalWarResult() {
        queries = new ArrayList<GlobalWarQuery>();
        provinces = new HashMap<String, Province>();
        battles = new HashMap<Integer, List<Battle>>();
        clanIds = new ArrayList<Integer>();
        campaigns = new ArrayList<Campaign>();
    }

    public List<GlobalWarQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<GlobalWarQuery> queries) {
        this.queries = queries;
    }

    public Map<String, Province> getProvinces() {
        return provinces;
    }

    public void setProvinces(Map<String, Province> provinces) {
        this.provinces = provinces;
    }

    public List<Integer> getClanIds() {
        return clanIds;
    }

    public void setClanIds(List<Integer> clanIds) {
        this.clanIds = clanIds;
    }

    public Map<Integer, List<Battle>> getBattles() {
        return battles;
    }

    public void setBattles(Map<Integer, List<Battle>> battles) {
        this.battles = battles;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<Campaign> campaigns) {
        this.campaigns = campaigns;
    }
}
