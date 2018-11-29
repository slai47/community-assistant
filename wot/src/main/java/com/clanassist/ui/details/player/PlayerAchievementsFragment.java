package com.clanassist.ui.details.player;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.model.events.details.PlayerClearEvent;
import com.clanassist.model.events.details.PlayerProfileHit;
import com.clanassist.model.infoobj.Achievement;
import com.clanassist.model.interfaces.IDetails;
import com.clanassist.model.listmodel.AchievementInfo;
import com.clanassist.model.player.Player;
import com.clanassist.ui.adapter.AchievementsAdapter;
import com.cp.assist.R;
import com.squareup.otto.Subscribe;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by slai4 on 9/15/2015.
 */
public class PlayerAchievementsFragment extends Fragment {

    private GridView battleGrid;
    private IDetails details;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_achievements, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        battleGrid = (GridView) view.findViewById(R.id.achievement_battle_grid);
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }

    private void initView() {
        Context ctx = getActivity();
        if(ctx == null){
            ctx = battleGrid.getContext();
        }
        if(ctx != null) {
            Prefs pref = new Prefs(ctx);
            if (details == null)
                details = (IDetails) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
            Player p = details.getPlayer(ctx);
            if (p != null && p.getAchievements() != null) {
                List<AchievementInfo> achs = new ArrayList<AchievementInfo>();
                for(String key : p.getAchievements().keySet()){
                    int num = p.getAchievements().get(key);
                    AchievementInfo info = new AchievementInfo();
                    info.setName(key);
                    info.setNumber(num);
                    if(CAApp.getInfoManager().getAchievements(getContext()).getAchievement(key) != null)
                        achs.add(info);
                }

                Collections.sort(achs, new Comparator<AchievementInfo>() {
                    @Override
                    public int compare(AchievementInfo lhs, AchievementInfo rhs) {
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
                        Dlog.d("Achievement", achievementName);
                        Achievement info = CAApp.getInfoManager().getAchievements(getContext()).getAchievement(achievementName);
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
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Subscribe
    public void onReceive(PlayerProfileHit event) {
        initView();
    }

    @Subscribe
    public void onRefresh(PlayerClearEvent event) {
        battleGrid.setAdapter(null);
    }

    public void setDetails(IDetails details) {
        this.details = details;
    }
}
