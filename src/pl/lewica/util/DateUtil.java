/*
 Copyright 2011 lewica.pl

 Licensed under the Apache Licence, Version 2.0 (the "Licence");
 you may not use this file except in compliance with the Licence.
 You may obtain a copy of the Licence at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the Licence is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the Licence for the specific language governing permissions and
 limitations under the Licence. 
*/
package pl.lewica.util;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Collection of date-related methods and constants
 * @author Krzysztof Kobrzak
 */
public class DateUtil {
	public static final String DATE_MASK_SQL = "yyyy-MM-dd HH:mm:ss";
    public static final Date FALL_OVER_DATE = new Date(2000, 1, 1);


	public static int getCurrentUnixTime() {
		return (int) (System.currentTimeMillis() / 1000L);
	}


	/**
	 * Be cautious when using this method inside a loop as it creates SimpleDateFormat objects every time it's called.
	 * @param date
	 * @param mask E.g.: "dd/MM/yyyy HH:mm"
	 * @return
	 */
	public static String convertDateToString(Date date, String mask) {
		SimpleDateFormat sdf	= new SimpleDateFormat(mask);

		return sdf.format(date);
	}

	public static Date parseDateString(String dateString) {
		return parseDateString(dateString, DATE_MASK_SQL, FALL_OVER_DATE);
	}

	/**
	 * @param dateFormat TODO
	 * @return
	 */
	public static Date parseDateString(String dateString, String dateFormat, Date fallOverDate) {
		Date date;

		try {
			DateFormat df	= new SimpleDateFormat(dateFormat);
			date	= df.parse(dateString);
		} catch (ParseException e) {
			// We don't want to let an invalid date crash the application so let's just use any date
			date	= fallOverDate;
		}
		return date;
	}
}