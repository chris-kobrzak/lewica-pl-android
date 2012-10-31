package pl.lewica.util.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import pl.lewica.util.DateUtil;


public class DateUtilTest extends TestCase {

	@Before
	public void setUp() throws Exception {
	}


	@Test
	public void testGetCurrentUnixTime() {
		int currentUnixTime		= (int) (System.currentTimeMillis() / 1000L);
		// The value returned should be the same but there could be a few seconds difference
		int currentUnixTimeMinusAFewMoments	= currentUnixTime - 5;
		int currentUnixTimePlusAFewMoments	= currentUnixTime + 5;

		int unixTimeReturned 	= DateUtil.getCurrentUnixTime();

		assertTrue(unixTimeReturned < currentUnixTimePlusAFewMoments && unixTimeReturned > currentUnixTimeMinusAFewMoments);
	}


	@Test
	public void testConvertDateToString() {
		StringBuilder sb;
		String expected;
		Calendar cal			= Calendar.getInstance();
		Date currentDate		= cal.getTime();

		int year	= cal.get(Calendar.YEAR);
		int month	= cal.get(Calendar.MONTH) + 1;
		int day		= cal.get(Calendar.DAY_OF_MONTH);
		int hour	= cal.get(Calendar.HOUR_OF_DAY);
		int minute	= cal.get(Calendar.MINUTE);
		int second	= cal.get(Calendar.SECOND);

		String defaultDateMask	= "yyyy-MM-dd HH:mm:ss";
		sb		= new StringBuilder();
		sb.append(year);
		sb.append("-");
		if (month < 10) {
			sb.append(0);
		}
		sb.append(month);
		sb.append("-");
		if (day < 10) {
			sb.append(0);
		}
		sb.append(day);
		sb.append(" ");
		if (hour < 10) {
			sb.append(0);
		}
		sb.append(hour);
		sb.append(":");
		if (minute < 10) {
			sb.append(0);
		}
		sb.append(minute);
		sb.append(":");
		if (second < 10) {
			sb.append(0);
		}
		sb.append(second);
		
		expected			= sb.toString();
		assertEquals(expected, DateUtil.convertDateToString(currentDate, defaultDateMask) );

		String customDateMask	= "MM/dd/yyyy HH:mm:ss";
		sb		= new StringBuilder();
		if (month < 10) {
			sb.append(0);
		}
		sb.append(month);
		sb.append("/");
		if (day < 10) {
			sb.append(0);
		}
		sb.append(day);
		sb.append("/");
		sb.append(year);
		sb.append(" ");
		if (hour < 10) {
			sb.append(0);
		}
		sb.append(hour);
		sb.append(":");
		if (minute < 10) {
			sb.append(0);
		}
		sb.append(minute);
		sb.append(":");
		if (second < 10) {
			sb.append(0);
		}
		sb.append(second);

		expected			= sb.toString();
		assertEquals(expected, DateUtil.convertDateToString(currentDate, customDateMask) );
	}

}
