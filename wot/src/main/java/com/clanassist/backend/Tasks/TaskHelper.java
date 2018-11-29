package com.clanassist.backend.Tasks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.cp.assist.R;
import com.utilities.Utils;

/**
 * Created by Harrison on 8/4/2014.
 */
public class TaskHelper {

    private Context ctx;
    private NotificationManager manager;
    private NotificationCompat.Builder mBuilder;
    private String clanName;
    private int clanId;

    public TaskHelper(Context ctx, String clanName, int clanId) {
        this.ctx = ctx;
        this.clanName = clanName;
        this.clanId = clanId;
    }

    public void start() {
        manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(ctx);
        mBuilder.setContentTitle(ctx.getString(R.string.download_notification_downloading) + " " + clanName + ctx.getString(R.string.download_notification_information))
                .setContentText(ctx.getString(R.string.download_notification_standard))
                .setSmallIcon(R.drawable.ic_drawer_clan)
                .setAutoCancel(true).setOngoing(true).setOnlyAlertOnce(true);
        Intent i = new Intent(ctx, CancelBroadcastListener.class);
        i.putExtra("Cancel", "cancel" + clanId);
        PendingIntent intent = PendingIntent.getBroadcast(ctx, 4747, i, 0);
        mBuilder.setContentIntent(intent);
        manager.notify(clanId, mBuilder.build());
    }

    public void sendProgressEvent(int progress, int total) {
        try {
            mBuilder.setProgress(total, progress, false);
            manager.notify(clanId, mBuilder.build());
        } catch (Exception e) {
        }
    }

    public void sendDescriptionChange(String change) {
        mBuilder.setContentText(change);
        manager.notify(clanId, mBuilder.build());
    }

    public void done() {
        mBuilder.setContentText(null).setProgress(0, 0, false).setContentTitle(clanName + " " + ctx.getString(R.string.download_notification_done));
        mBuilder.setOngoing(false);
        manager.notify(clanId, mBuilder.build());
        ctx = null;
        mBuilder = null;
        manager = null;
    }

    public void cancel() {
        try {
            mBuilder.setOngoing(false);
            manager.notify(clanId, mBuilder.build());
            manager.cancel(clanId);
            if (!Utils.hasInternetConnection(ctx))
                Toast.makeText(ctx, ctx.getString(R.string.task_no_internet), Toast.LENGTH_SHORT).show();
            ctx = null;
            mBuilder = null;
            manager = null;
        } catch (Exception e) {
        }
    }

    public static class CancelBroadcastListener extends BroadcastReceiver {

        public CancelBroadcastListener() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (CAApp.ongoingTask != null) {
                CAApp.ongoingTask.cancel(true);
            }
        }
    }

}
