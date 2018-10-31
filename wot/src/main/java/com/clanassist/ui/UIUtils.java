package com.clanassist.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;

import com.clanassist.CAApp;
import com.clanassist.SVault;
import com.clanassist.model.player.Player;
import com.clanassist.tools.NavigationDrawerManager;
import com.cp.assist.R;
import com.utilities.Utils;
import com.utilities.preferences.Prefs;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Harrison on 5/21/2014.
 */
public class UIUtils {

    public static final float CHART_SIZE_PORTION = 4.5f;

    public static void createReviewDialog(final Activity act){
        Prefs prefs = new Prefs(act);
        boolean hasReviewed = prefs.getBoolean("hasReviewed", false);
        if (!hasReviewed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle(act.getString(R.string.dialog_review_title));
            builder.setMessage(act.getString(R.string.info_write_a_review));
            builder.setPositiveButton("Google Play", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Prefs prefs = new Prefs(act);
                    prefs.setBoolean("hasReviewed", true);
                    String url = "https://play.google.com/store/apps/details?id=com.cp.assist";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    act.startActivity(i);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            CAApp.HAS_SHOWN_FIRST_DIALOG = true;
        }
    }

    public static void createDonationDialog(final Activity act){
        Prefs prefs = new Prefs(act);
        boolean hasDonated = prefs.getBoolean("hasDonated", false);
        if (!hasDonated) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle(act.getString(R.string.dialog_donation_title));
            builder.setMessage(act.getString(R.string.dialog_donation_message));
            builder.setPositiveButton(R.string.patreon, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Prefs prefs = new Prefs(act);
                    prefs.setBoolean("hasDonated", true);
                    String url = "https://www.patreon.com/slai47";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    act.startActivity(i);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.fragment_donate_view_ad, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity) act).selectDrawerGroup(NavigationDrawerManager.DONATE_ID);
                    DonationFragment.VIEW_AD = true;
                    dialog.dismiss();
                }
            });
            builder.setNeutralButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            CAApp.HAS_SHOWN_FIRST_DIALOG = true;
        }
    }

    public static void createBookmarkingDialogIfNeeded(final MainActivity act, final Player p) {
        Prefs prefs = new Prefs(act);
        boolean isFirstTimeAddingPlayer = prefs.getBoolean(SVault.PREF_FIRST_TIME_TO_BOOKMARK, true);
        if (isFirstTimeAddingPlayer) {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle(act.getString(R.string.dialog_title_bookmarking));
            builder.setMessage(act.getString(R.string.dialog_message_bookmarking));
            if (CAApp.isLightTheme(act))
                builder.setIcon(R.drawable.ic_drawer_favorite_light);
            else
                builder.setIcon(R.drawable.ic_drawer_favorite);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CAApp.setDefaultPlayer(act.getApplicationContext(), p.getName(), p.getId());
                    act.updateDrawer(null, true, true, false);
                    act.invalidateOptionsMenu();

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            prefs.setBoolean(SVault.PREF_FIRST_TIME_TO_BOOKMARK, false);
        }
    }

    public static String getTimeFormat(Context context, Calendar time, String errorMessage) {
        String t;
        if (time != null) {
            DateFormat df = Utils.getHourMinuteFormatter(context);
            t = df.format(time.getTime());
        } else {
            t = errorMessage;
        }
        return t;
    }


    public static void refreshActionBar(Activity act, String title) {
        if (TextUtils.isEmpty(title)) {
            title = act.getString(R.string.app_name);
        }
        ((AppCompatActivity) act).getSupportActionBar().setTitle(title);
        act.invalidateOptionsMenu();
    }

    public static void setUpCard(View parent, int id) {
        CardView card = null;
        if (id > 0)
            card = (CardView) parent.findViewById(id);
        else
            card = (CardView) parent;
        int backgroundInt = R.color.material_card_background;
        if (CAApp.isLightTheme(parent.getContext())) {
            backgroundInt = R.color.material_light_card_background;
        }
        card.setCardBackgroundColor(parent.getContext().getResources().getColor(backgroundInt));
        card.setRadius(6.0f);
        card.setCardElevation(2f);
    }

    public static int getChartHeight(Activity act) {
        Display display = act.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = (int) (size.y / CHART_SIZE_PORTION);
        if (size.y < size.x) {
            height = (int) (size.x / CHART_SIZE_PORTION);
        }
        return height;
    }
}
