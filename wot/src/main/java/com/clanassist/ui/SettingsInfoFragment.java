package com.clanassist.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.SVault;
import com.clanassist.alerts.Alert;
import com.clanassist.backend.Tasks.GetNeededInfoTask;
import com.clanassist.model.enums.Server;
import com.clanassist.model.events.InfoResult;
import com.clanassist.model.infoobj.InfoQuery;
import com.clanassist.tools.CPStorageManager;
import com.cp.assist.R;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.otto.Subscribe;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Obsidian47 on 3/13/14.
 */
public class SettingsInfoFragment extends Fragment {

    private View rlColorBlind;
    private CheckBox cbColorBlind;

    private View rlTheme;
    private CheckBox cbTheme;

    private View rlClearDirectories;
    private View rlClearPlayers;
    private View rlClearRefresh;

    private View llReview;
    private View llEmail;
    private View llTwitter;
    private View llVersionArea;
    private View llDevBlog;
    private View llWoWsApp;
    private View llSubreddit;

    private ImageView ivVersion;
    private ImageView ivReview;
    private ImageView ivEmail;

    private Spinner sServer;

    private Spinner sAppLanguage;
    private Spinner sServerLanguage;

    private final String[] languages = {"en", "ru", "pl", "de", "fr", "es", "zh-cn", "tr", "cs", "th", "vi", "ko", "ja", "zh-tw", "pt-br", "es-mx"};
    private final String[] languagesShow = {"English", "Русский", "Polski", "Deutsch", "Français", "Español", "简体中文", "Türkçe", "Čeština", "ไทย", "Tiếng Việt", "한국어", "日本語", "繁體中文", "Português do Brasil", "Español (México)"};

    private final String[] appLangauges = {"en", "ru", "de", "es", "fi", "hu", "pl", "sl"};
    private final String[] appLangaugesShow = {"English", "Русский", "Deutsch", "Español", "suomalainen", "Magyar", "Polskie", "Slovenski"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_info, null, false);

        TextView tv = (TextView) view.findViewById(R.id.settings_verison);
        try {
            tv.setText(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
        }
        llVersionArea = view.findViewById(R.id.settings_version_area);
        llReview = view.findViewById(R.id.settings_review);
        llEmail = view.findViewById(R.id.settings_contact);
        llTwitter = view.findViewById(R.id.settings_twitter);
        llDevBlog = view.findViewById(R.id.settings_wordpress);
        llWoWsApp = view.findViewById(R.id.settings_wot);
        llSubreddit = view.findViewById(R.id.settings_subreddit);

        ivVersion = (ImageView) view.findViewById(R.id.settings_version_image);
        ivReview = (ImageView) view.findViewById(R.id.settings_review_image);
        ivEmail = (ImageView) view.findViewById(R.id.settings_email_image);

        rlColorBlind = view.findViewById(R.id.info_colorblind_area);
        cbColorBlind = (CheckBox) view.findViewById(R.id.info_colorblind_checkbox);
        cbTheme = (CheckBox) view.findViewById(R.id.info_theme_checkbox);
        rlClearDirectories = view.findViewById(R.id.info_clear_downloads_area);
        rlClearPlayers = view.findViewById(R.id.info_clear_players_area);
        rlTheme = view.findViewById(R.id.info_theme_area);
        rlClearRefresh = view.findViewById(R.id.info_clear_wargaming_info);

        sServer = (Spinner) view.findViewById(R.id.info_server_spinner);

        sAppLanguage = (Spinner) view.findViewById(R.id.info_language_spinner);

        sServerLanguage = (Spinner) view.findViewById(R.id.info_server_language_spinner);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("filler", true);
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        UIUtils.refreshActionBar(getActivity(), getString(R.string.drawer_about));
        initView();
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    private void initView() {
        Prefs pref = new Prefs(getActivity());
        boolean isColorBlind = pref.getBoolean(SVault.PREF_COLORBLIND, false);
        boolean isLightTheme = CAApp.isLightTheme(getActivity());

        cbColorBlind.setChecked(isColorBlind);
        cbTheme.setChecked(isLightTheme);

        ivEmail.setImageResource(!isLightTheme ? R.drawable.ic_email : R.drawable.ic_email_light);
        ivVersion.setImageResource(!isLightTheme ? R.drawable.ic_version : R.drawable.ic_version_light);
        ivReview.setImageResource(!isLightTheme ? R.drawable.ic_star: R.drawable.ic_star_light);

        rlColorBlind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs pref = new Prefs(getActivity());
                boolean isColorBlind = pref.getBoolean(SVault.PREF_COLORBLIND, false);
                isColorBlind = !isColorBlind;
                cbColorBlind.setChecked(isColorBlind);
                pref.setBoolean(SVault.PREF_COLORBLIND, isColorBlind);
            }
        });
        rlTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs pref = new Prefs(getActivity());
                boolean isDarkTheme = CAApp.isLightTheme(v.getContext());
                isDarkTheme = !isDarkTheme;
                cbTheme.setChecked(isDarkTheme);
                pref.setBoolean(SVault.PREF_THEME_SELECTOR, isDarkTheme);
                Answers.getInstance().logCustom(new CustomEvent("Theme").putCustomAttribute("Type", isDarkTheme ? "Dark Theme" : "Light Theme"));
                CAApp.relaunchApplication(getActivity());
            }
        });
        rlClearDirectories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cleared = CPStorageManager.clearDownloadedClansResult(v.getContext());
                Dlog.d("InfoFragment", "cleared = " + cleared);
                Toast.makeText(v.getContext(), R.string.info_clean_clans_toast, Toast.LENGTH_SHORT).show();
            }
        });
        rlClearPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cleared = CPStorageManager.clearDownloadedPlayers(v.getContext());
                Dlog.d("InfoFragment", "cleared = " + cleared);
                Toast.makeText(v.getContext(), R.string.info_clean_clans_toast, Toast.LENGTH_SHORT).show();
            }
        });
        llVersionArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alert.createGeneralAlert(v.getContext(), getString(R.string.recent_changes_title), getString(R.string.recent_changes_news), getString(R.string.dismiss));
            }
        });
        llReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://play.google.com/store/apps/details?id=com.cp.assist";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getActivity().startActivity(i);
            }
        });
        llTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://twitter.com/slai47";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getActivity().startActivity(i);
            }
        });
        llDevBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://communityassistant.wordpress.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getActivity().startActivity(i);
            }
        });
        llWoWsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://play.google.com/store/apps/details?id=com.half.wowsca";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getActivity().startActivity(i);
            }
        });

        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "mailto:wotcommunityassistant@gmail.com";
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse(url));
                getActivity().startActivity(i);
            }
        });
        rlClearRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CAApp.getInfoManager().purge(getActivity());
                InfoQuery query = new InfoQuery();
                GetNeededInfoTask task = new GetNeededInfoTask();
                task.setCtx(getActivity());
                task.execute(query);
                Toast.makeText(getActivity(), R.string.refresh, Toast.LENGTH_SHORT).show();
            }
        });
        llSubreddit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.reddit.com/r/CommunityAssistant/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getActivity().startActivity(i);
            }
        });

        List<String> servers = new ArrayList<String>();
        Server current = CAApp.getServerType(getActivity());
        servers.add(current.getServerName().toUpperCase());
        for (Server s : Server.values()) {
            if (current.ordinal() != s.ordinal()) {
                servers.add(s.getServerName().toUpperCase());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, servers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sServer.setAdapter(adapter);
        sServer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Server> servers = new ArrayList<Server>();
                Server current = CAApp.getServerType(getActivity());
                servers.add(current);
                for (Server s : Server.values()) {
                    if (current.ordinal() != s.ordinal()) {
                        servers.add(s);
                    }
                }
                Server server = servers.get(position);
                if (server != current) {
                    Answers.getInstance().logCustom(new CustomEvent("Server Change").putCustomAttribute("Server", server.toString()));
                    Prefs pref = new Prefs(view.getContext());
                    pref.setString(CAApp.SERVER_NAME, server.toString());
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.server_change_text) + " " + server.getServerName().toUpperCase(), Toast.LENGTH_SHORT).show();
                    Crashlytics.setString(SVault.LOG_SERVER, server.toString());
                    Dlog.d("SERVER NOW", "s = " + server + " " + pref.getString(CAApp.SERVER_NAME, Server.US.getServerName().toUpperCase()));
                    CAApp.relaunchApplication(getActivity());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, languagesShow);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sServerLanguage.setAdapter(adapter2);
        int defaultSelected = 0;
        String currentLang = CAApp.getServerLanguage(getActivity());
        for(int i  = 0; i < languages.length; i++){
            if(languages[i].equals(currentLang)){
                defaultSelected = i;
                break;
            }
        }

        sServerLanguage.setSelection(defaultSelected);

        sServerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String current = CAApp.getServerLanguage(getContext());
                String selected = languages[position];
                if(!current.equals(selected)){
                    CAApp.setServerLanguage(getContext(), selected);
                    Answers.getInstance().logCustom(new CustomEvent("Server Language Change").putCustomAttribute("Language", selected));
                    rlClearRefresh.performClick();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initAppLanguage();
    }

    private void initAppLanguage(){
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, appLangaugesShow);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sAppLanguage.setAdapter(adapter2);
        int defaultSelected = 0;
        String currentLang = CAApp.getAppLanguage(sAppLanguage.getContext());
        for(int i  = 0; i < appLangauges.length; i++){
            if(appLangauges[i].equals(currentLang)){
                defaultSelected = i;
                break;
            }
        }

        sAppLanguage.setSelection(defaultSelected);

        sAppLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String current = CAApp.getAppLanguage(sAppLanguage.getContext());
                String selected = appLangauges[position];
                if(!current.equals(selected)){
                    CAApp.setAppLanguage(sAppLanguage.getContext(), selected);
                    Answers.getInstance().logCustom(new CustomEvent("App Language Change").putCustomAttribute("Language", selected));
                    CAApp.relaunchApplication(getActivity());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Subscribe
    public void onInfoRecieved(InfoResult result) {
        llVersionArea.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), R.string.refresh_finished, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
