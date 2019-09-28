package br.com.infobella.whatomail.whatomail.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import utils.FileUtils;
import utils.KeyUtils;
import utils.LogUtils;

/*
 * Created by HENRI on 24/04/2017.
 */

public class LogService extends IntentService{

    protected File fileLog;
    private FileOutputStream fileOutputStream;
    private OutputStreamWriter outputStreamWriter;

    public LogService() {
        super("LogService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(LogUtils.TAG_LOG, " -------------------------- LogService: onHandleIntent --------------------------------------");
        Bundle bundle = intent.getExtras();
        String tag = bundle.getString(KeyUtils.KEY_TAG);
        String log = bundle.getString(KeyUtils.KEY_LOG);
        String value = tag + " : " + log + "\r\n";
        if (outputStreamWriter != null){
            try {
                outputStreamWriter.append(value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        Log.v(LogUtils.TAG_LOG, " -------------------------- LogService: onCreate --------------------------------------");
        fileLog = FileUtils.getFileLog();
        try {
            fileOutputStream = new FileOutputStream(fileLog);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.v(LogUtils.TAG_LOG, " -------------------------- LogService: onDestroy ----------------------------------------");
        try {
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
