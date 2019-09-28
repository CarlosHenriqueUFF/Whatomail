package br.com.infobella.whatomail.whatomail.modelo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infobella.whatomail.whatomail.modelo.MessageReceived;
import br.com.infobella.whatomail.whatomail.modelo.WhatomailApplication;
import utils.LogUtils;

/**
 * Created by HENRI on 27/03/2017.
 */

public class MessageReceivedDB extends DataBaseDB<MessageReceived> {

    public MessageReceivedDB(Context context) {
        super(context);
    }

    @Override
    public long save(MessageReceived m) {
        long id = 0L;
        if (m.getId() != null){
            id = m.getId();
        }
        SQLiteDatabase db = getWritableDatabase();
        long idReturn = 0L;
        try{
            ContentValues values = new ContentValues();
            values.put(MessageReceived.ID_CONTACT_LONG, m.getIdContact());
            values.put(MessageReceived.TIMESTAMP_LONG, m.getTimeStamp());
            values.put(MessageReceived.STATUS_TEXT, m.getStatus().name());
            if (id != 0){
                String _id = String.valueOf(m.getId());
                whereArgs = new String[]{_id};
                whereClause = MessageReceived.ID_SQL_LONG +"=?";
                idReturn  = db.update(MessageReceived.TABLE, values, whereClause, whereArgs);
            } else {
                idReturn = db.insert(MessageReceived.TABLE, nullColumnHack, values);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());

        } finally {
            db.close();
        }
        return idReturn;
    }

    @Override
    public boolean delete(MessageReceived m) {
        long id = m.getId();
        SQLiteDatabase db = getWritableDatabase();
        int count = 0;
        try{
            if (id != 0){
                String _id = String.valueOf(m.getId());
                whereArgs = new String[]{_id};
                whereClause = MessageReceived.ID_SQL_LONG +"=?";
                count  = db.delete(MessageReceived.TABLE, whereClause, whereArgs);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return count != 0;
    }

    @Override
    public List<MessageReceived> toList(Cursor c) {
        List<MessageReceived> messages = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                MessageReceived m = new MessageReceived();
                m.setId(c.getLong(c.getColumnIndex(MessageReceived.ID_SQL_LONG)));
                m.setIdContact(c.getLong(c.getColumnIndex(MessageReceived.ID_CONTACT_LONG)));
                m.setTimeStamp(c.getLong(c.getColumnIndex(MessageReceived.TIMESTAMP_LONG)));
                m.setStatus(MessageReceived.Status.valueOf(c.getString(c.getColumnIndex(MessageReceived.STATUS_TEXT))));
                messages.add(m);
            } while (c.moveToNext());
        }
        return messages;
    }

    @Override
    public List<MessageReceived> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            Cursor c = db.query(distintc, MessageReceived.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            return toList(c);
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
            return new ArrayList<MessageReceived>();
        } finally {
            db.close();
        }
    }

    @Override
    public MessageReceived findById(long id) {
        SQLiteDatabase db = getWritableDatabase();
        MessageReceived m = null;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = MessageReceived.ID_SQL_LONG +"=?";
            selectionArgs = new String[]{String.valueOf(id)};
            Cursor c = db.query(distintc, MessageReceived.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                m = new MessageReceived();
                m.setId(c.getLong(c.getColumnIndex(MessageReceived.ID_SQL_LONG)));
                m.setIdContact(c.getLong(c.getColumnIndex(MessageReceived.ID_CONTACT_LONG)));
                m.setTimeStamp(c.getLong(c.getColumnIndex(MessageReceived.TIMESTAMP_LONG)));
                m.setStatus(MessageReceived.Status.valueOf(c.getString(c.getColumnIndex(MessageReceived.STATUS_TEXT))));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return m;
    }

    public List<MessageReceived> findNextForSend() {
        Date date = new Date();
        Long time = date.getTime();
        time = time - (3 * 60 * 1000);
        SQLiteDatabase db = getWritableDatabase();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = MessageReceived.TIMESTAMP_LONG +"<=? AND "+MessageReceived.STATUS_TEXT +"=?";
            selectionArgs = new String[]{String.valueOf(time), MessageReceived.Status.Waiting.name()};
            Cursor c = db.query(distintc, MessageReceived.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            return toList(c);
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
            return new ArrayList<MessageReceived>();
        } finally {
            db.close();
        }
    }

    public List<MessageReceived> findByStatus(MessageReceived.Status status) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = MessageReceived.STATUS_TEXT +"=?";
            selectionArgs = new String[]{status.name()};
            Cursor c = db.query(distintc, MessageReceived.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            return toList(c);
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
            return new ArrayList<MessageReceived>();
        } finally {
            db.close();
        }
    }

    public MessageReceived findMessagePendindByIdContact(long idContact) {
        SQLiteDatabase db = getWritableDatabase();
        MessageReceived m = null;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = MessageReceived.ID_CONTACT_LONG +"=? AND "+ MessageReceived.STATUS_TEXT +"=?";
            selectionArgs = new String[]{String.valueOf(idContact), MessageReceived.Status.Waiting.name()};
            Cursor c = db.query(distintc, MessageReceived.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                m = new MessageReceived();
                m.setId(c.getLong(c.getColumnIndex(MessageReceived.ID_SQL_LONG)));
                m.setIdContact(c.getLong(c.getColumnIndex(MessageReceived.ID_CONTACT_LONG)));
                m.setTimeStamp(c.getLong(c.getColumnIndex(MessageReceived.TIMESTAMP_LONG)));
                m.setStatus(MessageReceived.Status.valueOf(c.getString(c.getColumnIndex(MessageReceived.STATUS_TEXT))));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return m;
    }
}
