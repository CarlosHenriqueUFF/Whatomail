package utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.StrictMode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import br.com.infobella.whatomail.whatomail.modelo.WhatomailApplication;

/**
 * Created by Henrique on 02/07/2016.
 */
public class HttpUtils<E, R> {

    private Class<R> C;

    public HttpUtils(Class<R> c) {
        C = c;
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public R post(E[] e, String urlPersist) {

        String resp;
        R resposta = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlPersist);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.connect();


            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
                    .create();;
            String json = gson.toJson(e);
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, "Json: "+json);
            out.write(json);
            out.flush();
            out.close();

            if (! C.getSimpleName().equals(Void.class.getSimpleName())) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                resposta = new Gson().fromJson(reader, C);
                reader.close();
            }

        } catch (ProtocolException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } catch (MalformedURLException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } catch (IOException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return resposta;
    }

    public R post(byte[] b, String urlPersist) {

        String resp;
        R resposta = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlPersist);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("enctype", "multipart/form-data");

            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.connect();


            OutputStream out = connection.getOutputStream();
            out.write(b);
            out.flush();
            out.close();

            if (! C.getSimpleName().equals(Void.class.getSimpleName())) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                resposta = new Gson().fromJson(reader, C);
                reader.close();
            }

        } catch (ProtocolException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } catch (MalformedURLException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } catch (IOException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return resposta;
    }

    public R get(String url) {

        R resposta = null;
        try {
            InputStreamReader reader = new InputStreamReader(new URL(url).openStream());
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
                    .create();
            resposta = gson.fromJson(reader, C);
            reader.close();

        } catch (ProtocolException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } catch (MalformedURLException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } catch (IOException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        }
        return resposta;
    }

    public static boolean isNetworkAvailable(Context context){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null){
                return false;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Network[] networks = connectivityManager.getAllNetworks();
                    NetworkInfo networkInfo;
                    for (Network network: networks){
                        networkInfo = connectivityManager.getNetworkInfo(network);

                        if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)){
                            return true;
                        }
                    }
                } else {
                    if (connectivityManager.getActiveNetworkInfo() != null) {
                        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isAvailable()){
                            if(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (SecurityException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        }
        return false;
    }

    public R getUrlImagePerfil(String action, String id) {
        String url = UrlUtils.URL_PHOTO_PERFIL;
        if (action.equals(UrlUtils.ACTION_DOWNLOAD)){
            url = url + UrlUtils.ACTION_DOWNLOAD + "&id=" + id;
        } else if (action.equals(UrlUtils.ACTION_UPLOAD)){
            url = url + UrlUtils.ACTION_UPLOAD;
        }
        R resposta = null;
        try {
            InputStreamReader reader = new InputStreamReader(new URL(url).openStream());
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
                    .create();
            resposta = gson.fromJson(reader, C);
            reader.close();

        } catch (ProtocolException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } catch (MalformedURLException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } catch (IOException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        }
        return resposta;
    }

    public R getUrlImagePerfilSalao(String action, String id, String position) {
        String url = UrlUtils.URL_PHOTO_PERFIL_SALAO;
        if (action.equals(UrlUtils.ACTION_DOWNLOAD)){
            url = url + UrlUtils.ACTION_DOWNLOAD + "&id=" + id + "&pos=" + position;
        } else if (action.equals(UrlUtils.ACTION_UPLOAD)){
            url = url + UrlUtils.ACTION_UPLOAD;
        }
        R resposta = null;
        try {
            InputStreamReader reader = new InputStreamReader(new URL(url).openStream());
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
                    .create();
            resposta = gson.fromJson(reader, C);
            reader.close();

        } catch (ProtocolException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } catch (MalformedURLException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        } catch (IOException ex) {
            LogUtils.writeLog(WhatomailApplication.getContext(),LogUtils.TAG_HTTP, ex.toString());
        }
        return resposta;
    }

    public static void updateAPN(Context paramContext, boolean enable) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Method setMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(connectivityManager, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateWifi(Context paramContext, boolean enable) {
        try {
            WifiManager wifi = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
