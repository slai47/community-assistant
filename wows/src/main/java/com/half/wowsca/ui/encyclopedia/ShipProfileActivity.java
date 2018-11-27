package com.half.wowsca.ui.encyclopedia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;



import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.alerts.Alert;
import com.half.wowsca.anim.ProgressBarAnimation;
import com.half.wowsca.backend.GetShipEncyclopediaInfo;
import com.half.wowsca.model.ShipInformation;
import com.half.wowsca.model.encyclopedia.items.EquipmentInfo;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.encyclopedia.items.ShipModuleItem;
import com.half.wowsca.model.encyclopedia.items.ShipStat;
import com.half.wowsca.model.queries.ShipQuery;
import com.half.wowsca.model.result.ShipResult;
import com.half.wowsca.ui.CABaseActivity;
import com.half.wowsca.ui.UIUtils;
import org.greenrobot.eventbus.Subscribe;
import com.squareup.picasso.Picasso;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.views.SwipeBackLayout;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 10/31/2015.
 */
public class ShipProfileActivity extends CABaseActivity {

    public static final String SHIP_ID = "shipid";
    public static final String SHIP_DATA = "shipData";
    public static final String PATTERN = "###,###,###";

    private Toolbar mToolbar;
    private View progress;

    private ImageView imageView;
    private TextView tvPrice;
    private TextView tvNationTier;
    private TextView tvDescirption;

    //progress stats
    private View aArtillery;
    private TextView modArtilery;
    private ProgressBar progArtillery;

    private TextView modSurvival;
    private ProgressBar progSurvival;

    private View aTorps;
    private TextView modTorps;
    private ProgressBar progTorps;

    private View aAA;
    private TextView modAA;
    private ProgressBar progAA;

    private View aAircraft;
    private TextView modAircraft;
    private ProgressBar progAircraft;

    private TextView modMobility;
    private ProgressBar progMobility;

    private TextView modConcealmeat;
    private ProgressBar progConcealment;

    // ship stats
    private TextView statsGunRange;
    private TextView statstorpRange;
    private TextView statsConcealRange;
    private TextView statsConcealRangePlane;
    private TextView statsShellDamage;
    private TextView statsSpeed;
    private TextView statsHealth;
    private TextView statsArtilleryFireRate;
    private TextView statsTorpFireRate;
    private TextView statsNumGuns;
    private TextView statsNumTorps;
    private TextView statsRudderShiftTime;
    private TextView statsSecondaryRange;
    private TextView statsAARange;
    private TextView statsTorpDamage;
    private TextView statsTorpSpeed;
    private TextView statsNumPlanes;
    private TextView statsTurningRadius;
    private TextView statsDispersion;
    private TextView statsRotation;
    private TextView statsArmor;
    private TextView statsAAGun;

    //ship stats text
    private TextView statsGunRangeText;
    private TextView statstorpRangeText;
    private TextView statsConcealRangeText;
    private TextView statsConcealRangePlaneText;
    private TextView statsShellDamageText;
    private TextView statsSpeedText;
    private TextView statsHealthText;
    private TextView statsArtilleryFireRateText;
    private TextView statsTorpFireRateText;
    private TextView statsNumGunsText;
    private TextView statsNumTorpsText;
    private TextView statsRudderShiftTimeText;
    private TextView statsSecondaryRangeText;
    private TextView statsAARangeText;
    private TextView statsTorpDamageText;
    private TextView statsTorpSpeedText;
    private TextView statsNumPlanesText;
    private TextView statsDispersionText;
    private TextView statsRotationText;
    private TextView statsArmorText;
    private TextView statsAAGunText;

    //average stats
    private TextView avgDamage;
    private TextView avgWinRate;
    private TextView avgKills;
    private TextView avgPlanes;

    //upgrades
    private View aUpgrades;
    private LinearLayout llUpgrades;

    //next ship
    private View aNextShip;
    private LinearLayout llNextShips;

    private long shipId;
    private String shipServerInfo;

    private LinearLayout llModule1;
    private LinearLayout llModule2;

    public static Map<String,Long> MODULE_LIST;
    private TextView tvModulesBottomText;

    private ScrollView scroll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia_page);
        if (savedInstanceState != null) {
            shipId = savedInstanceState.getLong(SHIP_ID);
            shipServerInfo = savedInstanceState.getString(SHIP_DATA);
        } else {
            shipId = getIntent().getLongExtra(SHIP_ID, 0);
        }
        bindView();
    }

    private void bindView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("");

        scroll = (ScrollView) findViewById(R.id.encyclopedia_scroll);

        progress = findViewById(R.id.encyclopedia_progress);

        imageView = (ImageView) findViewById(R.id.encyclopedia_image);
        tvNationTier = (TextView) findViewById(R.id.encyclopedia_nation_tier);
        tvPrice = (TextView) findViewById(R.id.encyclopedia_price);
        tvDescirption = (TextView) findViewById(R.id.encyclopedia_description);

        modAA = (TextView) findViewById(R.id.encyclopedia_progress_aa_text);
        modAircraft = (TextView) findViewById(R.id.encyclopedia_progress_aircraft_text);
        modArtilery = (TextView) findViewById(R.id.encyclopedia_progress_artillery_text);
        modConcealmeat = (TextView) findViewById(R.id.encyclopedia_progress_concealment_text);
        modMobility = (TextView) findViewById(R.id.encyclopedia_progress_mobility_text);
        modSurvival = (TextView) findViewById(R.id.encyclopedia_progress_survival_text);
        modTorps = (TextView) findViewById(R.id.encyclopedia_progress_torps_text);

        progAA = (ProgressBar) findViewById(R.id.encyclopedia_progress_aa);
        progAircraft = (ProgressBar) findViewById(R.id.encyclopedia_progress_aircraft);
        progArtillery = (ProgressBar) findViewById(R.id.encyclopedia_progress_artillery);
        progTorps = (ProgressBar) findViewById(R.id.encyclopedia_progress_torps);
        progConcealment = (ProgressBar) findViewById(R.id.encyclopedia_progress_concealment);
        progSurvival = (ProgressBar) findViewById(R.id.encyclopedia_progress_survival);
        progMobility = (ProgressBar) findViewById(R.id.encyclopedia_progress_mobility);

        aArtillery = findViewById(R.id.encyclopedia_progress_artillery_area);
        aAA = findViewById(R.id.encyclopedia_progress_aa_area);
        aAircraft = findViewById(R.id.encyclopedia_progress_aircraft_area);
        aTorps = findViewById(R.id.encyclopedia_progress_torps_area);

        statsArtilleryFireRate = (TextView) findViewById(R.id.encyclopedia_fire_rate_artillery);
        statsConcealRange = (TextView) findViewById(R.id.encyclopedia_rang_concealment);
        statsGunRange = (TextView) findViewById(R.id.encyclopedia_range_gun);
        statsHealth = (TextView) findViewById(R.id.encyclopedia_health);
        statsNumGuns = (TextView) findViewById(R.id.encyclopedia_number_of_guns);
        statsNumTorps = (TextView) findViewById(R.id.encyclopedia_number_of_torps);
        statsSpeed = (TextView) findViewById(R.id.encyclopedia_speed);
        statsShellDamage = (TextView) findViewById(R.id.encyclopedia_range_spotting);
        statsTorpFireRate = (TextView) findViewById(R.id.encyclopedia_fire_rate_torps);
        statstorpRange = (TextView) findViewById(R.id.encyclopedia_range_torps);
        statsConcealRangePlane = (TextView) findViewById(R.id.encyclopedia_rang_concealment_plane);
        statsRudderShiftTime = (TextView) findViewById(R.id.encyclopedia_rudder_shift);
        statsSecondaryRange = (TextView) findViewById(R.id.encyclopedia_range_secondaries);
        statsAARange = (TextView) findViewById(R.id.encyclopedia_range_aa);
        statsTorpDamage = (TextView) findViewById(R.id.encyclopedia_damage_torps);
        statsTorpSpeed = (TextView) findViewById(R.id.encyclopedia_torps_speed);
        statsNumPlanes = (TextView) findViewById(R.id.encyclopedia_number_of_planes);
        statsTurningRadius = (TextView) findViewById(R.id.encyclopedia_turning_radius);
        statsDispersion = (TextView) findViewById(R.id.encyclopedia_gun_dispersion);
        statsRotation = (TextView) findViewById(R.id.encyclopedia_gun_rotation);
        statsArmor = (TextView) findViewById(R.id.encyclopedia_armor);
        statsAAGun = (TextView) findViewById(R.id.encyclopedia_aa_guns);

        statsArtilleryFireRateText = (TextView) findViewById(R.id.encyclopedia_fire_rate_artillery_text);
        statsConcealRangeText = (TextView) findViewById(R.id.encyclopedia_rang_concealment_text);
        statsGunRangeText = (TextView) findViewById(R.id.encyclopedia_range_gun_text);
        statsHealthText = (TextView) findViewById(R.id.encyclopedia_health_text);
        statsNumGunsText = (TextView) findViewById(R.id.encyclopedia_number_of_guns_text);
        statsNumTorpsText = (TextView) findViewById(R.id.encyclopedia_number_of_torps_text);
        statsSpeedText = (TextView) findViewById(R.id.encyclopedia_speed_text);
        statsShellDamageText = (TextView) findViewById(R.id.encyclopedia_range_spotting_text);
        statsTorpFireRateText = (TextView) findViewById(R.id.encyclopedia_fire_rate_torps_text);
        statstorpRangeText = (TextView) findViewById(R.id.encyclopedia_range_torps_text);
        statsConcealRangePlaneText = (TextView) findViewById(R.id.encyclopedia_rang_concealment_plane_text);
        statsRudderShiftTimeText = (TextView) findViewById(R.id.encyclopedia_rudder_shift_text);
        statsSecondaryRangeText = (TextView) findViewById(R.id.encyclopedia_range_secondaries_text);
        statsAARangeText = (TextView) findViewById(R.id.encyclopedia_range_aa_text);
        statsTorpDamageText = (TextView) findViewById(R.id.encyclopedia_damage_torps_text);
        statsTorpSpeedText = (TextView) findViewById(R.id.encyclopedia_torps_speed_text);
        statsNumPlanesText = (TextView) findViewById(R.id.encyclopedia_number_of_planes_text);
        statsDispersionText = (TextView) findViewById(R.id.encyclopedia_gun_dispersion_text);
        statsRotationText = (TextView) findViewById(R.id.encyclopedia_gun_rotation_text);
        statsArmorText = (TextView) findViewById(R.id.encyclopedia_armor_text);
        statsAAGunText = (TextView) findViewById(R.id.encyclopedia_aa_guns_text);

        avgDamage = (TextView) findViewById(R.id.encyclopedia_average_damage);
        avgKills = (TextView) findViewById(R.id.encyclopedia_average_kills);
        avgPlanes = (TextView) findViewById(R.id.encyclopedia_average_planes);
//        avgSurvival = (TextView) findViewById(R.id.encyclopedia_average_survival_rate);
        avgWinRate = (TextView) findViewById(R.id.encyclopedia_average_win_rate);
//        avgXP = (TextView) findViewById(R.id.encyclopedia_average_xp);

        aUpgrades = findViewById(R.id.encyclopedia_upgrades_area);
        aNextShip = findViewById(R.id.encyclopedia_next_ship_area);
        llUpgrades = (LinearLayout) findViewById(R.id.encyclopedia_upgrades_container);
        llNextShips = (LinearLayout) findViewById(R.id.encyclopedia_next_ship_container);

        llModule1 = (LinearLayout) findViewById(R.id.encyclopedia_module_list_1);
        llModule2 = (LinearLayout) findViewById(R.id.encyclopedia_module_list_2);
        tvModulesBottomText = (TextView) findViewById(R.id.encyclopedia_module_bottom_text);

        View view = findViewById(R.id.encyclopedia_warshipstats_area);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://warships.today/na/help/warships_today_rating"));
                startActivity(i);
            }
        });
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }



    private void initView() {
        if (shipId != 0) {
            ShipInfo shipInfo = CAApp.getInfoManager().getShipInfo(getApplicationContext()).get(shipId);
            if (shipInfo != null) {
                setTitle(shipInfo.getName());
                String nation = UIUtils.getNationText(getApplicationContext(), shipInfo.getNation());
                tvNationTier.setText(nation + getString(R.string.encyclopedia_nation_tier) + " " + shipInfo.getTier());
                Picasso.get().load(shipInfo.getBestImage()).error(R.drawable.ic_missing_image).into(imageView);

                DecimalFormat formatter = new DecimalFormat(PATTERN);
                if (shipInfo.isPremium()) {
                    tvPrice.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.premium_shade));
                    if (shipInfo.getGoldPrice() > 0) {
                        tvPrice.setText(formatter.format(shipInfo.getGoldPrice()) + getString(R.string.gold));
                    } else {
                        tvPrice.setText(R.string.price_not_known);
                    }
                } else {
                    if (shipInfo.getPrice() > 0) {
                        tvPrice.setText(formatter.format(shipInfo.getPrice()) + getString(R.string.credits));
                    } else {
                        tvPrice.setText(R.string.price_not_known);
                    }
                }
                if (!TextUtils.isEmpty(shipInfo.getDescription())) {
                    tvDescirption.setVisibility(View.VISIBLE);
                    tvDescirption.setText(shipInfo.getDescription());
                } else {
                    tvDescirption.setVisibility(View.GONE);
                }

                createModuleGrid();

                //upgrades
                if (shipInfo.getEquipments() != null && shipInfo.getEquipments().size() > 0) {
                    aUpgrades.setVisibility(View.VISIBLE);
                    createUpgrades(shipInfo);
                } else {
                    aUpgrades.setVisibility(View.GONE);
                }

                //nextship
                if (shipInfo.getNextShipIds() != null && shipInfo.getNextShipIds().size() > 0) {
                    aNextShip.setVisibility(View.VISIBLE);
                    createNextShip(shipInfo);
                } else {
                    aNextShip.setVisibility(View.GONE);
                }
            }
            progress.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(shipServerInfo)) {
                Dlog.wtf("Encyclopedia", "shipInfo = " + shipServerInfo);

                ShipInformation info = new ShipInformation();
                info.parse(shipServerInfo);
                if (info.getArmour() != null) {

                    //survival
                    statsHealth.setText("" + info.getHealth());
                    int survivalTotal = info.getSurvivalHealth();
                    modSurvival.setText("" + survivalTotal);
                    animate(progSurvival, survivalTotal);
                    int planes_amount = info.getPlanesAmount();
                    if (planes_amount > 0) {
                        statsNumPlanes.setText(planes_amount + "");
                    } else {
                        disableView(statsNumPlanesText);
                    }

                    StringBuilder strbug = new StringBuilder();
                    strbug.append(grabMinMax(info.getArmour(), "range", getString(R.string.range_armor)));
                    strbug.append("\n");
                    strbug.append(grabMinMax(info.getArmour(), "deck", getString(R.string.deck_armor)));
                    strbug.append("\n");
                    strbug.append(grabMinMax(info.getArmour(), "extremities", getString(R.string.bow_stern_armor)));
                    strbug.append("\n");
                    strbug.append(grabMinMax(info.getArmour(), "casemate", getString(R.string.gun_casemate_armor)));
                    strbug.append("\n");
                    strbug.append(grabMinMax(info.getArmour(), "citadel", getString(R.string.citadel_armor)));
                    statsArmor.setText(strbug.toString());

                    //artillery
                    int artileryTotal = info.getArtilleryTotal();
                    if (artileryTotal > 0) {
                        modArtilery.setText(artileryTotal + "");
                        animate(progArtillery, artileryTotal);
                        aArtillery.setVisibility(View.VISIBLE);
                    } else {
                        aArtillery.setVisibility(View.GONE);
                    }

                    //torps
                    int torpsTotal = info.getTorpTotal();
                    if (torpsTotal > 0) {
                        modTorps.setText(torpsTotal + "");
                        animate(progTorps, torpsTotal);
                        aTorps.setVisibility(View.VISIBLE);
                    } else {
                        aTorps.setVisibility(View.GONE);
                    }

                    //anti-aircraft
                    int aaTotal = info.getAntiAirTotal();
                    if (aaTotal > 0) {
                        modAA.setText(aaTotal + "");
                        animate(progAA, aaTotal);
                        aAA.setVisibility(View.VISIBLE);
                    } else {
                        aAA.setVisibility(View.GONE);
                    }

                    //aircraft
                    int aircraftTotal = info.getAircraftTotal();
                    if (aircraftTotal > 0) {
                        modAircraft.setText(aircraftTotal + "");
                        animate(progAircraft, aircraftTotal);
                        aAircraft.setVisibility(View.VISIBLE);
                    } else {
                        aAircraft.setVisibility(View.GONE);
                    }

                    //mobility
                    int mobilityTotal = info.getMobilityTotal();
                    modMobility.setText("" + mobilityTotal);
                    animate(progMobility, mobilityTotal);
                    statsTurningRadius.setText(info.getTurningRadius() + getString(R.string.meters));

                    //concealment
                    int concealTotal = info.getConcealmentTotal();
                    modConcealmeat.setText("" + concealTotal);
                    animate(progConcealment, concealTotal);

                    statsSpeed.setText(info.getSpeed()+ "");
                    statsRudderShiftTime.setText(info.getRudderTime() + getString(R.string.seconds));

                    if (info.getFire_control() != null) {
                        statsGunRange.setText(info.getArtiDistance() + getString(R.string.kilometers));
                    } else {
                        disableView(statsGunRangeText);
                    }

                    if (info.getAtba() != null) {
                        statsSecondaryRange.setText(info.getSecondaryRange() + getString(R.string.kilometers));
                    } else {
                        disableView(statsSecondaryRangeText);
                    }

                    if (info.getTorps() != null) {
                        statstorpRange.setText(info.getTorpDistance() + getString(R.string.kilometers));
                        double reloadTime = info.getTorpReload();
                        String suffix = "s";
                        if (reloadTime > 60) {
                            suffix = " mins";
                        }
                        statsTorpFireRate.setText(Utils.getOneDepthDecimalFormatter().format(reloadTime / 60) + suffix);
                        statsTorpDamage.setText(info.getTorpMaxDamage() + "");
                        statsTorpSpeed.setText(info.getTorpSpeed() + getString(R.string.kilometers_per_second));
                        int barrels = info.getTorpBarrels();
                        int turrets = info.getTorpGuns();
                        statsNumTorps.setText((barrels * turrets) + " " + barrels + "x" + turrets);
                    } else if (info.getTorpedoBombers() != null) {
                        statstorpRange.setText(info.getTorpBDistance() + getString(R.string.kilometers));
                        statsTorpFireRate.setText(Utils.getOneDepthDecimalFormatter().format(info.getTorpBprepareTime()) + getString(R.string.seconds));
                        statsTorpDamage.setText(info.getTorpBDamage() + "");
                        statsTorpSpeed.setText(info.getTorpBMaxSpeed() + getString(R.string.kilometers_per_second));
                        if (info.getFlightControl() != null) {
                            statsNumTorps.setText((info.getFighterSquadrons() + info.getBomberSquadrons() + info.getTorpedoSquadrons()) + "");
                        }
                    } else {
                        disableView(statsTorpFireRateText);
                        disableView(statstorpRangeText);
                        disableView(statsTorpDamage);
                        disableView(statsTorpSpeedText);
                        disableView(statsNumTorpsText);
                        disableView(statsTorpDamageText);
                    }

                    if (info.getConcealment() != null) {
                        statsConcealRange.setText(info.getConcealmentDistanceShip() + getString(R.string.kilometers));
                        statsConcealRangePlane.setText(info.getConcealmentDistancePlane() + getString(R.string.kilometers));
                    } else {
                        disableView(statsConcealRangeText);
                        disableView(statsConcealRangePlaneText);
                    }

                    if (info.getArtillery() != null) {
                        statsArtilleryFireRate.setText(info.getArtiGunRate() + "");
                        int barrels = info.getArtiBarrels();
                        int turrets = info.getArtiTurrets();
                        statsNumGuns.setText((barrels * turrets) + " " + barrels + "x" + turrets);
                        statsDispersion.setText(info.getArtiMaxDispersion() + "m");
                        statsRotation.setText(info.getArtiRotation()+"s");
                        JSONObject shells = info.getArtillery().optJSONObject("shells");
                        if (shells != null) {
                            JSONObject ap = shells.optJSONObject("AP");
                            JSONObject he = shells.optJSONObject("HE");
                            StringBuilder sb = new StringBuilder();
                            if (ap != null) {
                                sb.append("AP - " + info.getArtiAPDmg());
                                sb.append("\n");
                            }
                            if (he != null) {
                                sb.append("HE - " + info.getArtiHEDmg() + " " + Utils.getOneDepthDecimalFormatter().format(info.getArtiHEBurnProb()) + "%");
                            }
                            String shellDamage = sb.toString();
                            if (!TextUtils.isEmpty(shellDamage)) {
                                statsShellDamage.setText(shellDamage);
                            } else {
                            }
                        }
                    } else if (info.getDiveBombers() != null) {
                        statsArtilleryFireRateText.setText(R.string.encyclopedia_db_prep_time);
                        statsArtilleryFireRate.setText(info.getDiveBPrepareTime() + getString(R.string.seconds));
                        statsShellDamageText.setText(R.string.encyclopedia_db_damage);
                        statsShellDamage.setText(info.getDiveBMaxDmg() + " - " + Utils.getOneDepthDecimalFormatter().format(info.getDiveBBurnProbably()) + "%");
                        //do something with numGuns
                        disableView(statsNumGunsText);
                        disableView(statsRotationText);
                        disableView(statsDispersionText);
                    } else {
                        disableView(statsArtilleryFireRateText);
                        disableView(statsShellDamageText);
                        disableView(statsNumGunsText);
                        disableView(statsRotationText);
                        disableView(statsDispersionText);
                    }

                    if (info.getAa() != null) {
                        JSONObject slots = info.getAa().optJSONObject("slots");
                        if (slots != null) {
                            if (slots.length() > 0) {
                                StringBuilder strb = new StringBuilder();
                                Iterator<String> itea = slots.keys();
                                double topAARange = 0;
                                while (itea.hasNext()) {
                                    JSONObject aSlot = slots.optJSONObject(itea.next());
                                    double aaRange = aSlot.optDouble("distance");
                                    if (aaRange > topAARange) {
                                        topAARange = aaRange;
                                    }
                                    double avgDmg = aSlot.optDouble("avg_damage");
                                    double caliber = aSlot.optDouble("caliber");
                                    int guns = aSlot.optInt("guns");
                                    strb.append(guns + "  " + Math.round(caliber) + getString(R.string.millimeters) + "  " + Math.round(avgDmg)+ " " + getString(R.string.damage_per_second) + "  " + aaRange + getString(R.string.kilometers));
                                    strb.append("\n");
                                }
                                statsAAGun.setText(strb.toString().trim());
                                statsAARange.setText(topAARange + getString(R.string.kilometers));
                            }
                        }
                    } else {
                        disableView(statsAARangeText);
                        disableView(statsAAGunText);
                    }
                }
                ShipStat stats = CAApp.getInfoManager().getShipStats(getApplicationContext()).get(shipId);
                if (stats != null) {
                    avgDamage.setText("" + Utils.getOneDepthDecimalFormatter().format(stats.getDmg_dlt()));
                    avgWinRate.setText(Utils.getOneDepthDecimalFormatter().format(stats.getWins() * 100) + "%");
                    avgKills.setText("" + Utils.getDefaultDecimalFormatter().format(stats.getFrags()));
                    avgPlanes.setText("" + Utils.getOneDepthDecimalFormatter().format(stats.getPls_kd()));
                } else {
                    disableView(avgDamage);
                    disableView(avgWinRate);
                    disableView(avgKills);
                    disableView(avgPlanes);
                }
            } else {
                getShipInfo();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.encyclopedia_ship_not_found, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String grabMinMax(JSONObject overall, String value, String text){
        JSONObject armorPack = overall.optJSONObject(value);
        String min = armorPack.optString("min");
        String max = armorPack.optString("max");
        return text + " " + min + "-" + max;
    }

    private void animate(ProgressBar bar, int value){
        ProgressBarAnimation anim = new ProgressBarAnimation(bar, 0, value);
        anim.setDuration(1500);
        bar.startAnimation(anim);
    }

    private void createModuleGrid() {
        llModule1.removeAllViews();
        llModule2.removeAllViews();

        llModule1.post(new Runnable() {
            @Override
            public void run() {
                ShipInfo shipInfo = CAApp.getInfoManager().getShipInfo(getApplicationContext()).get(shipId);

                buildDefaultModuleList(shipInfo);

                ShipModuleItem artillery = shipInfo.getItems().get(MODULE_LIST.get(GetShipEncyclopediaInfo.ARTILLERY));
                ShipModuleItem torps = shipInfo.getItems().get(MODULE_LIST.get(GetShipEncyclopediaInfo.TORPEDOES));
                ShipModuleItem fireControl = shipInfo.getItems().get(MODULE_LIST.get(GetShipEncyclopediaInfo.FIRE_CONTROL));
                ShipModuleItem flightControl = shipInfo.getItems().get(MODULE_LIST.get(GetShipEncyclopediaInfo.FLIGHT_CONTROL));
                ShipModuleItem hull = shipInfo.getItems().get(MODULE_LIST.get(GetShipEncyclopediaInfo.HULL));
                ShipModuleItem engine = shipInfo.getItems().get(MODULE_LIST.get(GetShipEncyclopediaInfo.ENGINE));
                ShipModuleItem fighter = shipInfo.getItems().get(MODULE_LIST.get(GetShipEncyclopediaInfo.FIGHTER));
                ShipModuleItem diveBomber = shipInfo.getItems().get(MODULE_LIST.get(GetShipEncyclopediaInfo.DIVE_BOMBER));
                ShipModuleItem torpBomber = shipInfo.getItems().get(MODULE_LIST.get(GetShipEncyclopediaInfo.TORPEDO_BOMBER));

                // Add needed items to list
                List<Boolean> hasOptions = new ArrayList<>();
                List<ShipModuleItem> items = new ArrayList<>();

                addNecessaryModules(shipInfo, artillery, torps, fireControl, flightControl, hull, engine, fighter, diveBomber, torpBomber, hasOptions, items);

//                Dlog.d("ModulesLIST", MODULE_LIST.toString());
//                Dlog.d("Modules", items.toString());
//                Dlog.d("hasOptions", hasOptions.toString());

                boolean hasAnOption = buildModuleLists(hasOptions, items);

                if(hasAnOption){
                    tvModulesBottomText.setVisibility(View.VISIBLE);
                } else {
                    tvModulesBottomText.setVisibility(View.GONE);
                }
            }

            private void addNecessaryModules(ShipInfo shipInfo, ShipModuleItem artillery, ShipModuleItem torps, ShipModuleItem fireControl, ShipModuleItem flightControl, ShipModuleItem hull, ShipModuleItem engine, ShipModuleItem fighter, ShipModuleItem diveBomber, ShipModuleItem torpBomber, List<Boolean> hasOptions, List<ShipModuleItem> items) {
                if(artillery != null) {
                    items.add(artillery);
                    hasOptions.add(shipInfo.getArtillery().size() > 1);
                }
                if(torps != null) {
                    items.add(torps);
                    hasOptions.add(shipInfo.getTorps().size() > 1);
                }
                if(fireControl != null) {
                    items.add(fireControl);
                    hasOptions.add(shipInfo.getFireControl().size() > 1);
                }
                if(flightControl != null) {
                    items.add(flightControl);
                    hasOptions.add(shipInfo.getFlightControl().size() > 1);
                }
                if(hull != null) {
                    items.add(hull);
                    hasOptions.add(shipInfo.getHull().size() > 1);
                }
                if(engine != null) {
                    items.add(engine);
                    hasOptions.add(shipInfo.getEngine().size() > 1);
                }
                if(fighter != null) {
                    items.add(fighter);
                    hasOptions.add(shipInfo.getFighter().size() > 1);
                }
                if(diveBomber != null) {
                    items.add(diveBomber);
                    hasOptions.add(shipInfo.getDiveBomber().size() > 1);
                }
                if(torpBomber != null) {
                    items.add(torpBomber);
                    hasOptions.add(shipInfo.getTorpBomb().size() > 1);
                }
            }

            private void buildDefaultModuleList(ShipInfo shipInfo) {
                if(MODULE_LIST == null){
                    MODULE_LIST = new HashMap<>();
                    MODULE_LIST.put(GetShipEncyclopediaInfo.ARTILLERY, getBaseModuleToList(shipInfo, shipInfo.getArtillery()));
                    MODULE_LIST.put(GetShipEncyclopediaInfo.TORPEDOES, getBaseModuleToList(shipInfo, shipInfo.getTorps()));
                    MODULE_LIST.put(GetShipEncyclopediaInfo.FIRE_CONTROL, getBaseModuleToList(shipInfo, shipInfo.getFireControl()));
                    MODULE_LIST.put(GetShipEncyclopediaInfo.FLIGHT_CONTROL, getBaseModuleToList(shipInfo, shipInfo.getFlightControl()));
                    MODULE_LIST.put(GetShipEncyclopediaInfo.HULL, getBaseModuleToList(shipInfo, shipInfo.getHull()));
                    MODULE_LIST.put(GetShipEncyclopediaInfo.ENGINE, getBaseModuleToList(shipInfo, shipInfo.getEngine()));
                    MODULE_LIST.put(GetShipEncyclopediaInfo.FIGHTER, getBaseModuleToList(shipInfo, shipInfo.getFighter()));
                    MODULE_LIST.put(GetShipEncyclopediaInfo.DIVE_BOMBER, getBaseModuleToList(shipInfo, shipInfo.getDiveBomber()));
                    MODULE_LIST.put(GetShipEncyclopediaInfo.TORPEDO_BOMBER, getBaseModuleToList(shipInfo, shipInfo.getTorpBomb()));
                }
            }

            private boolean buildModuleLists(List<Boolean> hasOptions, List<ShipModuleItem> items) {
                boolean hasAnOption = false;
                for(int i = 0; i < items.size(); i++){

                    LinearLayout parent = llModule1;
                    if(i % 2 == 1)
                        parent = llModule2;

                    View convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_ship_module, parent, false);
                    TextView tv = (TextView) convertView.findViewById(R.id.list_module_top);
                    TextView tvText = (TextView) convertView.findViewById(R.id.list_module_text);

                    ShipModuleItem item = items.get(i);
                    boolean hasOpt = hasOptions.get(i);

                    if(hasOpt){
                        hasAnOption = true;
                        convertView.setBackgroundResource(R.drawable.encyclopedia_module_white);
                    } else {
                        convertView.setBackgroundResource(R.drawable.compare_normal_grid);
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append(item.getName());
                    if(!item.isDefault()){
                        DecimalFormat format = new DecimalFormat(PATTERN);
                        if(item.getPrice_xp() > 0)
                            sb.append("\n"+ format.format(item.getPrice_xp()) + "xp");
                        if(item.getPrice_credits() > 0)
                            sb.append("\n"+ format.format(item.getPrice_credits()) + "c");
                    }

                    tvText.setText(sb.toString());

                    cleanModuleTitle(tv, item.getType());

                    convertView.setTag(item.getId());

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Long id = (Long) v.getTag();
                            if(id != null){
                                final ShipInfo shipInfo = CAApp.getInfoManager().getShipInfo(getApplicationContext()).get(shipId);
                                ShipModuleItem item = shipInfo.getItems().get(id);
                                List<Long> typeIds = new ArrayList<Long>();
                                switch (item.getType()){
                                    case "Suo":
                                        typeIds = shipInfo.getFireControl();
                                        break;
                                    case "FlightControl":
                                        typeIds = shipInfo.getFlightControl();
                                        break;
                                    case "DiveBomber":
                                        typeIds = shipInfo.getDiveBomber();
                                        break;
                                    case "Fighter":
                                        typeIds = shipInfo.getFighter();
                                        break;
                                    case "Artillery":
                                        typeIds = shipInfo.getArtillery();
                                        break;
                                    case "Hull":
                                        typeIds = shipInfo.getHull();
                                        break;
                                    case "TorpedoBomber":
                                        typeIds = shipInfo.getTorpBomb();
                                        break;
                                    case "Torpedoes":
                                        typeIds = shipInfo.getTorps();
                                        break;
                                    case "Engine":
                                        typeIds = shipInfo.getEngine();
                                        break;
                                }
                                Collections.sort(typeIds, new Comparator<Long>() {
                                    @Override
                                    public int compare(Long lhs, Long rhs) {
                                        ShipModuleItem lhsItem = shipInfo.getItems().get(lhs);
                                        ShipModuleItem rhsItem = shipInfo.getItems().get(rhs);
                                        return lhsItem.getName().compareToIgnoreCase(rhsItem.getName());
                                    }
                                });
                                if(typeIds.size() > 1) {
                                    PopupMenu menu = new PopupMenu(ShipProfileActivity.this, v);
                                    menu.setGravity(Gravity.CENTER);
                                    final Map<String, ShipModuleItem> mapOfItems = new HashMap<String, ShipModuleItem>();
                                    Menu m = menu.getMenu();
                                    for (int i = 0; i < typeIds.size(); i++){
                                        ShipModuleItem it = shipInfo.getItems().get(typeIds.get(i));
                                        if(it != null) {
                                            m.add(it.getName());
                                            mapOfItems.put(it.getName(), it);
                                        }
                                    }

                                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            ShipModuleItem i = mapOfItems.get(item.getTitle());
                                            switch (i.getType()){
                                                case "Suo":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.FIRE_CONTROL, i.getId());
                                                    break;
                                                case "FlightControl":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.FLIGHT_CONTROL, i.getId());
                                                    break;
                                                case "DiveBomber":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.DIVE_BOMBER, i.getId());
                                                    break;
                                                case "Fighter":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.FIGHTER, i.getId());
                                                    break;
                                                case "Artillery":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.ARTILLERY, i.getId());
                                                    break;
                                                case "Hull":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.HULL, i.getId());
                                                    break;
                                                case "TorpedoBomber":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.TORPEDO_BOMBER, i.getId());
                                                    break;
                                                case "Torpedoes":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.TORPEDOES, i.getId());
                                                    break;
                                                case "Engine":
                                                    MODULE_LIST.put(GetShipEncyclopediaInfo.ENGINE, i.getId());
                                                    break;
                                            }

                                            //Display list of options and update the view.
                                            clearScreen();
                                            //clear data from screen
                                            getShipInfo();
                                            return false;
                                        }
                                    });
                                    menu.show();
                                }

                            }
                        }
                    });

                    parent.addView(convertView);
                }
                return hasAnOption;
            }
        });
    }

    private void clearScreen() {
        try {
            scroll.smoothScrollTo(0,0);
        } catch (Exception e) {
        }

        llModule1.removeAllViews();
        llModule2.removeAllViews();

        progAA.setProgress(0);
        progAircraft.setProgress(0);
        progArtillery.setProgress(0);
        progConcealment.setProgress(0);
        progMobility.setProgress(0);
        progSurvival.setProgress(0);
        progTorps.setProgress(0);

        modAA.setText("");
        modAircraft.setText("");
        modArtilery.setText("");
        modConcealmeat.setText("");
        modMobility.setText("");
        modSurvival.setText("");
        modTorps.setText("");

        avgDamage.setText("");
        avgKills.setText("");
        avgPlanes.setText("");
        avgWinRate.setText("");

        statsAARange.setText("");
        statsArtilleryFireRate.setText("");
        statsConcealRange.setText("");
        statsConcealRangePlane.setText("");
        statsGunRange.setText("");
        statsHealth.setText("");
        statsNumGuns.setText("");
        statstorpRange.setText("");
        statsTorpSpeed.setText("");
        statsSecondaryRange.setText("");
        statsShellDamage.setText("");
        statsSpeed.setText("");
        statsTorpFireRate.setText("");
        statsNumTorps.setText("");
        statsNumPlanes.setText("");
        statsRudderShiftTime.setText("");
        statsTurningRadius.setText("");
        statsAAGun.setText("");
        statsArmor.setText("");
    }

    private void cleanModuleTitle(TextView tv, String title){
        switch (title){
            case "Suo":
                title = getString(R.string.fire_control);
                break;
            case "FlightControl":
                title = getString(R.string.flight_control);
                break;
            case "TorpedoBomber":
                title = getString(R.string.torpedo_bomber);
                break;
            case "DiveBomber":
                title = getString(R.string.dive_bomber);
                break;
        }
        tv.setText(title);
    }

    private long getBaseModuleToList(ShipInfo info, List<Long> modules){
        if(modules != null && modules.size() > 0) {
            for (int i = 0; i < modules.size(); i++) {
                ShipModuleItem item = info.getItems().get(modules.get(i));
                if (item.isDefault()) {
                    return item.getId();
                }
            }
            return modules.get(0);
        } else
            return 0;
    }

    private void disableView(TextView tv) {
        tv.setAlpha(0.5f);
    }

    private void createNextShip(ShipInfo shipInfo) {
        llNextShips.removeAllViews();
        final List<Long> shipIds = shipInfo.getNextShipIds();
        llNextShips.post(new Runnable() {
            @Override
            public void run() {
                for (Long l : shipIds) {
                    ShipInfo info = CAApp.getInfoManager().getShipInfo(getApplicationContext()).get(l);
                    if (info != null) {
                        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_next_ship, llNextShips, false);
                        ImageView image = (ImageView) view.findViewById(R.id.list_next_ship_image);
                        TextView text = (TextView) view.findViewById(R.id.list_next_ship_text);

                        text.setText(info.getName());
                        Picasso.get().load(info.getImage()).error(R.drawable.ic_missing_image).into(image);

                        view.setTag(l);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                long id = (long) v.getTag();
                                Intent i = new Intent(getApplicationContext(), ShipProfileActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra(ShipProfileActivity.SHIP_ID, id);
                                startActivity(i);
                            }
                        });
                        llNextShips.addView(view);
                    }
                }
            }
        });
    }

    private void createUpgrades(ShipInfo shipInfo) {
        llUpgrades.removeAllViews();
        final List<Long> equipeIds = shipInfo.getEquipments();
        llUpgrades.post(new Runnable() {
            @Override
            public void run() {
                for (Long l : equipeIds) {
                    EquipmentInfo info = CAApp.getInfoManager().getUpgrades(getApplicationContext()).get(l);
                    if (info != null) {
                        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_upgrades, llNextShips, false);
                        ImageView image = (ImageView) view.findViewById(R.id.list_upgrades_image);
                        Picasso.get().load(info.getImage()).error(R.drawable.ic_missing_image).into(image);
                        view.setTag(l);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                long id = (long) v.getTag();
                                EquipmentInfo info = CAApp.getInfoManager().getUpgrades(getApplicationContext()).get(id);
                                Context ctx = ShipProfileActivity.this;
                                DecimalFormat formatter = new DecimalFormat(PATTERN);
                                Alert.createGeneralAlert(ctx, info.getName(), info.getDescription() + getString(R.string.encyclopedia_upgrade_cost) + formatter.format(info.getPrice()), getString(R.string.dismiss));
                            }
                        });
                        llUpgrades.addView(view);
                    }
                }
            }
        });
    }

    private void getShipInfo() {
        progress.setVisibility(View.VISIBLE);
        ShipQuery query = new ShipQuery();
        query.setServer(CAApp.getServerType(getApplicationContext()));
        query.setShipId(shipId);
        query.setLanguage(CAApp.getServerLanguage(getApplicationContext()));
        query.setModules(MODULE_LIST);

        GetShipEncyclopediaInfo async = new GetShipEncyclopediaInfo();
        async.execute(query);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SHIP_ID, shipId);
        outState.putString(SHIP_DATA, shipServerInfo);
    }

    @Subscribe
    public void onShipRecieveInfo(ShipResult result) {
        if (result.getShipInfo() != null && result.getShipId() == shipId) {
            shipServerInfo = result.getShipInfo();
            progress.post(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            });
        } else if(result.getShipId() == shipId){
            progress.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.failed_to_grab_data, Toast.LENGTH_SHORT).show();
                    shipServerInfo = "fail";
                    initView();
                }
            });
        }
    }
}
