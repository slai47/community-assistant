package com.half.wowsca.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.ui.viewcaptain.ViewCaptainTabbedFragment;
import com.utilities.swipeback.SwipeBackBaseActivity;

import java.util.Locale;

/**
 * Created by slai4 on 4/17/2016.
 */
public class CABaseActivity extends SwipeBackBaseActivity{

    protected static boolean FORCE_REFRESH;

    protected Toolbar mToolbar;

    protected FragmentManager.OnBackStackChangedListener backStackListener;

//    protected ImageView ivKarma;
//    protected TextView tvKarma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CAApp.setTheme(this);
        String current = CAApp.getAppLanguage(getApplicationContext());
        Locale myLocale = new Locale(current);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mToolbar !=  null)
            if(CAApp.isDarkTheme(getApplicationContext()))
                mToolbar.setPopupTheme(R.style.WoWSCAThemeToolbarDarkOverflow);
    }

    protected void initBackStackListener(){
        backStackListener = new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                invalidateOptionsMenu();
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
                try {
                    if(current instanceof ViewCaptainTabbedFragment){
                        ((ViewCaptainTabbedFragment) current).fix();
                    }
                } catch (Exception e) {
                }
            }
        };
    }

//    public void setUpKarma(Captain captain){
//        if(captain != null){
//            int karma = captain.getDetails().getKarma();
//            ivKarma.clearColorFilter();
//            ivKarma.setImageBitmap(null);
//            ivKarma.setVisibility(View.VISIBLE);
//            int color = CAApp.isOceanTheme(getApplicationContext()) ? ContextCompat.getColor(getApplicationContext(), R.color.toolbar_text_color) : ContextCompat.getColor(getApplicationContext(), R.color.average_up);
//            if(karma > 3){
//                tvKarma.setTextColor(CAApp.isOceanTheme(getApplicationContext()) ? ContextCompat.getColor(getApplicationContext(), R.color.toolbar_text_color) : ContextCompat.getColor(getApplicationContext(), R.color.average_up));
//                if(karma <= 12){
//                    ivKarma.setImageResource(R.drawable.ic_thumbs_up);
//                    ivKarma.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
//                } else if(karma <= 25){
//                    ivKarma.setImageResource(R.drawable.ic_thumbs_up_2);
//                    ivKarma.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
//                } else if(karma <= 50){
//                    ivKarma.setImageResource(R.drawable.ic_heart);
//                    ivKarma.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
//                } else {
//                    ivKarma.setImageResource(R.drawable.ic_angel);
//                    ivKarma.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
//                }
//            } else if(karma < -3){
//                tvKarma.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.average_down));
//                if(karma >= -8){
//                    ivKarma.setImageResource(R.drawable.ic_thumbs_down);
//                } else if(karma >= -16){
//                    ivKarma.setImageResource(R.drawable.ic_devil);
//                } else {
//                    ivKarma.setImageResource(R.drawable.ic_danger);
//                }
//            } else {
//                ivKarma.setVisibility(View.GONE);
//                tvKarma.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
//            }
//            if (karma != 0) {
//                tvKarma.setVisibility(View.VISIBLE);
//                tvKarma.setText((karma > 0 ? "+" : "") + karma);
//            } else {
//                tvKarma.setVisibility(View.GONE);
//            }
//        } else {
//            ivKarma.setVisibility(View.GONE);
//            tvKarma.setVisibility(View.GONE);
//        }
//    }
}