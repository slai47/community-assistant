package com.half.wowsca.ui.viewcaptain.tabs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;



import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.interfaces.ICaptain;
import com.half.wowsca.managers.CaptainManager;
import com.half.wowsca.managers.StorageManager;
import com.half.wowsca.model.Achievement;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.encyclopedia.holders.AchievementsHolder;
import com.half.wowsca.model.encyclopedia.items.AchievementInfo;
import com.half.wowsca.model.events.CaptainReceivedEvent;
import com.half.wowsca.model.events.ProgressEvent;
import com.half.wowsca.model.events.RefreshEvent;
import com.half.wowsca.model.saveobjects.SavedAchievements;
import com.half.wowsca.ui.CAFragment;
import com.half.wowsca.ui.adapter.AchievementsAdapter;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slai4 on 9/15/2015.
 */
public class CaptainAchievementsFragment extends CAFragment {

    private GridView battleGrid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_captain_achievements, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        battleGrid = (GridView) view.findViewById(R.id.achievement_battle_grid);
        bindSwipe(view);
        initSwipeLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    private void initView() {
        Captain captain = null;
        try {
            captain = ((ICaptain) getActivity()).getCaptain(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (captain != null && captain.getAchievements() != null) {
            refreshing(false);
            AchievementsHolder achievementsHolder = CAApp.getInfoManager().getAchievements(getContext());
            List<Achievement> achs = new ArrayList<>();
            Map<String, Integer> captainAchievements = new HashMap<>();
            for (Achievement achievement : captain.getAchievements()) {
                captainAchievements.put(achievement.getName(), achievement.getNumber());
            }

            if(achievementsHolder != null && achievementsHolder.getItems() != null) {
                for (AchievementInfo info : achievementsHolder.getItems().values()) {
                    Achievement ach = new Achievement();
                    ach.setName(info.getId());
                    Integer number = captainAchievements.get(info.getId());
                    ach.setNumber((number != null ? number : 0));
                    achs.add(ach);
                }
                Collections.sort(achs, new Comparator<Achievement>() {
                    @Override
                    public int compare(Achievement lhs, Achievement rhs) {
                        return rhs.getNumber() - lhs.getNumber();
                    }
                });

                AchievementsAdapter adapter = new AchievementsAdapter(getContext(), R.layout.list_achievement, achs);
                battleGrid.setAdapter(adapter);

                battleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final Context ctx = view.getContext();
                        String achievementName = (String) view.getTag();
                        AchievementInfo info = CAApp.getInfoManager().getAchievements(getContext()).get(achievementName);
                        if (info != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                            builder.setTitle(info.getName());
                            builder.setMessage(info.getDescription());
                            builder.setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        } else {
                            Toast.makeText(view.getContext(), "Achievement Information not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                getOldAchievements(CaptainManager.createCapIdStr(captain.getServer(), captain.getId()));
            }else {
//                Toast.makeText(getContext(), getString(R.string.resources_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getOldAchievements(final String accountId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SavedAchievements achievements = StorageManager.getPlayerAchievements(getContext(), accountId);
                    if(achievements != null && achievements.getSavedAchievements() != null && achievements.getSavedAchievements().size() > 1 && battleGrid.getAdapter() != null){
                        AchievementsAdapter adapter = (AchievementsAdapter) battleGrid.getAdapter();
                        List<Achievement> achis =  achievements.getSavedAchievements().get(1);
                        Map<String, Integer> mapAchi = new HashMap<>();
                        for(Achievement achievement : achis){
                            mapAchi.put(achievement.getName(), achievement.getNumber());
                        }
                        adapter.setSavedAchievements(mapAchi);
                    }
                } catch (Exception e) {
                }
            }
        });
        t.start();
    }

    @Subscribe
    public void onReceive(CaptainReceivedEvent event) {
        initView();
    }

    @Subscribe
    public void onRefresh(RefreshEvent event) {
        refreshing(true);
        battleGrid.setAdapter(null);
    }

    @Subscribe
    public void onProgressEvent(ProgressEvent event){
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(event.isRefreshing());
        }
    }
}
