package com.clanassist.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.clanassist.CAApp;
import com.cp.assist.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

/**
 * Created by slai4 on 3/13/2016.
 */
public class WebsiteFragment extends Fragment {

    //websites
    private View aWebsites;
    private View aWoT;
    private View aReddit;
    private View aDRMB;
    private View aAP;

    private ImageView ivWoTIcon;
    private ImageView ivReddit;
    private ImageView ivDRMB;
    private ImageView ivAP;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_websites, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        aWebsites = view.findViewById(R.id.resources_website_area);
        aWoT = view.findViewById(R.id.resources_website_wot);
        aReddit = view.findViewById(R.id.resources_website_reddit);
        aDRMB = view.findViewById(R.id.resources_website_drmb);
        aAP = view.findViewById(R.id.resources_website_ap);

        ivWoTIcon = (ImageView) view.findViewById(R.id.resources_website_website_icon);
        ivReddit = (ImageView) view.findViewById(R.id.resources_website_reddit_icon);
        ivDRMB = (ImageView) view.findViewById(R.id.resources_website_drmb_icon);
        ivAP = (ImageView) view.findViewById(R.id.resources_website_ap_icon);

        UIUtils.setUpCard(aWoT,0);
        UIUtils.setUpCard(aReddit, 0);
        UIUtils.setUpCard(aDRMB, 0);
        UIUtils.setUpCard(aAP, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        UIUtils.refreshActionBar(getActivity(), getString(R.string.drawer_websites));
        setUpWebsites();
    }

    private void setUpWebsites() {
        aWebsites.setVisibility(View.VISIBLE);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = (String) v.getTag();
                Answers.getInstance().logCustom(new CustomEvent("Website Clicked").putCustomAttribute("website", url));
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        };

        aWoT.setTag("http://www.worldoftanks" + CAApp.getServerType(getContext()).getSuffix());
        aReddit.setTag("http://www.reddit.com/r/worldoftanks");
        aDRMB.setTag("http://www.dontrevivemebro.com");
        aAP.setTag("http://thearmoredpatrol.com/");

        aWoT.setOnClickListener(listener);
        aReddit.setOnClickListener(listener);
        aDRMB.setOnClickListener(listener);
        aAP.setOnClickListener(listener);

        ivWoTIcon.setImageResource(R.drawable.worldoftankslogo);
        ivReddit.setImageResource(R.drawable.reddit_wot_logo);
        ivDRMB.setImageResource(R.drawable.twitch);
        ivAP.setImageResource(R.drawable.ic_armored_patrol);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
