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

import br.com.infobella.whatomail.whatomail.modelo.Veterinary;
import br.com.infobella.whatomail.whatomail.modelo.WhatomailApplication;
import utils.LogUtils;

/**
 * Created by HENRI on 20/05/2017.
 */

public class VeterinaryDB extends DataBaseDB<Veterinary> {

    public VeterinaryDB(Context context) {
        super(context);
    }

    @Override
    public long save(Veterinary ve) {
        long id = 0L;
        if (ve.getId() != null){
            id = ve.getId();
        }
        SQLiteDatabase db = getWritableDatabase();
        long idReturn = 0L;
        try{
            ContentValues values = new ContentValues();
            values.put(Veterinary.NAME_TEXT, ve.getName());
            values.put(Veterinary.PHONE_TEXT, ve.getPhone());
            if (id != 0){
                String _id = String.valueOf(ve.getId());
                whereArgs = new String[]{_id};
                whereClause = Veterinary.ID_SQL_LONG +"=?";
                idReturn  = db.update(Veterinary.TABLE, values, whereClause, whereArgs);
            } else {
                idReturn = db.insert(Veterinary.TABLE, nullColumnHack, values);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());

        } finally {
            db.close();
        }
        return idReturn;
    }

    @Override
    public boolean delete(Veterinary ve) {
        long id = ve.getId();
        SQLiteDatabase db = getWritableDatabase();
        int count = 0;
        try{
            if (id != 0){
                String _id = String.valueOf(ve.getId());
                whereArgs = new String[]{_id};
                whereClause = Veterinary.ID_SQL_LONG +"=?";
                count  = db.delete(Veterinary.TABLE, whereClause, whereArgs);
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return count != 0;
    }

    @Override
    public List<Veterinary> toList(Cursor c) {
        List<Veterinary> Veterinarys = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                Veterinary ve = new Veterinary();
                ve.setId(c.getLong(c.getColumnIndex(Veterinary.ID_SQL_LONG)));
                ve.setName(c.getString(c.getColumnIndex(Veterinary.NAME_TEXT)));
                ve.setPhone(c.getString(c.getColumnIndex(Veterinary.PHONE_TEXT)));
                Veterinarys.add(ve);
            } while (c.moveToNext());
        }
        return Veterinarys;
    }

    @Override
    public List<Veterinary> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            Cursor c = db.query(distintc, Veterinary.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            return toList(c);
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
            return new ArrayList<Veterinary>();
        } finally {
            db.close();
        }
    }

    @Override
    public Veterinary findById(long id) {
        SQLiteDatabase db = getWritableDatabase();
        Veterinary ve = null;
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            selection = Veterinary.ID_SQL_LONG +"=?";
            selectionArgs = new String[]{String.valueOf(id)};
            Cursor c = db.query(distintc, Veterinary.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (c.moveToFirst()){
                ve = new Veterinary();
                ve.setId(c.getLong(c.getColumnIndex(Veterinary.ID_SQL_LONG)));
                ve.setName(c.getString(c.getColumnIndex(Veterinary.NAME_TEXT)));
                ve.setPhone(c.getString(c.getColumnIndex(Veterinary.PHONE_TEXT)));
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return ve;
    }

    public Set<String> getSetJid() {
        SQLiteDatabase db = getWritableDatabase();
        Set<String> set = new HashSet<>();
        try {
            //distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
            Cursor c = db.query(distintc, Veterinary.TABLE, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            List<Veterinary> veterinaryList = toList(c);
            for (Veterinary veterinary: veterinaryList){
                set.add(veterinary.getJidWhatsApp());
            }
        } catch (SQLException ex){
            LogUtils.writeLog(WhatomailApplication.getContext(), LogUtils.TAG_ERROR, ex.toString());
        } finally {
            db.close();
        }
        return set;
    }

}
