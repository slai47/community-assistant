package com.clanassist.tools;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.clanassist.model.clan.Battle;
import com.clanassist.ui.ActivityFragment;
import com.clanassist.ui.MainActivity;
import com.clanassist.ui.UIUtils;
import com.cp.assist.R;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import java.util.Calendar;

/**
 * Created by Obsidian47 on 3/12/14.
 */
public class AlarmManagement extends BroadcastReceiver {

    public static final String EXTRA_BATTLE_ARENA = "arena";
    public static final String EXTRA_BATTLE_TYPE = "type";
    public static final String EXTRA_BATTLE_PROVINCE = "province";
    public static final String EXTRA_BATTLE_HASH = "hash";

    public static void createAlarm(Context act, Battle b, int minutesBefore) {
        long time = b.getTime().getTimeInMillis();
        Intent i = getIntent(act, b);
        long timeBefore = minutesBefore * 60 * 1000;
        Calendar timeToFire = Calendar.getInstance();
        timeToFire.setTimeInMillis(time - timeBefore);
//        timeToFire.setTimeInMillis(Calendar.getInstance().getTimeInMillis() + 5000); //Testing
        PendingIntent pi = PendingIntent.getBroadcast(act, b.getUuid(), i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) act.getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, timeToFire.getTimeInMillis(), pi);
        saveBattle(act, b, null);
        Toast.makeText(act, R.string.alarm_set, Toast.LENGTH_SHORT).show();
    }

    private static Intent getIntent(Context act, Battle b) {
        Intent i = new Intent(act, AlarmManagement.class);
        try {
            i.putExtra(EXTRA_BATTLE_ARENA, b.getArenaName());
            i.putExtra(EXTRA_BATTLE_PROVINCE, b.getProvince());
            i.putExtra(EXTRA_BATTLE_TYPE, b.getType());
            i.putExtra(EXTRA_BATTLE_HASH, b.getUuid());
        } catch (Exception e) {
            Crashlytics.log(2, "Battle Alarm Crash", "Battle = " + b + "");
        }
        return i;
    }

    public static boolean hasAlarm(Context act, Battle b) {
        boolean hasAlarm = getBattle(act, b.getUuid() + "") != null;
        return hasAlarm;
    }

    public static void removeAlarm(Context act, String hash) {
        Battle b = getBattle(act, hash);
        saveBattle(act, null, hash);
        Intent i = getIntent(act, b);
        AlarmManager am = (AlarmManager) act.getSystemService(Activity.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(act, b.getUuid(), i, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pi.cancel();
            am.cancel(pi);
            Toast.makeText(act, "Alarm removed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int hash = intent.getIntExtra(EXTRA_BATTLE_HASH, 0);
        Battle b = getBattle(context, hash + "");
        removeAlarm(context, hash + "");
        String provinceName = b.getProvinceName();
        Bitmap background = null;
        try {
//            background = BitmapFactory.decodeStream(context.getAssets().open("maps/10_hills_screen.jpg"));
        } catch (Exception e) {
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.notification_text));
        builder.setSmallIcon(R.drawable.ic_battle_activity);
        builder.setOnlyAlertOnce(true);
        if (background != null)
            builder.extend(new NotificationCompat.WearableExtender().setBackground(background));

        String time = UIUtils.getTimeFormat(context, b.getTime(), context.getString(R.string.scheduling));
        builder.setContentText(provinceName + " - " + time);
        builder.setTicker(context.getString(R.string.notification_text));

        Intent i = new Intent(context, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_ONE_SHOT));

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(hash, builder.build());
        ActivityFragment.updateList();
    }

    private static void saveBattle(Context context, Battle b, String hash) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = settings.edit();
        if (b != null) {
            Gson gson = new Gson();
            String str = gson.toJson(b);
            edit.putString(b.getUuid() + "", str);
        } else {
            edit.remove(hash);
        }
        edit.commit();
    }

    private static Battle getBattle(Context context, String hash) {
        Battle b = null;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String str = settings.getString(hash, "");
        if (!TextUtils.isEmpty(str)) {
            b = new Gson().fromJson(str, Battle.class);
        }
        return b;
    }
}
