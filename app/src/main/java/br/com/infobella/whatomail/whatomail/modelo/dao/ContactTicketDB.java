package br.com.infobella.whatomail.whatomail.modelo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.infobella.whatomail.whatomail.modelo.ContactTicket;
import br.com.infobella.whatomail.whatomail.modelo.WhatomailApplication;
import utils.LogUtils;

/*
 * Created by HENRI on 23/06/2017.
 */

public class ContactTicketDB extends DataBaseDB<ContactTicket> {

    public ContactTicketDB(Context context) {
        super(context);
    }

    @Override
    public long save(ContactTicket co) {
        long id = 0L;
        if (co.getId() != null){
            id = co.getId();
        }
        SQLiteDatabase db = getWritableDatabase();
        long idReturn = 0L;
        try{
            ContentValues values = new ContentValues();
            values.put(ContactTicket.ID_CONTACT_LONG, co.getIdContact());
            values.put(ContactTicket.TICKET_TEXT, co.getTicket());
            if (id != 0){
                String _id = String.valueOf(co.getId());
                whereArgs = new String[]{_id};
                whereClause = ContactTicket.ID_SQL_LONG +"=?";
                idReturn  = db.update(ContactTicket.TABLE, values, whereClause, whereArgs);
            } else {
                idReturn = db.insert(ContactTicket.TABLE, nullColumnHack, values);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());

        } finally {
            db.close();
        }
        return idReturn;
    }

    @Override
    public boolean delete(ContactTicket co) {
        long id = co.getId();
        SQLiteDatabase db = getWritableDatabase();
        int count = 0;
        try{
            if (id != 0){
                String _id = String.valueOf(co.getId());
                whereArgs = new String[]{_id};
                whereClause = ContactTicket.ID_SQL_LONG +"=?";
                count  = db.delete(ContactTicket.TABLE, whereClause, whereArgs);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return count != 0;
    }

    @Override
    public List<ContactTicket> toList(Cursor c) {
        List<ContactTicket> ContactTickets = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                ContactTicket co = new ContactTicket();
                co.setId(c.getLong(c.getColumnIndex(ContactTicket.ID_SQL_LONG)));
                co.setIdContact(c.getLong(c.getColumnIndex(ContactTicket.ID_CONTACT_LONG)));
                co.setTicket(c.getString(c.getColumnIndex(ContactTicket.TICKET_TEXT)));
                ContactTickets.add(co);
            } while (c.moveToNext());
        }
        return ContactTickets;
    }

    @Override
    public List<ContactTicket> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            Cursor c = db.query(distintc, ContactTicket.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            return toList(c);
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
            return new ArrayList<ContactTicket>();
        } finally {
            db.close();
        }
    }

    @Override
    public ContactTicket findById(long id) {
        SQLiteDatabase db = getWritableDatabase();
        ContactTicket co = null;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = ContactTicket.ID_SQL_LONG +"=?";
            selectionArgs = new String[]{String.valueOf(id)};
            Cursor c = db.query(distintc, ContactTicket.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                co = new ContactTicket();
                co.setId(c.getLong(c.getColumnIndex(ContactTicket.ID_SQL_LONG)));
                co.setIdContact(c.getLong(c.getColumnIndex(ContactTicket.ID_CONTACT_LONG)));
                co.setTicket(c.getString(c.getColumnIndex(ContactTicket.TICKET_TEXT)));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return co;
    }

    public ContactTicket findByTicket(String ticket) {
        SQLiteDatabase db = getWritableDatabase();
        ContactTicket co = null;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = ContactTicket.TICKET_TEXT +"=?";
            selectionArgs = new String[]{ticket};
            Cursor c = db.query(distintc, ContactTicket.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                co = new ContactTicket();
                co.setId(c.getLong(c.getColumnIndex(ContactTicket.ID_SQL_LONG)));
                co.setIdContact(c.getLong(c.getColumnIndex(ContactTicket.ID_CONTACT_LONG)));
                co.setTicket(c.getString(c.getColumnIndex(ContactTicket.TICKET_TEXT)));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return co;
    }
}
