package com.half.wowsca.ui.viewcaptain.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.interfaces.ICaptain;
import com.half.wowsca.model.BatteryStats;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.events.CaptainReceivedEvent;
import com.half.wowsca.model.events.ProgressEvent;
import com.half.wowsca.model.events.RefreshEvent;
import com.half.wowsca.ui.CAFragment;
import com.half.wowsca.ui.UIUtils;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.Utils;
import com.utilities.logging.Dlog;

import java.text.DecimalFormat;

/**
 * Created by slai4 on 1/20/2016.
 */
public class CaptainTopShipInfoFragment extends CAFragment {

    private TextView tvArmamentMain;
    private TextView tvArmamentTorps;
    private TextView tvArmamentAircraft;
    private TextView tvArmamentOthers;

    private ImageView ivTopKills;
    private TextView tvTopKills;
    private TextView tvTopKillsName;

    private ImageView ivTopDamage;
    private TextView tvTopDamage;
    private TextView tvTopDamageName;

    private ImageView ivTopXP;
    private TextView tvTopXP;
    private TextView tvTopXPName;

    private ImageView ivTopPlanes;
    private TextView tvTopPlanes;
    private TextView tvTopPlanesName;

    private ImageView ivTopSurvivalRate;
    private TextView tvTopSurvivalRate;
    private TextView tvTopSurvivalRateName;

    private ImageView ivTopWinRate;
    private TextView tvTopWinRate;
    private TextView tvTopWinRateName;

    private ImageView ivTopTotalKills;
    private TextView tvTopTotalKills;
    private TextView tvTopTotalKillsName;

    private ImageView ivTopAvgDmg;
    private TextView tvTopAvgDmg;
    private TextView tvTopAvgDmgName;

    private ImageView ivTopPlayed;
    private TextView tvTopPlayed;
    private TextView tvTopPlayedName;

    private ImageView ivTopDistance;
    private TextView tvTopDistance;
    private TextView tvTopDistanceName;

    private ImageView ivTopTotalDmg;
    private TextView tvTopTotalDmg;
    private TextView tvTopTotalDmgName;

    private ImageView ivTopTotalExp;
    private TextView tvTopTotalExp;
    private TextView tvTopTotalExpName;

    private ImageView ivTopSurvivedWins;
    private TextView tvTopSurvivedWins;
    private TextView tvTopSurvivedWinsName;

    private ImageView ivTopTotalPlanes;
    private TextView tvTopTotalPlanes;
    private TextView tvTopTotalPlanesName;

    private ImageView ivTopCARating;
    private TextView tvTopCaRating;
    private TextView tvTopCARatingName;

    private ImageView ivTopMBAccuracy;
    private TextView tvTopMBAccuracy;
    private TextView tvTopMBAccuracyName;

    private ImageView ivTopTBAccuracy;
    private TextView tvTopTBAccuracy;
    private TextView tvTopTBAccuracyName;

    private ImageView ivTopSpotted;
    private TextView tvTopSpotted;
    private TextView tvTopSpottedName;

    private ImageView ivTopScoutingDamage;
    private TextView tvTopScoutingDamage;
    private TextView tvTopScoutingDamageName;

    private ImageView ivTopTanking;
    private TextView tvTopTanking;
    private TextView tvTopTankingName;

    private ImageView ivTopTorpTanking;
    private TextView tvTopTorpTanking;
    private TextView tvTopTorpTankingName;

    private TextView tvBatteryMainDestroyed;
    private TextView tvBatteryMainHitRatio;
    private TextView tvBatteryMainShots;
    private ImageView ivBatteryMain;
    private TextView tvBatteryMainShipName;
    private TextView tvBatteryMainShipNumber;

    private TextView tvBatterySecondaryDestroyed;
    private TextView tvBatterySecondaryHitRatio;
    private TextView tvBatterySecondaryShots;
    private ImageView ivBatterySecondary;
    private TextView tvBatterySecondaryShipName;
    private TextView tvBatterySecondaryShipNumber;

    private TextView tvBatteryTorpsDestroyed;
    private TextView tvBatteryTorpsHitRatio;
    private TextView tvBatteryTorpsShots;
    private ImageView ivBatteryTorps;
    private TextView tvBatteryTorpsShipName;
    private TextView tvBatteryTorpsShipNumber;

    private TextView tvBatteryAircraftDestroyed;
    private TextView tvBatteryAircraftHitRatio;
    private TextView tvBatteryAircraftShots;
    private ImageView ivBatteryAircraft;
    private TextView tvBatteryAircraftShipName;
    private TextView tvBatteryAircraftShipNumber;

    private TextView tvBatteryRammingDestroyed;
    private TextView tvBatteryRammingHitRatio;
    private TextView tvBatteryRammingShots;
    private ImageView ivBatteryRamming;
    private TextView tvBatteryRammingShipName;
    private TextView tvBatteryRammingShipNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_captain_top_ship_info, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        tvArmamentMain = (TextView) view.findViewById(R.id.captain_details_battery_kills_main);
        tvArmamentAircraft = (TextView) view.findViewById(R.id.captain_details_battery_kills_aircraft);
        tvArmamentOthers = (TextView) view.findViewById(R.id.captain_details_battery_kills_other);
        tvArmamentTorps = (TextView) view.findViewById(R.id.captain_details_battery_kills_torps);

//        if(CAApp.isLightTheme(view.getContext())){
//            ((ImageView) view.findViewById(R.id.captain_details_battery_kills_main_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_details_battery_kills_aircraft_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_details_battery_kills_torps_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_details_battery_kills_other_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//        }

        ivTopKills = (ImageView) view.findViewById(R.id.captain_details_top_kills_icon);
        tvTopKills = (TextView) view.findViewById(R.id.captain_details_top_kills_text);
        tvTopKillsName = (TextView) view.findViewById(R.id.captain_details_top_kills_name_text);

        ivTopDamage = (ImageView) view.findViewById(R.id.captain_details_top_damage_icon);
        tvTopDamage = (TextView) view.findViewById(R.id.captain_details_top_damage_text);
        tvTopDamageName = (TextView) view.findViewById(R.id.captain_details_top_damage_name_text);

        ivTopXP = (ImageView) view.findViewById(R.id.captain_details_top_xp_icon);
        tvTopXP = (TextView) view.findViewById(R.id.captain_details_top_xp_text);
        tvTopXPName = (TextView) view.findViewById(R.id.captain_details_top_xp_name_text);

        ivTopPlanes = (ImageView) view.findViewById(R.id.captain_details_top_planes_icon);
        tvTopPlanes = (TextView) view.findViewById(R.id.captain_details_top_planes_text);
        tvTopPlanesName = (TextView) view.findViewById(R.id.captain_details_top_planes_name_text);

        //new ones
        ivTopSurvivalRate = (ImageView) view.findViewById(R.id.captain_details_top_survival_rate_icon);
        tvTopSurvivalRate = (TextView) view.findViewById(R.id.captain_details_top_survival_rate_text);
        tvTopSurvivalRateName = (TextView) view.findViewById(R.id.captain_details_top_survival_rate_name_text);

        ivTopWinRate = (ImageView) view.findViewById(R.id.captain_details_top_win_rate_icon);
        tvTopWinRate = (TextView) view.findViewById(R.id.captain_details_top_win_rate_text);
        tvTopWinRateName = (TextView) view.findViewById(R.id.captain_details_top_win_rate_name_text);

        ivTopPlayed = (ImageView) view.findViewById(R.id.captain_details_top_played_icon);
        tvTopPlayed = (TextView) view.findViewById(R.id.captain_details_top_played_text);
        tvTopPlayedName = (TextView) view.findViewById(R.id.captain_details_top_played_name_text);

        ivTopAvgDmg = (ImageView) view.findViewById(R.id.captain_details_top_average_dmg_icon);
        tvTopAvgDmg = (TextView) view.findViewById(R.id.captain_details_top_average_dmg_text);
        tvTopAvgDmgName = (TextView) view.findViewById(R.id.captain_details_top_average_dmg_name_text);

        ivTopTotalKills = (ImageView) view.findViewById(R.id.captain_details_top_total_kill_icon);
        tvTopTotalKills = (TextView) view.findViewById(R.id.captain_details_top_total_kill_text);
        tvTopTotalKillsName = (TextView) view.findViewById(R.id.captain_details_top_total_kill_name_text);

        ivTopDistance = (ImageView) view.findViewById(R.id.captain_details_top_distance_traveled_icon);
        tvTopDistance = (TextView) view.findViewById(R.id.captain_details_top_distance_traveled_text);
        tvTopDistanceName = (TextView) view.findViewById(R.id.captain_details_top_distance_traveled_name_text);

        ivTopTotalDmg = (ImageView) view.findViewById(R.id.captain_details_top_total_damage_icon);
        tvTopTotalDmg = (TextView) view.findViewById(R.id.captain_details_top_total_damage_text);
        tvTopTotalDmgName = (TextView) view.findViewById(R.id.captain_details_top_total_damage_name_text);

        ivTopTotalExp = (ImageView) view.findViewById(R.id.captain_details_top_total_exp_icon);
        tvTopTotalExp = (TextView) view.findViewById(R.id.captain_details_top_total_exp_text);
        tvTopTotalExpName = (TextView) view.findViewById(R.id.captain_details_top_total_exp_name_text);

        ivTopTotalPlanes = (ImageView) view.findViewById(R.id.captain_details_top_total_planes_icon);
        tvTopTotalPlanes = (TextView) view.findViewById(R.id.captain_details_top_total_planes_text);
        tvTopTotalPlanesName = (TextView) view.findViewById(R.id.captain_details_top_total_planes_name_text);

        ivTopSurvivedWins = (ImageView) view.findViewById(R.id.captain_details_top_survived_wins_icon);
        tvTopSurvivedWins = (TextView) view.findViewById(R.id.captain_details_top_survived_wins_text);
        tvTopSurvivedWinsName = (TextView) view.findViewById(R.id.captain_details_top_survived_wins_name_text);

        ivTopCARating = (ImageView) view.findViewById(R.id.captain_details_top_ca_rating_icon);
        tvTopCaRating = (TextView) view.findViewById(R.id.captain_details_top_ca_rating_text);
        tvTopCARatingName = (TextView) view.findViewById(R.id.captain_details_top_ca_rating_name_text);

        ivTopMBAccuracy = (ImageView) view.findViewById(R.id.captain_details_top_mb_acc_icon);
        tvTopMBAccuracy = (TextView) view.findViewById(R.id.captain_details_top_mb_acc_text);
        tvTopMBAccuracyName = (TextView) view.findViewById(R.id.captain_details_top_mb_acc_name_text);

        ivTopTBAccuracy = (ImageView) view.findViewById(R.id.captain_details_top_tb_acc_icon);
        tvTopTBAccuracy = (TextView) view.findViewById(R.id.captain_details_top_tb_acc_text);
        tvTopTBAccuracyName = (TextView) view.findViewById(R.id.captain_details_top_tb_acc_name_text);

        ivTopSpotted = (ImageView) view.findViewById(R.id.captain_details_top_spotted_icon);
        tvTopSpotted = (TextView) view.findViewById(R.id.captain_details_top_spotted_text);
        tvTopSpottedName = (TextView) view.findViewById(R.id.captain_details_top_spotted_name_text);

        ivTopScoutingDamage = (ImageView) view.findViewById(R.id.captain_details_top_spotting_damage_icon);
        tvTopScoutingDamage = (TextView) view.findViewById(R.id.captain_details_top_spotting_damage_text);
        tvTopScoutingDamageName = (TextView) view.findViewById(R.id.captain_details_top_spotting_damage_name_text);

        ivTopTanking = (ImageView) view.findViewById(R.id.captain_details_top_tanking_icon);
        tvTopTanking = (TextView) view.findViewById(R.id.captain_details_top_tanking_text);
        tvTopTankingName = (TextView) view.findViewById(R.id.captain_details_top_tanking_name_text);

        ivTopTorpTanking = (ImageView) view.findViewById(R.id.captain_details_top_torp_tanking_icon);
        tvTopTorpTanking = (TextView) view.findViewById(R.id.captain_details_top_torp_tanking_text);
        tvTopTorpTankingName = (TextView) view.findViewById(R.id.captain_details_top_torp_tanking_name_text);

        tvBatteryMainDestroyed = (TextView) view.findViewById(R.id.captain_details_main_destroyed);
        tvBatteryMainHitRatio = (TextView) view.findViewById(R.id.captain_details_main_hit_ratio);
        tvBatteryMainShots = (TextView) view.findViewById(R.id.captain_details_main_shots);
        ivBatteryMain = (ImageView) view.findViewById(R.id.captain_details_main_ship_icon);
        tvBatteryMainShipName = (TextView) view.findViewById(R.id.captain_details_main_ship_name);
        tvBatteryMainShipNumber = (TextView) view.findViewById(R.id.captain_details_main_ship_number);

        tvBatterySecondaryDestroyed = (TextView) view.findViewById(R.id.captain_details_secondary_destroyed);
        tvBatterySecondaryHitRatio = (TextView) view.findViewById(R.id.captain_details_secondary_hit_ratio);
        tvBatterySecondaryShots = (TextView) view.findViewById(R.id.captain_details_secondary_shots);
        ivBatterySecondary = (ImageView) view.findViewById(R.id.captain_details_secondary_ship_icon);
        tvBatterySecondaryShipName = (TextView) view.findViewById(R.id.captain_details_secondary_ship_name);
        tvBatterySecondaryShipNumber = (TextView) view.findViewById(R.id.captain_details_secondary_ship_number);

        tvBatteryTorpsDestroyed = (TextView) view.findViewById(R.id.captain_details_torps_destroyed);
        tvBatteryTorpsHitRatio = (TextView) view.findViewById(R.id.captain_details_torps_hit_ratio);
        tvBatteryTorpsShots = (TextView) view.findViewById(R.id.captain_details_torps_shots);
        ivBatteryTorps = (ImageView) view.findViewById(R.id.captain_details_torps_ship_icon);
        tvBatteryTorpsShipName = (TextView) view.findViewById(R.id.captain_details_torps_ship_name);
        tvBatteryTorpsShipNumber = (TextView) view.findViewById(R.id.captain_details_torps_ship_number);

        tvBatteryAircraftDestroyed = (TextView) view.findViewById(R.id.captain_details_aircraft_destroyed);
        tvBatteryAircraftHitRatio = (TextView) view.findViewById(R.id.captain_details_aircraft_hit_ratio);
        tvBatteryAircraftShots = (TextView) view.findViewById(R.id.captain_details_aircraft_shots);
        ivBatteryAircraft = (ImageView) view.findViewById(R.id.captain_details_aircraft_ship_icon);
        tvBatteryAircraftShipName = (TextView) view.findViewById(R.id.captain_details_aircraft_ship_name);
        tvBatteryAircraftShipNumber = (TextView) view.findViewById(R.id.captain_details_aircraft_ship_number);

        tvBatteryRammingDestroyed = (TextView) view.findViewById(R.id.captain_details_ramming_destroyed);
        tvBatteryRammingHitRatio = (TextView) view.findViewById(R.id.captain_details_ramming_hit_ratio);
        tvBatteryRammingShots = (TextView) view.findViewById(R.id.captain_details_ramming_shots);
        ivBatteryRamming = (ImageView) view.findViewById(R.id.captain_details_ramming_ship_icon);
        tvBatteryRammingShipName = (TextView) view.findViewById(R.id.captain_details_ramming_ship_name);
        tvBatteryRammingShipNumber = (TextView) view.findViewById(R.id.captain_details_ramming_ship_number);

        UIUtils.setUpCard(view, R.id.captain_details_top_damage_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_kills_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_exp_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_planes_area);


        UIUtils.setUpCard(view, R.id.captain_details_top_average_dmg_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_total_kill_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_survival_rate_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_win_rate_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_played_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_distance_traveled_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_ca_rating_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_mb_acc_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_tb_acc_area);

        UIUtils.setUpCard(view, R.id.captain_details_top_spotted_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_spotting_damage_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_tanking_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_torp_tanking_area);

        UIUtils.setUpCard(view, R.id.captain_details_top_total_damage_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_total_exp_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_total_planes_area);
        UIUtils.setUpCard(view, R.id.captain_details_top_survived_wins_area);

        UIUtils.setUpCard(view, R.id.captain_details_main_battery_area);
        UIUtils.setUpCard(view, R.id.captain_details_secondary_area);
        UIUtils.setUpCard(view, R.id.captain_details_torps_area);
        UIUtils.setUpCard(view, R.id.captain_details_aircraft_area);
        UIUtils.setUpCard(view, R.id.captain_details_ramming_area);

        bindSwipe(view);
        initSwipeLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }

    private void initView() {

        Captain captain = null;
        try {
            captain = ((ICaptain) getActivity()).getCaptain(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (captain != null && captain.getDetails() != null && captain.getDetails().getBattles() > 0) {

            refreshing(false);

            //armament used

            BatteryStats mainBatteryStats = captain.getDetails().getMainBattery();
            BatteryStats torpStats = captain.getDetails().getTorpedoes();
            BatteryStats aircraftStats = captain.getDetails().getAircraft();

            tvArmamentMain.setText("" + mainBatteryStats.getFrags());
            tvArmamentTorps.setText("" + torpStats.getFrags());
            tvArmamentAircraft.setText("" + aircraftStats.getFrags());
            int others = captain.getDetails().getFrags() - mainBatteryStats.getFrags() - torpStats.getFrags() - aircraftStats.getFrags();
            tvArmamentOthers.setText("" + others);
            setUpTopArea(captain);
            setUpBatteryInfo(captain);
        }
    }

    private void setUpTopArea(Captain captain) {
        boolean highDefImage = tvTopKillsName.getContext().getResources().getBoolean(R.bool.high_def_images);

        //kills
        ShipInfo topKillsShip = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxFragsInBattleShipId());
        setTopShipImageName(topKillsShip, tvTopKillsName, ivTopKills, highDefImage);
        tvTopKills.setText("" + captain.getDetails().getMaxFragsInBattle());

        //damage
        ShipInfo topDamageShip = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxDamageShipId());
        setTopShipImageName(topDamageShip, tvTopDamageName, ivTopDamage, highDefImage);
        tvTopDamage.setText("" + captain.getDetails().getMaxDamage());

        //xp
        ShipInfo topXPShip = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxXPShipId());
        setTopShipImageName(topXPShip, tvTopXPName, ivTopXP, highDefImage);
        tvTopXP.setText("" + captain.getDetails().getMaxXP());

        //planes
        ShipInfo topPlanesShip = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxPlanesKilledShipId());
        setTopShipImageName(topPlanesShip, tvTopPlanesName, ivTopPlanes, highDefImage);
        tvTopPlanes.setText("" + captain.getDetails().getMaxPlanesKilled());

        // survival Rate
        ShipInfo topSurvivalRate = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxSurvivalRateShipId());
        setTopShipImageName(topSurvivalRate, tvTopSurvivalRateName, ivTopSurvivalRate, highDefImage);
        tvTopSurvivalRate.setText(Utils.getOneDepthDecimalFormatter().format(captain.getDetails().getMaxSurvivalRate() * 100) + "%");

        // win rate
        ShipInfo topWinRate = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxWinRateShipId());
        setTopShipImageName(topWinRate, tvTopWinRateName, ivTopWinRate, highDefImage);
        tvTopWinRate.setText(Utils.getOneDepthDecimalFormatter().format(captain.getDetails().getMaxWinRate() * 100) + "%");

        // average dmg
        ShipInfo topAvgDmg = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxAvgDmgShipId());
        setTopShipImageName(topAvgDmg, tvTopAvgDmgName, ivTopAvgDmg, highDefImage);
        tvTopAvgDmg.setText((int) captain.getDetails().getMaxAvgDmg() + "");

        // total kills
        ShipInfo topKills = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxTotalKillsShipId());
        setTopShipImageName(topKills, tvTopTotalKillsName, ivTopTotalKills, highDefImage);
        tvTopTotalKills.setText(captain.getDetails().getMaxTotalKills() + "");

        // total battles
        ShipInfo topPlayed = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxPlayedShipId());
        setTopShipImageName(topPlayed, tvTopPlayedName, ivTopPlayed, highDefImage);
        tvTopPlayed.setText(captain.getDetails().getMaxPlayed() + "");

        // total planes
        ShipInfo topPlanes = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxTotalPlanesShipId());
        setTopShipImageName(topPlanes, tvTopTotalPlanesName, ivTopTotalPlanes, highDefImage);
        tvTopTotalPlanes.setText((int) captain.getDetails().getMaxTotalPlanes() + "");

        // total damage
        ShipInfo topDamage = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxTotalDmgShipId());
        setTopShipImageName(topDamage, tvTopTotalDmgName, ivTopTotalDmg, highDefImage);
        tvTopTotalDmg.setText((int) captain.getDetails().getMaxTotalDamage() + "");

        // total exp
        ShipInfo topXp = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxTotalExpShipId());
        setTopShipImageName(topXp, tvTopTotalExpName, ivTopTotalExp, highDefImage);
        tvTopTotalExp.setText((int) captain.getDetails().getMaxTotalExp() + "");

        // total exp
        ShipInfo topSurvivedWins = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxSurvivedWinsShipId());
        setTopShipImageName(topSurvivedWins, tvTopSurvivedWinsName, ivTopSurvivedWins, highDefImage);
        tvTopSurvivedWins.setText(Utils.getOneDepthDecimalFormatter().format(captain.getDetails().getMaxSurvivedWins() * 100) + "%");

        // distance
        ShipInfo topDistanceTraveled = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxMostTraveledShipId());
        setTopShipImageName(topDistanceTraveled, tvTopDistanceName, ivTopDistance, highDefImage);
        float kilos = captain.getDetails().getMaxMostTraveled() * 1.60934f; //kilos
        DecimalFormat format = new DecimalFormat("###,###,###");
        tvTopDistance.setText(format.format(kilos) + "km");

        // total ca
        ShipInfo topCARating = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxCARatingShipId());
        setTopShipImageName(topCARating, tvTopCARatingName, ivTopCARating, highDefImage);
        tvTopCaRating.setText((int) captain.getDetails().getMaxCARating() + "");

        // total mb accuracy
        ShipInfo topMBAccRating = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxMBAccuracyShipId());
        setTopShipImageName(topMBAccRating, tvTopMBAccuracyName, ivTopMBAccuracy, highDefImage);
        tvTopMBAccuracy.setText(Utils.getOneDepthDecimalFormatter().format(captain.getDetails().getMaxMBAccuracy() * 100) + "%");

        // total tb accuracy
        ShipInfo topTBAccRating = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxTBAccuracyShipId());
        setTopShipImageName(topTBAccRating, tvTopTBAccuracyName, ivTopTBAccuracy, highDefImage);
        tvTopTBAccuracy.setText(Utils.getOneDepthDecimalFormatter().format(captain.getDetails().getMaxTBAccuracy() * 100) + "%");

        ShipInfo topSpottingRating = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxSpottedShipId());
        setTopShipImageName(topSpottingRating, tvTopSpottedName, ivTopSpotted, highDefImage);
        tvTopSpotted.setText(Utils.getOneDepthDecimalFormatter().format(captain.getDetails().getMaxSpotted()) + "");

        ShipInfo topSpottingDamage = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxDamageScoutingShipId());
        setTopShipImageName(topSpottingDamage, tvTopScoutingDamageName, ivTopScoutingDamage, highDefImage);
        tvTopScoutingDamage.setText(Utils.getOneDepthDecimalFormatter().format(captain.getDetails().getMaxDamageScouting()) + "");

        ShipInfo topTanking = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxArgoDamageShipId());
        setTopShipImageName(topTanking, tvTopTankingName, ivTopTanking, highDefImage);
        tvTopTanking.setText(Utils.getOneDepthDecimalFormatter().format(captain.getDetails().getMaxTotalArgo()) + "");

        Dlog.d("CaptainTopShip","torp = " + captain.getDetails().getMaxTorpArgoDamageShipId());
        ShipInfo topTropArgo = CAApp.getInfoManager().getShipInfo(getContext()).get(captain.getDetails().getMaxTorpArgoDamageShipId());
        setTopShipImageName(topTropArgo, tvTopTorpTankingName, ivTopTorpTanking, highDefImage);
        tvTopTorpTanking.setText(Utils.getOneDepthDecimalFormatter().format(captain.getDetails().getMaxTorpTotalArgo()) + "");
    }

    private void setTopShipImageName(ShipInfo shipInfo, TextView shipName, ImageView image, boolean highDef) {
        if (shipInfo != null) {
            UIUtils.setShipImage(image, shipInfo);
            shipName.setText(shipInfo.getName());
        }
    }

    private void setUpBatteryInfo(Captain captain) {
        //main battery
        setBatteryStatistics(captain.getDetails().getMainBattery(), tvBatteryMainDestroyed, tvBatteryMainHitRatio,
                tvBatteryMainShots, ivBatteryMain, tvBatteryMainShipName, tvBatteryMainShipNumber);

        //secondaries
        setBatteryStatistics(captain.getDetails().getSecondaryBattery(), tvBatterySecondaryDestroyed, tvBatterySecondaryHitRatio,
                tvBatterySecondaryShots, ivBatterySecondary, tvBatterySecondaryShipName, tvBatterySecondaryShipNumber);

        //torpedoes
        setBatteryStatistics(captain.getDetails().getTorpedoes(), tvBatteryTorpsDestroyed, tvBatteryTorpsHitRatio,
                tvBatteryTorpsShots, ivBatteryTorps, tvBatteryTorpsShipName, tvBatteryTorpsShipNumber);

        //aircraft
        setBatteryStatistics(captain.getDetails().getAircraft(), tvBatteryAircraftDestroyed, tvBatteryAircraftHitRatio,
                tvBatteryAircraftShots, ivBatteryAircraft, tvBatteryAircraftShipName, tvBatteryAircraftShipNumber);

        //ramming
        setBatteryStatistics(captain.getDetails().getRamming(), tvBatteryRammingDestroyed, tvBatteryRammingHitRatio,
                tvBatteryRammingShots, ivBatteryRamming, tvBatteryRammingShipName, tvBatteryRammingShipNumber);
    }

    private void setBatteryStatistics(BatteryStats batteryStats, TextView destroyed, TextView hitratio, TextView shots, ImageView imageView, TextView shipName, TextView shipNumber) {
        destroyed.setText("" + batteryStats.getFrags());
        float hitRatio = 0;
        if (batteryStats.getShots() != 0) {
            hitRatio = (batteryStats.getHits() / (float) batteryStats.getShots()) * 100;
        }
        hitratio.setText(Utils.getOneDepthDecimalFormatter().format(hitRatio) + "%");
        shots.setText("" + batteryStats.getShots());
        shipNumber.setText("" + batteryStats.getMaxFrags());
        ShipInfo ship = CAApp.getInfoManager().getShipInfo(getContext()).get(batteryStats.getMaxFragsShipId());
        if (ship != null) {
            UIUtils.setShipImage(imageView, ship);
            shipName.setText("" + ship.getName());
        } else {
            imageView.setImageResource(R.color.transparent);
            shipNumber.setText("");
        }
    }

    private void removeBatteryStatistics(TextView destroyed, TextView hitratio, TextView shots, ImageView imageView, TextView shipName, TextView shipNumber) {
        destroyed.setText("");
        hitratio.setText("0%");
        shots.setText("");
        shipNumber.setText("");
        imageView.setImageResource(R.color.transparent);
        shipNumber.setText("");
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Subscribe
    public void onReceive(CaptainReceivedEvent event) {
        initView();
    }

    @Subscribe
    public void onRefresh(RefreshEvent event) {
        refreshing(true);

        tvArmamentMain.setText("");
        tvArmamentTorps.setText("");
        tvArmamentAircraft.setText("");
        tvArmamentOthers.setText("");

        clearTopInfo(ivTopKills, tvTopKillsName, tvTopKills);
        clearTopInfo(ivTopDamage, tvTopDamageName, tvTopDamage);
        clearTopInfo(ivTopXP, tvTopXPName, tvTopXP);
        clearTopInfo(ivTopPlanes, tvTopPlanes, tvTopPlanesName);
        clearTopInfo(ivTopSurvivalRate, tvTopSurvivalRate, tvTopSurvivalRateName);
        clearTopInfo(ivTopWinRate, tvTopWinRate, tvTopWinRateName);
        clearTopInfo(ivTopPlayed, tvTopPlayed, tvTopPlayedName);
        clearTopInfo(ivTopAvgDmg, tvTopAvgDmg, tvTopAvgDmgName);
        clearTopInfo(ivTopTotalKills, tvTopTotalKills, tvTopTotalKillsName);
        clearTopInfo(ivTopDistance, tvTopDistance, tvTopDistanceName);
        clearTopInfo(ivTopTotalDmg, tvTopTotalDmg, tvTopTotalDmgName);
        clearTopInfo(ivTopTotalExp, tvTopTotalExp, tvTopTotalExpName);
        clearTopInfo(ivTopTotalPlanes, tvTopTotalPlanes, tvTopTotalPlanesName);
        clearTopInfo(ivTopSurvivedWins, tvTopSurvivedWins, tvTopSurvivedWinsName);
        clearTopInfo(ivTopCARating, tvTopCaRating, tvTopCARatingName);
        clearTopInfo(ivTopMBAccuracy, tvTopMBAccuracy, tvTopMBAccuracyName);
        clearTopInfo(ivTopTBAccuracy, tvTopTBAccuracy, tvTopTBAccuracyName);

        clearTopInfo(ivTopSpotted, tvTopSpotted, tvTopSpottedName);
        clearTopInfo(ivTopScoutingDamage, tvTopScoutingDamage, tvTopScoutingDamageName);
        clearTopInfo(ivTopTanking, tvTopTanking, tvTopTankingName);
        clearTopInfo(ivTopTorpTanking, tvTopTorpTanking, tvTopTorpTankingName);

        //main battery
        removeBatteryStatistics(tvBatteryMainDestroyed, tvBatteryMainHitRatio,
                tvBatteryMainShots, ivBatteryMain, tvBatteryMainShipName, tvBatteryMainShipNumber);

        //secondaries
        removeBatteryStatistics(tvBatterySecondaryDestroyed, tvBatterySecondaryHitRatio,
                tvBatterySecondaryShots, ivBatterySecondary, tvBatterySecondaryShipName, tvBatterySecondaryShipNumber);

        //torpedoes
        removeBatteryStatistics(tvBatteryTorpsDestroyed, tvBatteryTorpsHitRatio,
                tvBatteryTorpsShots, ivBatteryTorps, tvBatteryTorpsShipName, tvBatteryTorpsShipNumber);

        //aircraft
        removeBatteryStatistics(tvBatteryAircraftDestroyed, tvBatteryAircraftHitRatio,
                tvBatteryAircraftShots, ivBatteryAircraft, tvBatteryAircraftShipName, tvBatteryAircraftShipNumber);

        //ramming
        removeBatteryStatistics(tvBatteryRammingDestroyed, tvBatteryRammingHitRatio,
                tvBatteryRammingShots, ivBatteryRamming, tvBatteryRammingShipName, tvBatteryRammingShipNumber);


    }

    private void clearTopInfo(ImageView iv, TextView tv, TextView tv1) {
        iv.setImageResource(R.color.transparent);
        tv.setText("");
        tv1.setText("");
    }

    @Subscribe
    public void onProgressEvent(ProgressEvent event){
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(event.isRefreshing());
        }
    }
}
