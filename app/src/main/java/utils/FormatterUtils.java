package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by HENRI on 24/04/2017.
 */

public class FormatterUtils {

    private static final String strDate = "dd/MM/yyyy";
    private static final String strTime = "HH:mm:ss";
    private static final String strTimeIni = "00:00:00";
    private static final String strTimeFim = "23:59:59";

    public static Date stringToDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(strDate, Locale.US);
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

    public static String dateToString(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat(strDate, Locale.US);
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

    public static String timeToString(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat(strTime, Locale.US);
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

    public static String dateToStringFileLog() {
        Date date = new Date();
        try {
            DateFormat dateFormat = new SimpleDateFormat(strDate, Locale.US);
            String d = dateFormat.format(date);
            return d.substring(6) + d.substring(3, 5) + d.substring(0, 2);
        } catch (Exception ex){
            return "Default";
        }
    }
}
