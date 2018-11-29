package com.half.wowsca.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.alerts.Alert;
import com.half.wowsca.backend.GetNeededInfoTask;
import com.half.wowsca.managers.StorageManager;
import com.half.wowsca.model.enums.Server;
import com.half.wowsca.model.queries.InfoQuery;
import com.half.wowsca.model.result.InfoResult;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.preferences.Prefs;
import com.utilities.views.SwipeBackLayout;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends CABaseActivity {

    public static final String SHOW_COMPARE = "showCompare";
    public static final String NO_ARP = "noArp";
    public static final String THEME_CHOICE = "themeChoice";
    public static final String LOGIN_USER = "login_user";
    public static final String AD_LAUNCH = "ad_launch";
    private Toolbar mToolbar;

    private View aColorblind;
    private CheckBox cbColorblind;

    private View aCompare;
    private CheckBox cbCompare;

    private View aNoArp;
    private CheckBox cbNoArp;

    private View aLogin;
    private CheckBox cbLogin;

    private View aAdLaunch;
    private CheckBox cbAdLaunch;

    private View aClearPlayer;

    private View aVersion;

    private View aRefreshInfo;


    private TextView tvVersionText;

    private View aReview;
    private View aEmail;
    private View aTwitter;
    private View aWordPress;
    private View aWoTCom;

    private Spinner sServerLanguage;
    private Spinner sTheme;
    private Spinner sAppLanguage;
    private Spinner sServer;

    private Spinner sCaptainSaves;
    private Spinner sShipSaves;

    private final String[] languages = {"en", "ru", "pl", "de", "fr", "es", "zh-cn", "tr", "cs", "th", "vi", "ja", "zh-tw", "pt-br", "es-mx"};
    private final String[] languagesShow = {"English", "Русский", "Polski", "Deutsch", "Français", "Español", "简体中文", "Türkçe", "Čeština", "ไทย", "Tiếng Việt", "日本語", "繁體中文", "Português do Brasil", "Español (México)"};

    private final String[] appLangauges = {"en", "ru", "de", "es", "hr", "nl"};
    private final String[] appLangaugesShow = {"English", "Русский", "Deutsch", "Español", "Magyar", "Nederlands"};


    private final String[] saveNumChoices = {"5", "10","15","20","25"};
    private final int[] saveNumbers = {5, 10, 15, 20, 25};

    private final String[] themes = {"ocean","dark"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        bindView();
    }

    private void bindView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        setTitle(R.string.settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        aColorblind = findViewById(R.id.settings_colorblind_area);
        cbColorblind = (CheckBox) findViewById(R.id.settings_colorblind_checkbox);

        aCompare = findViewById(R.id.settings_compare_area);
        cbCompare = (CheckBox) findViewById(R.id.settings_compare_checkbox);

        aNoArp = findViewById(R.id.settings_arp_area);
        cbNoArp = (CheckBox) findViewById(R.id.settings_arp_checkbox);

        aLogin = findViewById(R.id.settings_login_area);
        cbLogin = (CheckBox) findViewById(R.id.settings_login_checkbox);

        aVersion = findViewById(R.id.settings_version_area);

        aClearPlayer = findViewById(R.id.settings_clear_players_area);

        aRefreshInfo = findViewById(R.id.settings_clear_saved_data);

        aWordPress = findViewById(R.id.settings_wordpress);

        aAdLaunch = findViewById(R.id.settings_ad_launch_area);
        cbAdLaunch = (CheckBox) findViewById(R.id.settings_ad_launch_checkbox);

        tvVersionText = (TextView) findViewById(R.id.settings_verison);

        aReview = findViewById(R.id.settings_review);
        aEmail = findViewById(R.id.settings_contact);
        aTwitter = findViewById(R.id.settings_twitter);
        aWoTCom = findViewById(R.id.settings_wot);

        sServerLanguage = (Spinner) findViewById(R.id.settings_server_spinner);
        sTheme = (Spinner) findViewById(R.id.settings_theme_spinner);
        sAppLanguage = (Spinner) findViewById(R.id.settings_language_spinner);
        sServer = (Spinner) findViewById(R.id.settings_stats_server_spinner);

        sCaptainSaves = (Spinner) findViewById(R.id.settings_stats_captain_saves_spinner);
        sShipSaves = (Spinner) findViewById(R.id.settings_stats_ships_saves_spinner);

        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

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
        initCheckBoxes();

        initOnClickVersionRefresh();

        initLinks();

        initServerLangauge();

        initTheme();

        initServer();

        initSaveOptions();

        initAppLangauge();
    }

    private void initSaveOptions() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.ca_spinner_item_trans, saveNumChoices);
        adapter.setDropDownViewResource(!CAApp.isDarkTheme(sTheme.getContext()) ? R.layout.ca_spinner_item : R.layout.ca_spinner_item_dark);
        sCaptainSaves.setAdapter(adapter);
        int defaultSelected = 0;
        int numOfSaves = StorageManager.getStatsMax(getApplicationContext());
        for(int i  = 0; i < saveNumbers.length; i++){
            if(saveNumbers[i] == numOfSaves){
                defaultSelected = i;
                break;
            }
        }

        sCaptainSaves.setSelection(defaultSelected);

        sCaptainSaves.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int current = StorageManager.getStatsMax(getApplicationContext());
                final int selected = saveNumbers[position];
                if(current != selected){
                    if(current > selected){
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                        builder.setIcon(R.drawable.launcher_icon);
                        builder.setTitle(getString(R.string.settings_stats_number_dialog_title));
                        builder.setMessage(getString(R.string.settings_stats_number_dialog_message));
                        builder.setPositiveButton(getString(R.string.settings_stats_number_dialog_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StorageManager.setStatsMax(getApplicationContext(), selected);
                                aClearPlayer.callOnClick();
                            }
                        });
                        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int defaultSelected = 0;
                                int numOfSaves = StorageManager.getStatsMax(getApplicationContext());
                                for(int i  = 0; i < saveNumbers.length; i++){
                                    if(saveNumbers[i] == numOfSaves){
                                        defaultSelected = i;
                                        break;
                                    }
                                }

                                sCaptainSaves.setSelection(defaultSelected);
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        StorageManager.setStatsMax(getApplicationContext(), selected);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.ca_spinner_item_trans, saveNumChoices);
        adapter2.setDropDownViewResource(!CAApp.isDarkTheme(sTheme.getContext()) ? R.layout.ca_spinner_item : R.layout.ca_spinner_item_dark);
        sShipSaves.setAdapter(adapter2);
        int defaultSelected2 = 0;
        int numOfSaves2 = StorageManager.getShipsStatsMax(getApplicationContext());
        for(int i  = 0; i < saveNumbers.length; i++){
            if(saveNumbers[i] == numOfSaves2){
                defaultSelected2 = i;
                break;
            }
        }
        sShipSaves.setSelection(defaultSelected2);
        sShipSaves.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int current = StorageManager.getShipsStatsMax(getApplicationContext());
                final int selected = saveNumbers[position];
                if(current != selected){
                    if(current > selected){
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                        builder.setIcon(R.drawable.launcher_icon);
                        builder.setTitle(getString(R.string.settings_stats_number_dialog_title));
                        builder.setMessage(getString(R.string.settings_stats_number_dialog_message));
                        builder.setPositiveButton(getString(R.string.settings_stats_number_dialog_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StorageManager.setShipsStatsMax(getApplicationContext(), selected);
                                aClearPlayer.callOnClick();
                            }
                        });
                        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int defaultSelected2 = 0;
                                int numOfSaves2 = StorageManager.getShipsStatsMax(getApplicationContext());
                                for(int i  = 0; i < saveNumbers.length; i++){
                                    if(saveNumbers[i] == numOfSaves2){
                                        defaultSelected2 = i;
                                        break;
                                    }
                                }
                                sShipSaves.setSelection(defaultSelected2);
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        StorageManager.setShipsStatsMax(getApplicationContext(), selected);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initServer() {
        List<String> servers = new ArrayList<String>();
        Server current = CAApp.getServerType(getApplicationContext());
        servers.add(current.toString().toUpperCase());
        for (Server s : Server.values()) {
            if (current.ordinal() != s.ordinal()) {
                servers.add(s.toString().toUpperCase());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.ca_spinner_item_trans, servers);
        adapter.setDropDownViewResource(!CAApp.isDarkTheme(sServer.getContext()) ? R.layout.ca_spinner_item : R.layout.ca_spinner_item_dark);
        sServer.setAdapter(adapter);
        sServer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Server> servers = new ArrayList<Server>();
                Server current = CAApp.getServerType(getApplicationContext());
                servers.add(current);
                for (Server s : Server.values()) {
                    if (current.ordinal() != s.ordinal()) {
                        servers.add(s);
                    }
                }
                Server server = servers.get(position);
                if (server != current) {
                    CAApp.setServerType(getApplicationContext(), server);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initOnClickVersionRefresh() {
        aVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = SettingActivity.this;
                Alert.createGeneralAlert(ctx, getString(R.string.update_notes_title), getString(R.string.patch_notes) + getString(R.string.update_notes_achieve), "Dismiss");
            }
        });

        aClearPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageManager.clearDownloadedPlayers(getApplicationContext());
                Toast.makeText(getApplicationContext(), R.string.player_stored_data_deleted, Toast.LENGTH_SHORT).show();
            }
        });

        aRefreshInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CAApp.getInfoManager().purge(v.getContext());
                InfoQuery query = new InfoQuery();
                query.setServer(CAApp.getServerType(getApplicationContext()));
                GetNeededInfoTask task = new GetNeededInfoTask();
                task.setCtx(getApplicationContext());
                task.execute(query);
                Toast.makeText(getApplicationContext(), R.string.purging_refresh, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCheckBoxes() {
        boolean isColorblind = CAApp.isColorblind(getApplicationContext());
        Prefs prefs = new Prefs(getApplicationContext());
        boolean showCompare = prefs.getBoolean(SHOW_COMPARE, true);
        boolean noArp = prefs.getBoolean(NO_ARP, false);
        boolean login = prefs.getBoolean(LOGIN_USER, false);
        boolean showAdLaunch = prefs.getBoolean(AD_LAUNCH, false);
        cbAdLaunch.setChecked(showAdLaunch);
        aAdLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs prefs = new Prefs(getApplicationContext());
                boolean showAd = prefs.getBoolean(AD_LAUNCH, false);
                showAd = !showAd;
                cbAdLaunch.setChecked(showAd);
                prefs.setBoolean(AD_LAUNCH, showAd);
            }
        });
        cbColorblind.setChecked(isColorblind);
        aColorblind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isColorblind = CAApp.isColorblind(getApplicationContext());
                isColorblind = !isColorblind;
                cbColorblind.setChecked(isColorblind);
                CAApp.setColorblindMode(getApplicationContext(), isColorblind);
            }
        });
        cbCompare.setChecked(showCompare);
        aCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs prefs = new Prefs(getApplicationContext());
                boolean showCompare = prefs.getBoolean(SHOW_COMPARE, true);
                showCompare = !showCompare;
                cbCompare.setChecked(showCompare);
                prefs.setBoolean(SHOW_COMPARE, showCompare);
            }
        });

        cbNoArp.setChecked(noArp);
        aNoArp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs prefs = new Prefs(getApplicationContext());
                boolean showCompare = prefs.getBoolean(NO_ARP, true);
                showCompare = !showCompare;
                cbNoArp.setChecked(showCompare);
                prefs.setBoolean(NO_ARP, showCompare);
            }
        });

        cbLogin.setChecked(login);
        aLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs prefs = new Prefs(getApplicationContext());
                boolean loginUser = prefs.getBoolean(LOGIN_USER, true);
                loginUser = !loginUser;
                cbLogin.setChecked(loginUser);
                prefs.setBoolean(LOGIN_USER, loginUser);
            }
        });
    }

    private void initTheme() {
        String[] themesList = getResources().getStringArray(R.array.themes);
        Prefs prefs = new Prefs(getApplicationContext());
        String current = prefs.getString(THEME_CHOICE, "ocean");
        int position = 0;
        if(current.equals("dark")){
            position = 1;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.ca_spinner_item_trans, themesList);
        adapter.setDropDownViewResource(!CAApp.isDarkTheme(sTheme.getContext()) ? R.layout.ca_spinner_item : R.layout.ca_spinner_item_dark);
        sTheme.setAdapter(adapter);

        sTheme.setSelection(position);

        sTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Prefs prefs = new Prefs(getApplicationContext());
                String current = prefs.getString(THEME_CHOICE, "ocean");
                String selected = themes[position];
                if (!current.equals(selected)) {
                    prefs.setString(THEME_CHOICE, selected);
                    //restart app
                    CAApp.relaunchApplication(SettingActivity.this);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initLinks() {
        aWordPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://communityassistant.wordpress.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        try {
            tvVersionText.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
        }
        aReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://play.google.com/store/apps/details?id=com.half.wowsca";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        aEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "mailto:wotcommunityassistant@gmail.com";
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        aTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://twitter.com/slai47";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        aWoTCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://play.google.com/store/apps/details?id=com.cp.assist";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void initServerLangauge() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.ca_spinner_item_trans, languagesShow);
        adapter.setDropDownViewResource(!CAApp.isDarkTheme(sTheme.getContext()) ? R.layout.ca_spinner_item : R.layout.ca_spinner_item_dark);
        sServerLanguage.setAdapter(adapter);
        int defaultSelected = 0;
        String current = CAApp.getServerLanguage(getApplicationContext());
        for(int i  = 0; i < languages.length; i++){
            if(languages[i].equals(current)){
                defaultSelected = i;
                break;
            }
        }

        sServerLanguage.setSelection(defaultSelected);

        sServerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String current = CAApp.getServerLanguage(getApplicationContext());
                String selected = languages[position];
                if(!current.equals(selected)){
                    CAApp.setServerLanguage(getApplicationContext(), selected);
                    Toast t = Toast.makeText(getApplicationContext(), R.string.settings_update_to_new_language, Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER,0,0);
                    t.show();
                    aRefreshInfo.callOnClick();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initAppLangauge() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.ca_spinner_item_trans, appLangaugesShow);
        adapter.setDropDownViewResource(!CAApp.isDarkTheme(sTheme.getContext()) ? R.layout.ca_spinner_item : R.layout.ca_spinner_item_dark);
        sAppLanguage.setAdapter(adapter);
        int defaultSelected = 0;
        String current = CAApp.getAppLanguage(getApplicationContext());
        for(int i  = 0; i < appLangauges.length; i++){
            if(appLangauges[i].equals(current)){
                defaultSelected = i;
                break;
            }
        }

        sAppLanguage.setSelection(defaultSelected);

        sAppLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String current = CAApp.getAppLanguage(getApplicationContext());
                String selected = appLangauges[position];
                if(!current.equals(selected)){
                    CAApp.setAppLanguage(getApplicationContext(), selected);
                    CAApp.relaunchApplication(SettingActivity.this);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Subscribe
    public void onInfoRecieved(InfoResult result) {
        aRefreshInfo.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.purge_refresh_done, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
