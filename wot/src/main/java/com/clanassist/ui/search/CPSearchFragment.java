package com.clanassist.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.ListFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.alerts.Alert;
import com.clanassist.backend.ClanParser;
import com.clanassist.backend.PlayerParser;
import com.clanassist.model.clan.Clan;
import com.clanassist.model.enums.DrawerType;
import com.clanassist.model.player.Player;
import com.clanassist.model.search.enums.ClanSearchType;
import com.clanassist.model.search.enums.PlayerSearchType;
import com.clanassist.model.search.queries.ClanQuery;
import com.clanassist.model.search.queries.PlayerQuery;
import com.clanassist.model.search.results.ClanResult;
import com.clanassist.model.search.results.PlayerResult;
import com.clanassist.tools.CPManager;
import com.clanassist.tools.CPStorageManager;
import com.clanassist.tools.CompareManager;
import com.clanassist.tools.HitManager;
import com.clanassist.ui.UIUtils;
import com.clanassist.ui.adapter.ClanAdapter;
import com.clanassist.ui.adapter.CompareAdapter;
import com.clanassist.ui.adapter.PlayerAdapter;
import com.clanassist.ui.compare.CompareActivity;
import com.clanassist.ui.details.DetailsTabbedFragment;
import com.cp.assist.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.otto.Subscribe;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.search.Search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Obsidian47 on 3/1/14.
 */
public class CPSearchFragment extends ListFragment implements AdapterView.OnItemClickListener {

    public static DrawerType SEARCH_TYPE;

    private EditText etSearchBox;
    private ImageView ivClear;
    private TextView tvNoResults;
    private ProgressBar pbProgressBar;
    private ClanAdapter mClanAdapter;
    private PlayerAdapter mPlayerAdapter;

    private Button bSubmit;

    private boolean searching;

    private View aCompare;
    private Button bCompare;
    private TextView tvCompare;

    private CompareAdapter compareAdapter;

    public static ClanResult LAST_SEARCH;
    public static PlayerResult LAST_SEARCH_PLAYER;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null, false);
        bindView(view);
        initView(view);
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
        UIUtils.refreshActionBar(getActivity(), getString(R.string.drawer_search));
        if ((SEARCH_TYPE == DrawerType.CLAN && LAST_SEARCH == null)
                || (SEARCH_TYPE == DrawerType.PLAYER && LAST_SEARCH_PLAYER == null)
                ) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        if (SEARCH_TYPE != DrawerType.WN8 && SEARCH_TYPE != DrawerType.CHOOSE) {
            CPStorageManager.deleteTempClan(getActivity());
            CPStorageManager.deleteTempPlayer(getActivity());
            if (LAST_SEARCH != null && SEARCH_TYPE == DrawerType.CLAN) {
                receivedClanLogic(LAST_SEARCH);
            }
            if(SEARCH_TYPE == DrawerType.PLAYER) {
                if (LAST_SEARCH_PLAYER != null) {
                    receivedPlayerLogic(LAST_SEARCH_PLAYER);
                } else {
                    Map<Integer, Player> playerMap = CPManager.getSavedPlayers(getActivity());
                    if (playerMap != null) {
                        List<Player> players = new ArrayList<Player>();
                        int selected = CAApp.getDefaultId(getActivity());
                        for (Player p : playerMap.values()) {
                            if(p.getId() != selected)
                                players.add(p);
                        }
                        mPlayerAdapter = new PlayerAdapter(getContext(), R.layout.list_player, players);
                        setListAdapter(mPlayerAdapter);
                    }
                }
            }
        }
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PlayerAdapter adapter = (PlayerAdapter) getListView().getAdapter();
                if (adapter != null) {
                    Player cap = adapter.getItem(position);
                    int bSize = CompareManager.size();
                    if (!CompareManager.isAlreadyThere(cap.getId())) {
                        boolean wasAdded = CompareManager.addPlayer(cap, false);
                        if (bSize == 0) {
                            int selectedId = CAApp.getDefaultId(view.getContext());
                            if (selectedId != 0) {
                                Map<Integer, Player> captains = CPManager.getSavedPlayers(view.getContext());
                                Player selected = captains.get(selectedId);
                                if (!CompareManager.isAlreadyThere(selected.getId())) {
                                    Activity act = getActivity();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                                    builder.setTitle(act.getString(R.string.compare_player_added));
                                    builder.setMessage(act.getString(R.string.compare_player_message));
                                    builder.setPositiveButton(act.getString(R.string.compare_player_add), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int selectedId = CAApp.getDefaultId(getActivity());
                                            Map<Integer, Player> captains = CPManager.getSavedPlayers(getActivity());
                                            Player selected = captains.get(selectedId);
                                            CompareManager.addPlayer(selected, true);
                                            refreshCompareSection();
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                }
                            }
                        }
                        Dlog.wtf("SearchActivity", "size = " + bSize + " wasAdded = " + wasAdded);
                        if (wasAdded) {
                            refreshCompareSection();
                        } else {
                            Toast.makeText(getActivity(), R.string.compare_maxed_out, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        CompareManager.removePlayer(cap.getId());
                        refreshCompareSection();
                    }
                } else {

                }
                return true;
            }
        });
        if (CAApp.isLightTheme(getActivity())) {
            etSearchBox.setHintTextColor(Color.WHITE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    private void bindView(View view) {
        etSearchBox = (EditText) view.findViewById(R.id.search_et_search);
        ivClear = (ImageView) view.findViewById(R.id.search_clear);
        tvNoResults = (TextView) view.findViewById(R.id.search_no_results);
        pbProgressBar = (ProgressBar) view.findViewById(R.id.search_progress);
        bSubmit = (Button) view.findViewById(R.id.search_submit);

        aCompare = view.findViewById(R.id.search_bottom_area);
        bCompare = (Button) view.findViewById(R.id.search_compare_button);
        tvCompare = (TextView) view.findViewById(R.id.search_compare_text);
    }

    private void initView(View view) {
        if(CompareManager.size() > 1){
            bCompare.setEnabled(true);
        } else {
            bCompare.setEnabled(false);
        }
        refreshCompareSection();
        bCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity act = getActivity();
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle(R.string.search_text_compare_button);
                compareAdapter = new CompareAdapter(act, R.layout.list_compare, CompareManager.getPlayers());
                builder.setAdapter(compareAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Player c = compareAdapter.getItem(which);
                        CompareManager.removePlayer(c.getId());
                        compareAdapter.remove(c);
                        compareAdapter.notifyDataSetChanged();
                        refreshCompareSection();
                    }
                });
                builder.setPositiveButton(R.string.search_text_compare_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (CompareManager.size() > 1) {
                            Intent i = new Intent(getActivity(), CompareActivity.class);
                            Answers.getInstance().logCustom(new CustomEvent("Compare").putCustomAttribute("number", CompareManager.size()));
                            startActivity(i);
                            closeKeyboard(getActivity());
                        } else {
                            Toast.makeText(getActivity(), R.string.search_compare_not_enough_players, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.search_compare_clear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CompareManager.clear();
                        refreshCompareSection();
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });


        etSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEARCH && !searching) {
                    search(textView);
                }
                return false;
            }
        });

        etSearchBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER && !searching) {
                    search(etSearchBox);
                    return true;
                }
                return false;
            }
        });

        if (SEARCH_TYPE == DrawerType.PLAYER) {
            etSearchBox.setHint(getString(R.string.search_fragment_player_hint));
            aCompare.setVisibility(View.VISIBLE);
        } else if (SEARCH_TYPE == DrawerType.CLAN) {
            etSearchBox.setHint(getString(R.string.search_fragment_clan_hint));
            aCompare.setVisibility(View.GONE);
        }
        etSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(etSearchBox.getText().toString().trim())) {
                    ivClear.setVisibility(View.GONE);
                } else {
                    ivClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearchBox.setText("");
            }
        });
    }

    public void search(View view) {
        searching = true;
        boolean connected = Utils.hasInternetConnection(getActivity());
        if (connected) {
            String searchTerm = etSearchBox.getText().toString();
            if (!TextUtils.isEmpty(searchTerm)) {
                pbProgressBar.setVisibility(View.VISIBLE);
                tvNoResults.setVisibility(View.GONE);
                if (SEARCH_TYPE == DrawerType.CLAN) {
                    Answers.getInstance().logCustom(new CustomEvent("Search").putCustomAttribute("Type", "Clan"));
                    Search<ClanQuery, ClanResult> search = new Search<ClanQuery, ClanResult>(new ClanParser(), null, CAApp.getEventBus());
                    ClanQuery query = new ClanQuery();
                    query.setType(ClanSearchType.SEARCH);
                    query.setWebAddress(CAApp.getBaseAddress(view.getContext()));
                    query.setApplicationIdString(CAApp.getApplicationIdURLString(view.getContext()));
                    query.setSearch(searchTerm);
                    query.setLanguage(getString(R.string.language));
                    search.execute(query);
                    LAST_SEARCH = null;
                    if (mClanAdapter != null) {
                        mClanAdapter.clear();
                        mClanAdapter.notifyDataSetChanged();
                    }
                } else if (SEARCH_TYPE == DrawerType.PLAYER) {
                    Answers.getInstance().logCustom(new CustomEvent("Search").putCustomAttribute("Type", "Player"));
                    Search<PlayerQuery, PlayerResult> search = new Search<PlayerQuery, PlayerResult>(new PlayerParser(getActivity()), null, CAApp.getEventBus());
                    PlayerQuery query = new PlayerQuery();
                    query.setType(PlayerSearchType.SEARCH);
                    query.setWebAddress(CAApp.getBaseAddress(view.getContext()));
                    query.setApplicationIdString(CAApp.getApplicationIdURLString(view.getContext()));
                    query.setSearch(searchTerm);
                    query.setLanguage(getString(R.string.language));
                    search.execute(query);
                    LAST_SEARCH_PLAYER = null;
                    if (mPlayerAdapter != null) {
                        mPlayerAdapter.clear();
                        mPlayerAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                Toast.makeText(view.getContext(), R.string.no_text_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Context ctx = getActivity();
            Alert.generalNoInternetDialogAlert(getActivity(), ctx.getString(R.string.no_internet_title), ctx.getString(R.string.no_internet_message), ctx.getString(R.string.no_internet_neutral_text));
        }
    }

    private void receivedClanLogic(ClanResult result) {
        if (!result.getClans().isEmpty()) {
            mClanAdapter = new ClanAdapter(getActivity(), R.layout.list_clan, result.getClans());
            setListAdapter(mClanAdapter);
        } else {
            tvNoResults.setVisibility(View.VISIBLE);
        }
    }

    private void receivedPlayerLogic(final PlayerResult result) {
        if (!result.getPlayers().isEmpty()) {
            mPlayerAdapter = new PlayerAdapter(getActivity(), R.layout.list_player, result.getPlayers());
            setListAdapter(mPlayerAdapter);
        } else {
            tvNoResults.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int id = CAApp.EMPTY_NUMBER;
        boolean isPlayer = false;
        String name = null;
        try {
            if (SEARCH_TYPE == DrawerType.CLAN) {
                Clan clan = LAST_SEARCH.getClans().get(i);
                isPlayer = false;
                id = clan.getClanId();
                name = clan.getAbbreviation();

                CPStorageManager.saveTempStoredClan(view.getContext(), clan);
                HitManager.removeClanProfileHit(clan.getClanId());
            } else if (SEARCH_TYPE == DrawerType.PLAYER) {
                try {
                    Player p = LAST_SEARCH_PLAYER.getPlayers().get(i);
                    Player player = new Player();
                    id = player.getId();
                    player.setId(p.getId());
                    player.setName(p.getName());

                    isPlayer = true;
                    name = p.getName();

                    CPStorageManager.saveTempStoredPlayer(view.getContext(), player);
                    HitManager.removePlayerProfileHit(p.getId());
                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.error_no_player_found, Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        if (SEARCH_TYPE == DrawerType.CLAN || SEARCH_TYPE == DrawerType.PLAYER) {
            DetailsTabbedFragment frag = new DetailsTabbedFragment();
            frag.setFromSearch(true);
            frag.setAccountId(id);
            frag.setPlayer(isPlayer);
            frag.setName(name);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, frag).addToBackStack(id + "").commit();
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().invalidateOptionsMenu();
                }
            }, 500);

        }
    }

    public static void clear() {
        LAST_SEARCH = null;
        LAST_SEARCH_PLAYER = null;
    }

    @Subscribe
    public void onClanReceived(ClanResult result) {
        searching = false;
        pbProgressBar.setVisibility(View.GONE);
        if (result != null) {
            LAST_SEARCH = result;
            tvNoResults.setVisibility(View.GONE);
            receivedClanLogic(result);
        } else {

        }
    }

    @Subscribe
    public void onPlayerRecieved(PlayerResult result) {
        searching = false;
        pbProgressBar.setVisibility(View.GONE);
        if (result != null) {
            LAST_SEARCH_PLAYER = result;
            tvNoResults.setVisibility(View.GONE);
            receivedPlayerLogic(result);
        } else {
        }
    }

    public void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearchBox.getWindowToken(), 0);
    }

    private void refreshCompareSection() {
        StringBuilder sb = new StringBuilder();
        if(CompareManager.size() == 3){
            for(int i = 0; i < 3; i++){
                Player c = CompareManager.getPlayers().get(i);
                sb.append(c.getName());
                if(i == 0)
                    sb.append(", ");
                else if(i == 1)
                    sb.append(" and ");
            }
            sb.append(". 3/3");
            bCompare.setEnabled(true);
            bCompare.setEnabled(true);
        } else if(CompareManager.size() == 2){
            for(int i = 0; i < 2; i++){
                Player c = CompareManager.getPlayers().get(i);
                sb.append(c.getName());
                if(i == 0)
                    sb.append(" & ");
            }
            sb.append(". 2/3");
            bCompare.setEnabled(true);
        } else if(CompareManager.size() == 1){
            for(Player c : CompareManager.getPlayers()){
                sb.append(c.getName());
            }
            sb.append(". 1/3");
            bCompare.setEnabled(false);
        } else {
            sb.append(getString(R.string.search_text_compare));
            bCompare.setEnabled(false);
        }
        tvCompare.setText(sb.toString());
    }
}
