package com.half.wowsca.model.encyclopedia.items;

import android.text.TextUtils;

import com.utilities.logging.Dlog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 9/23/2015.
 */
public class ShipInfo {

    private long shipId;
    private String name;
    private String image;
    private String mediumImage;
    private String largeImage;
    private boolean isPremium;
    private int price;
    private int goldPrice;
    private int tier;
    private String description;
    private String type;
    private String nation;
    private List<Long> nextShipIds;
    private List<Long> equipments;

    private List<Long> engine;
    private List<Long> torpBomb;
    private List<Long> fighter;
    private List<Long> hull;
    private List<Long> artillery;
    private List<Long> torps;
    private List<Long> fireControl;
    private List<Long> flightControl;
    private List<Long> DiveBomber;

    private Map<Long, ShipModuleItem> items;

    public void parse(JSONObject ship){
        setName(ship.optString("name"));
        if(!TextUtils.isEmpty(name)){
            name = name.replaceAll("\\[","").replaceAll("]","");
        }
        setGoldPrice(ship.optInt("price_gold"));
        setIsPremium(ship.optBoolean("is_premium"));
        setPrice(ship.optInt("price_credit"));
        setTier(ship.optInt("tier"));
        setType(ship.optString("type"));
        setNation(ship.optString("nation"));
        setDescription(ship.optString("description"));

        JSONObject modules = ship.optJSONObject("modules");
        if (modules != null) {
            engine = new ArrayList<>();
            torpBomb = new ArrayList<>();
            fighter = new ArrayList<>();
            hull = new ArrayList<>();
            torps = new ArrayList<>();
            artillery = new ArrayList<>();
            fireControl = new ArrayList<>();
            flightControl = new ArrayList<>();
            DiveBomber = new ArrayList<>();
            getModuleList(modules, "engine", engine);
            getModuleList(modules, "torpedo_bomber", torpBomb);
            getModuleList(modules, "fighter", fighter);
            getModuleList(modules, "hull", hull);
            getModuleList(modules, "artillery", artillery);
            getModuleList(modules, "torpedoes", torps);
            getModuleList(modules, "fire_control", fireControl);
            getModuleList(modules, "flight_control", flightControl);
            getModuleList(modules, "dive_bomber", DiveBomber);
        }

        JSONObject modulesTree = ship.optJSONObject("modules_tree");
        if (modulesTree != null) {
            items = new HashMap<>();
            Iterator<String> modulesKeys = modulesTree.keys();
            while (modulesKeys.hasNext()) {
                String mKey = modulesKeys.next();
                JSONObject module = modulesTree.optJSONObject(mKey);
                if (module != null) {
                    ShipModuleItem item = ShipModuleItem.parse(module);
                    items.put(Long.parseLong(mKey), item);
                }
            }
            setItems(items);
        }

        setEquipments(new ArrayList<Long>());
        JSONArray array = ship.optJSONArray("upgrades");
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                getEquipments().add(array.optLong(i));
            }
        }

        setNextShipIds(new ArrayList<Long>());
        JSONObject nextShips = ship.optJSONObject("next_ships");
        if (nextShips != null) {
            Iterator<String> modulesKeys = nextShips.keys();
            while (modulesKeys.hasNext()) {
                String nextShipId = modulesKeys.next();
                Long nextShip = Long.parseLong(nextShipId);
                getNextShipIds().add(nextShip);
            }
        }

        JSONObject images = ship.optJSONObject("images");
        if (images != null) {
            setImage(images.optString("small"));
            setMediumImage(images.optString("medium"));
            setLargeImage(images.optString("large"));
        }
    }

    private void getModuleList(JSONObject obj, String key, List<Long> array){
        JSONArray object = obj.optJSONArray(key);
        if(object != null && object.length() > 0){
            addModuleToList(array, object);
        }
    }

    private void addModuleToList(List<Long> list, JSONArray array){
        for (int i = 0; i < array.length(); i++) {
            list.add(array.optLong(i));
        }
    }


    @Override
    public String toString() {
        return "{" +
                "shipId=" + shipId +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", isPremium=" + isPremium +
                ", price=" + price +
                ", goldPrice=" + goldPrice +
                ", tier=" + tier +
                ", type='" + type + '\'' +
                '}';
    }

    public long getShipId() {
        return shipId;
    }

    public void setShipId(long shipId) {
        this.shipId = shipId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setIsPremium(boolean isPremium) {
        this.isPremium = isPremium;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getGoldPrice() {
        return goldPrice;
    }

    public void setGoldPrice(int goldPrice) {
        this.goldPrice = goldPrice;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public List<Long> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<Long> equipments) {
        this.equipments = equipments;
    }

    public List<Long> getNextShipIds() {
        return nextShipIds;
    }

    public void setNextShipIds(List<Long> nextShipIds) {
        this.nextShipIds = nextShipIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediumImage() {
        return mediumImage;
    }

    public void setMediumImage(String mediumImage) {
        this.mediumImage = mediumImage;
    }

    public String getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }

    public String getBestImage(){
        String imageUrl = "";
        if(!TextUtils.isEmpty(largeImage)){
            imageUrl = largeImage;
        } else if(!TextUtils.isEmpty(mediumImage)){
            imageUrl = mediumImage;
        } else {
            imageUrl = image;
        }
        return imageUrl;
    }

    public Map<Long, ShipModuleItem> getItems() {
        return items;
    }

    public void setItems(Map<Long, ShipModuleItem> items) {
        this.items = items;
    }

    public List<Long> getEngine() {
        return engine;
    }

    public void setEngine(List<Long> engine) {
        this.engine = engine;
    }

    public List<Long> getTorpBomb() {
        return torpBomb;
    }

    public void setTorpBomb(List<Long> torpBomb) {
        this.torpBomb = torpBomb;
    }

    public List<Long> getFighter() {
        return fighter;
    }

    public void setFighter(List<Long> fighter) {
        this.fighter = fighter;
    }

    public List<Long> getHull() {
        return hull;
    }

    public void setHull(List<Long> hull) {
        this.hull = hull;
    }

    public List<Long> getArtillery() {
        return artillery;
    }

    public void setArtillery(List<Long> artillery) {
        this.artillery = artillery;
    }

    public List<Long> getTorps() {
        return torps;
    }

    public void setTorps(List<Long> torps) {
        this.torps = torps;
    }

    public List<Long> getFireControl() {
        return fireControl;
    }

    public void setFireControl(List<Long> fireControl) {
        this.fireControl = fireControl;
    }

    public List<Long> getFlightControl() {
        return flightControl;
    }

    public void setFlightControl(List<Long> flightControl) {
        this.flightControl = flightControl;
    }

    public List<Long> getDiveBomber() {
        return DiveBomber;
    }

    public void setDiveBomber(List<Long> diveBomber) {
        DiveBomber = diveBomber;
    }
}