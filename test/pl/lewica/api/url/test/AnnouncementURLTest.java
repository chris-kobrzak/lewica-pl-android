package pl.lewica.api.url.test;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import pl.lewica.api.url.AnnouncementURL;


public class AnnouncementURLTest extends TestCase {

	private AnnouncementURL announcementURL;


	@Before
	public void setUp() throws Exception {
		announcementURL	= new AnnouncementURL();
	}


	@Test
	public void testClassFields() {
		assertEquals(10,		AnnouncementURL.LIMIT);
		assertEquals("limit",	AnnouncementURL.PARAM_LIMIT);
		assertEquals("od",		AnnouncementURL.PARAM_NEWER_THAN);
	}


	@Test
	public void testBuildURL() {
		String baseUrl			= AnnouncementURL.WEB_SERVICE;
		String resultUrl;
		String expected;

		resultUrl	= this.announcementURL.buildURL();

		expected	= baseUrl + "?limit=10";
		assertEquals(expected, resultUrl);

		resultUrl	= announcementURL.buildURL();

		// Even if you try building a Url with the limit exceeding the allowed value, you should still get the default param in the URL.
		announcementURL.setLimit(AnnouncementURL.LIMIT + 1);

		assertEquals(expected, resultUrl);

		announcementURL.setNewerThan(0);
		resultUrl	= announcementURL.buildURL();
		
		expected	= baseUrl;
		assertEquals(expected, resultUrl);
	}


	@Test
	public void testBuildURL_withNewerThan() {
		int newerThan;
		String baseUrl			= AnnouncementURL.WEB_SERVICE;
		String resultUrl;
		String expected;

		newerThan	= 1025;
		announcementURL.setNewerThan(newerThan);
		resultUrl	= announcementURL.buildURL();

		expected	= baseUrl + "?od=" + newerThan + "&limit=" + AnnouncementURL.LIMIT;
		assertEquals(expected, resultUrl);

		newerThan	= 0;
		announcementURL.setNewerThan(newerThan);
		resultUrl	= announcementURL.buildURL();

		expected	= baseUrl  + "?limit=" + AnnouncementURL.LIMIT;
		assertEquals(expected, resultUrl);
	}


	@Test
	public void testBuildURL_withLimit() {
		int limit;
		String baseUrl			= AnnouncementURL.WEB_SERVICE;
		String resultUrl;
		String expected;

		limit	= 0;
		announcementURL.setLimit(limit);
		resultUrl	= announcementURL.buildURL();

		expected	= baseUrl;
		assertEquals(expected, resultUrl);

		limit	= 7;
		announcementURL.setLimit(limit);
		resultUrl	= announcementURL.buildURL();

		expected	= baseUrl + "?limit=" + limit;
		assertEquals(expected, resultUrl);

		limit	= AnnouncementURL.LIMIT + 1;
		announcementURL.setLimit(limit);
		resultUrl	= announcementURL.buildURL();

		expected	= baseUrl;
		assertEquals(expected, resultUrl);
	}


	@Test
	public void testSetNewerThan() {
		int newerThan	= 1234;

		announcementURL.setNewerThan(newerThan);

		assertEquals(newerThan, announcementURL.getNewerThan() );
	}

}
