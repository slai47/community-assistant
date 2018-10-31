package com.half.wowsca.ui.viewcaptain;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;



import com.half.wowsca.CAApp;
import com.half.wowsca.NumberVault;
import com.half.wowsca.R;
import com.half.wowsca.alerts.Alert;
import com.half.wowsca.backend.GetCaptainTask;
import com.half.wowsca.interfaces.ICaptain;
import com.half.wowsca.managers.CaptainManager;
import com.half.wowsca.managers.StorageManager;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;
import com.half.wowsca.model.enums.Server;
import com.half.wowsca.model.events.AddRemoveCaptainEvent;
import com.half.wowsca.model.events.AddRemoveEvent;
import com.half.wowsca.model.events.CaptainReceivedEvent;
import com.half.wowsca.model.events.RefreshEvent;
import com.half.wowsca.model.events.ShipClickedEvent;
import com.half.wowsca.model.queries.CaptainQuery;
import com.half.wowsca.model.result.CaptainResult;
import com.half.wowsca.ui.CABaseActivity;
import com.half.wowsca.ui.UIUtils;
import com.half.wowsca.ui.viewcaptain.tabs.CaptainShipsFragment;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;
import com.utilities.views.SwipeBackLayout;

import java.util.Map;

public class ViewCaptainActivity extends CABaseActivity implements ICaptain {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_SERVER = "server";

    private int id;
    private String name;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        if (savedInstanceState == null) {
            id = getIntent().getIntExtra(EXTRA_ID, 0);
            name = getIntent().getStringExtra(EXTRA_NAME);
            try {
                server = Server.valueOf(getIntent().getStringExtra(EXTRA_SERVER));
            } catch (IllegalArgumentException e) {
                server = CAApp.getServerType(getApplicationContext());
            }
        } else {
            id = savedInstanceState.getInt(EXTRA_ID);
            name = savedInstanceState.getString(EXTRA_NAME);
            server = Server.valueOf(getIntent().getStringExtra(EXTRA_SERVER));
        }
    }

    private void bindView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initBackStackListener();
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
        getSupportFragmentManager().addOnBackStackChangedListener(backStackListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
        getSupportFragmentManager().removeOnBackStackChangedListener(backStackListener);
    }

    private void initView() {
        //place fragment in with variables

        Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
        if (current == null) {
            ViewCaptainTabbedFragment fragment = new ViewCaptainTabbedFragment();
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.container, fragment).commit();
        }
        Captain captain = null;
        if (CaptainManager.fromSearch(getApplicationContext(), server, id)) {
            captain = CaptainManager.getTEMP(getApplicationContext());
        } else {
            captain = CaptainManager.getCaptains(getApplicationContext()).get(CaptainManager.createCapIdStr(server, id));
        }
        boolean search = false;
        if (captain == null) {
            search = true;
        } else if (captain.getShips() == null) {
            search = true;
        }
        if (FORCE_REFRESH)
            search = true;

        setTopTitle(captain);

        Dlog.wtf("ViewCaptain", "captain = " + (captain != null) + " search = " + search + " force = " + FORCE_REFRESH);
        boolean connected = Utils.hasInternetConnection(this);
        if (connected) {
            if (search) {
                FORCE_REFRESH = false;
                CaptainQuery query = new CaptainQuery();
                query.setId(id);
                query.setName(name);
                query.setServer(server);
                GetCaptainTask task = new GetCaptainTask();
                task.setCtx(getApplicationContext());
                task.execute(query);
            } else {
            }
        } else {
            Alert.generalNoInternetDialogAlert(this, getString(R.string.no_internet_title), getString(R.string.no_internet_message), getString(R.string.no_internet_neutral_text));
        }
    }

    private void setTopTitle(Captain captain) {
        if(captain != null){
            StringBuilder sb = new StringBuilder();
            if(!TextUtils.isEmpty(captain.getClanName())) {
                sb.append("[" + captain.getClanName() + "] ");
            }
            sb.append(captain.getName());
            setTitle(sb.toString());
        } else{
            setTitle(name);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_ID, id);
        outState.putString(EXTRA_NAME, name);
        outState.putString(EXTRA_SERVER, server.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof ViewCaptainTabbedFragment) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
//            if (CaptainManager.getCaptains(getApplicationContext()).size() > 1) {
                menu.getItem(NumberVault.MENU_CAPTAINS).setVisible(false); // shows other captains
//            }
            menu.getItem(NumberVault.MENU_REFRESH).setVisible(true); // shows refresh
            menu.getItem(NumberVault.MENU_VIEW_AD).setVisible(false);
            if (CaptainManager.createCapIdStr(server, id).equals(CAApp.getSelectedId(getApplicationContext()))) {
                menu.getItem(NumberVault.MENU_BOOKMARK).setVisible(true); // shows bookmark
                menu.getItem(NumberVault.MENU_BOOKMARK).setTitle(R.string.menu_unfavorite);
                menu.getItem(NumberVault.MENU_BOOKMARK).setIcon(R.drawable.ic_drawer_favorite);
            } else {
                menu.getItem(NumberVault.MENU_BOOKMARK).setVisible(true); // shows bookmark
                menu.getItem(NumberVault.MENU_BOOKMARK).setTitle(R.string.menu_favorite);
                menu.getItem(NumberVault.MENU_BOOKMARK).setIcon(R.drawable.ic_drawer_not_favorite);
            }
            if (CaptainManager.getCaptains(getApplicationContext()).get(CaptainManager.createCapIdStr(server, id)) != null) {
                menu.getItem(NumberVault.MENU_SAVE).setIcon(R.drawable.ic_action_trash);
                menu.getItem(NumberVault.MENU_SAVE).setTitle(R.string.delete_captain);
            } else {
                menu.getItem(NumberVault.MENU_SAVE).setIcon(R.drawable.ic_action_save);
                menu.getItem(NumberVault.MENU_SAVE).setTitle(R.string.save_captain);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i = null;
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_refresh:
                if (CaptainManager.fromSearch(getApplicationContext(), server, id)) {
                    CaptainManager.deleteTemp(getApplicationContext());
                }
                FORCE_REFRESH = true;
                CAApp.getEventBus().post(new RefreshEvent(false));
                initView();
                break;
            case R.id.action_captains:
                UIUtils.createCaptainListViewMenu(this, CaptainManager.createCapIdStr(server, this.id));
                break;
            case R.id.action_bookmark:
                String selectedId = CAApp.getSelectedId(getApplicationContext());
                AddRemoveCaptainEvent event = new AddRemoveCaptainEvent();
                if (!CaptainManager.createCapIdStr(server, this.id).equals(selectedId)) {
                    if (CaptainManager.getCaptains(getApplicationContext()).get(this.id) == null) {
                        CaptainManager.saveCaptain(getApplicationContext(), getCaptain(getApplicationContext()));
                    }
                    event.setRemoved(false);
                    CAApp.setSelectedId(getApplicationContext(), CaptainManager.createCapIdStr(server, this.id));
                    FORCE_REFRESH = true;
                    invalidateOptionsMenu();
                } else {
                    event.setRemoved(true);
                    CaptainManager.removeCaptain(getApplicationContext(), CaptainManager.createCapIdStr(server, this.id));
                    CAApp.setSelectedId(getApplicationContext(), null);
                }
                CAApp.getEventBus().post(event);
                break;
            case R.id.action_save:
                Map<String, Captain> captains = CaptainManager.getCaptains(getApplicationContext());
                Captain captain = getCaptain(getApplicationContext());
                if(captain != null) {
                    AddRemoveEvent e = new AddRemoveEvent();
                    e.setCaptain(captain);
                    if (captains.get(CaptainManager.getCapIdStr(captain)) == null) {
                        e.setRemove(false);
                        Toast.makeText(getApplicationContext(), captain.getName() + " " + getString(R.string.list_clan_added_message), Toast.LENGTH_SHORT).show();
                    } else {
                        e.setRemove(true);
                        Toast.makeText(getApplicationContext(), captain.getName() + " " + getString(R.string.list_clan_removed_message), Toast.LENGTH_SHORT).show();
                    }
                    CAApp.getEventBus().post(e);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.crap_contact_me, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_warships_today:
                Captain captain2 = getCaptain(getApplicationContext());
                if(captain2 != null) {
                    String url = "http://" + captain2.getServer().getWarshipsToday() + ".warships.today/player/" + captain2.getId() + "/" + captain2.getName();
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
            if (CaptainManager.fromSearch(getApplicationContext(), server, id)) {
                CaptainManager.deleteTemp(getApplicationContext());
            }
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
                        if (CaptainManager.getCapIdStr(captain).equals(CaptainManager.createCapIdStr(server, id))) {
                            Prefs prefs = new Prefs(getApplicationContext());
                            prefs.setString(CaptainShipsFragment.SAVED_SORT, "Battle");
                            Map<String, Captain> captains = CaptainManager.getCaptains(getApplicationContext());
                            if (captains.get(CaptainManager.getCapIdStr(captain)) != null) {
                                CaptainManager.saveCaptain(mToolbar.getContext(), captain);
                                StorageManager.savePlayerStats(mToolbar.getContext(), captain);
                            } else {
                                CaptainManager.saveTempStoredCaptain(getApplicationContext(), captain);
                            }
                            setTopTitle(captain);
                            CAApp.getEventBus().post(new CaptainReceivedEvent());
                        } else {
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
        Captain captain = null;
        if (CaptainManager.fromSearch(getApplicationContext(), server, id)) {
            captain = CaptainManager.getTEMP(getApplicationContext());
        } else {
            captain = CaptainManager.getCaptains(getApplicationContext()).get(CaptainManager.createCapIdStr(server, id));
        }
        return captain;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Subscribe
    public void onAddRemove(AddRemoveEvent event) {
        if (!event.isRemove()) {
            UIUtils.createBookmarkingDialogIfNeeded(this, event.getCaptain());
            CaptainManager.saveCaptain(getApplicationContext(), getCaptain(getApplicationContext()));
        } else {
            CaptainManager.removeCaptain(getApplicationContext(), CaptainManager.getCapIdStr(event.getCaptain()));
            String selectedId = CAApp.getSelectedId(getApplicationContext());
            if (CaptainManager.getCapIdStr(event.getCaptain()).equals(selectedId)) {
                CAApp.setSelectedId(getApplicationContext(), null);
            }
            finish();
        }
        invalidateOptionsMenu();
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
}