package com.half.wowsca.ui.encyclopedia.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;



import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.alerts.Alert;
import com.half.wowsca.model.encyclopedia.holders.CaptainSkillHolder;
import com.half.wowsca.model.encyclopedia.items.CaptainSkill;
import com.half.wowsca.model.events.CaptainSkillClickedEvent;
import com.half.wowsca.ui.CAFragment;
import com.half.wowsca.ui.adapter.CaptainSkillsAdapter;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by slai4 on 4/25/2016.
 */
public class CaptainSkillsFragment extends CAFragment {

    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    CaptainSkillsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_recycler_view, container, false);
        recyclerView = (RecyclerView) view;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        CaptainSkillHolder holder = CAApp.getInfoManager().getCaptainSkills(getContext());
        if(holder != null && holder.getItems() != null && recyclerView.getAdapter() == null){
            layoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.shipopedia_upgrade_grid));
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            List<CaptainSkill> skills = new ArrayList<>();
            skills.addAll(holder.getItems().values());

            Collections.sort(skills, new Comparator<CaptainSkill>() {
                @Override
                public int compare(CaptainSkill lhs, CaptainSkill rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });
            Collections.sort(skills, new Comparator<CaptainSkill>() {
                @Override
                public int compare(CaptainSkill lhs, CaptainSkill rhs) {
                    return lhs.getTier() - rhs.getTier();
                }
            });

            adapter = new CaptainSkillsAdapter(skills, getContext());
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Subscribe
    public void captainClickedEvent(CaptainSkillClickedEvent event){
        CaptainSkillHolder holder = CAApp.getInfoManager().getCaptainSkills(getContext());
        CaptainSkill skill = holder.get(event.getId() + "");
        if (skill != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.encyclopedia_tier_start)+ " " + skill.getTier() + "\n\n");
            if(skill.getAbilities() != null && !skill.getAbilities().isEmpty()){
                for(int i = 0; i < skill.getAbilities().size(); i++){
                    sb.append(skill.getAbilities().get(i));
                    if(i < (skill.getAbilities().size() - 1)){
                        sb.append("\n");
                    }
                }
            }
            Alert.createGeneralAlert(getActivity(), skill.getName(), sb.toString(), getString(R.string.dismiss), R.drawable.ic_captain_skills);
        } else {
            Toast.makeText(getContext(), R.string.resources_error, Toast.LENGTH_SHORT).show();
        }

    }
}
