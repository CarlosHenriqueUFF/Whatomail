package utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import br.com.infobella.whatomail.whatomail.modelo.WhatomailApplication;

/**
 * Created by Henrique on 09/07/2016.
 */
public class IOUtils {

    public static byte[] toBytes(InputStream in) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (Exception e) {
            Log.e(LogUtils.TAG_GERAL, e.getMessage(), e);
            return null;
        } finally {
            try {
                bos.close();
                in.close();
            } catch (IOException ex) {
                LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_IO, ex.toString());
            }
        }
    }

    public static void writeBytes(File file, byte[] bytes) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_IO, ex.toString());
        }
    }

}
