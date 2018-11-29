package com.half.wowsca.ui.compare;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.managers.CompareManager;
import com.half.wowsca.ui.CAFragment;
import com.half.wowsca.ui.adapter.ShipModuleCompareAdapter;
import org.greenrobot.eventbus.Subscribe;
import com.utilities.logging.Dlog;

/**
 * Created by slai47 on 5/21/2017.
 */

public class ShipModuleListFragment extends CAFragment {

    private RecyclerView gridView;

    private ShipModuleCompareAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare_module_list, container, false);
        onBind(view);
        return view;
    }

    private void onBind(View view) {
        gridView = (RecyclerView) view.findViewById(R.id.fragment_compare_module_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        initView();
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    private void initView() {
        if(adapter == null) {
            GridLayoutManager manager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.ship_rows));
            gridView.setLayoutManager(manager);
            adapter = new ShipModuleCompareAdapter(CompareManager.getSHIPS(), getContext());
            gridView.setAdapter(adapter);
        } else {

        }
    }

    @Subscribe
    public void onRefresh(Long shipId){
        Dlog.d("ShiModuleListFragment", "onRefresh = " + shipId);
        if(adapter != null){
            int i;
            for(i = 0 ; i < CompareManager.getSHIPS().size(); i++){
                if(CompareManager.getSHIPS().get(i) == shipId)
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    }
}