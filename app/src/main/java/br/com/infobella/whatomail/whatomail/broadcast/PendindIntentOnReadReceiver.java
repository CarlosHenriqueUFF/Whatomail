package br.com.infobella.whatomail.whatomail.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import utils.KeyUtils;
import utils.LogUtils;


/*
 * Created by HENRI on 08/04/2017.
 */

public class PendindIntentOnReadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.writeLog(context, LogUtils.TAG_RECEIVER, "PendindIntentOnReadReceiver onReceive");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(android.support.v7.appcompat.R.drawable.notification_icon_background);
        builder.setContentTitle("PendindIntent onRead");
        builder.setContentText("Send All PendindIntent onReaady");
        builder.setTicker(KeyUtils.KEY_ACTION_PENDIND_INTENT_ON_READ);
        builder.setAutoCancel(true);
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(KeyUtils.ID_PENDIND_INTENT_ON_READ, builder.build());
    }
}
