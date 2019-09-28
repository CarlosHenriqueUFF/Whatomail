package br.com.infobella.whatomail.whatomail.job;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import utils.KeyUtils;
import utils.LogUtils;

/**
 * Created by HENRI on 13/04/2017.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class WifiConnectedJobService extends JobService {

    public WifiConnectedJobService() {
        super();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        LogUtils.writeLog(this, LogUtils.TAG_RECEIVER, "WifiConnectedJobService onStart");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.support.v7.appcompat.R.drawable.notification_icon_background);
        builder.setContentTitle("PendindIntent onRead");
        builder.setContentText("Send All PendindIntent onRead");
        builder.setTicker(KeyUtils.KEY_ACTION_PENDIND_INTENT_NET_ON);
        builder.setAutoCancel(true);
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.notify(KeyUtils.ID_PENDIND_INTENT_ON_READ, builder.build());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LogUtils.writeLog(this, LogUtils.TAG_RECEIVER, "WifiConnectedJobService onStopJob");
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.writeLog(this, LogUtils.TAG_RECEIVER, "WifiConnectedJobService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.writeLog(this, LogUtils.TAG_RECEIVER, "WifiConnectedJobService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.writeLog(this, LogUtils.TAG_RECEIVER, "WifiConnectedJobService onDestroy");
    }
}
