package com.half.wowsca.managers;

import android.content.Context;
import android.os.AsyncTask;
import androidx.collection.LongSparseArray;
import android.widget.Toast;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.backend.GetCaptainTask;
import com.half.wowsca.backend.GetShipEncyclopediaInfo;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.enums.Server;
import com.half.wowsca.model.queries.CaptainQuery;
import com.half.wowsca.model.queries.ShipQuery;
import com.utilities.logging.Dlog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 10/12/2015.
 */
public class CompareManager {

    private static final List<Captain> captains = new ArrayList<>();

    public static void search(Context ctx) {
        if (size() > 1) {
            for (int i = 0; i < size(); i++) {
                Captain c = captains.get(i);
                CaptainQuery query = new CaptainQuery();
                query.setId(c.getId());
                query.setName(c.getName());
                query.setServer(c.getServer());
                GetCaptainTask task = new GetCaptainTask();
                task.setCtx(ctx);
                task.execute(query);
            }
        } else {
            Toast.makeText(ctx, R.string.compare_needs_two, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean captainsHaveInfo() {
        boolean allInfoThere = true;
        for (int i = 0; i < captains.size(); i++) {
            Captain c = captains.get(i);
            if (c.getDetails() == null || c.getShips() == null) {
                allInfoThere = false;
            }
        }
        return allInfoThere;
    }

    public static boolean addCaptain(Captain cap, boolean addToFirst) {
        Captain c = cap.copy();
        boolean added = false;
        if (size() < 3) {
            if (!isAlreadyThere(c.getServer(), c.getId())) {
                if(!addToFirst)
                    captains.add(c);
                else
                    captains.add(0, c);
                added = true;
            }
        }
        return added;
    }

    public static void overrideCaptain(Captain cap) {
        for (int i = 0; i < captains.size(); i++) {
            Captain c = captains.get(i);
            if (c.getId() == cap.getId() && c.getServer().ordinal() == cap.getServer().ordinal()) {
                captains.set(i, cap);
            }
        }
    }

    public static void removeCaptain(Server s, long id) {
        if (size() > 0) {
            for (int i = 0; i < captains.size(); i++) {
                Captain c = captains.get(i);
                if (c.getId() == id && c.getServer().ordinal() == s.ordinal()) {
                    captains.remove(i);
                    break;
                }
            }
        }
    }

    public static void clear() {
        captains.clear();
    }

    public static boolean isAlreadyThere(Server s, long id) {
        boolean there = false;
        if (size() > 0) {
            for (int i = 0; i < captains.size(); i++) {
                Captain c = captains.get(i);
                if (c.getId() == id && c.getServer().ordinal() == s.ordinal()) {
                    there = true;
                    break;
                }
            }
        }
        return there;
    }

    public static int size() {
        return captains.size();
    }

    public static List<Captain> getCaptains() {
        return captains;
    }

    /**
     *
     * SHIP COMPARE AREA
     *
     */

    private static List<Long> SHIPS;
    private static LongSparseArray<String> SHIP_INFORMATION;
    private static LongSparseArray<Map<String,Long>> MODULE_LIST;

    public static boolean GRABBING_INFO;

    private static List<GetShipEncyclopediaInfo> asyncTasks;

    public static void searchShips(Context ctx){
        GRABBING_INFO = true;
        asyncTasks = new ArrayList<GetShipEncyclopediaInfo>();

        for (Long i : SHIPS) {
            GetShipEncyclopediaInfo info = new GetShipEncyclopediaInfo();
            asyncTasks.add(info);
            ShipQuery query = new ShipQuery();
            query.setShipId(i);
            query.setServer(CAApp.getServerType(ctx));
            query.setLanguage(CAApp.getServerLanguage(ctx));
            query.setModules(getModuleList().get(i));
            info.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
        }
    }

    public static void searchShip(Context ctx, long shipId){
        GetShipEncyclopediaInfo info = new GetShipEncyclopediaInfo();
        ShipQuery query = new ShipQuery();
        query.setShipId(shipId);
        query.setServer(CAApp.getServerType(ctx));
        query.setLanguage(CAApp.getServerLanguage(ctx));
        query.setModules(getModuleList().get(shipId));
        info.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
    }

    public static List<Long> getSHIPS() {
        if(SHIPS == null)
            SHIPS = new ArrayList<>();
        return SHIPS;
    }

    public static LongSparseArray<String> getShipInformation() {
        if(SHIP_INFORMATION == null)
            SHIP_INFORMATION = new LongSparseArray<>();
        return SHIP_INFORMATION;
    }

    public static LongSparseArray<Map<String, Long>> getModuleList() {
        if(MODULE_LIST == null)
            MODULE_LIST = new LongSparseArray<>();
        return MODULE_LIST;
    }

    public static void addShipInfo(Long id,String shipInfo){
        if(SHIP_INFORMATION == null){
            SHIP_INFORMATION = new LongSparseArray<>();
        }
        SHIP_INFORMATION.put(id, shipInfo);
    }

    public static void addShipID(long shipID){
        if(SHIPS == null){
            SHIPS = new ArrayList<>();
            SHIP_INFORMATION = new LongSparseArray<>();
            MODULE_LIST = new LongSparseArray<>();
        }
        SHIPS.add(shipID);
    }

    public static void removeShipID(long shipID){
        SHIPS.remove(shipID);
    }

    public static void clearShips(boolean clearShips){
        if(clearShips)
            SHIPS = null;
        SHIP_INFORMATION = null;
        MODULE_LIST = null;
    }

    public static void checkForDone(){
        if(asyncTasks != null) {
            GRABBING_INFO = getShipInformation().size() != asyncTasks.size();
            Dlog.d("Checkfordone", "grabbing = " + GRABBING_INFO);
            if (GRABBING_INFO) {
                asyncTasks = null;
            }
        }
    }
}