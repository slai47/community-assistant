package com.clanassist.model.clan;

import com.clanassist.backend.ParserHelper;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.infoobj.Tanks;
import com.clanassist.model.player.minimized.MinimizedPlayer;
import com.clanassist.model.player.minimized.MinimizedVehicleInfo;
import com.utilities.logging.Dlog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Harrison on 4/19/2015.
 */
public class ClanGraphs {

    private Map<Integer, Double> gamesPerTier;
    private Map<Integer, Double> wn8PerTier;

    private Map<String, Double> wn8PerClassType;
    private Map<String, Double> gamesPerClassType;

    private Map<Integer, Double> wn8PerTier10;
    private Map<Integer, Double> gamesPerTier10;
    private Map<Integer, Double> winRatePerTier10;
    private Map<Integer, Double> avgDamagePerTier10;
    private Map<Integer, Double> totalTankersPerTier10;

    private Map<Integer, Double> wn8PerTier8;
    private Map<Integer, Double> gamesPerTier8;
    private Map<Integer, Double> winRatePerTier8;
    private Map<Integer, Double> avgDamagePerTier8;
    private Map<Integer, Double> totalTankersPerTier8;

    private Map<Integer, Double> wn8PerTier6;
    private Map<Integer, Double> gamesPerTier6;
    private Map<Integer, Double> winRatePerTier6;
    private Map<Integer, Double> avgDamagePerTier6;
    private Map<Integer, Double> totalTankersPerTier6;

    public static ClanGraphs create(Set<Integer> tier10, Set<Integer> tier8, Set<Integer> tier6) {
        ClanGraphs graph = new ClanGraphs();
        graph.setAvgDamagePerTier10(new HashMap<Integer, Double>());
        graph.setGamesPerTier10(new HashMap<Integer, Double>());
        graph.setWinRatePerTier10(new HashMap<Integer, Double>());
        graph.setWn8PerTier10(new HashMap<Integer, Double>());
        graph.setTotalTankersPerTier10(new HashMap<Integer, Double>());


        graph.setAvgDamagePerTier8(new HashMap<Integer, Double>());
        graph.setGamesPerTier8(new HashMap<Integer, Double>());
        graph.setWinRatePerTier8(new HashMap<Integer, Double>());
        graph.setWn8PerTier8(new HashMap<Integer, Double>());
        graph.setTotalTankersPerTier8(new HashMap<Integer, Double>());


        graph.setAvgDamagePerTier6(new HashMap<Integer, Double>());
        graph.setGamesPerTier6(new HashMap<Integer, Double>());
        graph.setWinRatePerTier6(new HashMap<Integer, Double>());
        graph.setWn8PerTier6(new HashMap<Integer, Double>());
        graph.setTotalTankersPerTier6(new HashMap<Integer, Double>());

        setUpTierMaps(graph.getAvgDamagePerTier10(), tier10);
        setUpTierMaps(graph.getGamesPerTier10(), tier10);
        setUpTierMaps(graph.getWinRatePerTier10(), tier10);
        setUpTierMaps(graph.getWn8PerTier10(), tier10);
        setUpTierMaps(graph.getTotalTankersPerTier10(), tier10);

        setUpTierMaps(graph.getAvgDamagePerTier8(), tier8);
        setUpTierMaps(graph.getGamesPerTier8(), tier8);
        setUpTierMaps(graph.getWinRatePerTier8(), tier8);
        setUpTierMaps(graph.getWn8PerTier8(), tier8);
        setUpTierMaps(graph.getTotalTankersPerTier8(), tier8);

        setUpTierMaps(graph.getAvgDamagePerTier6(), tier6);
        setUpTierMaps(graph.getGamesPerTier6(), tier6);
        setUpTierMaps(graph.getWinRatePerTier6(), tier6);
        setUpTierMaps(graph.getWn8PerTier6(), tier6);
        setUpTierMaps(graph.getTotalTankersPerTier6(), tier6);

        graph.setGamesPerClassType(ParserHelper.createClassTypeMap());
        graph.setGamesPerTier(ParserHelper.createTierMap());

        graph.setWn8PerClassType(ParserHelper.createClassTypeMap());
        graph.setWn8PerTier(ParserHelper.createTierMap());
        return graph;
    }

    //Add all the values to the maps.
    public int calculate(List<MinimizedPlayer> players, Map<Integer, VehicleWN8> vehicles, Tanks tanks) {
        int averageTier = 0;
        int total = 0;
        long start = Calendar.getInstance().getTimeInMillis();
        Map<String, Double> totalTanksPerClass = ParserHelper.createClassTypeMap(); //need this stuff for wn8 averages
        Map<Integer, Double> totalTanksPerTier = ParserHelper.createTierMap();
        for (MinimizedPlayer player : players) {
            for (MinimizedVehicleInfo info : player.getInfos()) {
                total++;
                int id = info.getId();
                VehicleWN8 v8 = vehicles.get(id);
                Tank tankInfo = tanks.getTank(id);
                if (v8 != null && tankInfo != null) {
                    int tier = tankInfo.getTier();
                    int games = info.getOverall().getGames();
                    int wn8 = (int) info.getWn8();
                    averageTier += tier;
                    if (tier == 10) {
                        getAvgDamagePerTier10().put(id, getAvgDamagePerTier10().get(id) + info.getOverall().getDamage());
                        getGamesPerTier10().put(id, getGamesPerTier10().get(id) + info.getOverall().getGames());
                        getWinRatePerTier10().put(id, getWinRatePerTier10().get(id) + info.getOverall().getWins());
                        getWn8PerTier10().put(id, getWn8PerTier10().get(id) + info.getWn8());
                        getTotalTankersPerTier10().put(id, getTotalTankersPerTier10().get(id) + 1);
                    } else if(tier == 8){
                        getAvgDamagePerTier8().put(id, getAvgDamagePerTier8().get(id) + info.getOverall().getDamage());
                        getGamesPerTier8().put(id, getGamesPerTier8().get(id) + info.getOverall().getGames());
                        getWinRatePerTier8().put(id, getWinRatePerTier8().get(id) + info.getOverall().getWins());
                        getWn8PerTier8().put(id, getWn8PerTier8().get(id) + info.getWn8());
                        getTotalTankersPerTier8().put(id, getTotalTankersPerTier8().get(id) + 1);
                    } else if(tier == 6){
                        getAvgDamagePerTier6().put(id, getAvgDamagePerTier6().get(id) + info.getOverall().getDamage());
                        getGamesPerTier6().put(id, getGamesPerTier6().get(id) + info.getOverall().getGames());
                        getWinRatePerTier6().put(id, getWinRatePerTier6().get(id) + info.getOverall().getWins());
                        getWn8PerTier6().put(id, getWn8PerTier6().get(id) + info.getWn8());
                        getTotalTankersPerTier6().put(id, getTotalTankersPerTier6().get(id) + 1);
                    }
                    totalTanksPerClass.put(tankInfo.getClassType(), totalTanksPerClass.get(tankInfo.getClassType()) + 1);
                    totalTanksPerTier.put(tier, totalTanksPerTier.get(tier) + 1);

                    getGamesPerClassType().put(tankInfo.getClassType(), getGamesPerClassType().get(tankInfo.getClassType()) + games);
                    getGamesPerTier().put(tier, getGamesPerTier().get(tier) + games);
                    getWn8PerClassType().put(tankInfo.getClassType(), getWn8PerClassType().get(tankInfo.getClassType()) + wn8);
                    getWn8PerTier().put(tier, getWn8PerTier().get(tier) + wn8);
                }
            }
        }

        ParserHelper.averageDoubleMap(getWn8PerTier(), totalTanksPerTier);
        ParserHelper.averageStrDoubleMap(getWn8PerClassType(), totalTanksPerClass);

        ParserHelper.averageDoubleMap(getAvgDamagePerTier10(), getTotalTankersPerTier10());
        ParserHelper.averageDoubleMap(getWinRatePerTier10(), getTotalTankersPerTier10());
        ParserHelper.averageDoubleMap(getWn8PerTier10(), getTotalTankersPerTier10());

        ParserHelper.averageDoubleMap(getAvgDamagePerTier8(), getTotalTankersPerTier8());
        ParserHelper.averageDoubleMap(getWinRatePerTier8(), getTotalTankersPerTier8());
        ParserHelper.averageDoubleMap(getWn8PerTier8(), getTotalTankersPerTier8());

        ParserHelper.averageDoubleMap(getAvgDamagePerTier6(), getTotalTankersPerTier6());
        ParserHelper.averageDoubleMap(getWinRatePerTier6(), getTotalTankersPerTier6());
        ParserHelper.averageDoubleMap(getWn8PerTier6(), getTotalTankersPerTier6());

        long end = Calendar.getInstance().getTimeInMillis();
        Dlog.d("ClanGraph", "dif = " + (end - start));

        if (total > 0) {
            averageTier = averageTier / total;
        }
        return averageTier;
    }

    private static void setUpTierMaps(Map<Integer, Double> map, Set<Integer> tiers) {
        for (Integer i : tiers) {
            map.put(i, 0.0);
        }
    }

    @Override
    public String toString() {
        return "ClanGraphs{" +
                "gamesPerTier=" + gamesPerTier +
                ", wn8PerTier=" + wn8PerTier +
                ", wn8PerClassType=" + wn8PerClassType +
                ", gamesPerClassType=" + gamesPerClassType +
                ", wn8PerTier10=" + wn8PerTier10 +
                ", gamesPerTier10=" + gamesPerTier10 +
                ", winRatePerTier10=" + winRatePerTier10 +
                ", avgDamagePerTier10=" + avgDamagePerTier10 +
                ", totalTankersPerTier10=" + totalTankersPerTier10 +
                ", wn8PerTier8=" + wn8PerTier8 +
                ", gamesPerTier8=" + gamesPerTier8 +
                ", winRatePerTier8=" + winRatePerTier8 +
                ", avgDamagePerTier8=" + avgDamagePerTier8 +
                ", totalTankersPerTier8=" + totalTankersPerTier8 +
                ", wn8PerTier6=" + wn8PerTier6 +
                ", gamesPerTier6=" + gamesPerTier6 +
                ", winRatePerTier6=" + winRatePerTier6 +
                ", avgDamagePerTier6=" + avgDamagePerTier6 +
                ", totalTankersPerTier6=" + totalTankersPerTier6 +
                '}';
    }

    public Map<Integer, Double> getGamesPerTier() {
        return gamesPerTier;
    }

    public void setGamesPerTier(Map<Integer, Double> gamesPerTier) {
        this.gamesPerTier = gamesPerTier;
    }

    public Map<Integer, Double> getWn8PerTier() {
        return wn8PerTier;
    }

    public void setWn8PerTier(Map<Integer, Double> wn8PerTier) {
        this.wn8PerTier = wn8PerTier;
    }

    public Map<String, Double> getWn8PerClassType() {
        return wn8PerClassType;
    }

    public void setWn8PerClassType(Map<String, Double> wn8PerClassType) {
        this.wn8PerClassType = wn8PerClassType;
    }

    public Map<String, Double> getGamesPerClassType() {
        return gamesPerClassType;
    }

    public void setGamesPerClassType(Map<String, Double> gamesPerClassType) {
        this.gamesPerClassType = gamesPerClassType;
    }

    public Map<Integer, Double> getWn8PerTier10() {
        return wn8PerTier10;
    }

    public void setWn8PerTier10(Map<Integer, Double> wn8PerTier10) {
        this.wn8PerTier10 = wn8PerTier10;
    }

    public Map<Integer, Double> getGamesPerTier10() {
        return gamesPerTier10;
    }

    public void setGamesPerTier10(Map<Integer, Double> gamesPerTier10) {
        this.gamesPerTier10 = gamesPerTier10;
    }

    public Map<Integer, Double> getWinRatePerTier10() {
        return winRatePerTier10;
    }

    public void setWinRatePerTier10(Map<Integer, Double> winRatePerTier10) {
        this.winRatePerTier10 = winRatePerTier10;
    }

    public Map<Integer, Double> getAvgDamagePerTier10() {
        return avgDamagePerTier10;
    }

    public void setAvgDamagePerTier10(Map<Integer, Double> avgDamagePerTier10) {
        this.avgDamagePerTier10 = avgDamagePerTier10;
    }

    public Map<Integer, Double> getTotalTankersPerTier10() {
        return totalTankersPerTier10;
    }

    public void setTotalTankersPerTier10(Map<Integer, Double> totalTankersPerTier10) {
        this.totalTankersPerTier10 = totalTankersPerTier10;
    }

    public Map<Integer, Double> getWn8PerTier8() {
        return wn8PerTier8;
    }

    public void setWn8PerTier8(Map<Integer, Double> wn8PerTier8) {
        this.wn8PerTier8 = wn8PerTier8;
    }

    public Map<Integer, Double> getGamesPerTier8() {
        return gamesPerTier8;
    }

    public void setGamesPerTier8(Map<Integer, Double> gamesPerTier8) {
        this.gamesPerTier8 = gamesPerTier8;
    }

    public Map<Integer, Double> getWinRatePerTier8() {
        return winRatePerTier8;
    }

    public void setWinRatePerTier8(Map<Integer, Double> winRatePerTier8) {
        this.winRatePerTier8 = winRatePerTier8;
    }

    public Map<Integer, Double> getAvgDamagePerTier8() {
        return avgDamagePerTier8;
    }

    public void setAvgDamagePerTier8(Map<Integer, Double> avgDamagePerTier8) {
        this.avgDamagePerTier8 = avgDamagePerTier8;
    }

    public Map<Integer, Double> getTotalTankersPerTier8() {
        return totalTankersPerTier8;
    }

    public void setTotalTankersPerTier8(Map<Integer, Double> totalTankersPerTier8) {
        this.totalTankersPerTier8 = totalTankersPerTier8;
    }

    public Map<Integer, Double> getWn8PerTier6() {
        return wn8PerTier6;
    }

    public void setWn8PerTier6(Map<Integer, Double> wn8PerTier6) {
        this.wn8PerTier6 = wn8PerTier6;
    }

    public Map<Integer, Double> getGamesPerTier6() {
        return gamesPerTier6;
    }

    public void setGamesPerTier6(Map<Integer, Double> gamesPerTier6) {
        this.gamesPerTier6 = gamesPerTier6;
    }

    public Map<Integer, Double> getWinRatePerTier6() {
        return winRatePerTier6;
    }

    public void setWinRatePerTier6(Map<Integer, Double> winRatePerTier6) {
        this.winRatePerTier6 = winRatePerTier6;
    }

    public Map<Integer, Double> getAvgDamagePerTier6() {
        return avgDamagePerTier6;
    }

    public void setAvgDamagePerTier6(Map<Integer, Double> avgDamagePerTier6) {
        this.avgDamagePerTier6 = avgDamagePerTier6;
    }

    public Map<Integer, Double> getTotalTankersPerTier6() {
        return totalTankersPerTier6;
    }

    public void setTotalTankersPerTier6(Map<Integer, Double> totalTankersPerTier6) {
        this.totalTankersPerTier6 = totalTankersPerTier6;
    }
}
