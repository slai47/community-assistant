package com.clanassist.tools;

import android.content.Context;

import com.clanassist.CAApp;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.enums.StatsChoice;
import com.clanassist.model.player.Player;
import com.clanassist.model.player.PlayerVehicleInfo;
import com.clanassist.model.statistics.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Harrison on 6/2/2014.
 */
public class WN8Manager {

    /**
     * Calculates a wn8 and grabs the list of vehicles off the bat before going to the main method
     * <p/>
     * Helper method
     *
     * @param ctx
     * @param overall
     * @param vehicleList
     * @param chooseClanStats
     * @return
     */
    public static float CalculateWN8s(Context ctx, Statistics overall, List<PlayerVehicleInfo> vehicleList, StatsChoice chooseClanStats) {
        float wn8 = 0;
        wn8 = CalculateWN8s(overall, vehicleList, chooseClanStats, CAApp.getInfoManager().getWN8Data(ctx).getWN8s());
        return wn8;
    }

    /**
     * Calculates the WN8 using a statistics and vehicle list given to it.
     *
     * @param overall
     * @param vehicleList
     * @return
     */
    public static float CalculateWN8s(Statistics overall, List<PlayerVehicleInfo> vehicleList, StatsChoice choice, Map<Integer, VehicleWN8> vMap) {
        float wn8 = 0;
        float battles = 0;
        if (overall != null)
            battles = overall.getBattles();
        if (battles != 0) {
            try {
                //grab info from player overall node
                double wins = overall.getWins();
                double dmg = overall.getDamageDealt();
                double spot = overall.getSpotted();
                double frag = overall.getFrags();
                double def = overall.getDroppedCapture_points();
                //Create average numbers for player
                double winRate = wins / battles * 100;
                double avgDamage = dmg / battles;
                double avgSpot = spot / battles;
                double avgFrag = frag / battles;
                double avgDef = def / battles;
                //grab all vehicle stats from the data source and compare the stats with the vehicle info from wargaming.
                double eDmg = 0, eSpot = 0, eFrag = 0, eDef = 0, eWinrate = 0;
                for (PlayerVehicleInfo info : vehicleList) {
                    try {
                        VehicleWN8 vehicleWN8 = vMap.get(info.getTankId());
                        Statistics stats = info.getOverallStats();
                        if (choice == StatsChoice.CLAN){
                            stats = info.getClanStats();
                        }else if(choice == StatsChoice.STRONGHOLD) {
                            stats = info.getStrongholdStats();
                        }

                        float vehiclesBattles = stats.getBattles();
                        if (vehiclesBattles > 0 && vehicleWN8 != null) {
                            eDmg += vehiclesBattles * vehicleWN8.getDmg();
                            eSpot += vehiclesBattles * vehicleWN8.getSpot();
                            eFrag += vehiclesBattles * vehicleWN8.getFrag();
                            eDef += vehiclesBattles * vehicleWN8.getDef();
                            eWinrate += vehiclesBattles * vehicleWN8.getWin();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                // get the average expected performance
                double expDmg = eDmg / battles;
                double expSpot = eSpot / battles;
                double expFrag = eFrag / battles;
                double expDef = eDef / battles;
                double expWinRate = eWinrate / battles;
                // calulate r-Values used in WN8 (Step 1)
                double rDamage = avgDamage / expDmg;
                double rSpot = avgSpot / expSpot;
                double rFrag = avgFrag / expFrag;
                double rDef = avgDef / expDef;
                double rWin = winRate / expWinRate;
                // step 2
                double rWINc = Math.max(0, (rWin - 0.71) / (1 - 0.71));
                double rDAMAGEc = Math.max(0, (rDamage - 0.22) / (1 - 0.22));
                double rFRAGc = Math.max(0, Math.min((rDAMAGEc + 0.2), (rFrag - 0.12) / (1 - 0.12)));
                double rSPOTc = Math.max(0, Math.min((rDAMAGEc + 0.1), (rSpot - 0.38) / (1 - 0.38)));
                double rDEFc = Math.max(0, Math.min((rDAMAGEc + 0.1), (rDef - 0.10) / (1 - 0.10)));

                double wn8c = 980 * rDAMAGEc + 210 * rDAMAGEc * rFRAGc + 155 * rFRAGc * rSPOTc + 75 * rDEFc * rFRAGc + 145 * Math.min(1.8, rWINc);
                wn8 = (float) wn8c;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return wn8;
    }

    /**
     * Calculates a wn8 for a tank
     *
     * @param vehicle
     * @param vWN8
     * @return
     */
    public static float CalculateWN8(PlayerVehicleInfo vehicle, VehicleWN8 vWN8, StatsChoice type) {
        Statistics overall = vehicle.getOverallStats();
        if (type == StatsChoice.CLAN) {
            overall = vehicle.getClanStats();
        } else if(type == StatsChoice.STRONGHOLD){
            overall = vehicle.getStrongholdStats();
        }
        float wn8 = 0;
        float battles = 0;
        if (overall != null)
            battles = overall.getBattles();
        if (battles != 0) {
            try {
                //grab info from player overall node
                double wins = overall.getWins();
                double dmg = overall.getDamageDealt();
                double spot = overall.getSpotted();
                double frag = overall.getFrags();
                double def = overall.getDroppedCapture_points();

                //Create average numbers for player
                double winRate = wins / battles * 100;
                double avgDamage = dmg / battles;
                double avgSpot = spot / battles;
                double avgFrag = frag / battles;
                double avgDef = def / battles;

                //grab all vehicle stats from the data source and compare the stats with the vehicle info from wargaming.
                double eDmg = 0, eSpot = 0, eFrag = 0, eDef = 0, eWinrate = 0;

                float vehiclesBattles = vehicle.getOverallStats().getBattles();
                eDmg += vehiclesBattles * vWN8.getDmg();
                eSpot += vehiclesBattles * vWN8.getSpot();
                eFrag += vehiclesBattles * vWN8.getFrag();
                eDef += vehiclesBattles * vWN8.getDef();
                eWinrate += vehiclesBattles * vWN8.getWin();

                // get the average expected performance
                double expDmg = eDmg / battles;
                double expSpot = eSpot / battles;
                double expFrag = eFrag / battles;
                double expDef = eDef / battles;
                double expWinRate = eWinrate / battles;

                // calulate r-Values used in WN8 (Step 1)
                double rDamage = avgDamage / expDmg;
                double rSpot = avgSpot / expSpot;
                double rFrag = avgFrag / expFrag;
                double rDef = avgDef / expDef;
                double rWin = winRate / expWinRate;

                // step 2
                double rWINc = Math.max(0, (rWin - 0.71) / (1 - 0.71));
                double rDAMAGEc = Math.max(0, (rDamage - 0.22) / (1 - 0.22));
                double rFRAGc = Math.max(0, Math.min((rDAMAGEc + 0.2), (rFrag - 0.12) / (1 - 0.12)));
                double rSPOTc = Math.max(0, Math.min((rDAMAGEc + 0.1), (rSpot - 0.38) / (1 - 0.38)));
                double rDEFc = Math.max(0, Math.min((rDAMAGEc + 0.1), (rDef - 0.10) / (1 - 0.10)));

                double wn8c = (980 * rDAMAGEc) + (210 * rDAMAGEc * rFRAGc) + (155 * rFRAGc * rSPOTc) + (75 * rDEFc * rFRAGc) + (145 * Math.min(1.8, rWINc));
                wn8 = (float) wn8c;
            } catch (Exception e) {
            }
        }
        return wn8;
    }

    /**
     * Calculates a given wn8 of a player
     *
     * @param ctx
     * @param p
     * @return
     */
    public static float CalculateWN8s(Context ctx, Player p, StatsChoice chooseClanStats) {
        Statistics stats = p.getOverallStats();
        if(chooseClanStats == StatsChoice.CLAN){
            stats = p.getClanStats();
        } else if(chooseClanStats == StatsChoice.STRONGHOLD){
            stats = p.getStrongholdStats();
        }

        return CalculateWN8s(ctx, stats, p.getPlayerVehicleInfoList(), chooseClanStats);
    }

    /**
     * Calculates a single vehicles wn8 with given information
     *
     * @param ctx
     * @param info - must have battles, win rate, damage and such
     * @return
     */
    public static float CalculateWN8s(Context ctx, PlayerVehicleInfo info) {
        List<PlayerVehicleInfo> list = new ArrayList<PlayerVehicleInfo>();
        list.add(info);
        return CalculateWN8s(ctx, info.getOverallStats(), list, StatsChoice.OVERALL);
    }


}
