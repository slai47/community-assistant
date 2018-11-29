package com.half.wowsca.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;



import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.half.wowsca.CAApp;
import com.half.wowsca.NumberVault;
import com.half.wowsca.R;
import com.half.wowsca.alerts.Alert;
import com.half.wowsca.backend.GetCaptainTask;
import com.half.wowsca.interfaces.ICaptain;
import com.half.wowsca.managers.CaptainManager;
import com.half.wowsca.managers.NavigationDrawerManager;
import com.half.wowsca.managers.StorageManager;
import com.half.wowsca.model.AuthInfo;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.drawer.DrawerChild;
import com.half.wowsca.model.enums.Server;
import com.half.wowsca.model.events.AddRemoveEvent;
import com.half.wowsca.model.events.CaptainReceivedEvent;
import com.half.wowsca.model.events.ProgressEvent;
import com.half.wowsca.model.events.RefreshEvent;
import com.half.wowsca.model.events.ShipClickedEvent;
import com.half.wowsca.model.queries.CaptainQuery;
import com.half.wowsca.model.result.CaptainResult;
import com.half.wowsca.ui.encyclopedia.EncyclopediaTabbedActivity;
import com.half.wowsca.ui.viewcaptain.ShipFragment;
import com.half.wowsca.ui.viewcaptain.ViewCaptainActivity;
import com.half.wowsca.ui.viewcaptain.ViewCaptainTabbedFragment;
import com.half.wowsca.ui.viewcaptain.tabs.CaptainShipsFragment;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import org.greenrobot.eventbus.Subscribe;

import java.util.Collection;
import java.util.Map;

public class MainActivity extends CABaseActivity implements ICaptain {

    public static final String HAS_LEARNED_ABOUT_DRAWER = "has_learned_about_drawer";

    private String selectedId;

    private Drawer drawer;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        selectedId = CAApp.getSelectedId(getApplicationContext());
    }

    private void bindView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);

//        ivKarma = (ImageView) findViewById(R.id.toolbar_icon);
//        tvKarma = (TextView) findViewById(R.id.toolbar_text);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(!CAApp.DEVELOPMENT_MODE ? getString(R.string.ad_unit_id) : getString(R.string.test_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }
        });

        setSupportActionBar(mToolbar);

        setUpDrawer();

        initBackStackListener();

        getSwipeBackLayout().setEnableGesture(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        invalidateOptionsMenu();
        initView();
        setUpDrawer();
        routeShortcuts();
        getSupportFragmentManager().addOnBackStackChangedListener(backStackListener);
    }

    private void routeShortcuts() {
        if(CAApp.ROUTING != null){
            Intent i = null;
            switch (CAApp.ROUTING){
                case ENCYCLOPEDIA:
                    i = new Intent(getApplicationContext(), EncyclopediaTabbedActivity.class);
                    break;
                case SEARCH:
                    i = new Intent(getApplicationContext(), SearchActivity.class);
                    break;
                case TWITCH:
                    i = new Intent(getApplicationContext(), ResourcesActivity.class);
                    i.putExtra(ResourcesActivity.EXTRA_TYPE, ResourcesActivity.EXTRA_TWITCH);
                    break;
                case AD_LAUNCH:
                    requestNewInterstitial();
                    break;
            }
            if(i != null) {
                startActivity(i);
            }
            CAApp.ROUTING = null;
        }
    }

    private void initView() {
        drawer.setSelection(-1, false);
        Prefs pref = new Prefs(getApplicationContext());
        int launchCount = pref.getInt(CAApp.LAUNCH_COUNT, 0);
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.container);
        selectedId = CAApp.getSelectedId(getApplicationContext());
        Map<String, Captain> captains = CaptainManager.getCaptains(getApplicationContext());
        Captain captain = captains.get(selectedId);
        if (captain != null) {
            //else if player show that fragment
            setCaptainTitle(captain);

            selectedId = CaptainManager.createCapIdStr(captain.getServer(), captain.getId());
            boolean switchFragment = false;
            if (currentFrag == null) {
                switchFragment = true;
            } else if (!(currentFrag instanceof ViewCaptainTabbedFragment)) {
                switchFragment = true;
            }

            if (switchFragment) {
                try {
                    ViewCaptainTabbedFragment fragment = new ViewCaptainTabbedFragment();
                    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                    trans.replace(R.id.container, fragment).commit();
                } catch (IllegalStateException e) {
                } catch (Exception e){
                }
            }
            if (captain.getShips() == null || FORCE_REFRESH) {
                CAApp.getEventBus().post(new ProgressEvent(true));
                FORCE_REFRESH = false;
                boolean connected = Utils.hasInternetConnection(this);
                if (connected) {
                    boolean useLogin = new Prefs(getApplicationContext()).getBoolean(SettingActivity.LOGIN_USER, false);
                    if(useLogin) {
                        AuthInfo info = AuthInfo.getAuthInfo(getApplicationContext());
                        boolean sameUser = captain.getName().equals(info.getUsername());
                        if (sameUser && !info.isExpired()) {
                            grabCaptain(captain, info);
                        } else {
                            Intent t = new Intent(getApplicationContext(), AuthenticationActivity.class);
                            startActivity(t);
                            FORCE_REFRESH = true;
                        }
                    } else {
                        grabCaptain(captain, null);
                    }
                } else {
                    Alert.generalNoInternetDialogAlert(this, getString(R.string.no_internet_title), getString(R.string.no_internet_message), getString(R.string.no_internet_neutral_text));
                }
            } else {
//                setUpKarma(captain);
            }
        } else {
            //if no player selected, show default
            launchCount = showDefaultScreen(pref, launchCount);
        }
        showDialogs(launchCount);
    }

    private void setCaptainTitle(Captain captain) {
        StringBuilder sb = new StringBuilder();
        if(!TextUtils.isEmpty(captain.getClanName())) {
            sb.append("[" + captain.getClanName() + "] ");
        }
        sb.append(captain.getName());
        setTitle(sb.toString());
    }

    private void showDialogs(int launchCount) {
        Prefs prefs = new Prefs(this);
        boolean hasUserLearnedDrawer = prefs.getBoolean(HAS_LEARNED_ABOUT_DRAWER, false);
        if (hasUserLearnedDrawer) {
            if (launchCount > 2) {
                if (launchCount % 5 == 0 && !CAApp.HAS_SHOWN_FIRST_DIALOG) {
                    UIUtils.createDonationDialog(this);
                }

                if (launchCount % 8 == 0 && !CAApp.HAS_SHOWN_FIRST_DIALOG) {
                    UIUtils.createReviewDialog(this);
                }

                if (launchCount % 13 == 0 && !CAApp.HAS_SHOWN_FIRST_DIALOG) {
                    UIUtils.createFollowDialog(this);
                }
            }
        }
    }

    private int showDefaultScreen(Prefs pref, int launchCount) {
        setTitle(getString(R.string.welcome));
        DefaultFragment fragment = new DefaultFragment();
        try {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.container, fragment).commit();
        } catch (Exception e) {
        }
        if (launchCount < 2) {
            launchCount++;
            pref.setInt(CAApp.LAUNCH_COUNT, launchCount);
            Intent i = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(i);
        }
        return launchCount;
    }

    private void grabCaptain(Captain captain, AuthInfo info) {
        CaptainQuery query = new CaptainQuery();
        query.setId(captain.getId());
        query.setName(captain.getName());
        query.setServer(captain.getServer());
        query.setToken(info != null ? info.getToken() : null);

        GetCaptainTask task = new GetCaptainTask();
        task.setCtx(getApplicationContext());
        task.execute(query);
    }

    private void setUpDrawer() {
        Prefs prefs = new Prefs(this);
        boolean hasUserLearnedDrawer = prefs.getBoolean(HAS_LEARNED_ABOUT_DRAWER, false);
        if (drawer == null) {
            drawer = NavigationDrawerManager.createDrawer(this,
                    mToolbar,
                    new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if (drawerItem != null) {
                                DrawerChild drawerObj = (DrawerChild) drawerItem.getTag();
                                Intent i = null;
                                if (drawerObj != null) {
                                    switch (drawerObj.getType()) {
                                        case SEARCH:
                                            i = new Intent(getApplicationContext(), SearchActivity.class);
                                            break;
                                        case CAPTAIN:
                                            Server server = drawerObj.getServer();
                                            String captainName = drawerObj.getTitle();
                                            Map<String, Captain> captains = CaptainManager.getCaptains(getApplicationContext());
                                            Collection<Captain> caps = captains.values();
                                            for (Captain captain : caps) {
                                                if (captain.getName().equals(captainName) && captain.getServer().ordinal() == server.ordinal()) {
                                                    i = new Intent(getApplicationContext(), ViewCaptainActivity.class);
                                                    i.putExtra(ViewCaptainActivity.EXTRA_ID, captain.getId());
                                                    i.putExtra(ViewCaptainActivity.EXTRA_SERVER, captain.getServer().toString());
                                                    i.putExtra(ViewCaptainActivity.EXTRA_NAME, captain.getName());
                                                    break;
                                                }
                                            }
                                            break;
                                        case SHIPOPEDIA:
                                            i = new Intent(getApplicationContext(), EncyclopediaTabbedActivity.class);
                                            break;
                                        case WEBSITES_TOOLS:
                                            i = new Intent(getApplicationContext(), ResourcesActivity.class);
                                            i.putExtra(ResourcesActivity.EXTRA_TYPE, ResourcesActivity.EXTRA_WEBSITES_TOOLS);
                                            break;
                                        case TWITCH:
                                            i = new Intent(getApplicationContext(), ResourcesActivity.class);
                                            i.putExtra(ResourcesActivity.EXTRA_TYPE, ResourcesActivity.EXTRA_TWITCH);
                                            break;
                                        case DONATE:
                                            i = new Intent(getApplicationContext(), ResourcesActivity.class);
                                            i.putExtra(ResourcesActivity.EXTRA_TYPE, ResourcesActivity.EXTRA_DONATE);
                                            break;
                                        case SETTINGS:
                                            i = new Intent(getApplicationContext(), SettingActivity.class);
                                            break;
                                        case SERVER:
                                            i = new Intent(getApplicationContext(), ResourcesActivity.class);
                                            i.putExtra(ResourcesActivity.EXTRA_TYPE, ResourcesActivity.EXTRA_SERVERS);
                                            break;
                                    }
                                    if (i != null) {
                                        CAApp.setLastShipPos(0);
                                        startActivity(i);
                                        drawer.closeDrawer();
                                    }
                                }
                            }
                            return true;
                        }
                    }
            );
            if (!hasUserLearnedDrawer && !drawer.isDrawerOpen()) {
                drawer.openDrawer();
                prefs.setBoolean(HAS_LEARNED_ABOUT_DRAWER, true);
            }
        } else {
            drawer.removeAllItems();
            drawer.addItems(NavigationDrawerManager.getDrawerItemList(getApplicationContext()));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
        getSupportFragmentManager().removeOnBackStackChangedListener(backStackListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof ViewCaptainTabbedFragment) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            String selected_id = CAApp.getSelectedId(getApplicationContext());
            if (selected_id != null) {
                menu.getItem(NumberVault.MENU_REFRESH).setVisible(true); // show refresh

                menu.getItem(NumberVault.MENU_SAVE).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                menu.getItem(NumberVault.MENU_SAVE).setIcon(R.drawable.ic_action_trash);
                menu.getItem(NumberVault.MENU_SAVE).setTitle(R.string.delete_captain);

                menu.getItem(NumberVault.MENU_BOOKMARK).setVisible(true); // shows unbookmark
                menu.getItem(NumberVault.MENU_BOOKMARK).setTitle(R.string.menu_unfavorite);
                menu.getItem(NumberVault.MENU_BOOKMARK).setIcon(R.drawable.ic_drawer_favorite);

                menu.getItem(NumberVault.MENU_VIEW_AD).setVisible(true);
            } else {
                menu.getItem(NumberVault.MENU_SAVE).setVisible(false);
            }
            menu.getItem(NumberVault.MENU_CAPTAINS).setVisible(false);
        } else {

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                FORCE_REFRESH = true;
                CAApp.getEventBus().post(new RefreshEvent(false));
                initView();
                break;
            case R.id.action_bookmark:
                CAApp.setSelectedId(getApplicationContext(), null);
                selectedId = null;
                initView();
                invalidateOptionsMenu();
                break;
            case R.id.action_save:
                Map<String, Captain> captains = CaptainManager.getCaptains(getApplicationContext());
                Captain captain = captains.get(selectedId);
                if (captain != null) {
                    AddRemoveEvent event = new AddRemoveEvent();
                    event.setCaptain(captain);
                    event.setRemove(true);
                    Toast.makeText(getApplicationContext(), captain.getName() + " " + getString(R.string.list_clan_removed_message), Toast.LENGTH_SHORT).show();
                    CAApp.getEventBus().post(event);
                }
                break;
            case R.id.action_view_ad:
                Intent i = new Intent(getApplicationContext(), ResourcesActivity.class);
                i.putExtra(ResourcesActivity.EXTRA_TYPE, ResourcesActivity.EXTRA_DONATE);
                i.putExtra(ResourcesActivity.EXTRA_VIEW_AD, true);
                startActivity(i);
                break;
            case R.id.action_warships_today:
                Captain captain2 = getCaptain(getApplicationContext());
                if(captain2 != null) {
                    String url = "http://"+captain2.getServer().getWarshipsToday()+".warships.today/player/" + captain2.getId() + "/" + captain2.getName();
                    Intent i2 = new Intent(Intent.ACTION_VIEW);
                    i2.setData(Uri.parse(url));
                    startActivity(i2);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onRefresh(RefreshEvent event){
        if(event.isFromSwipe()){
            FORCE_REFRESH = true;
            initView();
        }
    }

    @Subscribe
    public void onRecieveCaptain(final CaptainResult result) {
        if (result != null) {
            mToolbar.post(new Runnable() {
                @Override
                public void run() {
                    Captain captain = result.getCaptain();
                    if (captain != null && !result.isHidden()) {
                        Dlog.wtf("ViewCaptain", "id = " + selectedId + " captain = " + captain);
                        if (CaptainManager.createCapIdStr(captain.getServer(), captain.getId()).equals(selectedId)) {
                            Prefs prefs = new Prefs(getApplicationContext());
                            prefs.setString(CaptainShipsFragment.SAVED_SORT, "Battle");
                            CaptainManager.saveCaptain(getApplicationContext(), captain);
                            setCaptainTitle(captain);
                            StorageManager.savePlayerStats(mToolbar.getContext(), captain);
                            CAApp.getEventBus().post(new CaptainReceivedEvent());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), result.isHidden() ? getString(R.string.player_private) : getString(R.string.failure_in_loading), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public Captain getCaptain(Context ctx) {
        return CaptainManager.getCaptains(getApplicationContext()).get(selectedId);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof ViewCaptainTabbedFragment) {
            if (!drawer.isDrawerOpen()) {
                drawer.openDrawer();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe
    public void onAddRemove(AddRemoveEvent event) {
        if (!event.isRemove()) {
            UIUtils.createBookmarkingDialogIfNeeded(this, event.getCaptain());
            CaptainManager.saveCaptain(getApplicationContext(), getCaptain(getApplicationContext()));
        } else {
            CaptainManager.removeCaptain(getApplicationContext(), CaptainManager.createCapIdStr(event.getCaptain().getServer(), event.getCaptain().getId()));
            CAApp.setSelectedId(getApplicationContext(), null);
            selectedId = null;
            initView();
        }
        invalidateOptionsMenu();
        setUpDrawer();
    }

    @Subscribe
    public void showShip(ShipClickedEvent ship) {
        if (ship != null) {
            ShipFragment shipFragment = new ShipFragment();
            shipFragment.setId(ship.getId());
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left, R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
                    .replace(R.id.container, shipFragment)
                    .addToBackStack("ship")
                    .commit();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }
}
