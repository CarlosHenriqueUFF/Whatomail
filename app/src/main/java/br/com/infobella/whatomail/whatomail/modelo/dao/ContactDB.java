package br.com.infobella.whatomail.whatomail.modelo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.infobella.whatomail.whatomail.modelo.Contact;
import br.com.infobella.whatomail.whatomail.modelo.WhatomailApplication;
import utils.LogUtils;

/**
 * Created by HENRI on 27/03/2017.
 */

public class ContactDB extends DataBaseDB<Contact> {

    public ContactDB(Context context) {
        super(context);
    }

    @Override
    public long save(Contact co) {
        long id = 0L;
        if (co.getId() != null){
            id = co.getId();
        }
        SQLiteDatabase db = getWritableDatabase();
        long idReturn = 0L;
        try{
            ContentValues values = new ContentValues();
            values.put(Contact.CONTACT_NAME_TEXT, co.getContactName());
            values.put(Contact.JID_WHATSAPP_TEXT, co.getJidWhatsApp());
            values.put(Contact.PHONE_TEXT, co.getPhone());
            if (co.getTicket() != null) {
                values.put(Contact.TICKET_TEXT, co.getTicket());
            }
            if (id != 0){
                String _id = String.valueOf(co.getId());
                whereArgs = new String[]{_id};
                whereClause = Contact.ID_SQL_LONG +"=?";
                idReturn  = db.update(Contact.TABLE, values, whereClause, whereArgs);
            } else {
                idReturn = db.insert(Contact.TABLE, nullColumnHack, values);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());

        } finally {
            db.close();
        }
        return idReturn;
    }

    @Override
    public boolean delete(Contact co) {
        long id = co.getId();
        SQLiteDatabase db = getWritableDatabase();
        int count = 0;
        try{
            if (id != 0){
                String _id = String.valueOf(co.getId());
                whereArgs = new String[]{_id};
                whereClause = Contact.ID_SQL_LONG +"=?";
                count  = db.delete(Contact.TABLE, whereClause, whereArgs);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return count != 0;
    }

    @Override
    public List<Contact> toList(Cursor c) {
        List<Contact> contacts = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                Contact co = new Contact();
                co.setId(c.getLong(c.getColumnIndex(Contact.ID_SQL_LONG)));
                co.setJidWhatsApp(c.getString(c.getColumnIndex(Contact.JID_WHATSAPP_TEXT)));
                co.setContactName(c.getString(c.getColumnIndex(Contact.CONTACT_NAME_TEXT)));
                co.setPhone(c.getString(c.getColumnIndex(Contact.PHONE_TEXT)));
                co.setTicket(c.getString(c.getColumnIndex(Contact.TICKET_TEXT)));
                contacts.add(co);
            } while (c.moveToNext());
        }
        return contacts;
    }

    @Override
    public List<Contact> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            Cursor c = db.query(distintc, Contact.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            return toList(c);
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
            return new ArrayList<Contact>();
        } finally {
            db.close();
        }
    }

    @Override
    public Contact findById(long id) {
        SQLiteDatabase db = getWritableDatabase();
        Contact co = null;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = Contact.ID_SQL_LONG +"=?";
            selectionArgs = new String[]{String.valueOf(id)};
            Cursor c = db.query(distintc, Contact.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                co = new Contact();
                co.setId(c.getLong(c.getColumnIndex(Contact.ID_SQL_LONG)));
                co.setJidWhatsApp(c.getString(c.getColumnIndex(Contact.JID_WHATSAPP_TEXT)));
                co.setContactName(c.getString(c.getColumnIndex(Contact.CONTACT_NAME_TEXT)));
                co.setPhone(c.getString(c.getColumnIndex(Contact.PHONE_TEXT)));
                co.setTicket(c.getString(c.getColumnIndex(Contact.TICKET_TEXT)));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return co;
    }

    public Contact findByIdWhatsApp(String jidWhatsApp) {
        SQLiteDatabase db = getWritableDatabase();
        Contact co = null;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = Contact.JID_WHATSAPP_TEXT +"=?";
            selectionArgs = new String[]{jidWhatsApp};
            Cursor c = db.query(distintc, Contact.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                co = new Contact();
                co.setId(c.getLong(c.getColumnIndex(Contact.ID_SQL_LONG)));
                co.setJidWhatsApp(c.getString(c.getColumnIndex(Contact.JID_WHATSAPP_TEXT)));
                co.setContactName(c.getString(c.getColumnIndex(Contact.CONTACT_NAME_TEXT)));
                co.setPhone(c.getString(c.getColumnIndex(Contact.PHONE_TEXT)));
                co.setTicket(c.getString(c.getColumnIndex(Contact.TICKET_TEXT)));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return co;
    }

    public Contact findByTicket(String ticket) {
        SQLiteDatabase db = getWritableDatabase();
        Contact co = null;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = Contact.TICKET_TEXT +"=?";
            selectionArgs = new String[]{ticket};
            Cursor c = db.query(distintc, Contact.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                co = new Contact();
                co.setId(c.getLong(c.getColumnIndex(Contact.ID_SQL_LONG)));
                co.setJidWhatsApp(c.getString(c.getColumnIndex(Contact.JID_WHATSAPP_TEXT)));
                co.setContactName(c.getString(c.getColumnIndex(Contact.CONTACT_NAME_TEXT)));
                co.setPhone(c.getString(c.getColumnIndex(Contact.PHONE_TEXT)));
                co.setTicket(c.getString(c.getColumnIndex(Contact.TICKET_TEXT)));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return co;
    }
}
