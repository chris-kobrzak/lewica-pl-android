package pl.lewica.util;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;


/**
 * NOT IN USE
 * @author Krzysztof Kobrzak
 *
 */
public class DateUtil {
	public static final String DATE_FORMAT_SQL = "yyyy-MM-dd HH:mm:ss";


	public static String now() {
		Calendar cal			= Calendar.getInstance();
		SimpleDateFormat sdf	= new SimpleDateFormat(DATE_FORMAT_SQL);

		return sdf.format(cal.getTime());
	}


	/**
	 * Be cautious when using this method inside a loop as it creates a SimpleDateFormat object every time it's called.
	 * @param date
	 * @param dateFormat E.g.: "dd/MM/yyyy HH:mm"
	 * @return
	 */
	public static String convertDateToString(Date date, String dateFormat) {
		SimpleDateFormat sdf	= new SimpleDateFormat(dateFormat);

		return sdf.format(date);
	}

}