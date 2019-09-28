package br.com.infobella.whatomail.whatomail.modelo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infobella.whatomail.whatomail.modelo.MessageReply;
import br.com.infobella.whatomail.whatomail.modelo.WhatomailApplication;
import utils.LogUtils;

/**
 * Created by HENRI on 28/05/2017.
 */

public class MessageReplyDB extends DataBaseDB<MessageReply> {

    public MessageReplyDB(Context context) {
        super(context);
    }

    @Override
    public long save(MessageReply m) {
        long id = 0L;
        if (m.getId() != null){
            id = m.getId();
        }
        SQLiteDatabase db = getWritableDatabase();
        long idReturn = 0L;
        try{
            ContentValues values = new ContentValues();
            values.put(MessageReply.TICKET_TEXT, m.getTicket());
            values.put(MessageReply.TEXT_TEXT, m.getText());
            values.put(MessageReply.STATUS_TEXT, m.getStatus().name());
            values.put(MessageReply.DATE_TIME_RECEIVED_DATE, m.getDataTimeReceived().getTime());
            if (m.getDataTimeSent() != null) {
                values.put(MessageReply.DATE_TIME_SENT_DATE, m.getDataTimeSent().getTime());
            } else {
                values.put(MessageReply.DATE_TIME_SENT_DATE, 0);
            }
            values.put(MessageReply.CONTACT_JID_TEXT, m.getContactJid());
            values.put(MessageReply.CONTACT_NAME_TEXT, m.getContactName());
            if (id != 0){
                String _id = String.valueOf(m.getId());
                whereArgs = new String[]{_id};
                whereClause = MessageReply.ID_SQL_LONG +"=?";
                idReturn  = db.update(MessageReply.TABLE, values, whereClause, whereArgs);
            } else {
                idReturn = db.insert(MessageReply.TABLE, nullColumnHack, values);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());

        } finally {
            db.close();
        }
        return idReturn;
    }

    @Override
    public boolean delete(MessageReply m) {
        long id = m.getId();
        SQLiteDatabase db = getWritableDatabase();
        int count = 0;
        try{
            if (id != 0){
                String _id = String.valueOf(m.getId());
                whereArgs = new String[]{_id};
                whereClause = MessageReply.ID_SQL_LONG +"=?";
                count  = db.delete(MessageReply.TABLE, whereClause, whereArgs);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return count != 0;
    }

    @Override
    public List<MessageReply> toList(Cursor c) {
        List<MessageReply> messages = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                MessageReply m = new MessageReply();
                m.setId(c.getLong(c.getColumnIndex(MessageReply.ID_SQL_LONG)));
                m.setTicket(c.getString(c.getColumnIndex(MessageReply.TICKET_TEXT)));
                m.setText(c.getString(c.getColumnIndex(MessageReply.TEXT_TEXT)));
                m.setStatus(MessageReply.Status.valueOf(c.getString(c.getColumnIndex(MessageReply.STATUS_TEXT))));
                m.setDataTimeReceived(new Date(c.getLong(c.getColumnIndex(MessageReply.DATE_TIME_RECEIVED_DATE))));
                long dthrSent = c.getLong(c.getColumnIndex(MessageReply.DATE_TIME_SENT_DATE));
                if (dthrSent == 0) {
                    m.setDataTimeSent(null);
                } else {
                    m.setDataTimeSent(new Date(c.getLong(c.getColumnIndex(MessageReply.DATE_TIME_SENT_DATE))));
                }
                m.setContactName(c.getString(c.getColumnIndex(MessageReply.CONTACT_NAME_TEXT)));
                m.setContactJid(c.getString(c.getColumnIndex(MessageReply.CONTACT_JID_TEXT)));
                messages.add(m);
            } while (c.moveToNext());
        }
        return messages;
    }

    @Override
    public List<MessageReply> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            orderBy = MessageReply.DATE_TIME_RECEIVED_DATE + " DESC";
            Cursor c = db.query(distintc, MessageReply.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            return toList(c);
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
            return new ArrayList<MessageReply>();
        } finally {
            db.close();
        }
    }

    @Override
    public MessageReply findById(long id) {
        SQLiteDatabase db = getWritableDatabase();
        MessageReply m = null;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = MessageReply.ID_SQL_LONG +"=?";
            selectionArgs = new String[]{String.valueOf(id)};
            Cursor c = db.query(distintc, MessageReply.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                m = new MessageReply();
                m.setId(c.getLong(c.getColumnIndex(MessageReply.ID_SQL_LONG)));
                m.setTicket(c.getString(c.getColumnIndex(MessageReply.TICKET_TEXT)));
                m.setText(c.getString(c.getColumnIndex(MessageReply.TEXT_TEXT)));
                m.setStatus(MessageReply.Status.valueOf(c.getString(c.getColumnIndex(MessageReply.STATUS_TEXT))));
                m.setDataTimeReceived(new Date(c.getLong(c.getColumnIndex(MessageReply.DATE_TIME_RECEIVED_DATE))));
                long dthrSent = c.getLong(c.getColumnIndex(MessageReply.DATE_TIME_SENT_DATE));
                if (dthrSent == 0) {
                    m.setDataTimeSent(null);
                } else {
                    m.setDataTimeSent(new Date(c.getLong(c.getColumnIndex(MessageReply.DATE_TIME_SENT_DATE))));
                }
                m.setContactName(c.getString(c.getColumnIndex(MessageReply.CONTACT_NAME_TEXT)));
                m.setContactJid(c.getString(c.getColumnIndex(MessageReply.CONTACT_JID_TEXT)));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return m;
    }

    public List<MessageReply> findByStatus(MessageReply.Status status) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = MessageReply.STATUS_TEXT +"=?";
            selectionArgs = new String[]{status.name()};
            orderBy = MessageReply.DATE_TIME_RECEIVED_DATE + " DESC";
            Cursor c = db.query(distintc, MessageReply.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            return toList(c);
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
            return new ArrayList<MessageReply>();
        } finally {
            db.close();
        }
    }
}
