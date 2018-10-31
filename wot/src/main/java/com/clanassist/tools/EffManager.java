package com.clanassist.tools;

import com.clanassist.model.player.Player;

/**
 * Created by slai4 on 10/24/2015.
 */
public class EffManager {

    public static float getEff(Player p){
//        DAMAGE * (10 / (TIER + 2)) * (0.21 + 3*TIER / 100) + FRAGS * 250 + SPOT * 150 + log(CAP + 1) / log(1.732) * 150 + DEF * 150
//        DAMAGE - average damage FRAGS - average kills SPOT - average spots CAP - average capture points per game DEF - average defense points per game
        float eff = 0;
        float battles = p.getOverallStats().getBattles();
        if(battles > 0){
            float damage = (float) (p.getOverallStats().getDamageDealt() / battles);
            float frags = p.getOverallStats().getFrags() / battles;
            float spotting = p.getOverallStats().getSpotted() / battles;
            float cap = p.getOverallStats().getCapturePoints() / battles;
            float def = p.getOverallStats().getDroppedCapture_points() / battles;



        }


        return eff;
    }

}
