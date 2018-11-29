package com.clanassist.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.backend.Tasks.GetServerInfo;
import com.clanassist.model.ServerInfo;
import com.clanassist.model.ServerResult;
import com.clanassist.model.enums.Server;
import com.cp.assist.R;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slai4 on 3/6/2016.
 */
public class ServerInfoFragment extends Fragment{

    //servsers
    private View serverProgress;
    private LinearLayout llServerContainer;
    private TextView tvWoTServerNumber;
    private TextView tvWoWsServerNumber;

    public static ServerResult serverResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_info, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        llServerContainer = (LinearLayout) view.findViewById(R.id.resources_server_container);
        serverProgress = view.findViewById(R.id.resources_server_progress);
        tvWoTServerNumber = (TextView) view.findViewById(R.id.resources_server_wot_numbers);
        tvWoWsServerNumber = (TextView) view.findViewById(R.id.resources_server_wows_numbers);
    }


    @Override
    public void onResume() {
        super.onResume();
        UIUtils.refreshActionBar(getActivity(), getString(R.string.drawer_server_info));
        CAApp.getEventBus().register(this);
        initView();
    }

    private void initView() {
        if (serverResult != null) {
            serverProgress.setVisibility(View.GONE);
            llServerContainer.setVisibility(View.VISIBLE);
            llServerContainer.removeAllViews();

            int totalWot = 0, totalWoWs = 0;

            for (ServerInfo serInfo : serverResult.getWotNumbers()) {
                totalWot += serInfo.getPlayers();
            }

            for (ServerInfo serInfo : serverResult.getWowsNumbers()) {
                totalWoWs += serInfo.getPlayers();
            }

            tvWoTServerNumber.setText("" + totalWot);

            tvWoWsServerNumber.setText("" + totalWoWs);

            Server s = CAApp.getServerType(getActivity());

            createServer(s);

            for (Server server : Server.values()) {
                if (server.ordinal() != s.ordinal()) {
                    createServer(server);
                }
            }
        } else {
            GetServerInfo info = new GetServerInfo(getActivity());
            info.execute("");
            serverProgress.setVisibility(View.VISIBLE);
            llServerContainer.setVisibility(View.GONE);
        }
    }

    private void createServer(Server s) {
        View serverInfo = LayoutInflater.from(getContext()).inflate(R.layout.list_server_info, llServerContainer, false);

        TextView serverName = (TextView) serverInfo.findViewById(R.id.server_info_name);

        serverName.setText(s.toString().toUpperCase());

        LinearLayout wotContainer = (LinearLayout) serverInfo.findViewById(R.id.server_info_container_1);
        LinearLayout wowsContainer = (LinearLayout) serverInfo.findViewById(R.id.server_info_container_2);

        List<ServerInfo> currentServerWoT = new ArrayList<ServerInfo>();
        List<ServerInfo> currentServerWoWs = new ArrayList<ServerInfo>();

        int totalWot = 0, totalWoWs = 0;

        for (ServerInfo serInfo : serverResult.getWotNumbers()) {
            if (serInfo.getServer().ordinal() == s.ordinal()) {
                currentServerWoT.add(serInfo);
                totalWot += serInfo.getPlayers();
            }
        }

        for (ServerInfo serInfo : serverResult.getWowsNumbers()) {
            if (serInfo.getServer().ordinal() == s.ordinal()) {
                currentServerWoWs.add(serInfo);
                totalWoWs += serInfo.getPlayers();
            }
        }

        int layoutId = R.layout.list_server;
        View serverWoTTitle = LayoutInflater.from(getContext()).inflate(layoutId, wotContainer, false);
        TextView tvwot = (TextView) serverWoTTitle.findViewById(R.id.list_server_text);
        tvwot.setText(getString(R.string.resources_wot_total_c) + totalWot);


        View serverWowsTitle = LayoutInflater.from(getContext()).inflate(layoutId, wotContainer, false);
        TextView tvwows = (TextView) serverWowsTitle.findViewById(R.id.list_server_text);
        tvwows.setText(getString(R.string.resources_wows_total_c) + totalWoWs);

        wotContainer.addView(serverWoTTitle);
        wowsContainer.addView(serverWowsTitle);

        for (ServerInfo info : currentServerWoT) {
            View server = LayoutInflater.from(getContext()).inflate(layoutId, wotContainer, false);
            TextView text = (TextView) server.findViewById(R.id.list_server_text);
            text.setText(info.getName() + " - " + info.getPlayers());
            wotContainer.addView(server);
        }

        for (ServerInfo info : currentServerWoWs) {
            View server = LayoutInflater.from(getContext()).inflate(layoutId, wowsContainer, false);
            TextView text = (TextView) server.findViewById(R.id.list_server_text);
            text.setText(info.getName() + " - " + info.getPlayers());
            wowsContainer.addView(server);
        }
        llServerContainer.addView(serverInfo);
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Subscribe
    public void onRecieveServers(ServerResult result) {
        if (result != null) {
            serverResult = result;
            serverProgress.post(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            });
        } else {
            serverResult = new ServerResult();
            serverProgress.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    initView();
                }
            });
        }
    }


}
