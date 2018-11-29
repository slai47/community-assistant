package com.half.wowsca.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;



import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.managers.CaptainManager;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.Statistics;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.enums.Server;
import com.half.wowsca.ui.viewcaptain.ViewCaptainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 9/19/2015.
 */
public class UIUtils {

    public static void createReviewDialog(final Activity act) {
        Prefs prefs = new Prefs(act);
        boolean hasReviewed = prefs.getBoolean("hasReviewed", false);
        if (!hasReviewed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle(act.getString(R.string.dialog_review_app));
            builder.setMessage(act.getString(R.string.dialog_review_message));
            builder.setPositiveButton(act.getString(R.string.dialog_review_positive), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Prefs prefs = new Prefs(act);
                    prefs.setBoolean("hasReviewed", true);
                    String url = "https://play.google.com/store/apps/details?id=com.half.wowsca";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    act.startActivity(i);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(act.getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            CAApp.HAS_SHOWN_FIRST_DIALOG = true;
        }
    }

    public static void createDonationDialog(final Activity act) {
        Prefs prefs = new Prefs(act);
        boolean hasDonated = prefs.getBoolean("hasDonated2", false);
        if (!hasDonated) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle(act.getString(R.string.dialog_assist_title));
            builder.setMessage(act.getString(R.string.dialog_assist_message));
            builder.setPositiveButton(act.getString(R.string.patreon), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Prefs prefs = new Prefs(act);
                    prefs.setBoolean("hasDonated2", true);
                    String url = "https://patreon.com/slai47";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    act.startActivity(i);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNeutralButton(act.getString(R.string.dialog_view_ad), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(act.getApplicationContext(), ResourcesActivity.class);
                    i.putExtra(ResourcesActivity.EXTRA_TYPE, ResourcesActivity.EXTRA_DONATE);
                    i.putExtra(ResourcesActivity.EXTRA_VIEW_AD, true);
                    act.startActivity(i);
                    dialog.dismiss();
                }
            });
            builder.show();
            CAApp.HAS_SHOWN_FIRST_DIALOG = true;
        }
    }

    public static void createFollowDialog(final Activity act) {
        Prefs prefs = new Prefs(act);
        boolean hasFollowed = prefs.getBoolean("hasFollowed", false);
        if (!hasFollowed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle(act.getString(R.string.dialog_follow_title));
            builder.setMessage(act.getString(R.string.dialog_follow_message));
            builder.setPositiveButton("Twitter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Prefs prefs = new Prefs(act);
                    prefs.setBoolean("hasFollowed", true);
                    String url = "https://twitter.com/slai47";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    act.startActivity(i);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            CAApp.HAS_SHOWN_FIRST_DIALOG = true;
        }
    }

    public static void createBookmarkingDialogIfNeeded(final Activity act, final Captain p) {
        Prefs prefs = new Prefs(act);
        boolean isFirstTimeAddingPlayer = prefs.getBoolean("hasSeenSelectedDialog", true);
        if (isFirstTimeAddingPlayer) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle(act.getString(R.string.dialog_bookmark_title));
            builder.setMessage(act.getString(R.string.dialog_bookmark_message));

            builder.setPositiveButton(act.getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CAApp.setSelectedId(act.getApplicationContext(), CaptainManager.createCapIdStr(p.getServer(), p.getId()));
                    act.invalidateOptionsMenu();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            prefs.setBoolean("hasSeenSelectedDialog", false);
        }
    }

    public static void createCaptainListViewMenu(final Activity act, String currentId) {
        View captainsView = act.findViewById(R.id.action_captains);
        PopupMenu popupMenu = new PopupMenu(act, captainsView);
        MenuInflater inflate = popupMenu.getMenuInflater();
        inflate.inflate(R.menu.menu_popup_captains, popupMenu.getMenu());
        Map<String, Captain> captains = CaptainManager.getCaptains(act.getApplicationContext());
        Collection<Captain> caps = captains.values();
        for (Captain captain : caps) {
            if (!CaptainManager.createCapIdStr(captain.getServer(), captain.getId()).equals(currentId))
                popupMenu.getMenu().add(captain.getServer().toString().toUpperCase() + " " + captain.getName());
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String name = item.getTitle().toString();
                String[] split = name.split(" ");
                Server server = Server.valueOf(split[0]);
                String captainName = split[1];
                Map<String, Captain> captains = CaptainManager.getCaptains(act.getApplicationContext());
                Collection<Captain> caps = captains.values();
                for (Captain captain : caps) {
                    if (captain.getName().equals(captainName) && captain.getServer().ordinal() == server.ordinal()) {
                        Intent i = new Intent(act.getApplicationContext(), ViewCaptainActivity.class);
                        i.putExtra(ViewCaptainActivity.EXTRA_ID, captain.getId());
                        i.putExtra(ViewCaptainActivity.EXTRA_SERVER, captain.getServer().toString());
                        i.putExtra(ViewCaptainActivity.EXTRA_NAME, captain.getName());
                        act.startActivity(i);
                        break;
                    }
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public static void setUpCard(View parent, int id) {
        CardView card = null;
        if (id > 0)
            card = (CardView) parent.findViewById(id);
        else
            card = (CardView) parent;
        int backgroundInt = R.color.transparent;
        if(CAApp.isDarkTheme(parent.getContext()))
            backgroundInt = R.color.material_card_background_dark;
        card.setCardBackgroundColor(ContextCompat.getColor(parent.getContext(), backgroundInt));
        card.setRadius(6.0f);
        card.setCardElevation(4f);
    }

    public static String getNationText(Context ctx, String nationCode){
        String nation = nationCode;
        if (nation.equals("ussr")) {
            nation = ctx.getString(R.string.russia);
        } else if (nation.equals("germany")) {
            nation = ctx.getString(R.string.germany);
        } else if (nation.equals("usa")) {
            nation = ctx.getString(R.string.usa);
        } else if (nation.equals("poland")) {
            nation = ctx.getString(R.string.poland);
        } else if (nation.equals("japan")) {
            nation = ctx.getString(R.string.japan);
        } else if (nation.equals("uk")) {
            nation = ctx.getString(R.string.uk);
        } else if(nation.equals("pan_asia")){
            nation = ctx.getString(R.string.pan_asia);
        } else if(nation.equals("france")){
            nation = ctx.getString(R.string.nation_france);
        } else if(nation.equals("commonwealth")){
            nation = ctx.getString(R.string.nation_commonwealth);
        }
        return nation;
    }

    private static Map<Long, Long> arpShips;
    public static final long MYOKO_SHIP_ID = 4286494416l;
    public static final long KONGO_SHIP_ID = 4287575760l;

    public static void setShipImage(@NonNull ImageView view, ShipInfo ship){
        setShipImage(view, ship, false);
    }

    public static void setShipImage(@NonNull ImageView view, ShipInfo ship, boolean forceBigImage){
        if(arpShips == null) {
            arpShips = new HashMap<>();
            arpShips.put(3551442640l, MYOKO_SHIP_ID); // haguro
            arpShips.put(3552523984l, KONGO_SHIP_ID); // Hiei
            arpShips.put(3553572560l, KONGO_SHIP_ID); // haruna
            arpShips.put(3554621136l, KONGO_SHIP_ID); // kirishima
            arpShips.put(3555636944l, MYOKO_SHIP_ID); // arp myoko
            arpShips.put(3555669712l, KONGO_SHIP_ID); // arp Kongo
        }
        boolean highDefImage = view.getContext().getResources().getBoolean(R.bool.high_def_images);
        if(forceBigImage)
            highDefImage = true;
        if(CAApp.isNoArp(view.getContext())) {
            Long arpShip = arpShips.get(ship.getShipId());
            if (arpShip != null) {
                ship = CAApp.getInfoManager().getShipInfo(view.getContext()).get(arpShip);
            }
        }
        if(ship != null)
            Picasso.get().load(highDefImage ? ship.getBestImage() : ship.getImage()).error(R.drawable.ic_missing_image).into(view, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Dlog.d(UIUtils.class.getName(),e.getMessage());
                }
            });
    }

    public static void createOtherStatsArea(LinearLayout area, List<String> listStrs, List<Statistics> list){
        area.removeAllViews();
        for(int i = 0; i < list.size(); i++){
            String title = listStrs.get(i);
            Statistics stats = list.get(i);

            View view = LayoutInflater.from(area.getContext()).inflate(R.layout.list_statistics, area, false);

            UIUtils.setUpCard(view, R.id.list_statistics_area);

            TextView tvTitle = (TextView) view.findViewById(R.id.list_statistics_title);

            TextView tvDamage = (TextView) view.findViewById(R.id.list_statistics_avg_dmg);
            TextView tvBattles = (TextView) view.findViewById(R.id.list_statistics_battles);
            TextView tvAvgExp = (TextView) view.findViewById(R.id.list_statistics_avg_exp);
            TextView tvWinRate = (TextView) view.findViewById(R.id.list_statistics_win_rate);
            TextView tvAvgKills = (TextView) view.findViewById(R.id.list_statistics_k_d);

            TextView tvBatteryMain = (TextView) view.findViewById(R.id.list_statistics_battery_kills_main);
            TextView tvBatteryTorps = (TextView) view.findViewById(R.id.list_statistics_battery_kills_torps);
            TextView tvBatteryAircraft = (TextView) view.findViewById(R.id.list_statistics_battery_kills_aircraft);
            TextView tvBatteryOther = (TextView) view.findViewById(R.id.list_statistics_battery_kills_other);


            TextView tvMaxKills = (TextView) view.findViewById(R.id.list_statistics_max_kills);
            TextView tvMaxDamage = (TextView) view.findViewById(R.id.list_statistics_max_dmg);
            TextView tvMaxPlanes = (TextView) view.findViewById(R.id.list_statistics_max_planes_killed);
            TextView tvMaxXP = (TextView) view.findViewById(R.id.list_statistics_max_xp);

            TextView tvTotalKills = (TextView) view.findViewById(R.id.list_statistics_kills);
            TextView tvTotalDamage = (TextView) view.findViewById(R.id.list_statistics_total_dmg);
            TextView tvTotalPlanes = (TextView) view.findViewById(R.id.list_statistics_planes_downed);
            TextView tvTotalXP = (TextView) view.findViewById(R.id.list_statistics_total_xp);


            TextView tvSurvivalRate = (TextView) view.findViewById(R.id.list_statistics_survival_rate);
            TextView tvSurvivedWins = (TextView) view.findViewById(R.id.list_statistics_survived_wins);
            TextView tvCaptures = (TextView) view.findViewById(R.id.list_statistics_captures);

            TextView tvMainAccuracy = (TextView) view.findViewById(R.id.list_statistics_main_battery_accuracy);
            TextView tvTorpAccuracy = (TextView) view.findViewById(R.id.list_statistics_torp_battery_accuracy);
            TextView tvDrpCaptures = (TextView) view.findViewById(R.id.list_statistics_drp_captures);


            TextView tvSpottingDamage = (TextView) view.findViewById(R.id.list_statistics_total_spotting);
            TextView tvArgoDamage = (TextView) view.findViewById(R.id.list_statistics_total_argo);
            TextView tvBuildingDamage = (TextView) view.findViewById(R.id.list_statistics_total_building);
            TextView tvArgoTorpDamage = (TextView) view.findViewById(R.id.list_statistics_total_torp_argo);

            TextView tvSuppressionCount = (TextView) view.findViewById(R.id.list_statistics_total_supressions);
            TextView tvSpottingCount = (TextView) view.findViewById(R.id.list_statistics_total_spots);
            TextView tvMaxSpotting = (TextView) view.findViewById(R.id.list_statistics_max_spots);

            tvTitle.setText(title);

            float battles = stats.getBattles();

            tvDamage.setText((int) (stats.getTotalDamage() / battles) + "");
            tvBattles.setText((int) battles + "");
            tvAvgExp.setText((int) (stats.getTotalXP() / battles) + "");
            tvWinRate.setText(Utils.getDefaultDecimalFormatter().format((stats.getWins() / battles) * 100) + "%");
            tvAvgKills.setText(Utils.getDefaultDecimalFormatter().format(stats.getFrags() / battles) + "");

            tvBatteryMain.setText(stats.getMainBattery().getFrags() + "");
            tvBatteryTorps.setText(stats.getTorpedoes().getFrags() + "");
            tvBatteryAircraft.setText(stats.getAircraft().getFrags() + "");
            int other = stats.getFrags() - stats.getMainBattery().getFrags() - stats.getTorpedoes().getFrags() - stats.getAircraft().getFrags();
            tvBatteryOther.setText(other + "");

            DecimalFormat format = new DecimalFormat("###,###,###");

            tvMaxKills.setText(stats.getMaxFragsInBattle() + "");
            tvMaxDamage.setText(format.format(stats.getMaxDamage()) + "");
            tvMaxPlanes.setText(stats.getMaxPlanesKilled() + "");
            tvMaxXP.setText(format.format(stats.getMaxXP()) + "");

            tvTotalKills.setText(format.format(stats.getFrags()) + "");
            tvTotalDamage.setText(format.format(stats.getTotalDamage()) + "");
            tvTotalPlanes.setText(stats.getPlanesKilled() + "");
            tvTotalXP.setText(format.format(stats.getTotalXP()) + "");

            tvSurvivalRate.setText(Utils.getOneDepthDecimalFormatter().format((stats.getSurvivedBattles() / battles) * 100) + "%");
            tvSurvivedWins.setText(Utils.getOneDepthDecimalFormatter().format((stats.getSurvivedWins() / battles) * 100) + "%");
            tvCaptures.setText(Utils.getOneDepthDecimalFormatter().format(stats.getCapturePoints() / battles) + "");
            tvDrpCaptures.setText(Utils.getOneDepthDecimalFormatter().format(stats.getDroppedCapturePoints() / battles) + "");

            tvMainAccuracy.setText(Utils.getOneDepthDecimalFormatter().format((stats.getMainBattery().getHits() / (float) stats.getMainBattery().getShots()) * 100) + "%");
            tvTorpAccuracy.setText(Utils.getOneDepthDecimalFormatter().format((stats.getTorpedoes().getHits() / (float) stats.getTorpedoes().getShots()) * 100) + "%");

            Context ctx = area.getContext();
            String argoDamage = "" + stats.getTotalArgoDamage();
            if(stats.getTotalArgoDamage() > 1000000){
                argoDamage = "" + Utils.getDefaultDecimalFormatter().format(stats.getTotalArgoDamage() / 1000000) + ctx.getString(R.string.million);
            }
            tvArgoDamage.setText(argoDamage);

            String argoTorpDamage = "" + stats.getTorpArgoDamage();
            if(stats.getTotalArgoDamage() > 1000000){
                argoTorpDamage = "" + Utils.getDefaultDecimalFormatter().format(stats.getTotalArgoDamage() / 1000000) + ctx.getString(R.string.million);
            }
            tvArgoTorpDamage.setText(argoTorpDamage);

            String buildingDamage = "" + stats.getBuildingDamage();
            if(stats.getBuildingDamage() > 1000000){
                buildingDamage = "" + Utils.getDefaultDecimalFormatter().format(stats.getBuildingDamage() / 1000000) + ctx.getString(R.string.million);
            }
            tvBuildingDamage.setText(buildingDamage);

            String scoutingDamage = "" + stats.getScoutingDamage();
            if(stats.getScoutingDamage() > 1000000){
                scoutingDamage = "" + Utils.getDefaultDecimalFormatter().format(stats.getScoutingDamage() / 1000000) + ctx.getString(R.string.million);
            }
            tvSpottingDamage.setText(scoutingDamage);

            tvSpottingCount.setText("" + stats.getShipsSpotted());
            tvSuppressionCount.setText("" + stats.getSuppressionCount());
            tvMaxSpotting.setText("" + stats.getMaxSpotted());

            area.addView(view);
        }
    }
}
