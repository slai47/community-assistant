package com.half.wowsca.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.half.wowsca.R;

/**
 * Created by slai4 on 9/15/2015.
 */
public class DefaultFragment extends CAFragment {

    TextView tvText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        tvText = (TextView) view.findViewById(R.id.textView);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        tvText.setText(getString(R.string.patch_notes) + getString(R.string.update_notes_achieve));
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
