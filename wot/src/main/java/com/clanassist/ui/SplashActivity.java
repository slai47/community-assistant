package com.clanassist.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.clanassist.CAApp;
import com.clanassist.alerts.Alert;
import com.clanassist.backend.Tasks.GetNeededInfoTask;
import com.clanassist.model.events.InfoResult;
import com.clanassist.model.infoobj.InfoQuery;
import com.cp.assist.R;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.utilities.Utils;

/**
 * Created by slai4 on 2/25/2016.
 */
public class SplashActivity extends Activity {

    private ImageView ivImage;
    private View progress;
    private Button bRefresh;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bindView();
    }

    private void bindView() {
        ivImage = (ImageView) findViewById(R.id.splash_image);
        progress = findViewById(R.id.splash_progress);
        tvMessage = (TextView) findViewById(R.id.splash_text);

        bRefresh = (Button) findViewById(R.id.splash_button);

        Picasso.with(getApplicationContext()).load(R.drawable.web_hi_res_512).into(ivImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }

    private void initView() {
        boolean goToNext = true;
        boolean isInfoThere = CAApp.getInfoManager().isInfoThere(getApplicationContext());
        boolean hasConnection = Utils.hasInternetConnection(this);

        if(hasConnection) {
            progress.setVisibility(View.VISIBLE);
            if (!isInfoThere) {
                GetNeededInfoTask task = new GetNeededInfoTask();
                task.setCtx(getApplicationContext());
                InfoQuery query = new InfoQuery();
                task.execute(query);
                goToNext = false;
            }
            if (goToNext) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        CAApp.getInfoManager().getAchievements(getApplicationContext());
                        CAApp.getInfoManager().getTanks(getApplicationContext());
                        CAApp.getInfoManager().getWN8Data(getApplicationContext());
                    }
                };
                Thread t = new Thread(r);
                t.start();
                goToNext(2000);
            }
            bRefresh.setVisibility(View.GONE);
        } else {
            bRefresh.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            bRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initView();
                }
            });
            Alert.generalNoInternetDialogAlert(this, getString(R.string.no_internet_title),
                    getString(R.string.no_internet_message), getString(R.string.no_internet_neutral_text));
        }
    }

    private void goToNext(int delay) {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        }, delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }


    @Subscribe
    public void onInfoRecieved(InfoResult result) {
        progress.post(new Runnable() {
            @Override
            public void run() {
                goToNext(500);
            }
        });
    }
}
