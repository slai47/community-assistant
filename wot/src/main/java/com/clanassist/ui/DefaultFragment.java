package com.clanassist.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clanassist.CAApp;
import com.clanassist.tools.NavigationDrawerManager;
import com.cp.assist.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Created by Harrison on 5/16/2015.
 */
public class DefaultFragment extends Fragment {

    private View progress;

    private FloatingActionsMenu fab;
    private FloatingActionButton clanSearch;
    private FloatingActionButton playerSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default, container, false);
        bindView(view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("filler", true);
    }

    private void bindView(View view) {
        progress = view.findViewById(R.id.default_progress);
        fab = (FloatingActionsMenu) view.findViewById(R.id.default_fab);

        clanSearch = (FloatingActionButton) view.findViewById(R.id.default_clan_button);
        playerSearch = (FloatingActionButton) view.findViewById(R.id.default_player_button);

        clanSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).selectDrawerGroup(NavigationDrawerManager.CLAN_SEARCH_ID);
                fab.collapse();
            }
        });

        playerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).selectDrawerGroup(NavigationDrawerManager.PLAYER_SEARCH_ID);
                fab.collapse();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        UIUtils.refreshActionBar(getActivity(), getString(R.string.drawer_home));
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }
}
