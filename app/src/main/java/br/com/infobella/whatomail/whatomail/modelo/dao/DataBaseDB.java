package br.com.infobella.whatomail.whatomail.modelo.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.infobella.whatomail.whatomail.modelo.Contact;
import br.com.infobella.whatomail.whatomail.modelo.ContactTicket;
import br.com.infobella.whatomail.whatomail.modelo.ConversationReceived;
import br.com.infobella.whatomail.whatomail.modelo.MessageReceived;
import br.com.infobella.whatomail.whatomail.modelo.MessageReply;
import br.com.infobella.whatomail.whatomail.modelo.Veterinary;
import br.com.infobella.whatomail.whatomail.modelo.WhatomailApplication;
import utils.LogUtils;
import utils.UtilsManager;


/*
 * Created by Henrique on 27/07/2016.
 */
public abstract class DataBaseDB<T> extends SQLiteOpenHelper {

    protected static UtilsManager utilsManager;

    public static final String NOME_BANCO = "whatomail.sqlite";
    public static final int VERSAO_BANCO = 4;

    protected final String strDate = "dd/MM/yyyy";
    protected final String strTime = "HH:mm:ss";
    protected final String strTimeIni = "00:00:00";
    protected final String strTimeFim = "23:59:59";

    protected boolean distintc;
    protected String groupBy;
    protected String having;
    protected String orderBy;
    protected String limit;
    protected String[] columns;
    protected String selection;
    protected String[] selectionArgs;

    protected String nullColumnHack;

    protected String whereClause;
    protected String[] whereArgs;

    public DataBaseDB(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
        LogUtils.writeLog(context, LogUtils.TAG_SQL, "Instanciando DataBaseDB");
        distintc = false;
        groupBy = null;
        having = null;
        orderBy = null;
        limit = null;
        columns = null;
        selection = null;
        selectionArgs = null;
        nullColumnHack = null;
        whereClause = null;
        whereArgs = null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_SQL, "Create Table ConversationReceived...");
        db.execSQL("create table if not exists " + ConversationReceived.TABLE + " (" +
                ConversationReceived.ID_SQL_LONG + " integer primary key autoincrement, " +
                ConversationReceived.ID_MESSAGE_LONG + " integer not null, " +
                ConversationReceived.ORDER_INT + " integer not null, " +
                ConversationReceived.TYPE_TEXT + " text not null, " +
                ConversationReceived.FILE_TEXT + " text, " +
                ConversationReceived.KEY_TEXT + " text, " +
                ConversationReceived.TEXT_TEXT + " text not null);");

        LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_SQL, "Create Table MessageReceived...");
        db.execSQL("create table if not exists " + MessageReceived.TABLE + " (" +
                MessageReceived.ID_SQL_LONG + " integer primary key autoincrement, " +
                MessageReceived.ID_CONTACT_LONG + " integer not null, " +
                MessageReceived.STATUS_TEXT + " text not null, " +
                MessageReceived.TIMESTAMP_LONG + " integer not null);");

        LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_SQL, "Create Table Contact...");
        db.execSQL("create table if not exists " + Contact.TABLE + " (" +
                Contact.ID_SQL_LONG + " integer primary key autoincrement, " +
                Contact.JID_WHATSAPP_TEXT + " text not null, " +
                Contact.CONTACT_NAME_TEXT + " text not null, " +
                Contact.PHONE_TEXT + " tetxt not null, " +
                Contact.TICKET_TEXT + " tetxt);");

        LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_SQL, "Create Table Veterinary...");
        db.execSQL("create table if not exists " + Veterinary.TABLE + " (" +
                Veterinary.ID_SQL_LONG + " integer primary key autoincrement, " +
                Veterinary.NAME_TEXT + " text not null, " +
                Veterinary.PHONE_TEXT + " tetxt not null);");

        LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_SQL, "Create Table MessageReply...");
        db.execSQL("create table if not exists " + MessageReply.TABLE + " (" +
                MessageReply.ID_SQL_LONG + " integer primary key autoincrement, " +
                MessageReply.TICKET_TEXT + " text not null, " +
                MessageReply.TEXT_TEXT + " text not null, " +
                MessageReply.STATUS_TEXT + " text not null, " +
                MessageReply.DATE_TIME_RECEIVED_DATE + " integer not null, " +
                MessageReply.DATE_TIME_SENT_DATE + " integer not null, " +
                MessageReply.CONTACT_JID_TEXT + " text not null, " +
                MessageReply.CONTACT_NAME_TEXT + " text);");

        LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_SQL, "Create Table ContactTicket...");
        db.execSQL("create table if not exists " + ContactTicket.TABLE + " (" +
                ContactTicket.ID_SQL_LONG + " integer primary key autoincrement, " +
                ContactTicket.ID_CONTACT_LONG + " integer not null, " +
                ContactTicket.TICKET_TEXT + " text);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_SQL, "DataBaseDB onUpgrade -> oldVersion: " + oldVersion + " - newVersion: " + newVersion);
        if (oldVersion == 1 && newVersion == 2){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_SQL, "Create Table Veterinary...");
            db.execSQL("create table if not exists " + Veterinary.TABLE + " (" +
                    Veterinary.ID_SQL_LONG + " integer primary key autoincrement, " +
                    Veterinary.NAME_TEXT + " text not null, " +
                    Veterinary.PHONE_TEXT + " text not null);");
        } else if (newVersion == 3){
            if (oldVersion == 1){
                LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_SQL, "Create Table Veterinary...");
                db.execSQL("create table if not exists " + Veterinary.TABLE + " (" +
                        Veterinary.ID_SQL_LONG + " integer primary key autoincrement, " +
                        Veterinary.NAME_TEXT + " text not null, " +
                        Veterinary.PHONE_TEXT + " text not null);");
            }

            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_SQL, "Create Table MessageReply...");
            db.execSQL("create table if not exists " + MessageReply.TABLE + " (" +
                    MessageReply.ID_SQL_LONG + " integer primary key autoincrement, " +
                    MessageReply.TICKET_TEXT + " text not null, " +
                    MessageReply.TEXT_TEXT + " text not null, " +
                    MessageReply.STATUS_TEXT + " text not null, " +
                    MessageReply.DATE_TIME_RECEIVED_DATE + " integer not null, " +
                    MessageReply.DATE_TIME_SENT_DATE + " integer not null, " +
                    MessageReply.CONTACT_JID_TEXT + " text not null, " +
                    MessageReply.CONTACT_NAME_TEXT + " text);");
        } else if (newVersion == 4){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_SQL, "Create Table ContactTicket...");
            db.execSQL("create table if not exists " + ContactTicket.TABLE + " (" +
                    ContactTicket.ID_SQL_LONG + " integer primary key autoincrement, " +
                    ContactTicket.ID_CONTACT_LONG + " integer not null, " +
                    ContactTicket.TICKET_TEXT + " text);");
        }
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

    public void execSQL(String sql) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(sql);
        } catch (SQLException ex) {
            Log.e(utilsManager.logUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
    }

    public void execSQL(String sql, Object[] args) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(sql, args);
        } catch (SQLException ex) {
            Log.e(utilsManager.logUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
    }

    public abstract long save(T t);

    public abstract boolean delete(T t);

    public abstract List<T> toList(Cursor c);

    public abstract List<T> findAll();

    public abstract T findById(long id);

}
