package com.clanassist.ui.details.clan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.SVault;
import com.clanassist.backend.ClanParser;
import com.clanassist.listeners.AddRemoveClanListener;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.events.ClanLoadedFinishedEvent;
import com.clanassist.model.events.ClanPlayerAddRemoveEvent;
import com.clanassist.model.events.details.ClanProfileHit;
import com.clanassist.model.interfaces.IDetails;
import com.clanassist.model.interfaces.INameOfFragment;
import com.clanassist.model.listmodel.CPStatModel;
import com.clanassist.model.player.Player;
import com.clanassist.model.search.enums.ClanSearchType;
import com.clanassist.model.search.queries.ClanQuery;
import com.clanassist.model.search.results.ClanPlayerWN8sTaskResult;
import com.clanassist.model.search.results.ClanResult;
import com.clanassist.tools.CPManager;
import com.clanassist.tools.CPStorageManager;
import com.clanassist.tools.DownloadedClanManager;
import com.clanassist.tools.HitManager;
import com.clanassist.tools.WN8ColorManager;
import com.clanassist.tools.WebsiteUrlBuilder;
import com.clanassist.ui.UIUtils;
import com.clanassist.ui.adapter.CPStatAdapter;
import com.clanassist.ui.details.DetailsTabbedFragment;
import com.cp.assist.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;
import com.utilities.search.Search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Obsidian47 on 3/1/14.
 */
public class ClanDetailsFragment extends Fragment implements INameOfFragment {

    public static final String CLAN_DETAILS = "Clan Details";
    public static final String DELIMITER = ",";

    private TextView etAbbr;
    private TextView etName;
    private TextView etCommander;
    private TextView etMemberCount;
    private ImageView ivImage;
    private CheckBox cbAddRemoveButton;
    private View addArea;

    private TextView etMotto;

    private TextView etDescription;

    private View clanDetailedArea;

    private Button bClanTools;
    private Button bWoTLabs;

    private IDetails details;

    private boolean isColorBlind;

    private GridView mGridView;
    private CPStatAdapter mStatAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clan_details, container, false);
        bindView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        try {
            initView();
        } catch (Exception e) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    private void bindView(View view) {
        etAbbr = (TextView) view.findViewById(R.id.clan_details_abbr);
        etName = (TextView) view.findViewById(R.id.clan_details_name);
        etCommander = (TextView) view.findViewById(R.id.clan_details_owner_name);
        etMemberCount = (TextView) view.findViewById(R.id.clan_details_member_count);
        ivImage = (ImageView) view.findViewById(R.id.clan_details_image);
        etMotto = (TextView) view.findViewById(R.id.clan_details_motto);
        etDescription = (TextView) view.findViewById(R.id.clan_details_description);
        cbAddRemoveButton = (CheckBox) view.findViewById(R.id.clan_details_addremove_button);
        addArea = view.findViewById(R.id.clan_details_addremove_button_area);

        clanDetailedArea = view.findViewById(R.id.clan_details_advanced_area);

        mGridView = (GridView) view.findViewById(R.id.clan_details_advanced_grid);

        bClanTools = (Button) view.findViewById(R.id.clan_details_open_noobmeter);
        bWoTLabs = (Button) view.findViewById(R.id.clan_details_open_wotlabs);

        UIUtils.setUpCard(view, R.id.clan_details_description_card);
        UIUtils.setUpCard(view, R.id.clan_details_advanced_area);
    }

    private void initView() {
        if (details == null)
            details = (IDetails) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        Prefs pref = new Prefs(getActivity());
        isColorBlind = pref.getBoolean(SVault.PREF_COLORBLIND, false);
        Clan clan = details.getClan(getActivity());
        boolean hasConnection = Utils.hasInternetConnection(getActivity());
        if (hasConnection) {
            try {
                boolean hasHitProfile = details.hitProfile();
                if (!hasHitProfile) {
                    Search<ClanQuery, ClanResult> search = new Search<ClanQuery, ClanResult>(new ClanParser(), null, CAApp.getEventBus());
                    ClanQuery query = new ClanQuery();
                    query.setWebAddress(CAApp.getBaseAddress(getActivity()));
                    query.setApplicationIdString(CAApp.getApplicationIdURLString(getActivity()));
                    query.setLanguage(getActivity().getString(R.string.language));
                    query.setType(ClanSearchType.DETAILS);
                    query.setClanId(clan.getClanId());
                    search.execute(query);
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Context ctx = getActivity();
            Toast.makeText(ctx, ctx.getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
        }
        if (clan != null) {
            if (!clan.isClanDisbanded() || CPManager.getSavedClans(getActivity()).get(clan.getClanId()) != null) {
                if (CPManager.getSavedClans(getActivity()).get(clan.getClanId()) != null) {
                    cbAddRemoveButton.setChecked(true);
                } else {
                    cbAddRemoveButton.setChecked(false);
                }
                addArea.setOnClickListener(new AddRemoveClanListener(cbAddRemoveButton.getContext(), clan, cbAddRemoveButton, false));
                addArea.setVisibility(View.VISIBLE);
            } else {
                addArea.setVisibility(View.GONE);
            }
            etName.setText(clan.getName());
            etAbbr.setText("[" + clan.getAbbreviation() + "]");
            int color = Color.parseColor(clan.getColor());
            if (!isColorBlind)
                etAbbr.setTextColor(color);

            etCommander.setText(clan.getOwnerName());
            etMemberCount.setText(clan.getMembers_count() + "");

            if(clan.getEmblem() != null)
                if (!TextUtils.isEmpty(clan.getEmblem().getBw_tank()))
                    Picasso.with(getActivity()).load(clan.getEmblem().getBw_tank()).into(ivImage);

            etMotto.setText(clan.getMotto());

            // init the advanced view if stuff is there.
            if (!TextUtils.isEmpty(clan.getDescriptionHtml())) {
                etDescription.setText(Html.fromHtml(clan.getDescriptionHtml()));
                Linkify.addLinks(etDescription, Linkify.ALL);
            } else if (!TextUtils.isEmpty(clan.getDescription())) {
                etDescription.setText(clan.getDescription());
                Linkify.addLinks(etDescription, Linkify.ALL);
            }

            if (clan.getMembers() != null) {
                List<Player> commanderSort = clan.getMembers();
                Collections.sort(commanderSort);
                try {
                    Player com = clan.getMembers().get(0);
                    etCommander.setText(com.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            setUpClanExport(clan);

            if (details.hitProfile())
                if (CPStorageManager.hasClanTaskResult(getActivity(), clan.getClanId())) {
                    initAdvancedInfo();
                } else {
                    clanDetailedArea.setVisibility(View.GONE);
                }
        } else {
            Toast.makeText(getActivity(), R.string.failure, Toast.LENGTH_SHORT).show();
        }
        setUpButtons();
    }

    private void setUpButtons() {
        bClanTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("Details Website").putCustomAttribute("Website", "ClanTools"));
//                Fragment frag = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
//                DetailsTabbedFragment fragment = (DetailsTabbedFragment) frag;
//                Clan clan = details.getClan(v.getContext());
                int account = details.getAccountId();
                String url = WebsiteUrlBuilder.getClanToolsAddress(getActivity(), details.isPlayer(), account);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        bWoTLabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("Details Website").putCustomAttribute("Website", "WoTLabs"));
                Fragment frag = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                DetailsTabbedFragment fragment2 = (DetailsTabbedFragment) frag;
                String url2 = WebsiteUrlBuilder.getWoTLabsAddress(getActivity(), fragment2.isPlayer(), fragment2.getUrlPlayerName(getActivity()), fragment2.getUrlClanName(getActivity()));
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse(url2));
                startActivity(i2);
            }
        });
    }

    private void initAdvancedInfo() {
        int clanId = details.getClan(getActivity()).getClanId();
//        Dlog.d("ClanDetails", "initView initAdvancedInfo " + DownloadedClanManager.hasClanDownloadLoaded(getActivity(), clanId + ""));
        if (DownloadedClanManager.hasClanDownloadLoaded(getActivity(), clanId + "")) {
            ClanPlayerWN8sTaskResult result = DownloadedClanManager.getClanDownload(clanId + "");
            //figure a way to decide whether or not to open this area and init it.

            clanDetailedArea.setVisibility(View.VISIBLE);

            setUpAdapter(result);
            setUpOnItemClick(result);
        } else {
            if (!DownloadedClanManager.isClanLoading(clanId + "") && CPStorageManager.hasClanTaskResult(getActivity(), clanId)) {
                DownloadedClanManager.loadClanDownload(getActivity(), clanId);
            }
            clanDetailedArea.setVisibility(View.GONE);
        }
    }

    private void setUpAdapter(ClanPlayerWN8sTaskResult result) {
        List<CPStatModel> stats = new ArrayList<CPStatModel>();
        int defaultBackground = (!CAApp.isLightTheme(getActivity()) ? getResources().getColor(R.color.material_grid_view_default) : getResources().getColor(R.color.material_light_grid_view_default));
        stats.add(new CPStatModel(((int) result.getOverallWN8()) + "", getString(R.string.wn8_cap), getString(R.string.overall), WN8ColorManager.getBackgroundColor(getActivity(), (int) result.getOverallWN8())));

        stats.add(new CPStatModel(((int) result.getOverallClanWN8()) + "", getString(R.string.wn8_cap), getString(R.string.clan_wars_cap), WN8ColorManager.getBackgroundColor(getActivity(), (int) result.getOverallClanWN8())));

        stats.add(new CPStatModel(((int) result.getOverallSHWN8()) + "", getString(R.string.wn8_cap), getString(R.string.stronghold_cap), WN8ColorManager.getBackgroundColor(getActivity(), (int) result.getOverallSHWN8())));


        stats.add(new CPStatModel(((int) result.getAverageDayWn8()) + "",getString(R.string.wn8_cap), getString(R.string.day_wn8), WN8ColorManager.getBackgroundColor(getActivity(), (int) result.getAverageDayWn8())));

        stats.add(new CPStatModel(((int) result.getAverage7DayWn8()) + "",getString(R.string.wn8_cap), getString(R.string.week_wn8), WN8ColorManager.getBackgroundColor(getActivity(), (int) result.getAverage7DayWn8())));

        stats.add(new CPStatModel(((int) result.getAverage30DayWn8()) + "",getString(R.string.wn8_cap), getString(R.string.month_wn8), WN8ColorManager.getBackgroundColor(getActivity(), (int) result.getAverage30DayWn8())));

        stats.add(new CPStatModel(((int) result.getAverage60DayWn8()) + "",getString(R.string.wn8_cap), getString(R.string.two_month_wn8), WN8ColorManager.getBackgroundColor(getActivity(), (int) result.getAverage60DayWn8())));


        stats.add(new CPStatModel(Utils.getOneDepthDecimalFormatter().format(result.getAverageWinRate()) + "" + "%", getString(R.string.win_rate), getString(R.string.overall), WN8ColorManager.getWinRateColor(getActivity(), (int) result.getAverageWinRate())));

        stats.add(new CPStatModel(Utils.getOneDepthDecimalFormatter().format(result.getAverageClanWinRate()) + "%", getString(R.string.win_rate), getString(R.string.clan_wars_cap), WN8ColorManager.getBackgroundColor(getActivity(), (int) result.getAverageClanWinRate())));

        stats.add(new CPStatModel(Utils.getOneDepthDecimalFormatter().format(result.getAverageSHWinRate()) + "%", getString(R.string.win_rate), getString(R.string.stronghold_cap), WN8ColorManager.getBackgroundColor(getActivity(), (int) result.getAverageSHWinRate())));

        CPStatModel bestWN8Model = new CPStatModel(result.getBestWN8AccountNumber() + "", result.getBestWN8AccountName(), getString(R.string.overall), WN8ColorManager.getBackgroundColor(getActivity(), result.getBestWN8AccountNumber()));
        bestWN8Model.setAccountId(result.getBestWN8AccountId());
        bestWN8Model.setAccountName(result.getBestWN8AccountName());
        bestWN8Model.setMidText("MVP");
        stats.add(bestWN8Model);

        CPStatModel bestClanWN8Model = new CPStatModel(result.getBestClanWN8AccountNumber() + "", result.getBestClanWN8AccountName(), getString(R.string.clan_wars_cap), WN8ColorManager.getBackgroundColor(getActivity(), result.getBestClanWN8AccountNumber()));
        bestClanWN8Model.setAccountId(result.getBestClanWN8AccountId());
        bestClanWN8Model.setAccountName(result.getBestClanWN8AccountName());
        bestClanWN8Model.setMidText("CW MVP");
        stats.add(bestClanWN8Model);

        CPStatModel bestSHWN8Model = new CPStatModel(result.getBestSHWN8AccountNumber() + "", result.getBestSHWN8AccountName(), getString(R.string.stronghold_cap), WN8ColorManager.getBackgroundColor(getActivity(), result.getBestSHWN8AccountNumber()));
        bestSHWN8Model.setAccountId(result.getBestSHWN8AccountId());
        bestSHWN8Model.setAccountName(result.getBestSHWN8AccountName());
        bestSHWN8Model.setMidText("SH MVP");
        stats.add(bestSHWN8Model);

        stats.add(new CPStatModel(((int) result.getAverageBattles()) + "", getString(R.string.avg_battles), getString(R.string.overall), WN8ColorManager.getBattlesColor(getActivity(), result.getAverageBattles())));

        stats.add(new CPStatModel(((int) result.getAverageKills()) + "", getString(R.string.avg_kills), getString(R.string.overall), WN8ColorManager.getKillsColor(getActivity(), result.getAverageKills())));

        stats.add(new CPStatModel(((int) result.getAverageDamage()) + "", getString(R.string.avg_damage), getString(R.string.overall), defaultBackground));

        stats.add(new CPStatModel(((int) result.getAverageExp()) + "", getString(R.string.avg_exp), getString(R.string.overall), defaultBackground));

        mStatAdapter = new CPStatAdapter(getActivity(), R.layout.list_cp_stat, stats);
        mStatAdapter.setIsColorBlind(isColorBlind);
        mStatAdapter.setDefaultBackground(defaultBackground);
        mGridView.setAdapter(mStatAdapter);
        mGridView.requestLayout();
    }

    private void setUpOnItemClick(ClanPlayerWN8sTaskResult result) {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id2) {
                if (mStatAdapter == null) {
                    int clanId = details.getClan(getActivity()).getClanId();
                    setUpAdapter(DownloadedClanManager.getClanDownload(clanId + ""));
                }
                if (mStatAdapter != null) {
                    CPStatModel model = mStatAdapter.getItem(position);
                    if (model.getAccountId() > 0) {
                        String name = model.getAccountName();
                        int id = model.getAccountId();
                        Player player = new Player();
                        player.setId(id);
                        player.setName(name);
                        CPStorageManager.saveTempStoredPlayer(getActivity(), player);

                        HitManager.removePlayerProfileHit(player.getId());

                        DetailsTabbedFragment frag = new DetailsTabbedFragment();
                        frag.setFromSearch(true);
                        frag.setAccountId(id);
                        frag.setPlayer(true);
                        frag.setName(name);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.container, frag).addToBackStack(name).commit();
                    }
                }
            }
        });
    }

    @Subscribe
    public void addRemoveClan(ClanPlayerAddRemoveEvent event) {
        if (details.isFromSearch())
            if (event.getClan() != null) {
                if (!event.isRemove()) {
                    CPManager.saveClan(getActivity(), event.getClan());
                } else {
                    CPManager.removeClan(getActivity(), event.getClan());
                }
            }
    }

    @Subscribe
    public void onReceived(ClanProfileHit result) {
        initView();
    }

    @Subscribe
    public void clanLoaded(ClanLoadedFinishedEvent event) {
        Clan c = getDetails().getClan(getActivity());
        if (c != null && event.getClanId().equals(c.getClanId() + "") && event.isMerged()) {
            clanDetailedArea.post(new Runnable() {
                @Override
                public void run() {
                    initAdvancedInfo();
                }
            });
        }
    }

    @Override
    public String getNameOfFragment() {
        String name = CLAN_DETAILS;
        Clan clan = details.getClan(getActivity());
        if (clan != null) {
            name = clan.getName();
        }
        return name;
    }

    public IDetails getDetails() {
        return details;
    }

    public void setDetails(IDetails details) {
        this.details = details;
    }
}
