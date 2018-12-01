package com.half.wowsca.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.alerts.Alert;
import com.half.wowsca.interfaces.SearchInterface;
import com.half.wowsca.managers.CaptainManager;
import com.half.wowsca.managers.CompareManager;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.enums.Server;
import com.half.wowsca.model.events.AddRemoveEvent;
import com.half.wowsca.model.result.SearchResults;
import com.half.wowsca.ui.adapter.CompareAdapter;
import com.half.wowsca.ui.adapter.SearchAdapter;
import com.half.wowsca.ui.compare.CompareActivity;
import com.half.wowsca.ui.viewcaptain.ViewCaptainActivity;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.Utils;
import com.utilities.logging.Dlog;
import com.utilities.views.SwipeBackLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends CABaseActivity implements SearchInterface {

    private Toolbar mToolbar;

    private EditText etSearch;
    private View delete;

    private Spinner sServers;

    private View progress;

    private ListView listView;

    private TextView tvError;

    private boolean searching;

    private Button bCompare;
    private TextView tvCompare;

    private CompareAdapter compareAdapter;

    private String savedSearch;

    private SearchPresenter controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        bindView();
        if (savedInstanceState != null) {
            savedSearch = savedInstanceState.getString("search");
        }
    }

    private void bindView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        etSearch = (EditText) findViewById(R.id.search_et);
        delete = findViewById(R.id.search_et_delete);
        sServers = (Spinner) findViewById(R.id.search_server_spinner);
        listView = (ListView) findViewById(R.id.search_listview);
        progress = findViewById(R.id.progressBar);
        tvError = (TextView) findViewById(R.id.search_error_text);

        bCompare = (Button) findViewById(R.id.search_compare_button);
        tvCompare = (TextView) findViewById(R.id.search_compare_text);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        controller = new SearchPresenter(getApplicationContext());
        controller.view = this;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("search", etSearch.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
        CaptainManager.deleteTemp(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        controller.dispose();
    }

    private void initView() {
        initCompare();

        initSearch();

        initServerSpinner();

        initListOnClick();

        initAutoSearchOnRotate();

        autoPlaceSavedCaptains();
    }

    private void initAutoSearchOnRotate() {
        if (!TextUtils.isEmpty(savedSearch)) {
            etSearch.setText(savedSearch);
            search();
        }
        if (listView.getAdapter() != null) {
            try {
                SearchAdapter adapter1 = (SearchAdapter) listView.getAdapter();
                adapter1.notifyDataSetChanged();
            } catch (Exception e) {
            }
        }
    }

    private void autoPlaceSavedCaptains() {
        if (etSearch.getText().toString().trim().length() == 0) {
            List<Captain> captains = new ArrayList<>();
            Map<String, Captain> savedCaptains = CaptainManager.getCaptains(getApplicationContext());
            String selectedId = CAApp.getSelectedId(getApplicationContext());
            for (Captain c : savedCaptains.values()) {
                if (!CaptainManager.createCapIdStr(c.getServer(), c.getId()).equals(selectedId)) {
                    captains.add(c);
                }
            }
            if (captains.size() > 0) {
                SearchAdapter defaultSearch = new SearchAdapter(getApplicationContext(), R.layout.list_search, captains);
                listView.setAdapter(defaultSearch);
            }
        }
    }

    private void initCompare() {
        if (CompareManager.size() > 1) {
            bCompare.setEnabled(true);
        } else {
            bCompare.setEnabled(false);
        }
        refreshCompareSection();
        bCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity act = SearchActivity.this;
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle(getString(R.string.compare_list));
                compareAdapter = new CompareAdapter(act, R.layout.list_compare, CompareManager.getCaptains());
                builder.setAdapter(compareAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Captain c = compareAdapter.getItem(which);
                        CompareManager.removeCaptain(c.getServer(), c.getId());
                        compareAdapter.remove(c);
                        compareAdapter.notifyDataSetChanged();
                        refreshCompareSection();
                    }
                });
                builder.setPositiveButton(getString(R.string.compare), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (CompareManager.size() > 1) {
                            Intent i = new Intent(getApplicationContext(), CompareActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.not_enough_captains, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.clear_list), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CompareManager.clear();
                        refreshCompareSection();
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton(getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void initSearch() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEARCH && !searching) {
                    search();
                }
                return false;
            }
        });

        etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER && !searching) {
                    search();
                    return true;
                }
                return false;
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etSearch.getText().toString().length() > 0) {
                    delete.setVisibility(View.VISIBLE);
                } else {
                    delete.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initListOnClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchAdapter adapter = (SearchAdapter) listView.getAdapter();
                if (adapter != null) {
                    Captain cap = adapter.getItem(position);
                    Intent i = new Intent(getApplicationContext(), ViewCaptainActivity.class);
                    i.putExtra(ViewCaptainActivity.EXTRA_ID, cap.getId());
                    i.putExtra(ViewCaptainActivity.EXTRA_NAME, cap.getName());
                    i.putExtra(ViewCaptainActivity.EXTRA_SERVER, cap.getServer().toString());
                    startActivity(i);
                } else {

                }
            }
        });
        // on long click to add or remove
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SearchAdapter adapter = (SearchAdapter) listView.getAdapter();
                if (adapter != null) {
                    Captain cap = adapter.getItem(position);
                    int bSize = CompareManager.size();
                    if (!CompareManager.isAlreadyThere(cap.getServer(), cap.getId())) {
                        boolean wasAdded = CompareManager.addCaptain(cap, false);
                        if (bSize == 0) {
                            String selectedId = CAApp.getSelectedId(view.getContext());
                            if (!TextUtils.isEmpty(selectedId)) {
                                Map<String, Captain> captains = CaptainManager.getCaptains(view.getContext());
                                Captain selected = captains.get(selectedId);
                                if (selected != null && !CompareManager.isAlreadyThere(selected.getServer(), selected.getId())) {
                                    Activity act = SearchActivity.this;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                                    builder.setTitle(getString(R.string.compare_add_title));
                                    builder.setMessage(getString(R.string.compare_dialog_message));
                                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String selectedId = CAApp.getSelectedId(getApplicationContext());
                                            Map<String, Captain> captains = CaptainManager.getCaptains(getApplicationContext());
                                            Captain selected = captains.get(selectedId);
                                            CompareManager.addCaptain(selected, true);
                                            refreshCompareSection();
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                } else {
                                    CAApp.setSelectedId(view.getContext(), null);
                                }
                            }
                        }
                        Dlog.wtf("SearchActivity", "size = " + bSize + " wasAdded = " + wasAdded);
                        if (wasAdded) {
                            refreshCompareSection();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.compare_max_message, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        CompareManager.removeCaptain(cap.getServer(), cap.getId());
                        refreshCompareSection();
                    }
                } else {
                }
                return true;
            }
        });
    }

    private void initServerSpinner() {
        List<String> servers = new ArrayList<String>();
        Server current = CAApp.getServerType(getApplicationContext());
        servers.add(current.toString().toUpperCase());
        for (Server s : Server.values()) {
            if (current.ordinal() != s.ordinal()) {
                servers.add(s.toString().toUpperCase());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.ca_spinner_item_trans, servers);
        adapter.setDropDownViewResource(!CAApp.isDarkTheme(sServers.getContext()) ? R.layout.ca_spinner_item : R.layout.ca_spinner_item_dark);
        sServers.setAdapter(adapter);
        sServers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void refreshCompareSection() {
        StringBuilder sb = new StringBuilder();
        if (CompareManager.size() == 3) {
            for (int i = 0; i < 3; i++) {
                Captain c = CompareManager.getCaptains().get(i);
                sb.append(c.getName());
                if (i == 0)
                    sb.append(", ");
                else if (i == 1)
                    sb.append(" and ");
            }
            sb.append(getString(R.string.compare_bottom_text_max));
            bCompare.setEnabled(true);
            bCompare.setEnabled(true);
        } else if (CompareManager.size() == 2) {
            for (int i = 0; i < 2; i++) {
                Captain c = CompareManager.getCaptains().get(i);
                sb.append(c.getName());
                if (i == 0)
                    sb.append(" & ");
            }
            sb.append(getString(R.string.compare_bottom_text_middle));
            bCompare.setEnabled(true);
        } else if (CompareManager.size() == 1) {
            for (Captain c : CompareManager.getCaptains()) {
                sb.append(c.getName());
            }
            sb.append(getString(R.string.compare_bottom_text_single));
            bCompare.setEnabled(false);
        } else {
            sb.append(getString(R.string.search_compare_default_text));
            bCompare.setEnabled(false);
        }
        tvCompare.setText(sb.toString());
    }

    private void search() {
        boolean connected = Utils.hasInternetConnection(this);
        if (connected) {
            String searchTerm = etSearch.getText().toString();
            if (!TextUtils.isEmpty(searchTerm)) {
                searching = true;
                closeKeyboard(this);
                progress.setVisibility(View.VISIBLE);
                listView.setAdapter(null);
                tvError.setVisibility(View.GONE);
                controller.search(searchTerm);
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_text_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Alert.generalNoInternetDialogAlert(this, getString(R.string.no_internet_title), getString(R.string.no_internet_message), getString(R.string.no_internet_neutral_text));
        }
    }

    public void onReceive(final SearchResults result) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
                searching = false;
                if (result != null) {
                    if (result.getCaptains() != null) {
                        if (result.getCaptains().size() > 0) {
                            SearchAdapter adapter = new SearchAdapter(getApplicationContext(), R.layout.list_search, result.getCaptains());
                            listView.setAdapter(adapter);
                            tvError.setVisibility(View.GONE);
                        } else {
                            tvError.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvError.setVisibility(View.VISIBLE);
                    }
                } else {
                }
            }
        });
    }

    @Subscribe
    public void onAddRemove(AddRemoveEvent event) {
        if (!event.isRemove()) {
            UIUtils.createBookmarkingDialogIfNeeded(this, event.getCaptain());
            CaptainManager.saveCaptain(getApplicationContext(), event.getCaptain());
        } else {
            CaptainManager.removeCaptain(getApplicationContext(), CaptainManager.createCapIdStr(event.getCaptain().getServer(), event.getCaptain().getId()));
        }

    }

    public void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }
}
