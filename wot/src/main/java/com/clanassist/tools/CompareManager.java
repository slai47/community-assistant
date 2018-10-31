package com.clanassist.tools;

import android.content.Context;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.backend.Tasks.GetAllPlayerInfo;
import com.clanassist.model.infoobj.VehicleWN8;
import com.clanassist.model.player.Player;
import com.cp.assist.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 10/12/2015.
 */
public class CompareManager {

    private static final List<Player> players = new ArrayList<Player>();

    public static void search(Context ctx){
        if(size() > 1){
            for(int i = 0; i < size(); i++) {
                Player c = players.get(i);
                String baseUrl = CAApp.getApplicationIdURLString(ctx);
                String language = ctx.getResources().getString(R.string.language);
                String server = CAApp.getBaseAddress(ctx);

                GetAllPlayerInfo info = new GetAllPlayerInfo(ctx, null, baseUrl, language, server);
                info.setGrabRecentWN8(true);
                info.execute(c.getId());
            }
        } else {
            Toast.makeText(ctx, "Comparison needs two people to compare", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean playersHaveInfo(){
        boolean allInfoThere = true;
        for(int i = 0; i < players.size(); i++){
            Player c = players.get(i);
            if(c.getOverallStats() == null || c.getPlayerVehicleInfoList() == null){
                allInfoThere = false;
            }
        }
        return allInfoThere;
    }

    public static boolean addPlayer(Player cap, boolean intoFirst){
        Player c = cap.copy();
        boolean added = false;
        if(size() < 3) {
            if (!isAlreadyThere(c.getId())) {
                if(!intoFirst)
                    players.add(c);
                else
                    players.add(0, c);
                added = true;
            }
        }
        return added;
    }

    public static void overridePlayer(Player cap){
        for(int i = 0; i < players.size(); i++){
            Player c = players.get(i);
            if(c.getId() == cap.getId()){
                players.set(i, cap);
            }
        }
    }

    public static void removePlayer(int id){
        if(size() > 0) {
            for (int i = 0; i < players.size(); i++) {
                Player c = players.get(i);
                if(c.getId() == id){
                    players.remove(i);
                    break;
                }
            }
        }
    }

    public static void clear(){
        players.clear();
    }

    public static boolean isAlreadyThere(int id){
        boolean there = false;
        if(size() > 0) {
            for (int i = 0; i < players.size(); i++) {
                Player c = players.get(i);
                if(c.getId() == id){
                    there = true;
                    break;
                }
            }
        }
        return there;
    }

    public static int size(){
        return players.size();
    }

    public static List<Player> getPlayers() {
        return players;
    }
}