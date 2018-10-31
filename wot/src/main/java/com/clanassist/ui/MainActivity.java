package com.clanassist.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.SVault;
import com.clanassist.backend.Tasks.DownloadClanAsync;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.enums.DrawerType;
import com.clanassist.model.events.ClanPlayerAddRemoveEvent;
import com.clanassist.model.events.UpdateClanListEvent;
import com.clanassist.model.events.details.PlayerRefreshEvent;
import com.clanassist.model.holders.DrawerChild;
import com.clanassist.model.interfaces.IDetails;
import com.clanassist.model.interfaces.IRefresh;
import com.clanassist.model.player.Player;
import com.clanassist.tools.CPManager;
import com.clanassist.tools.NavigationDrawerManager;
import com.clanassist.ui.details.DetailsTabbedFragment;
import com.clanassist.ui.images.ImageFragment;
import com.clanassist.ui.search.CPSearchFragment;
import com.clanassist.ui.search.EncyclopediaSearchFragment;
import com.cp.assist.R;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.otto.Subscribe;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    public static final String HAS_USER_LEARNED_DRAWER = "hasUserLearnedDrawer";
    private final String EXTRA_POSITION = "position";

    private Toolbar mToolbar;
    private DrawerLayout drawerLayout;

    private Drawer.Result drawer;

    private int lastPosition;

    private final String PREF_VERISON_CONTROL = "verison_number";

    private BackStackListner listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set language
        String current = CAApp.getAppLanguage(getApplicationContext());
        Locale myLocale = new Locale(current);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        CAApp.setTheme(this);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(mToolbar);
        if (savedInstanceState != null) {
            lastPosition = savedInstanceState.getInt(EXTRA_POSITION);
        }
        SharedPreferences prefs = getSharedPreferences(CAApp.PREF_FILE, Context.MODE_PRIVATE);
        String verison = prefs.getString(PREF_VERISON_CONTROL, "1.0");
        String currentVerison = "";
        try {
            currentVerison = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!currentVerison.equals(verison)) {
            SharedPreferences.Editor e = prefs.edit();
            e.putString(PREF_VERISON_CONTROL, currentVerison);
            e.commit();
//            Context ctx = this;
//            Alert.createGeneralAlert(ctx, getString(R.string.recent_changes_title), getString(R.string.recent_changes_news), getString(R.string.dismiss));
        }
        updateDrawer(savedInstanceState, false, false, false);
    }

    private void setUpDrawer() {
        Prefs prefs = new Prefs(this);
        boolean hasUserLearnedDrawer = prefs.getBoolean(HAS_USER_LEARNED_DRAWER, false);
        if (drawer == null) {
            drawer = NavigationDrawerManager.createDrawer(this,
                    mToolbar,
                    new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id, IDrawerItem iDrawerItem) {
                            lastPosition = position;
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            Fragment frag = null;
                            if (iDrawerItem != null) {
                                DrawerChild drawerObj = (DrawerChild) iDrawerItem.getTag();
                                Answers.getInstance().logCustom(new CustomEvent("Drawer Select").putCustomAttribute("Type", drawerObj.getType().toString()));
                                switch (drawerObj.getType()) {
                                    case CLAN:
                                        if (iDrawerItem.getIdentifier() == NavigationDrawerManager.CLAN_SEARCH_ID) {
                                            CPSearchFragment.SEARCH_TYPE = drawerObj.getType();
                                            frag = new CPSearchFragment();
                                        } else if (iDrawerItem.getIdentifier() != NavigationDrawerManager.CLAN_GROUP_ID) {
                                            CAApp.SELECTED_VEHICLE_ID = 0;
                                            DetailsTabbedFragment frag2 = new DetailsTabbedFragment();
                                            frag2.setFromSearch(false);
                                            frag2.setAccountId(iDrawerItem.getIdentifier());
                                            frag2.setName(drawerObj.getTitle());
                                            frag2.setPlayer(false);
                                            frag = frag2;
                                        }
                                        break;
                                    case ACTIVITY:
                                        frag = new ActivityFragment();
                                        break;
                                    case PLAYER:
                                        if (iDrawerItem.getIdentifier() == NavigationDrawerManager.PLAYER_SEARCH_ID) {
                                            CPSearchFragment.SEARCH_TYPE = drawerObj.getType();
                                            frag = new CPSearchFragment();
                                        } else if (iDrawerItem.getIdentifier() != NavigationDrawerManager.PLAYER_GROUP_ID) {
                                            CAApp.SELECTED_VEHICLE_ID = 0;
                                            DetailsTabbedFragment frag2 = new DetailsTabbedFragment();
                                            frag2.setFromSearch(false);
                                            frag2.setAccountId(iDrawerItem.getIdentifier());
                                            frag2.setName(drawerObj.getTitle());
                                            frag2.setPlayer(true);
                                            frag = frag2;
                                        }
                                        break;
                                    case DEFAULT_PLAYER:
                                        CAApp.SELECTED_VEHICLE_ID = 0;
                                        DetailsTabbedFragment frag2 = new DetailsTabbedFragment();
                                        frag2.setFromSearch(false);
                                        frag2.setAccountId(iDrawerItem.getIdentifier());
                                        frag2.setName(drawerObj.getTitle());
                                        frag2.setPlayer(true);
                                        frag = frag2;
                                        break;
                                    case DONATE:
                                        frag = new DonationFragment();
                                        break;
                                    case WN8:
                                        EncyclopediaSearchFragment.TYPE = DrawerType.WN8;
                                        frag = new EncyclopediaSearchFragment();
                                        break;
//                                    case MAPS:
//                                        EncyclopediaSearchFragment.TYPE = DrawerType.MAPS;
//                                        frag = new EncyclopediaSearchFragment();
//                                        break;
                                    case TWITCH:
                                        frag = new TwitchFragment();
                                        break;
                                    case WEBSITE:
                                        frag = new WebsiteFragment();
                                        break;
                                    case SETTINGS:
                                        frag = new SettingsInfoFragment();
                                        break;
                                    case SERVER_INFO:
                                        frag =  new ServerInfoFragment();
                                        break;
                                    case DEFAULT:
                                        frag = new DefaultFragment();
                                }

                                if (frag != null) {
                                    FragmentManager fm = getSupportFragmentManager();
                                    for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                        fm.popBackStack();
                                    }
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.container, frag).commit();
                                    Dlog.d("MainActivity", "replacing");
                                    closeKeyboard(MainActivity.this, mToolbar);
                                }
                            }
                        }
                    }
            );

            if (!hasUserLearnedDrawer && !drawer.isDrawerOpen()) {
                drawer.openDrawer();
                prefs.setBoolean(HAS_USER_LEARNED_DRAWER, true);
            }
        } else {
            drawer.removeAllItems();
            drawer.addItems(NavigationDrawerManager.getDrawerItemList(getApplicationContext()));
        }
    }

    public void closeKeyboard(Activity activity, View v) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void updateDrawer(Bundle savedInstanceState, boolean refresh, boolean added, boolean setDefault) {
        setUpDrawer();
        Prefs prefs = new Prefs(this);
        int selectedId = prefs.getInt(SVault.PREF_SELECTED_USER_ID, 0);
        if (!refresh) {
            if (savedInstanceState == null) {
                drawer.setSelection(0);
            } else {
                if (!isFragmentThere()) {
                    drawer.setSelection(lastPosition);
                } else {
                    drawer.setSelection(-1);
                }
            }
        } else if (refresh) {
            if (setDefault) {
                drawer.setSelection(0);
            } else {
                if (!added) {
                    if (selectedId > 0)
                        drawer.setSelection(1);
                    else
                        drawer.setSelection(0);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        if (!isFragmentThere()) {
            drawer.setSelectionByIdentifier(NavigationDrawerManager.DEFAULT_GROUP);
        }
        Prefs pref = new Prefs(getApplicationContext());
        int launchCount = pref.getInt(CAApp.LAUNCH_AMOUNT, 0);
        if (launchCount > 2) {
            if (launchCount % 5 == 0 && !CAApp.HAS_SHOWN_FIRST_DIALOG) {
                UIUtils.createDonationDialog(this);
            }

            if (launchCount % 8 == 0 && !CAApp.HAS_SHOWN_FIRST_DIALOG) {
                UIUtils.createReviewDialog(this);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    public boolean isFragmentThere() {
        return getSupportFragmentManager().findFragmentById(R.id.container) != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.container);
        boolean isDarkTheme = CAApp.isLightTheme(getApplicationContext());
        if (frag instanceof IRefresh) {
            int refreshMenu = R.menu.refresh_menu;
            getMenuInflater().inflate(refreshMenu, menu);
        } else if (frag instanceof DetailsTabbedFragment) {
            int detailsMenu = R.menu.details_menu;
            getMenuInflater().inflate(detailsMenu, menu);
            if (!((IDetails) frag).isPlayer()) {
                menu.removeItem(R.id.menu_details_refresh);
                menu.removeItem(R.id.menu_details_default);
            } else {
                menu.removeItem(R.id.menu_details_download);
                int accountId = ((DetailsTabbedFragment) frag).getAccountId();
                int selected = CAApp.getDefaultId(getApplicationContext());
                if (accountId == selected) {
                    menu.getItem(1).setIcon(R.drawable.ic_drawer_favorite);
                } else {
                    menu.getItem(1).setIcon(R.drawable.ic_drawer_not_favorite);
                }
            }
        } else {
            menu.clear();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.container);
        switch (id) {
            case R.id.menu_refresh:
                if (frag instanceof IRefresh) {
                    IRefresh refresh = (IRefresh) frag;
                    refresh.refresh();
                }
                break;
            case R.id.menu_details_refresh:
                CAApp.getEventBus().post(new PlayerRefreshEvent());
                break;
            case R.id.menu_details_default:
                try {
                    DetailsTabbedFragment fragment4 = (DetailsTabbedFragment) frag;
                    int account = fragment4.getAccountId();
                    String name = fragment4.getName();
                    if (fragment4.isPlayer()) {
                        int defaultId = CAApp.getDefaultId(getApplicationContext());
                        if (account != defaultId) {
                            Map<Integer, Player> players = CPManager.getSavedPlayers(getApplicationContext());
                            if (!players.containsKey(account)) {
                                Player player = new Player();
                                player.setId(account);
                                player.setName(name);
                                CPManager.savePlayer(getApplicationContext(), player);
                            }
                            CAApp.setDefaultPlayer(getApplicationContext(), name, account);
                            item.setIcon(R.drawable.ic_drawer_favorite);
                        } else {
                            item.setIcon(R.drawable.ic_drawer_not_favorite);
                            CAApp.setDefaultPlayer(getApplicationContext(), null, 0);
                        }
                        updateDrawer(null, true, item.isChecked(), true);
                    }
                } catch (Exception e) {
                }
                break;
            case R.id.menu_details_download:
                fireOffClanDownloadIfPossible();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fireOffClanDownloadIfPossible() {
        IDetails details = (IDetails) getSupportFragmentManager().findFragmentById(R.id.container);
        Clan c = details.getClan(getApplicationContext());
        if(c != null) {
            boolean isTaskRunning = CAApp.isTaskRunning();
            boolean canDownload = CAApp.canDownloadClan(getApplicationContext(), c.getClanId() + "");
            boolean isDisbanded = c.isClanDisbanded();

            if (!isDisbanded) {
                if (!canDownload) {
                    setNextTimeToDownload(c);
                } else if (isTaskRunning) {
                    DownloadClanAsync task = CAApp.ongoingTask;
                    if (task != null) {
                        String cId = c.getClanId() + "";
                        if (!cId.equalsIgnoreCase(task.getClanId())) {
                            Toast.makeText(getApplicationContext(), R.string.clan_details_waiting_for_clan, Toast.LENGTH_LONG).show();
                        } else if (!canDownload)
                            setNextTimeToDownload(c);
                        else {
                            Toast.makeText(getApplicationContext(), R.string.clan_details_download_starts, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    fireClanDownload();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private void fireClanDownload() {
        boolean hasInternet = Utils.hasInternetConnection(getApplicationContext());
        if (hasInternet) {
            IDetails details = (IDetails) getSupportFragmentManager().findFragmentById(R.id.container);
            final Clan c = details.getClan(getApplicationContext());
            final String id = c.getClanId() + "";
            final String name = c.getAbbreviation();
            Prefs prefs = new Prefs(getApplicationContext());
            if(c.getMembers() != null && c.getMembers().size() > 0) {
                boolean firstTime = prefs.getBoolean(SVault.PREF_FIRST_TIME_DOWNLOAD_CLAN, true);
                if (firstTime) {
                    //fire off an alert with a message and if they agree, fire off the process.
                    final Context context = mToolbar.getContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.first_time_download_title));
                    builder.setMessage(context.getString(R.string.first_time_download_text));
                    builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton(R.string.download_dialog_pos_message, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Prefs prefs = new Prefs(context);
                            prefs.setBoolean(SVault.PREF_FIRST_TIME_DOWNLOAD_CLAN, false);
                            if (!CAApp.isTaskRunning()) {
                                //fire off task if they can
                                DownloadClanAsync tasks = new DownloadClanAsync(getApplicationContext(), CAApp.getEventBus(), id, name, c.getMembers());
                                tasks.start();
                                Toast.makeText(getApplicationContext(), R.string.clan_details_download_starts, Toast.LENGTH_SHORT).show();
                            } else {
                                //idk
                                Toast.makeText(getApplicationContext(), R.string.download_task_running, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                } else {
                    if (!CAApp.isTaskRunning()) {
                        //fire off task if they can
                        DownloadClanAsync tasks = new DownloadClanAsync(getApplicationContext(), CAApp.getEventBus(), id, name, c.getMembers());
                        tasks.start();
                        Toast.makeText(getApplicationContext(), R.string.clan_details_download_starts, Toast.LENGTH_SHORT).show();
                    } else {
                        //idk
                        Toast.makeText(getApplicationContext(), R.string.download_task_running, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.task_no_internet, Toast.LENGTH_SHORT).show();
        }

    }

    private void setNextTimeToDownload(Clan c) {
        long previousTime = CAApp.clanDownloadedTime(getApplicationContext(), c.getClanId() + "");
        previousTime += (1000 * 60 * 60 * 24 * CAApp.DAYS_BETWEEN_DOWNLOAD);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(previousTime);
        String nextDownloadTime = Utils.getDayMonthFormatter().format(cal.getTime()) + " " + Utils.getHourMinuteFormatter(getApplicationContext()).format(cal.getTime());
        Toast.makeText(getApplicationContext(), getString(R.string.tank_clan_member_next_download_time_text) + nextDownloadTime, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        boolean poppedBackStack = false;
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        if (f != null) {
            if (f instanceof ImageFragment || (f instanceof EncyclopediaSearchFragment && EncyclopediaSearchFragment.TYPE == DrawerType.CHOOSE)) {
                getSupportFragmentManager().addOnBackStackChangedListener(getListener());
                getSupportFragmentManager().popBackStack();
                poppedBackStack = true;
            } else if (f instanceof DetailsTabbedFragment && ((IDetails) f).isFromSearch()) {
                getSupportFragmentManager().addOnBackStackChangedListener(getListener());
                getSupportFragmentManager().popBackStack();
                poppedBackStack = true;
            }

        }
        if (!poppedBackStack) {
            if (!drawer.isDrawerOpen()) {
                drawer.openDrawer();
            } else {
                super.onBackPressed();
            }
        } else {
            if (drawer.isDrawerOpen())
                drawer.closeDrawer();
            invalidateOptionsMenu();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("test", false);
    }

    @Subscribe
    public void addRemoveClanPlayer(ClanPlayerAddRemoveEvent event) {
        if (event.getClan() != null) {
            if (!event.isRemove()) {
                CPManager.saveClan(getApplicationContext(), event.getClan());
            } else {
                CPManager.removeClan(getApplicationContext(), event.getClan());
            }
            UpdateClanListEvent event2 = new UpdateClanListEvent();
            event2.setClan(event.getClan());
            CAApp.getEventBus().post(event2);
        } else {
            Player p = new Player();
            p.setId(event.getPlayer().getId());
            p.setName(event.getPlayer().getName());
            if (!event.isRemove()) {
                CPManager.savePlayer(getApplicationContext(), p);
                UIUtils.createBookmarkingDialogIfNeeded(this, p);
            } else {
                int selected = CAApp.getDefaultId(getApplicationContext());
                if (event.getPlayer().getId() == selected) {
                    CAApp.setDefaultPlayer(getApplicationContext(), null, 0);
                }
                CPManager.removePlayer(getApplicationContext(), p);
            }
        }
        if (event.isRemove() && !event.isFromSearch()) {
            updateDrawer(null, true, false, false);
        } else {
            updateDrawer(null, true, true, false);
        }
    }

    public void selectDrawerGroup(int number) {
        drawer.setSelectionByIdentifier(number);
    }


    private BackStackListner getListener() {
        if (listener == null) {
            listener = new BackStackListner();
        }
        return listener;
    }

    private class BackStackListner implements FragmentManager.OnBackStackChangedListener {

        @Override
        public void onBackStackChanged() {
            Dlog.d("Backstack", "called");
            Fragment f2 = getSupportFragmentManager().findFragmentById(R.id.container);
            if (f2 instanceof DetailsTabbedFragment) {
                UIUtils.refreshActionBar(MainActivity.this, ((DetailsTabbedFragment) f2).getName());
            }
            getSupportFragmentManager().removeOnBackStackChangedListener(listener);
        }
    }
}
