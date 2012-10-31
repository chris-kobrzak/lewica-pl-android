package pl.lewica.api.url.test;

import java.util.Calendar;

import junit.framework.TestCase;

import org.junit.Test;

import pl.lewica.api.url.HistoryURL;


public class HistoryURLTest extends TestCase {

	@Test
	public void testClassFields() {
		assertEquals("http://lewica.pl/api/kalendarium.php",	HistoryURL.WEB_SERVICE);
		assertEquals(100,		HistoryURL.LIMIT);
		assertEquals("dzien",	HistoryURL.PARAM_DAY);
		assertEquals("miesiac",	HistoryURL.PARAM_MONTH);
		assertEquals("limit",	HistoryURL.PARAM_LIMIT);
	}


	@Test
	public void testBuildURL() {
		Calendar cal			= Calendar.getInstance();
		String baseUrl			= HistoryURL.WEB_SERVICE;
		String resultUrl;
		String expected;
		int currentDayOfMonth	= cal.get(Calendar.DAY_OF_MONTH);
		int currentMonth		= cal.get(Calendar.MONTH) + 1;
		HistoryURL historyURL	= new HistoryURL();

		resultUrl	= historyURL.buildURL();

		expected	= baseUrl + "?dzien=" + currentDayOfMonth + "&miesiac=" + currentMonth;
		assertEquals(expected, resultUrl);

		historyURL.setDay(21);
		historyURL.setMonth(12);
		resultUrl	= historyURL.buildURL();

		expected	= baseUrl + "?dzien=21&miesiac=12";
		assertEquals(expected, resultUrl);

		// Limit exceeding the allowed value
		historyURL.setLimit(HistoryURL.LIMIT + 5);
		assertEquals(expected, resultUrl);

		historyURL.setLimit(-3);
		assertEquals(expected, resultUrl);
	}


	@Test
	public void testBuildURL_withLimit() {
		Calendar cal			= Calendar.getInstance();
		String baseUrl			= HistoryURL.WEB_SERVICE;
		String resultUrl;
		String expected;
		int currentDayOfMonth	= cal.get(Calendar.DAY_OF_MONTH);
		int currentMonth		= cal.get(Calendar.MONTH) + 1;
		HistoryURL historyURL	= new HistoryURL();

		// Passing a sensible value
		historyURL.setLimit(10);
		resultUrl	= historyURL.buildURL();

		expected	= baseUrl + "?limit=10&dzien=" + currentDayOfMonth + "&miesiac=" + currentMonth;
		assertEquals(expected, resultUrl);

		historyURL.setDay(25);
		historyURL.setMonth(4);
		resultUrl	= historyURL.buildURL();

		expected	= baseUrl + "?limit=10&dzien=25&miesiac=4";
		assertEquals(expected, resultUrl);

		// Limit exceeding the allowed value
		historyURL.setLimit(HistoryURL.LIMIT + 5);
		assertEquals(expected, resultUrl);

		historyURL.setLimit(-3);
		assertEquals(expected, resultUrl);
	}

}
