package com.clanassist.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.clanassist.CAApp;
import com.cp.assist.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Harrison on 4/25/2015.
 */
public class DonationFragment extends Fragment {

    String link = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=C4NPEHPXGYPN8&lc=US&item_name=HALF&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";

    public static boolean VIEW_AD;

    InterstitialAd mInterstitialAd;

//    private WebView webView;
    private Button bPatreon;
    private Button bViewAd;
    private View progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate, container, false);
//        webView = (WebView) view.findViewById(R.id.donation_webview);
        bViewAd = (Button) view.findViewById(R.id.donation_view_ad);
        progress = view.findViewById(R.id.donation_view_ad_progress);
        bPatreon = (Button) view.findViewById(R.id.donation_paypal);

        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(!CAApp.DEVELOPMENT_MODE ? getString(R.string.ad_unit_id) : getString(R.string.test_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                progress.setVisibility(View.GONE);
            }
        });

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
        UIUtils.refreshActionBar(getActivity(), getString(R.string.drawer_donate));
//        webView.setWebChromeClient(new WebChromeClient());
//        webView.setWebViewClient(new MyWebViewClient());
//        webView.setBackgroundColor(Color.TRANSPARENT);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setSupportZoom(false);
//        webView.loadUrl("file:///android_asset/donation.html");

        bViewAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNewInterstitial();
            }
        });
        if(VIEW_AD){
            requestNewInterstitial();
        }

        bPatreon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("Support").putCustomAttribute("Type", "Patreon"));
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.patreon.com/slai47"));
                startActivity(i);
            }
        });
    }

    private void requestNewInterstitial() {
        progress.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
        Answers.getInstance().logCustom(new CustomEvent("Support").putCustomAttribute("Type", "Ad"));
        VIEW_AD = false;
    }

    protected class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url)) {
                Answers.getInstance().logCustom(new CustomEvent("Support").putCustomAttribute("Type", "Donate"));
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getActivity().startActivity(i);
                return true;
            }
            return false;
        }
    }

}
