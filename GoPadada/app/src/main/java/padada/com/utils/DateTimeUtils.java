package padada.com.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Vlad on 04.12.2016.
 */

public class DateTimeUtils {
    public static final String TEMPLATE_DAY_OF_WEEK = "EEEE dd, MM-yyyy";

    public static String getDayOfWeek(long date) throws ParseException {
        Date d = new Date();
        d.setTime(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat(TEMPLATE_DAY_OF_WEEK, Locale.getDefault()).format(cal.getTime());

        return monthName;
    }
}
