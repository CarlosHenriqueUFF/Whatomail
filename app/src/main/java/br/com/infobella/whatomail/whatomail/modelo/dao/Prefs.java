package br.com.infobella.whatomail.whatomail.modelo.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Henrique on 02/07/2016.
 */
public class Prefs extends AppCompatActivity {

    public static final String PREF_ID = "whatomailDB";

    public static void setBoolean(Context context, String chave, boolean on){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(chave, on);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String chave){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        boolean b = pref.getBoolean(chave, false);
        return b;
    }

    public static void setInt(Context context, String chave, int valor){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(chave, valor);
        editor.commit();
    }

    public static int getInt(Context context, String chave){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        int i = pref.getInt(chave, 0);
        return i;
    }

    public static void setLong(Context context, String chave, long valor){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(chave, valor);
        editor.commit();
    }

    public static long getLong(Context context, String chave){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        long l = pref.getLong(chave, 0);
        return l;
    }

    public static void setFloat(Context context, String chave, float valor){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(chave, valor);
        editor.commit();
    }

    public static float getFloat(Context context, String chave){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        float f = pref.getFloat(chave, 0);
        return f;
    }

    public static void setString(Context context, String chave, String valor){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(chave, valor);
        editor.commit();
    }

    public static String getString(Context context, String chave){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        String s = pref.getString(chave, "");
        return s;
    }
}
