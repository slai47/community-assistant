package com.clanassist.model.search.results;

import com.clanassist.model.encyclopedia.Map;
import com.clanassist.model.search.queries.EncyclopediaQuery;
import com.utilities.search.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harrison on 4/4/2015.
 */
public class EncyclopediaResult extends Result<EncyclopediaQuery> {

    private List<EncyclopediaQuery> queries;

    private List<Map> maps;

    public EncyclopediaResult() {
        queries = new ArrayList<EncyclopediaQuery>();
        this.maps = new ArrayList<Map>();
    }

    public List<Map> getMaps() {
        return maps;
    }

    public void setMaps(List<Map> maps) {
        this.maps = maps;
    }

    public List<EncyclopediaQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<EncyclopediaQuery> queries) {
        this.queries = queries;
    }
}
