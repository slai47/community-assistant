package com.half.wowsca.ui;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.model.AuthInfo;
import com.half.wowsca.model.enums.Server;
import com.utilities.logging.Dlog;
import com.utilities.preferences.Prefs;
import com.utilities.views.SwipeBackLayout;

/**
 * Created by slai4 on 5/1/2016.
 */
public class AuthenticationActivity extends CABaseActivity {

    WebView webview;
    Toolbar mToolbar;
    View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        webview = (WebView) findViewById(R.id.webView);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        progress = findViewById(R.id.progressBar);

        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.login));

        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.setWebViewClient(new MyCustomWebClient());
        webview.setWebChromeClient(new MyCustonChromeClient());

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        Server server = CAApp.getServerType(getApplicationContext());
        String url = webview.getUrl();
        Dlog.d("AUTH", "url = " + url);
        if(TextUtils.isEmpty(url)){
           webview.loadUrl("https://api.worldoftanks" + server.getSuffix() + "/wot/auth/login/?application_id=" + server.getAppId() + "&redirect_uri=https%3A%2F%2Fna.wargaming.net%2Fdevelopers%2Fapi_explorer%2Fwot%2Fauth%2Flogin%2Fcomplete%2F");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_auth, menu);
        return true;
    }

    private class MyCustomWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Here put your code
            Log.d("My Webview", url);

            if(url.contains("complete/?&status=ok")){
                Uri uri = Uri.parse(url);
                AuthInfo info = new AuthInfo();
                info.setToken(uri.getQueryParameter("access_token"));
                info.setAccount_id(Long.parseLong(uri.getQueryParameter("account_id")));
                info.setExpires(Long.parseLong(uri.getQueryParameter("expires_at")));
                info.setUsername(uri.getQueryParameter("nickname"));
                Dlog.d("AuthURL", info.toString());
//                String[] split = url.split("?");
//                if(split.length > 1){
//                    String params = split[1];
//                    Dlog.d("Auth", params);
//                    String[] pList = params.split("&");
//                    AuthInfo info = new AuthInfo();
//                    for(int i = 0; i < pList.length; i++){
//                        String param = pList[i];
//                        Dlog.d("AuthP", param);
//                        if(param.contains("access_token")){
//                            String token = getParamObj(param);
//                            info.setToken(token);
//                        } else if(param.contains("expires_at")){
//                            String token = getParamObj(param);
//                            info.setExpires(Long.parseLong(token));
//                        } else if(param.contains("account_id")){
//                            String token = getParamObj(param);
//                            info.setAccount_id(Long.parseLong(token));
//                        } else if(param.contains("username")){
//                            String token = getParamObj(param);
//                            info.setToken(token);
//                        }
//                    }
                    info.save(getApplicationContext());
                    finish();
//                }
                return true;
            }
            return false;
        }
    }

    private String getParamObj(String params){
        String[] paramList = params.split("=");
        return paramList[1];
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() ==  android.R.id.home){
            onBackPressed();
        } else if (item.getItemId() == R.id.action_Login){
            Prefs prefs = new Prefs(getApplicationContext());
            prefs.setBoolean(SettingActivity.LOGIN_USER, false);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyCustonChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if(newProgress < 100){
                progress.setVisibility(View.VISIBLE);
            } else {
                progress.setVisibility(View.GONE);
            }
        }
    }
}
