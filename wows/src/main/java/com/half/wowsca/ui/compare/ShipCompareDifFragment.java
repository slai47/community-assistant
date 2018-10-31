package com.half.wowsca.ui.compare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.half.wowsca.R;
import com.half.wowsca.ui.CAFragment;

/**
 * Created by slai47 on 3/5/2017.
 */

public class ShipCompareDifFragment extends CAFragment {

    /**
     * this holds all the comparison for every ship
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare_ships_dif, container, false);
        onBind(view);
        return view;
    }

    private void onBind(View view) {

    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {

    }
}
