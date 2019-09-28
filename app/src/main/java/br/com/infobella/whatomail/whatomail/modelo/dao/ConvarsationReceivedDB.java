package br.com.infobella.whatomail.whatomail.modelo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infobella.whatomail.whatomail.modelo.ConversationReceived;
import br.com.infobella.whatomail.whatomail.modelo.WhatomailApplication;
import utils.LogUtils;

/**
 * Created by HENRI on 27/03/2017.
 */

public class ConvarsationReceivedDB extends DataBaseDB<ConversationReceived> {

    public ConvarsationReceivedDB(Context context) {
        super(context);
    }

    @Override
    public long save(ConversationReceived c) {
        long id = 0L;
        if (c.getId() != null){
            id = c.getId();
        }
        SQLiteDatabase db = getWritableDatabase();
        long idReturn = 0L;
        try{
            ContentValues values = new ContentValues();
            values.put(ConversationReceived.ID_MESSAGE_LONG, c.getIdMessage());
            values.put(ConversationReceived.ORDER_INT, c.getOrder());
            values.put(ConversationReceived.TEXT_TEXT, c.getText());
            values.put(ConversationReceived.TYPE_TEXT, c.getType().name());
            values.put(ConversationReceived.KEY_TEXT, c.getKey());
            values.put(ConversationReceived.FILE_TEXT, c.getFile());
            if (id != 0){
                String _id = String.valueOf(c.getId());
                whereArgs = new String[]{_id};
                whereClause = ConversationReceived.ID_SQL_LONG +"=?";
                idReturn  = db.update(ConversationReceived.TABLE, values, whereClause, whereArgs);
            } else {
                idReturn = db.insert(ConversationReceived.TABLE, nullColumnHack, values);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());

        } finally {
            db.close();
        }
        return idReturn;
    }

    @Override
    public boolean delete(ConversationReceived c) {
        long id = c.getId();
        SQLiteDatabase db = getWritableDatabase();
        int count = 0;
        try{
            if (id != 0){
                String _id = String.valueOf(c.getId());
                whereArgs = new String[]{_id};
                whereClause = ConversationReceived.ID_SQL_LONG +"=?";
                count  = db.delete(ConversationReceived.TABLE, whereClause, whereArgs);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return count != 0;
    }

    @Override
    public List<ConversationReceived> toList(Cursor c) {
        List<ConversationReceived> conversations = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                ConversationReceived cr = new ConversationReceived();
                cr.setId(c.getLong(c.getColumnIndex(ConversationReceived.ID_SQL_LONG)));
                cr.setIdMessage(c.getLong(c.getColumnIndex(ConversationReceived.ID_MESSAGE_LONG)));
                cr.setOrder(c.getInt(c.getColumnIndex(ConversationReceived.ORDER_INT)));
                cr.setText(c.getString(c.getColumnIndex(ConversationReceived.TEXT_TEXT)));
                cr.setType(ConversationReceived.Type.valueOf(c.getString(c.getColumnIndex(ConversationReceived.TYPE_TEXT))));
                cr.setKey(c.getString(c.getColumnIndex(ConversationReceived.KEY_TEXT)));
                cr.setFile(c.getString(c.getColumnIndex(ConversationReceived.FILE_TEXT)));
                conversations.add(cr);
            } while (c.moveToNext());
        }
        return conversations;
    }

    @Override
    public List<ConversationReceived> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            Cursor c = db.query(distintc, ConversationReceived.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            return toList(c);
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
            return new ArrayList<ConversationReceived>();
        } finally {
            db.close();
        }
    }

    @Override
    public ConversationReceived findById(long id) {
        SQLiteDatabase db = getWritableDatabase();
        ConversationReceived cr = null;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = ConversationReceived.ID_SQL_LONG +"=?";
            selectionArgs = new String[]{String.valueOf(id)};
            Cursor c = db.query(distintc, ConversationReceived.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                cr = new ConversationReceived();
                cr.setId(c.getLong(c.getColumnIndex(ConversationReceived.ID_SQL_LONG)));
                cr.setIdMessage(c.getLong(c.getColumnIndex(ConversationReceived.ID_MESSAGE_LONG)));
                cr.setOrder(c.getInt(c.getColumnIndex(ConversationReceived.ORDER_INT)));
                cr.setText(c.getString(c.getColumnIndex(ConversationReceived.TEXT_TEXT)));
                cr.setType(ConversationReceived.Type.valueOf(c.getString(c.getColumnIndex(ConversationReceived.TYPE_TEXT))));
                cr.setKey(c.getString(c.getColumnIndex(ConversationReceived.KEY_TEXT)));
                cr.setFile(c.getString(c.getColumnIndex(ConversationReceived.FILE_TEXT)));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return cr;
    }

    public List<ConversationReceived> findByIdMessage(Long idMessage) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = ConversationReceived.ID_MESSAGE_LONG +"=?";
            selectionArgs = new String[]{String.valueOf(idMessage)};
            orderBy = ConversationReceived.ORDER_INT;
            Cursor c = db.query(distintc, ConversationReceived.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            return toList(c);
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
            return new ArrayList<ConversationReceived>();
        } finally {
            db.close();
        }
    }

    public int findMaxOrderByIdMessage(Long idMessage) {
        SQLiteDatabase db = getWritableDatabase();
        int maxOrder = 0;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = ConversationReceived.ID_MESSAGE_LONG +"=?";
            selectionArgs = new String[]{String.valueOf(idMessage)};
            orderBy = ConversationReceived.ORDER_INT + " DESC";
            Cursor c = db.query(distintc, ConversationReceived.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                maxOrder = c.getInt(c.getColumnIndex(ConversationReceived.ORDER_INT));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return maxOrder;
    }

    public Set<String> getSetByIdMessage(Long idMessage) {
        SQLiteDatabase db = getWritableDatabase();
        HashSet<String> set = new HashSet<>();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = ConversationReceived.ID_MESSAGE_LONG +"=?";
            selectionArgs = new String[]{String.valueOf(idMessage)};
            orderBy = ConversationReceived.ORDER_INT;
            Cursor c = db.query(distintc, ConversationReceived.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            List<ConversationReceived> list = toList(c);

            for (ConversationReceived conversationReceived: list){
                set.add(conversationReceived.getText());
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return set;
    }

    public ConversationReceived findByKey(String key) {
        SQLiteDatabase db = getWritableDatabase();
        ConversationReceived cr = null;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = ConversationReceived.KEY_TEXT +"=?";
            selectionArgs = new String[]{key};
            Cursor c = db.query(distintc, ConversationReceived.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                cr = new ConversationReceived();
                cr.setId(c.getLong(c.getColumnIndex(ConversationReceived.ID_SQL_LONG)));
                cr.setIdMessage(c.getLong(c.getColumnIndex(ConversationReceived.ID_MESSAGE_LONG)));
                cr.setOrder(c.getInt(c.getColumnIndex(ConversationReceived.ORDER_INT)));
                cr.setText(c.getString(c.getColumnIndex(ConversationReceived.TEXT_TEXT)));
                cr.setType(ConversationReceived.Type.valueOf(c.getString(c.getColumnIndex(ConversationReceived.TYPE_TEXT))));
                cr.setKey(c.getString(c.getColumnIndex(ConversationReceived.KEY_TEXT)));
                cr.setFile(c.getString(c.getColumnIndex(ConversationReceived.FILE_TEXT)));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return cr;
    }
}
