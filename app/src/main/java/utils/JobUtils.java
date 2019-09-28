package utils;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import br.com.infobella.whatomail.whatomail.job.WifiConnectedJobService;
import br.com.infobella.whatomail.whatomail.job.WifiDisconnectedJobService;

/*
 * Created by HENRI on 13/04/2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobUtils {

    public static void scheduleWhifiConnected(Context context, int id){
        //JobService que vai executar
        ComponentName componentName = new ComponentName(context, WifiConnectedJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(id, componentName);
        //wifi
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        //Agenda o job
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = builder.build();
        jobScheduler.schedule(jobInfo);
    }

    public static void scheduleWhifiDisconnected(Context context, int id){
        //JobService que vai executar
        ComponentName componentName = new ComponentName(context, WifiDisconnectedJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(id, componentName);
        //wifi
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        //Agenda o job
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = builder.build();
        jobScheduler.schedule(jobInfo);
    }

    public static void cancel(Context context, int id){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(id);
    }

    public static void calcelAll(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
    }
}
