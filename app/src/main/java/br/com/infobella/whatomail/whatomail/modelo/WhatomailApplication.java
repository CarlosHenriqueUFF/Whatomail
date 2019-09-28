package br.com.infobella.whatomail.whatomail.modelo;

import android.app.Application;
import android.content.Context;

import utils.LogUtils;
import utils.UtilsManager;


/**
 * Created by Henrique on 17/07/2016.
 */
public class WhatomailApplication extends Application {

    private static WhatomailApplication instance = null;
    private UtilsManager utilsManager;

    public static WhatomailApplication getInstance(){
        if (instance == null){
            instance = new WhatomailApplication();
        }
        return instance;
    }

    public static Context getContext(){
        return WhatomailApplication.getInstance().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.writeLog(this, LogUtils.TAG_GERAL, "WhatomailApplication onCreate()");
        instance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtils.writeLog(this, LogUtils.TAG_GERAL, "WhatomailApplication onTerminate()");
    }
}
