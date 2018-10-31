package com.half.wowsca.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.alerts.Alert;
import com.half.wowsca.backend.GetNeededInfoTask;
import com.half.wowsca.model.enums.ShortcutRoutes;
import com.half.wowsca.model.queries.InfoQuery;
import com.half.wowsca.model.result.InfoResult;
import org.greenrobot.eventbus.Subscribe;
import com.squareup.picasso.Picasso;
import com.utilities.Utils;
import com.utilities.preferences.Prefs;

/**
 * Created by slai4 on 10/31/2015.
 */
public class SplashActivity extends CABaseActivity {

    public static final String GRABBING_INFO = "grabbingInfo";
    public static final String CALL_NEXT_DONE = "callNextDone";
    private View progressBar;
    private View button;
    private ImageView iv;

    private boolean callNext;
    private boolean grabbingInfo;
    private boolean goToNext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (savedInstanceState != null) {
            callNext = savedInstanceState.getBoolean(CALL_NEXT_DONE);
            grabbingInfo = savedInstanceState.getBoolean(GRABBING_INFO);
        }
        bindView();
        if(getIntent() != null && !TextUtils.isEmpty(getIntent().getAction())){
            String action = getIntent().getAction();
            if(action.equals("com.half.wowsca.VIEW_SHIPOPEDIA")){
                CAApp.ROUTING = ShortcutRoutes.ENCYCLOPEDIA;
            } else if (action.equals("com.half.wowsca.VIEW_TWITCH")){
                CAApp.ROUTING = ShortcutRoutes.TWITCH;
            } else if (action.equals("com.half.wowsca.VIEW_SEARCH")){
                CAApp.ROUTING = ShortcutRoutes.SEARCH;
            } else {
                Prefs prefs = new Prefs(getApplicationContext());
                if(prefs.getBoolean(SettingActivity.AD_LAUNCH, false))
                    CAApp.ROUTING = ShortcutRoutes.AD_LAUNCH;
                else
                    CAApp.ROUTING = null;
            }
        } else {
            CAApp.ROUTING = null;
        }
    }

    private void bindView() {
        progressBar = findViewById(R.id.splash_progress);
        button = findViewById(R.id.splash_button);
        iv = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this).load(R.drawable.web_hi_res_512).into(iv);

        getSwipeBackLayout().setEnableGesture(false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            iv.setImageBitmap(null);
        } catch (Exception e) {
        }
    }

    private void initView() {
        boolean hasAllInfo = CAApp.getInfoManager().isInfoThere(getApplicationContext());
        boolean connected = Utils.hasInternetConnection(getApplicationContext());
        if (connected) {
            progressBar.setVisibility(View.VISIBLE);
            button.setVisibility(View.GONE);
            if (!callNext && hasAllInfo) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        CAApp.getInfoManager().load(getApplicationContext());
                    }
                };
                r.run();
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (goToNext) {
                            goToNext();
                        }
                    }
                }, 2000);
                goToNext = true;
                callNext = true;
            } else if (!grabbingInfo) {
                getInfo();
            }
        } else {
            Alert.generalNoInternetDialogAlert(this, getString(R.string.no_internet_title), getString(R.string.no_internet_message), getString(R.string.no_internet_neutral_text));
            progressBar.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                initView();
            }
        });
    }

    private void goToNext() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    private void getInfo() {
        getNeededInfo();
    }

    private void getNeededInfo() {
        grabbingInfo = true;
        InfoQuery query = new InfoQuery();
        query.setServer(CAApp.getServerType(getApplicationContext()));
        GetNeededInfoTask task = new GetNeededInfoTask();
        task.setCtx(getApplicationContext());
        task.execute(query);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CALL_NEXT_DONE, callNext);
        outState.putBoolean(GRABBING_INFO, grabbingInfo);
    }

    @Subscribe
    public void onInfoRecieved(InfoResult result) {
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                grabbingInfo = false;
                goToNext();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToNext = false;
    }
}
