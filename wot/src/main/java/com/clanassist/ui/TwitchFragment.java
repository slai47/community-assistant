package com.clanassist.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clanassist.CAApp;
import com.clanassist.backend.Tasks.GetTwitchInfo;
import com.clanassist.model.TwitchObj;
import com.clanassist.ui.adapter.TwitchAdapter;
import com.cp.assist.R;
import com.squareup.otto.Subscribe;
import com.utilities.logging.Dlog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by slai4 on 3/13/2016.
 */
public class TwitchFragment extends Fragment {

    //twitch
    private RecyclerView recyclerView;
    private TwitchAdapter adapter;
    private LinearLayoutManager layoutManager;
    private View twitchProgress;
    public static List<TwitchObj> streamers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twitch, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.resources_twitch_list);
        twitchProgress = view.findViewById(R.id.resources_twitch_progress);
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        UIUtils.refreshActionBar(getActivity(), getString(R.string.drawer_twitch_youtube));
        setUpTwitch();
    }

    private void setUpTwitch() {
        if (streamers != null) {
            if (adapter == null) {
                layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                adapter = new TwitchAdapter();
                recyclerView.setLayoutManager(layoutManager);
                adapter.setTwitchObjs(streamers);
                adapter.setCtx(getActivity());
                recyclerView.setAdapter(adapter);
            } else {
                adapter.setTwitchObjs(streamers);
                adapter.sort();
                adapter.notifyDataSetChanged();
            }
            twitchProgress.setVisibility(View.GONE);
        } else {
            twitchProgress.setVisibility(View.VISIBLE);
            String[] array = {"Trobsmonkey", "hyf1re", "timhamlett", "SirHavoc09"};
            int number_of_cores = Runtime.getRuntime().availableProcessors();
            BlockingQueue<Runnable> mWorkQueue = new LinkedBlockingQueue<Runnable>();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(number_of_cores, number_of_cores, 60, TimeUnit.SECONDS, mWorkQueue);
            for (String name : array) {
                GetTwitchInfo info = new GetTwitchInfo();
                info.executeOnExecutor(executor, name);
            }
            streamers = new ArrayList<>();
        }
    }

    @Subscribe
    public void onTwitchReceived(final TwitchObj obj) {
        twitchProgress.post(new Runnable() {
            @Override
            public void run() {
                streamers.add(obj);
                setUpTwitch();
            }
        });
    }

    @Subscribe
    public void urlSent(String url) {
        Dlog.wtf("urlSent", "url = " + url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }
}
