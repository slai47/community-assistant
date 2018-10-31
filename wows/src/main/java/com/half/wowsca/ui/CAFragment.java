package com.half.wowsca.ui;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.model.events.RefreshEvent;

/**
 * Created by slai4 on 4/19/2016.
 */
public class CAFragment extends Fragment {

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected void bindSwipe(View view){
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
    }

    protected void initSwipeLayout() {
        if(mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    CAApp.getEventBus().post(new RefreshEvent(true));
                }
            });
    }

    protected void refreshing(boolean refreshing)
    {
        if(mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(refreshing);
    }
}