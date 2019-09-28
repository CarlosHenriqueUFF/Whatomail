package utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by HENRI on 26/03/2017.
 */

public class ContatoUtils {

    public static String getFoneWhatsapp(Context context, String id){
        String fone = null;
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                null, null);

        try {
            while (cursor.moveToNext()){

                /*for (String name: cursor.getColumnNames()){
                    LogUtils.writeLog(context, LogUtils.TAG_CONTACT, name+": "+ cursor.getString(cursor.getColumnIndex(name)));
                }*/
                int colFone = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA4);
                int colType = cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE);
                fone = cursor.getString(colFone);
                String type = cursor.getString(colType);
                if (type != null && type.equals("com.whatsapp")){
                    if (fone != null) {
                        fone = fone.replace("+", "");
                        return fone;
                    }
                }
            }
        } finally {
            cursor.close();
        }
        return fone;
    }

    public static String getNameByFone(Context context, String foneContact){
        String name = null;
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null,
                null, null);

        try {
            while (cursor.moveToNext()){
                int colFone = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA4);
                String fone = cursor.getString(colFone);
                if (fone != null && fone.contains(foneContact)){
                    int colName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    name = cursor.getString(colName);
                    return name;
                }
            }
        } finally {
            cursor.close();
        }
        return name;
    }
}
