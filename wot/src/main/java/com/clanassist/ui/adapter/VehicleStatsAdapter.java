package com.clanassist.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.clanassist.CAApp;
import com.clanassist.SVault;
import com.clanassist.model.infoobj.CombinedInfoObject;
import com.clanassist.ui.UIUtils;
import com.cp.assist.R;
import com.squareup.picasso.Picasso;
import com.utilities.preferences.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Harrison on 5/8/14.
 */
public class VehicleStatsAdapter extends ArrayAdapter<CombinedInfoObject> implements Filterable {

    public static final double GREAT_MULTIPLER = 1.5;
    public static final double UNICUM_MULTIPLER = 1.8;

    private List<CombinedInfoObject> objects;
    private ArrayList<CombinedInfoObject> backupObjects;
    private VehicleSearchAdapterFilter filter;
    private boolean isColorBlind;
    private boolean isLightTheme;

    private int selectedId;

    public VehicleStatsAdapter(Context context, int resource, List<CombinedInfoObject> objects) {
        super(context, resource, objects);
        this.objects = objects;
        backupObjects = (ArrayList<CombinedInfoObject>) objects;
        Prefs pref = new Prefs(context);
        isColorBlind = pref.getBoolean(SVault.PREF_COLORBLIND, false);
        isLightTheme = CAApp.isLightTheme(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_vehicle_stat, null);

        UIUtils.setUpCard(view, 0);
        TextView vehicleName = (TextView) view.findViewById(R.id.list_vehicle_name);
        TextView vehicleWin = (TextView) view.findViewById(R.id.list_vehicle_win);
        TextView vehicleFrags = (TextView) view.findViewById(R.id.list_vehicle_frag);
        TextView vehicleDef = (TextView) view.findViewById(R.id.list_vehicle_def);
        TextView vehicleSpots = (TextView) view.findViewById(R.id.list_vehicle_spots);
        TextView vehicleDamageA = (TextView) view.findViewById(R.id.list_vehicle_dam_a);
        TextView vehicleDamageG = (TextView) view.findViewById(R.id.list_vehicle_dam_g);
        TextView vehicleDamageU = (TextView) view.findViewById(R.id.list_vehicle_dam_u);
        TextView vehicleTier = (TextView) view.findViewById(R.id.list_vehicle_tier);
        TextView vehicleNation = (TextView) view.findViewById(R.id.list_vehicle_nation);
        ImageView vehicleImage = (ImageView) view.findViewById(R.id.list_vehicle_image);

        CombinedInfoObject item = objects.get(position);

        if(item.getTank() != null) {
            String nation = item.getTank().getNation();
            getPubilcNationName(vehicleNation, nation);
            vehicleTier.setText(item.getTank().getTier() + "");
            vehicleName.setText(item.getTank().getName());
            if (item.getTank().isPremium() && !isColorBlind) {
                vehicleName.setTextColor(getContext().getResources().getColor(R.color.premium_shade));
            } else {
                if (isLightTheme) {
                    vehicleName.setTextColor(getContext().getResources().getColor(R.color.black));
                } else {
                    vehicleName.setTextColor(getContext().getResources().getColor(R.color.white));
                }
            }
            Picasso.with(getContext()).load(item.getTank().getLargeImage()).error(R.drawable.ic_missing_image).into(vehicleImage);
            view.setTag(item.getTank().getId());
        }

        if(item.getWn8() != null) {
            vehicleWin.setText(item.getWn8().getWin() + "%");
            vehicleDef.setText(item.getWn8().getDef() + "");
            vehicleFrags.setText(item.getWn8().getFrag() + "");
            vehicleSpots.setText(item.getWn8().getSpot() + "");
            int averageDamage = (int) item.getWn8().getDmg();
            int greatDamage = (int) (averageDamage * GREAT_MULTIPLER);
            int unicumDamage = (int) (averageDamage * UNICUM_MULTIPLER);
            vehicleDamageA.setText(averageDamage + "");
            vehicleDamageG.setText(greatDamage + "");
            vehicleDamageU.setText(unicumDamage + "");
            if (isColorBlind) {
                vehicleDamageA.setTextColor(vehicleDef.getTextColors().getDefaultColor());
                vehicleDamageG.setTextColor(vehicleDef.getTextColors().getDefaultColor());
                vehicleDamageU.setTextColor(vehicleDef.getTextColors().getDefaultColor());
            }
            if(item.getTank() == null){
                vehicleTier.setText("");
                vehicleName.setText("" + item.getWn8().getId());
                vehicleNation.setText("");
            }
            int backgroundInt = 0;
            if (selectedId == item.getWn8().getId()) {
                backgroundInt = R.color.material_card_background_selected;
                if (isLightTheme)
                    backgroundInt = R.color.material_light_card_background_selected;
            } else {
                backgroundInt = R.color.material_card_background;
                if (isLightTheme)
                    backgroundInt = R.color.material_light_card_background;
            }
            ((CardView) view).setCardBackgroundColor(parent.getContext().getResources().getColor(backgroundInt));
        }
        return view;
    }

    public static void getPubilcNationName(TextView vehicleNation, String nation) {
        if (!TextUtils.isEmpty(nation))
            if (nation.equals(SVault.NATION_CHINA)) {
                vehicleNation.setText(R.string.nation_china);
            } else if (nation.equals(SVault.NATION_FRANCE)) {
                vehicleNation.setText(R.string.nation_france);
            } else if (nation.equals(SVault.NATION_GERMANY)) {
                vehicleNation.setText(R.string.nation_germany);
            } else if (nation.equals(SVault.NATION_JAPAN)) {
                vehicleNation.setText(R.string.nation_japan);
            } else if (nation.equals(SVault.NATION_UK)) {
                vehicleNation.setText(R.string.nation_uk);
            } else if (nation.equals(SVault.NATION_USA)) {
                vehicleNation.setText(R.string.nation_usa);
            } else if (nation.equals(SVault.NATION_USSR)) {
                vehicleNation.setText(R.string.nation_ussr);
            } else if (nation.equals(SVault.NATION_CZ)){
                vehicleNation.setText(R.string.nation_cz);
            } else if (nation.equals(SVault.NATION_SWEDEN)){
                vehicleNation.setText(R.string.nation_sweden);
            } else{
                vehicleNation.setText(nation);
            }
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new VehicleSearchAdapterFilter();
        }
        return filter;
    }

    public void restoreAllCards() {
        objects = (ArrayList<CombinedInfoObject>) backupObjects.clone();
        notifyDataSetChanged();
    }

    private class VehicleSearchAdapterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null) {
                results.values = backupObjects;
                results.count = backupObjects.size();
            } else {
                Integer tierSearch = null;
                try {
                    tierSearch = Integer.parseInt(constraint + "");
                    if (tierSearch < 0 || tierSearch > 10)
                        tierSearch = null;
                } catch (Exception e) {

                }
                List<CombinedInfoObject> filteredList = new ArrayList<CombinedInfoObject>();
                Locale local = Locale.getDefault();
                String constraintVar = constraint.toString().toLowerCase(local);
                for (CombinedInfoObject item : backupObjects) {
                    String lower;
                    if (tierSearch == null)
                        lower = item.getTank().getName().toLowerCase(local);
                    else
                        lower = item.getTank().getTier() + "";
                    boolean accepted = lower.contains(constraintVar);
                    if (accepted) {
                        filteredList.add(item);
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                objects = (ArrayList<CombinedInfoObject>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    public void setSelectedId(int selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }
}
