package br.com.infobella.whatomail.whatomail.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observer;

import br.com.infobella.whatomail.whatomail.modelo.dao.DaoManager;
import br.com.infobella.whatomail.whatomail.modelo.MyObservable;
import br.com.infobella.whatomail.whatomail.R;
import utils.UtilsManager;

/**
 * Created by Henrique on 01/07/2016.
 */
public abstract class BaseActivity extends AppCompatActivity implements Observer {

    protected String[] sexos = new String[]{"Selecione", "Masculino", "Feminino"};
    protected String[] estados = new String[]{"AC","AL","AP","AM","BA","CE","DF","ES","GO","MA","MT","MS","MG","PA",
            "PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO"};
    protected final String strDate = "dd/MM/yyyy";
    protected final String strTime = "HH:mm:ss";
    protected final String strTimeIni = "00:00:00";
    protected final String strTimeFim = "23:59:59";

    protected static DaoManager daoManager = new DaoManager();
    protected UtilsManager utilsManager = new UtilsManager();

    protected Context getContext() {
        return this;
    }

    protected static MyObservable myObservable = new MyObservable();

    protected void alertCurto(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    protected void alertLongo(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    protected void alertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void alertDialogFinishActivity(String title, String message, final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        activity.finish();
                    }
                }
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected Date stringToDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(strDate);
        Date date = null;
        if ((!dateStr.equals("  /  /    ")) && (!dateStr.substring(0, 2).equals("00")) && (!dateStr.substring(3, 5).equals("00"))) {
            try {
                date = formatter.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    protected String dateToString(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat(strDate);
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

}
