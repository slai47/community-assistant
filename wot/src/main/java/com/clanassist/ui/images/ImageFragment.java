package com.clanassist.ui.images;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.clanassist.CAApp;
import com.cp.assist.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Harrison on 12/6/2014.
 */
public class ImageFragment extends Fragment {

    public static final String DESCRIPTION = "description";
    public static final String URL = "url";
    private ImageView image;
    private ImageButton close;
    private TextView tvDescription;

    private String url;
    private String description;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        bindView(view);

        if (savedInstanceState != null){
            description = savedInstanceState.getString(DESCRIPTION);
            url = savedInstanceState.getString(URL);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DESCRIPTION, description);
        outState.putString(URL, url);
    }

    private void bindView(View view) {
        image = (ImageView) view.findViewById(R.id.image_view);
        close = (ImageButton) view.findViewById(R.id.image_close);
        tvDescription = (TextView) view.findViewById(R.id.image_description);
        if (CAApp.isLightTheme(close.getContext())) {
            view.setBackgroundResource(R.color.image_fragment_background_light);
            tvDescription.setTextColor(getResources().getColor(R.color.black));
        } else {
            view.setBackgroundResource(R.color.image_fragment_background);
            tvDescription.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        Picasso.with(getActivity()).load(url).error(R.drawable.ic_missing_image).into(image);
        if (!TextUtils.isEmpty(description)) {
            tvDescription.setText(description);
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setMovementMethod(new ScrollingMovementMethod());
        } else {
            tvDescription.setVisibility(View.GONE);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
