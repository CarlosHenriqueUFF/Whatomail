package utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import br.com.infobella.whatomail.whatomail.modelo.Config;
import br.com.infobella.whatomail.whatomail.service.LogService;

/**
 * Created by Henrique on 17/07/2016.
 */
public class LogUtils {
    public static final String TAG_GERAL    = "WTM_APP";
    public static final String TAG_LOG      = "WTM_APP_LOG";
    public static final String TAG_LISTENER = "WTM_APP_LISTENER";
    public static final String TAG_STORAGE  = "WTM_APP_STORAGE";
    public static final String TAG_ALARM    = "WTM_APP_ALARM";
    public static final String TAG_CONTACT  = "WTM_APP_CONTACT";
    public static final String TAG_HTTP     = "WTM_APP_HTTP";
    public static final String TAG_IMAGE    = "WTM_APP_IMAGE";
    public static final String TAG_IO       = "WTM_APP_IO";
    public static final String TAG_RECEIVER = "WTM_APP_RECEIVER";
    public static final String TAG_ERROR    = "WTM_APP_ERRO";
    public static final String TAG_SQL      = "WTM_APP_SQL";
    public static final String TAG_SHARE    = "WTM_APP_SHARE";


    public static void writeLog(Context context, String tag, String log){
        if (Config.LOG) {
            Log.v(tag, log);
        }
        if (Config.LOG_FILE) {
            Intent intent = new Intent(context, LogService.class);
            intent.putExtra(KeyUtils.KEY_TAG, tag);
            intent.putExtra(KeyUtils.KEY_LOG, log);
            context.startService(intent);
        }
    }
}
