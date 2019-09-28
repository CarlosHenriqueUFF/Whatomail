package utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;

import java.util.Date;

/**
 * Created by HENRI on 08/04/2017.
 */

public class AlarmUtils {

    public static void schedule(Context context, Intent intent, long triggerAtMillis) {
        PendingIntent p = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, p);
        LogUtils.writeLog(context, LogUtils.TAG_ALARM, "Alarme agendado com sucesso");
    }

    public static void scheduleRepeat(Context context, Intent intent, long triggerAtMillis, long intervalMillis) {
        PendingIntent p = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, p);
        LogUtils.writeLog(context, LogUtils.TAG_ALARM, "Alarme com repetição agendado com sucesso");
    }

    public static void cancel(Context context, Intent intent){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent p = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(p);
        LogUtils.writeLog(context, LogUtils.TAG_ALARM, "Alarme cancelado com sucesso");
    }

    public static long getTime(long seconds){
        Date date = new Date();
        long time = date.getTime();
        if (seconds > 0){
            return time + (seconds * 1000);
        } else {
            return time;
        }
    }
}
