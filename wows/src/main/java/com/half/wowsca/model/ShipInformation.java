package com.half.wowsca.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by slai4 on 7/29/2017.
 */

public class ShipInformation {

    JSONObject armour;
    JSONObject hull;
    JSONObject fire_control;
    JSONObject weaponry;
    JSONObject concealment;
    JSONObject atba ;
    JSONObject mobility;
    JSONObject aa;
    JSONObject torps;
    JSONObject artillery;

    JSONObject flightControl;
    JSONObject torpedoBombers;
    JSONObject diveBombers;

    private int health;
    private int survivalHealth;
    private int planesAmount;

    //armor
    private int overallMin;
    private int overallMax;
    private int deckMin;
    private int deckMax;
    private int extremitiesMin;
    private int extremitiesMax;
    private int casemateMin;
    private int casemateMax;
    private int citadelMin;
    private int citadelMax;

    private int artilleryTotal;
    private int torpTotal;
    private int antiAirTotal;
    private int aircraftTotal;
    private int mobilityTotal;
    private int concealmentTotal;

    private double turningRadius;

    private double speed;
    private double rudderTime;

    private double artiDistance;
    private double secondaryRange;

    //torp
    private boolean hasTorps;
    private double torpDistance;
    private double torpReload;
    private int torpMaxDamage;
    private double torpSpeed;
    private int torpSlots;
    private int torpBarrels;
    private int torpGuns;

    //Torp bommbers
    private boolean isCarrier;
    public double torpBDistance;
    private double torpBprepareTime;
    private int torpBDamage;
    private double torpBMaxSpeed;
    private int fighterSquadrons;
    private int bomberSquadrons;
    private int torpedoSquadrons;

    private double concealmentDistanceShip;
    private double concealmentDistancePlane;

    private double artiGunRate;
    private int artiBarrels;
    private int artiTurrets;
    private int artiMaxDispersion;
    private double artiRotation;
    private int artiAPDmg;
    private int artiHEDmg;
    private double artiHEBurnProb;

    private double diveBPrepareTime;
    private int diveBMaxDmg;
    private double diveBBurnProbably;

    private double topAARange;

    public void parse(String string){
        JSONObject obj = null;
        try {
            obj = new JSONObject(string);
        } catch (JSONException e) {
        }
        if (obj != null) {
             armour = obj.optJSONObject("armour");
             hull = obj.optJSONObject("hull");
             fire_control = obj.optJSONObject("fire_control");
             weaponry = obj.optJSONObject("weaponry");
             concealment = obj.optJSONObject("concealment");
             atba = obj.optJSONObject("atba");
             mobility = obj.optJSONObject("mobility");
             aa = obj.optJSONObject("anti_aircraft");
             torps = obj.optJSONObject("torpedoes");
             artillery = obj.optJSONObject("artillery");

             flightControl = obj.optJSONObject("flight_control");
             torpedoBombers = obj.optJSONObject("torpedo_bomber");
             diveBombers = obj.optJSONObject("dive_bomber");

            //survival
            health = hull.optInt("health");
            survivalHealth = armour.optInt("total");
            planesAmount = hull.optInt("planes_amount");


            JSONObject overallArmor = armour.optJSONObject("range");
            JSONObject deckArmor = armour.optJSONObject("deck");
            JSONObject extreArmor = armour.optJSONObject("extremities");
            JSONObject casemateArmor = armour.optJSONObject("casemate");
            JSONObject citadelArmor = armour.optJSONObject("citadel");

            overallMin = overallArmor.optInt("min");
            overallMax = overallArmor.optInt("max");
            deckMin = deckArmor.optInt("min");
            deckMax = deckArmor.optInt("max");
            extremitiesMin = extreArmor.optInt("min");
            extremitiesMax = extreArmor.optInt("max");
            casemateMin = casemateArmor.optInt("min");
            casemateMax = casemateArmor.optInt("max");
            citadelMin = citadelArmor.optInt("min");
            citadelMax = citadelArmor.optInt("max");

            //artillery
            artilleryTotal = weaponry.optInt("artillery");

            //torps
            torpTotal = weaponry.optInt("torpedoes");

            //anti-aircraft
            antiAirTotal = weaponry.optInt("anti_aircraft");

            //aircraft
            aircraftTotal = weaponry.optInt("aircraft");

            //mobility
            mobilityTotal = mobility.optInt("total");

            turningRadius = mobility.optDouble("turning_radius");

            //concealment
            concealmentTotal = concealment.optInt("total");

            speed = mobility.optDouble("max_speed");

            rudderTime = mobility.optDouble("rudder_time");

            if(fire_control != null)
                artiDistance = fire_control.optDouble("distance");

            if (atba != null)
                secondaryRange = atba.optDouble("distance");


            if (torps != null) {
                torpDistance = torps.optDouble("distance");
                torpReload = torps.optDouble("reload_time");

                torpMaxDamage = torps.optInt("max_damage");
                torpSpeed = torps.optDouble("torpedo_speed");
                JSONObject slots = torps.optJSONObject("slots");
                if (slots != null) {
                    JSONObject torp = slots.optJSONObject("0");
                    if (torp != null) {
                        torpBarrels = torp.optInt("barrels");
                        torpGuns = torp.optInt("guns");
                    }
                }
            } else if (torpedoBombers != null) {
                torpBDistance = torpedoBombers.optDouble("torpedo_distance");
                torpBprepareTime = torpedoBombers.optDouble("prepare_time");
                torpBDamage = torpedoBombers.optInt("max_damage");
                torpBMaxSpeed = torpedoBombers.optDouble("torpedo_max_speed");
                if (flightControl != null) {
                    fighterSquadrons = flightControl.optInt("fighter_squadrons");
                    bomberSquadrons = flightControl.optInt("bomber_squadrons");
                    torpedoSquadrons = flightControl.optInt("torpedo_squadrons");
                }
            }

            if (concealment != null) {
                concealmentDistanceShip = concealment.optDouble("detect_distance_by_ship");
                concealmentDistancePlane = concealment.optDouble("detect_distance_by_plane");
            }
            if (artillery != null) {
                artiGunRate = artillery.optDouble("gun_rate");
                JSONObject slots = artillery.optJSONObject("slots");
                if (slots != null) {
                    JSONObject guns = slots.optJSONObject("0");
                    if (guns != null) {
                        artiBarrels = guns.optInt("barrels");
                        artiTurrets = guns.optInt("guns");
                    }
                }
                artiMaxDispersion = artillery.optInt("max_dispersion");
                artiRotation = artillery.optDouble("rotation_time");
                JSONObject shells = artillery.optJSONObject("shells");
                if (shells != null) {
                    JSONObject ap = shells.optJSONObject("AP");
                    JSONObject he = shells.optJSONObject("HE");
                    if (ap != null) {
                        artiAPDmg = ap.optInt("damage");
                    }
                    if (he != null) {
                        artiHEDmg = he.optInt("damage");
                        artiHEBurnProb = he.optDouble("burn_probability");
                    }
                }
            } else if (diveBombers != null) {
                diveBMaxDmg = diveBombers.optInt("max_damage");
                diveBPrepareTime = diveBombers.optDouble("prepare_time");
                diveBBurnProbably = diveBombers.optDouble("bomb_burn_probability");
            }

            if (aa != null) {
                JSONObject slots = aa.optJSONObject("slots");
                if (slots != null) {
                    if (slots.length() > 0) {
                        Iterator<String> itea = slots.keys();
                        while (itea.hasNext()) {
                            JSONObject aSlot = slots.optJSONObject(itea.next());
                            double aaRange = aSlot.optDouble("distance");
                            if (aaRange > topAARange) {
                                topAARange = aaRange;
                            }
                        }
                    }
                }
            }
        }
    }

    public JSONObject getArmour() {
        return armour;
    }

    public void setArmour(JSONObject armour) {
        this.armour = armour;
    }

    public JSONObject getHull() {
        return hull;
    }

    public void setHull(JSONObject hull) {
        this.hull = hull;
    }

    public JSONObject getFire_control() {
        return fire_control;
    }

    public void setFire_control(JSONObject fire_control) {
        this.fire_control = fire_control;
    }

    public JSONObject getWeaponry() {
        return weaponry;
    }

    public void setWeaponry(JSONObject weaponry) {
        this.weaponry = weaponry;
    }

    public JSONObject getConcealment() {
        return concealment;
    }

    public void setConcealment(JSONObject concealment) {
        this.concealment = concealment;
    }

    public JSONObject getAtba() {
        return atba;
    }

    public void setAtba(JSONObject atba) {
        this.atba = atba;
    }

    public JSONObject getMobility() {
        return mobility;
    }

    public void setMobility(JSONObject mobility) {
        this.mobility = mobility;
    }

    public JSONObject getAa() {
        return aa;
    }

    public void setAa(JSONObject aa) {
        this.aa = aa;
    }

    public JSONObject getTorps() {
        return torps;
    }

    public void setTorps(JSONObject torps) {
        this.torps = torps;
    }

    public JSONObject getArtillery() {
        return artillery;
    }

    public void setArtillery(JSONObject artillery) {
        this.artillery = artillery;
    }

    public JSONObject getFlightControl() {
        return flightControl;
    }

    public void setFlightControl(JSONObject flightControl) {
        this.flightControl = flightControl;
    }

    public JSONObject getTorpedoBombers() {
        return torpedoBombers;
    }

    public void setTorpedoBombers(JSONObject torpedoBombers) {
        this.torpedoBombers = torpedoBombers;
    }

    public JSONObject getDiveBombers() {
        return diveBombers;
    }

    public void setDiveBombers(JSONObject diveBombers) {
        this.diveBombers = diveBombers;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getSurvivalHealth() {
        return survivalHealth;
    }

    public void setSurvivalHealth(int survivalHealth) {
        this.survivalHealth = survivalHealth;
    }

    public int getPlanesAmount() {
        return planesAmount;
    }

    public void setPlanesAmount(int planesAmount) {
        this.planesAmount = planesAmount;
    }

    public int getOverallMin() {
        return overallMin;
    }

    public void setOverallMin(int overallMin) {
        this.overallMin = overallMin;
    }

    public int getOverallMax() {
        return overallMax;
    }

    public void setOverallMax(int overallMax) {
        this.overallMax = overallMax;
    }

    public int getDeckMin() {
        return deckMin;
    }

    public void setDeckMin(int deckMin) {
        this.deckMin = deckMin;
    }

    public int getDeckMax() {
        return deckMax;
    }

    public void setDeckMax(int deckMax) {
        this.deckMax = deckMax;
    }

    public int getExtremitiesMin() {
        return extremitiesMin;
    }

    public void setExtremitiesMin(int extremitiesMin) {
        this.extremitiesMin = extremitiesMin;
    }

    public int getExtremitiesMax() {
        return extremitiesMax;
    }

    public void setExtremitiesMax(int extremitiesMax) {
        this.extremitiesMax = extremitiesMax;
    }

    public int getCasemateMin() {
        return casemateMin;
    }

    public void setCasemateMin(int casemateMin) {
        this.casemateMin = casemateMin;
    }

    public int getCasemateMax() {
        return casemateMax;
    }

    public void setCasemateMax(int casemateMax) {
        this.casemateMax = casemateMax;
    }

    public int getCitadelMin() {
        return citadelMin;
    }

    public void setCitadelMin(int citadelMin) {
        this.citadelMin = citadelMin;
    }

    public int getCitadelMax() {
        return citadelMax;
    }

    public void setCitadelMax(int citadelMax) {
        this.citadelMax = citadelMax;
    }

    public int getArtilleryTotal() {
        return artilleryTotal;
    }

    public void setArtilleryTotal(int artilleryTotal) {
        this.artilleryTotal = artilleryTotal;
    }

    public int getTorpTotal() {
        return torpTotal;
    }

    public void setTorpTotal(int torpTotal) {
        this.torpTotal = torpTotal;
    }

    public int getAntiAirTotal() {
        return antiAirTotal;
    }

    public void setAntiAirTotal(int antiAirTotal) {
        this.antiAirTotal = antiAirTotal;
    }

    public int getAircraftTotal() {
        return aircraftTotal;
    }

    public void setAircraftTotal(int aircraftTotal) {
        this.aircraftTotal = aircraftTotal;
    }

    public int getMobilityTotal() {
        return mobilityTotal;
    }

    public void setMobilityTotal(int mobilityTotal) {
        this.mobilityTotal = mobilityTotal;
    }

    public int getConcealmentTotal() {
        return concealmentTotal;
    }

    public void setConcealmentTotal(int concealmentTotal) {
        this.concealmentTotal = concealmentTotal;
    }

    public double getTurningRadius() {
        return turningRadius;
    }

    public void setTurningRadius(double turningRadius) {
        this.turningRadius = turningRadius;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getRudderTime() {
        return rudderTime;
    }

    public void setRudderTime(double rudderTime) {
        this.rudderTime = rudderTime;
    }

    public double getArtiDistance() {
        return artiDistance;
    }

    public void setArtiDistance(double artiDistance) {
        this.artiDistance = artiDistance;
    }

    public double getSecondaryRange() {
        return secondaryRange;
    }

    public void setSecondaryRange(double secondaryRange) {
        this.secondaryRange = secondaryRange;
    }

    public boolean isHasTorps() {
        return hasTorps;
    }

    public void setHasTorps(boolean hasTorps) {
        this.hasTorps = hasTorps;
    }

    public double getTorpDistance() {
        return torpDistance;
    }

    public void setTorpDistance(double torpDistance) {
        this.torpDistance = torpDistance;
    }

    public double getTorpReload() {
        return torpReload;
    }

    public void setTorpReload(double torpReload) {
        this.torpReload = torpReload;
    }

    public int getTorpMaxDamage() {
        return torpMaxDamage;
    }

    public void setTorpMaxDamage(int torpMaxDamage) {
        this.torpMaxDamage = torpMaxDamage;
    }

    public double getTorpSpeed() {
        return torpSpeed;
    }

    public void setTorpSpeed(double torpSpeed) {
        this.torpSpeed = torpSpeed;
    }

    public int getTorpSlots() {
        return torpSlots;
    }

    public void setTorpSlots(int torpSlots) {
        this.torpSlots = torpSlots;
    }

    public int getTorpBarrels() {
        return torpBarrels;
    }

    public void setTorpBarrels(int torpBarrels) {
        this.torpBarrels = torpBarrels;
    }

    public int getTorpGuns() {
        return torpGuns;
    }

    public void setTorpGuns(int torpGuns) {
        this.torpGuns = torpGuns;
    }

    public boolean isCarrier() {
        return isCarrier;
    }

    public void setCarrier(boolean carrier) {
        isCarrier = carrier;
    }

    public double getTorpBDistance() {
        return torpBDistance;
    }

    public void setTorpBDistance(double torpBDistance) {
        this.torpBDistance = torpBDistance;
    }

    public double getTorpBprepareTime() {
        return torpBprepareTime;
    }

    public void setTorpBprepareTime(double torpBprepareTime) {
        this.torpBprepareTime = torpBprepareTime;
    }

    public int getTorpBDamage() {
        return torpBDamage;
    }

    public void setTorpBDamage(int torpBDamage) {
        this.torpBDamage = torpBDamage;
    }

    public double getTorpBMaxSpeed() {
        return torpBMaxSpeed;
    }

    public void setTorpBMaxSpeed(double torpBMaxSpeed) {
        this.torpBMaxSpeed = torpBMaxSpeed;
    }

    public int getFighterSquadrons() {
        return fighterSquadrons;
    }

    public void setFighterSquadrons(int fighterSquadrons) {
        this.fighterSquadrons = fighterSquadrons;
    }

    public int getBomberSquadrons() {
        return bomberSquadrons;
    }

    public void setBomberSquadrons(int bomberSquadrons) {
        this.bomberSquadrons = bomberSquadrons;
    }

    public int getTorpedoSquadrons() {
        return torpedoSquadrons;
    }

    public void setTorpedoSquadrons(int torpedoSquadrons) {
        this.torpedoSquadrons = torpedoSquadrons;
    }

    public double getConcealmentDistanceShip() {
        return concealmentDistanceShip;
    }

    public void setConcealmentDistanceShip(double concealmentDistanceShip) {
        this.concealmentDistanceShip = concealmentDistanceShip;
    }

    public double getConcealmentDistancePlane() {
        return concealmentDistancePlane;
    }

    public void setConcealmentDistancePlane(double concealmentDistancePlane) {
        this.concealmentDistancePlane = concealmentDistancePlane;
    }

    public double getArtiGunRate() {
        return artiGunRate;
    }

    public void setArtiGunRate(double artiGunRate) {
        this.artiGunRate = artiGunRate;
    }

    public int getArtiBarrels() {
        return artiBarrels;
    }

    public void setArtiBarrels(int artiBarrels) {
        this.artiBarrels = artiBarrels;
    }

    public int getArtiTurrets() {
        return artiTurrets;
    }

    public void setArtiTurrets(int artiTurrets) {
        this.artiTurrets = artiTurrets;
    }

    public int getArtiMaxDispersion() {
        return artiMaxDispersion;
    }

    public void setArtiMaxDispersion(int artiMaxDispersion) {
        this.artiMaxDispersion = artiMaxDispersion;
    }

    public double getArtiRotation() {
        return artiRotation;
    }

    public void setArtiRotation(double artiRotation) {
        this.artiRotation = artiRotation;
    }

    public int getArtiAPDmg() {
        return artiAPDmg;
    }

    public void setArtiAPDmg(int artiAPDmg) {
        this.artiAPDmg = artiAPDmg;
    }

    public int getArtiHEDmg() {
        return artiHEDmg;
    }

    public void setArtiHEDmg(int artiHEDmg) {
        this.artiHEDmg = artiHEDmg;
    }

    public double getArtiHEBurnProb() {
        return artiHEBurnProb;
    }

    public void setArtiHEBurnProb(double artiHEBurnProb) {
        this.artiHEBurnProb = artiHEBurnProb;
    }

    public double getDiveBPrepareTime() {
        return diveBPrepareTime;
    }

    public void setDiveBPrepareTime(double diveBPrepareTime) {
        this.diveBPrepareTime = diveBPrepareTime;
    }

    public int getDiveBMaxDmg() {
        return diveBMaxDmg;
    }

    public void setDiveBMaxDmg(int diveBMaxDmg) {
        this.diveBMaxDmg = diveBMaxDmg;
    }

    public double getDiveBBurnProbably() {
        return diveBBurnProbably;
    }

    public void setDiveBBurnProbably(double diveBBurnProbably) {
        this.diveBBurnProbably = diveBBurnProbably;
    }

    public double getTopAARange() {
        return topAARange;
    }

    public void setTopAARange(double topAARange) {
        this.topAARange = topAARange;
    }
}