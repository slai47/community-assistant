package com.half.wowsca.managers;

import com.half.wowsca.model.Captain;
import com.half.wowsca.model.Ship;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.encyclopedia.items.ShipStat;
import com.utilities.logging.Dlog;

import java.util.Map;

/**
 * Created by slai4 on 1/17/2016.
 */
public class CARatingManager {

    public static final float DAMAGE_COEF = 0.50f;
    public static final float KILLS_COEF = 0.30f;
    public static final float WR_COEF = 0.20f;
    public static final float ONE = 1f;
    private static int NORMALIZE_VALUE = 1000;

    /**
     * Creates a rating based off of performance on a ship
     *
     * You must check if there are no battles before calling this method.
     *
     * @param ship
     * @param info
     * @return
     */
    public static float CalculateCAShipRating(Ship ship, ShipStat info){
        float rating = 0;
        float battles = ship.getBattles();

        //Calculate c's by total / battles
        float cDmg = (float) (ship.getTotalDamage() / battles);
        float cWin = ship.getWins() / battles;
        float cKills = ship.getFrags() / battles;

        float xDmg = ONE;
        if(info.getDmg_dlt() > 0)
            xDmg = (cDmg / info.getDmg_dlt()); // c / e damage
        float xWR = ONE;
        if(info.getWins() > 0)
            xWR = (cWin / info.getWins()); // c / e wins
        float xKills = ONE;
        if(info.getFrags() > 0)
            xKills = (cKills / info.getFrags());  // c / e kills

//        Dlog.d("CalculateShipRating", "dmg = " + xDmg + " wr = " + xWR + " kills = " + xKills);

        //Add up portions of the total
        float totalPortions = (xDmg * DAMAGE_COEF) + (xKills * KILLS_COEF) + (xWR * WR_COEF); // (c/e) * portion

        rating = totalPortions * NORMALIZE_VALUE; // normalize 1000

//        Dlog.d("CalculateShipRating", "id = " + ship.getShipId() + " rating = " + rating);
//        Dlog.d("CalculateShipRating", "tRating = " + rating);

        return rating;
    }
}
