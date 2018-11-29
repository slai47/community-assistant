package com.clanassist.ui.details.clan;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clanassist.CAApp;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.enums.DrawerType;
import com.clanassist.model.events.ClanLoadedFinishedEvent;
import com.clanassist.model.events.details.ClanQuerySortFinished;
import com.clanassist.model.events.details.TankSelectedEvent;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.interfaces.IDetails;
import com.clanassist.tools.CPStorageManager;
import com.clanassist.tools.DownloadedClanManager;
import com.clanassist.tools.threading.ContextThread;
import com.clanassist.ui.adapter.VehicleStatsAdapter;
import com.clanassist.ui.search.EncyclopediaSearchFragment;
import com.cp.assist.R;
import com.squareup.otto.Subscribe;

/**
 * Created by Harrison on 8/20/2014.
 */
public class ClanQueryFragment extends Fragment {

    public static boolean REFRESH = true;

    private IDetails details;

    private Button bSelectTank;
    private TextView tvTankName;
    private TextView tvSecondDescription;
    private TextView tvThirdDescription;
    private View descriptionArea;

    private LinearLayout container;

    private TextView tvMessageText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_query, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        bSelectTank = (Button) view.findViewById(R.id.details_query_select_tank);
        tvTankName = (TextView) view.findViewById(R.id.details_query_tank_name);
        tvSecondDescription = (TextView) view.findViewById(R.id.details_query_tank_second_description);
        tvThirdDescription = (TextView) view.findViewById(R.id.details_query_tank_third_description);
        descriptionArea = view.findViewById(R.id.details_query_tank_description_area);

        container = (LinearLayout) view.findViewById(R.id.details_query_container);

        tvMessageText = (TextView) view.findViewById(R.id.details_query_middle_message);
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }

    private void initView() {
        try {
            if (details == null)
                details = (IDetails) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        } catch (Exception e) {
        }
        //check if the clanresult is there. Else forget about it.
        if(details != null) {
            Clan clan = details.getClan(getActivity());
            if (clan != null) {
                boolean isLoaded = DownloadedClanManager.hasClanDownloadLoaded(getActivity(), clan.getClanId() + "");
                boolean isDownloaded = CPStorageManager.hasClanTaskResult(getActivity(), clan.getClanId());
                if (!isDownloaded) {
                    tvMessageText.setText(R.string.details_query_clan_info_not_downloaded);
                    tvMessageText.setVisibility(View.VISIBLE);
                    disableView();
                } else if (!isLoaded) {
                    tvMessageText.setText(R.string.details_query_loading);
                    tvMessageText.setVisibility(View.VISIBLE);
                    disableView();
                } else {
                    if (CAApp.SELECTED_VEHICLE_ID > 0) {
                        tvMessageText.setVisibility(View.GONE);
                        disableView();
                        ContextThread thread = new ContextThread(getActivity(), container, null);
                        thread.createClanQueryList(DownloadedClanManager.getClanDownload(clan.getClanId() + ""), CAApp.SELECTED_VEHICLE_ID);

                        Tank tankInfo = CAApp.getInfoManager().getTanks(getActivity()).getTank(CAApp.SELECTED_VEHICLE_ID);
                        tvTankName.setText(tankInfo.getName());
                        tvSecondDescription.setText(getString(R.string.details_query_description_tier) + " " + tankInfo.getTier() + " " + getPublicTankTypeName(tankInfo.getClassType()));
                        VehicleStatsAdapter.getPubilcNationName(tvThirdDescription, tankInfo.getNation());
                    } else {
                        tvMessageText.setVisibility(View.VISIBLE);
                        tvMessageText.setText(R.string.details_query_no_tank_selected);
                        enableView();
                    }
                }
                bSelectTank.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EncyclopediaSearchFragment fragment = new EncyclopediaSearchFragment();
                        EncyclopediaSearchFragment.TYPE = DrawerType.CHOOSE;

                        getActivity().getSupportFragmentManager().beginTransaction()
//                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                                .add(R.id.container, fragment).addToBackStack("ChooseATank").commit();
                    }
                });
            }
        }
    }

    private String getPublicTankTypeName(String classType) {
        String publicName = "";
        if (!TextUtils.isEmpty(classType)) {
            if (classType.equalsIgnoreCase("mediumTank")) {
                publicName = getString(R.string.medium_tank);
            } else if (classType.equalsIgnoreCase("heavyTank")) {
                publicName = getString(R.string.heavy_tank);
            } else if (classType.equalsIgnoreCase("AT-SPG")) {
                publicName = getString(R.string.tank_destroyer);
            } else if (classType.equalsIgnoreCase("lightTank")) {
                publicName = getString(R.string.light_tank);
            } else if (classType.equalsIgnoreCase("SPG")) {
                publicName = getString(R.string.artillery);
            }
        }
        return publicName;
    }

    private void disableView() {
        bSelectTank.setEnabled(false);
        bSelectTank.setAlpha(0.6f);
        descriptionArea.setAlpha(0.6f);
        tvTankName.setText("");
        tvThirdDescription.setText("");
        tvSecondDescription.setText("");
        container.removeAllViews();
    }

    private void enableView() {
        bSelectTank.setEnabled(true);
        bSelectTank.setAlpha(1f);
        descriptionArea.setAlpha(1f);
    }

    @Subscribe
    public void clanSorted(final ClanQuerySortFinished event) {
        bSelectTank.post(new Runnable() {
            @Override
            public void run() {
                enableView();
                if (event.isEmpty()) {
                    tvMessageText.setVisibility(View.VISIBLE);
                    tvMessageText.setText(getString(R.string.details_query_no_players_have_that_tank));
                }
            }
        });
    }

    @Subscribe
    public void clanLoaded(ClanLoadedFinishedEvent event) {
        Clan c = getDetails().getClan(getActivity());
        if (c != null && event.getClanId().equals(c.getClanId() + "") && event.isMerged()) {
            container.post(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            });
        }
    }

    @Subscribe
    public void tankSelected(TankSelectedEvent event) {
        container.post(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        });
    }


    public IDetails getDetails() {
        return details;
    }

    public void setDetails(IDetails details) {
        this.details = details;
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }
}
