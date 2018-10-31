package com.half.wowsca.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.backend.GetServerInfo;
import com.half.wowsca.backend.GetTwitchInfo;
import com.half.wowsca.model.ServerInfo;
import com.half.wowsca.model.TwitchObj;
import com.half.wowsca.model.enums.Server;
import com.half.wowsca.model.enums.TwitchStatus;
import com.half.wowsca.model.result.ServerResult;
import com.half.wowsca.ui.adapter.TwitchAdapter;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;
import com.utilities.views.SwipeBackLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by slai4 on 11/29/2015.
 */
public class ResourcesActivity extends CABaseActivity {

    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_VIEW_AD = "view_ad";

    public static final String EXTRA_WEBSITES_TOOLS = "tools";
    public static final String EXTRA_DONATE = "donate";
    public static final String EXTRA_TWITCH = "twitch";
    public static final String EXTRA_SERVERS = "servers";

    public static ServerResult serverResult;

    public static List<TwitchObj> streamers;

    private String type;

    private Toolbar mToolbar;

    //donation area
    private View aDonation;
//    private WebView webView;
    private Button bPaypal;
    private Button bViewAd;
    InterstitialAd mInterstitialAd;
    private boolean viewAd;
    private View adProgress;

    //servsers
    private View aServers;
    private View serverProgress;
    private LinearLayout llServerContainer;
    private TextView tvWoTServerNumber;
    private TextView tvWoWsServerNumber;

    //twitch
    private View aTwitch;
    private RecyclerView recyclerView;
    private TwitchAdapter adapter;
    private GridLayoutManager layoutManager;
    private View twitchProgress;

    //websites
    private View aWebsites;
    private View aShipComrade;
    private View aWowsSite;
    private View aReddit;
    private View aDRMB;
    private View aAP;
    private View aWoWsReplays;

    private ImageView ivShipComrade;
    private ImageView ivWoWsIcon;
    private ImageView ivReddit;
    private ImageView ivDRMB;
    private ImageView ivAP;
    private ImageView ivWoWsReplays;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);
        if (savedInstanceState != null) {
            type = savedInstanceState.getString(EXTRA_TYPE);
            viewAd = false;
        } else {
            type = getIntent().getStringExtra(EXTRA_TYPE);
            viewAd = getIntent().getBooleanExtra(EXTRA_VIEW_AD, false);
        }
        bindView();
    }

    private void bindView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (!TextUtils.isEmpty(type)) {
            if (type.equals(EXTRA_WEBSITES_TOOLS)) {
                setTitle(getString(R.string.resources_websites));
            } else if (type.equals(EXTRA_DONATE)) {
                setTitle(getString(R.string.resources_support));
            } else if (type.equals(EXTRA_TWITCH)) {
                setTitle(getString(R.string.action_twitch));
            } else if (type.equals(EXTRA_SERVERS)) {
                setTitle(getString(R.string.resources_server_info));
            }
        }
        aDonation = findViewById(R.id.resources_donation_area);
//        webView = (WebView) findViewById(R.id.resources_webview);
        bPaypal = (Button) findViewById(R.id.resources_donation_paypal);
        bViewAd = (Button) findViewById(R.id.resources_view_add);
        adProgress = findViewById(R.id.resources_view_ad_progress);

        aServers = findViewById(R.id.resources_server_area);
        llServerContainer = (LinearLayout) findViewById(R.id.resources_server_container);
        serverProgress = findViewById(R.id.resources_server_progress);
        tvWoTServerNumber = (TextView) findViewById(R.id.resources_server_wot_numbers);
        tvWoWsServerNumber = (TextView) findViewById(R.id.resources_server_wows_numbers);

        aTwitch = findViewById(R.id.resources_twitch_area);
        recyclerView = (RecyclerView) findViewById(R.id.resources_twitch_list);
        twitchProgress = findViewById(R.id.resources_twitch_progress);

        aWebsites = findViewById(R.id.resources_website_area);
        aShipComrade = findViewById(R.id.resources_website_ship_comrade);
        aWowsSite = findViewById(R.id.resources_website_wows);
        aReddit = findViewById(R.id.resources_website_reddit);
        aDRMB = findViewById(R.id.resources_website_drmb);
        aAP = findViewById(R.id.resources_website_ap);
        aWoWsReplays = findViewById(R.id.resources_website_wowsreplays);

        ivShipComrade = (ImageView) findViewById(R.id.resources_website_ship_comrade_icon);
        ivWoWsIcon = (ImageView) findViewById(R.id.resources_website_website_icon);
        ivReddit = (ImageView) findViewById(R.id.resources_website_reddit_icon);
        ivDRMB = (ImageView) findViewById(R.id.resources_website_drmb_icon);
        ivAP = (ImageView) findViewById(R.id.resources_website_ap_icon);
        ivWoWsReplays = (ImageView) findViewById(R.id.resources_website_wowsreplays_icon);

        UIUtils.setUpCard(aShipComrade, 0);
        UIUtils.setUpCard(aWowsSite,0);
        UIUtils.setUpCard(aReddit, 0);
        UIUtils.setUpCard(aDRMB, 0);
        UIUtils.setUpCard(aAP, 0);
        UIUtils.setUpCard(aWoWsReplays, 0);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(!CAApp.DEVELOPMENT_MODE ? getString(R.string.ad_unit_id) : getString(R.string.test_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                adProgress.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
                adProgress.setVisibility(View.GONE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                adProgress.setVisibility(View.GONE);
            }
        });

        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_TYPE, type);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    private void initView() {
        if (!TextUtils.isEmpty(type)) {
            if (type.equals(EXTRA_DONATE)) {
                setUpDonationArea();
            } else if (type.equals(EXTRA_SERVERS)) {
                setUpServerInfo();
            } else if (type.equals(EXTRA_TWITCH)) {
                setUpTwitch();
            } else if (type.equals(EXTRA_WEBSITES_TOOLS)) {
                setUpWebsites();
            }
        }
    }

    private void setUpDonationArea() {
        aDonation.setVisibility(View.VISIBLE);
        bViewAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNewInterstitial();
//                Prefs prefs = new Prefs(getApplicationContext());
//                prefs.setBoolean("hasDonated", true);
            }
        });
        bPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs prefs = new Prefs(getApplicationContext());
                prefs.setBoolean("hasDonated2", true);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://patreon.com/slai47"));
                startActivity(i);
            }
        });

//        webView.setWebChromeClient(new WebChromeClient());
//        webView.setWebViewClient(new MyWebViewClient());
//        webView.setBackgroundColor(Color.TRANSPARENT);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setSupportZoom(false);
//        webView.loadUrl("file:///android_asset/donation.html");
        if (viewAd) {
            requestNewInterstitial();
        }
    }

    private void setUpServerInfo() {
        aServers.setVisibility(View.VISIBLE);
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

            Server s = CAApp.getServerType(getApplicationContext());

            createServer(s);

            for (Server server : Server.values()) {
                if (server.ordinal() != s.ordinal()) {
                    createServer(server);
                }
            }
        } else {
            GetServerInfo info = new GetServerInfo(getApplicationContext());
            info.execute("");
            serverProgress.setVisibility(View.VISIBLE);
            llServerContainer.setVisibility(View.GONE);
        }
    }

    private void setUpTwitch() {
        aTwitch.setVisibility(View.VISIBLE);
        if (streamers != null) {
            if (adapter == null) {
                layoutManager = new GridLayoutManager(getApplicationContext(), getResources().getInteger(R.integer.twitch_cols));
                layoutManager.setOrientation(GridLayoutManager.VERTICAL);
                adapter = new TwitchAdapter();
                recyclerView.setLayoutManager(layoutManager);
                addYoutubeOnlyStar();
                adapter.setTwitchObjs(streamers);
                adapter.setCtx(getApplicationContext());
                recyclerView.setAdapter(adapter);
            } else {
                adapter.setTwitchObjs(streamers);
                adapter.sort();
                adapter.notifyDataSetChanged();
            }
            twitchProgress.setVisibility(View.GONE);
        } else {
            twitchProgress.setVisibility(View.VISIBLE);
            String[] array = {"iChaseGaming", "Mejash", "nozoupforyou", "Aerroon", "BaronVonGamez", "wda_punisher", "wargaming", "iEarlGrey", "Flamuu", "clydethamonkey", "crysantos", "kamisamurai", "notser"};
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

    private void addYoutubeOnlyStar(){
        TwitchObj obj = new TwitchObj("Jammin411");
        obj.setLive(TwitchStatus.YOUTUBE);
        streamers.add(obj);
    }

    private void setUpWebsites() {
        aWebsites.setVisibility(View.VISIBLE);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = (String) v.getTag();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        };

        aWowsSite.setTag("http://www.worldofwarships" + CAApp.getServerType(getApplicationContext()).getSuffix());
        aShipComrade.setTag("http://www.shipcomrade.com");
        aReddit.setTag("http://www.reddit.com/r/worldofwarships");
        aDRMB.setTag("http://www.dontrevivemebro.com");
        aAP.setTag("http://thearmoredpatrol.com/");
        aWoWsReplays.setTag("http://wowreplays.com/");

        aWowsSite.setOnClickListener(listener);
        aShipComrade.setOnClickListener(listener);
        aReddit.setOnClickListener(listener);
        aDRMB.setOnClickListener(listener);
        aAP.setOnClickListener(listener);
        aWoWsReplays.setOnClickListener(listener);

        ivWoWsIcon.setImageResource(R.drawable.ic_wows_logo);
        ivShipComrade.setImageResource(R.drawable.ic_ship_comrade);
        ivReddit.setImageResource(R.drawable.ic_reddit);
        ivDRMB.setImageResource(R.drawable.twitch);
        ivAP.setImageResource(R.drawable.ic_armored_patrol);
        ivWoWsReplays.setImageResource(R.drawable.ic_wowsreplays);
    }

    private void requestNewInterstitial() {
        adProgress.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
        viewAd = false;
    }

    private void createServer(Server s) {
        View serverInfo = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_server_info, llServerContainer, false);

        TextView serverName = (TextView) serverInfo.findViewById(R.id.server_info_name);

        serverName.setText(s.toString().toUpperCase());

        LinearLayout wotContainer = (LinearLayout) serverInfo.findViewById(R.id.server_info_container_1);
        LinearLayout wowsContainer = (LinearLayout) serverInfo.findViewById(R.id.server_info_container_2);

        List<ServerInfo> currentServerWoT = new ArrayList<>();
        List<ServerInfo> currentServerWoWs = new ArrayList<>();

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
        View serverWoTTitle = LayoutInflater.from(getApplicationContext()).inflate(layoutId, wotContainer, false);
        TextView tvwot = (TextView) serverWoTTitle.findViewById(R.id.list_server_text);
        tvwot.setText(getString(R.string.resources_wot_total_c) + totalWot);


        View serverWowsTitle = LayoutInflater.from(getApplicationContext()).inflate(layoutId, wotContainer, false);
        TextView tvwows = (TextView) serverWowsTitle.findViewById(R.id.list_server_text);
        tvwows.setText(getString(R.string.resources_wows_total_c) + totalWoWs);

        wotContainer.addView(serverWoTTitle);
        wowsContainer.addView(serverWowsTitle);

        for (ServerInfo info : currentServerWoT) {
            View server = LayoutInflater.from(getApplicationContext()).inflate(layoutId, wotContainer, false);
            TextView text = (TextView) server.findViewById(R.id.list_server_text);
            text.setText(info.getName() + " - " + info.getPlayers());
            wotContainer.addView(server);
        }

        for (ServerInfo info : currentServerWoWs) {
            View server = LayoutInflater.from(getApplicationContext()).inflate(layoutId, wowsContainer, false);
            TextView text = (TextView) server.findViewById(R.id.list_server_text);
            text.setText(info.getName() + " - " + info.getPlayers());
            wowsContainer.addView(server);
        }
        llServerContainer.addView(serverInfo);
    }

    @Subscribe
    public void onTwitchReceived(final TwitchObj obj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                streamers.add(obj);
                initView();
            }
        });
    }


    @Subscribe
    public void onRecieveServers(ServerResult result) {
        if (result != null) {
            serverResult = result;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            });
        } else {
            serverResult = new ServerResult();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.resources_error, Toast.LENGTH_SHORT).show();
                    initView();
                }
            });
        }
    }

    @Subscribe
    public void urlSent(String url) {
        Dlog.wtf("urlSent", "url = " + url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    protected class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url)) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
                return true;
            }
            return false;
        }
    }
}