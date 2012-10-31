package pl.lewica.api.url.test;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import pl.lewica.api.url.BlogPostURL;

public class BlogPostURLTest extends TestCase {

	private BlogPostURL blogPostURL;


	@Before
	public void setUp() throws Exception {
		blogPostURL	= new BlogPostURL();
	}


	@Test
	public void testClassFields() {
		assertEquals("http://lewica.pl/api/blog-posty.php",	BlogPostURL.WEB_SERVICE);
		assertEquals(15,		BlogPostURL.LIMIT);
		assertEquals("od",		BlogPostURL.PARAM_NEWER_THAN);
		assertEquals("limit",	BlogPostURL.PARAM_LIMIT);
	}


	@Test
	public void testBuildURL() {
		String baseUrl			= BlogPostURL.WEB_SERVICE;
		String resultUrl;
		String expected;
		BlogPostURL blogPostURL	= new BlogPostURL();

		resultUrl	= blogPostURL.buildURL();

		expected	= baseUrl + "?limit=15";
		assertEquals(expected, resultUrl);

		resultUrl	= blogPostURL.buildURL();

		// Even if you try building a Url with the limit exceeding the allowed value, you should still get the default param in the URL.
		blogPostURL.setLimit(BlogPostURL.LIMIT + 1);

		assertEquals(expected, resultUrl);

		blogPostURL.setNewerThan(0);
		resultUrl	= blogPostURL.buildURL();
		
		expected	= baseUrl;
		assertEquals(expected, resultUrl);
	}


	@Test
	public void testBuildURL_withNewerThan() {
		int newerThan;
		String baseUrl			= BlogPostURL.WEB_SERVICE;
		String resultUrl;
		String expected;

		newerThan	= 1025;
		blogPostURL.setNewerThan(newerThan);
		resultUrl	= blogPostURL.buildURL();

		expected	= baseUrl + "?od=" + newerThan + "&limit=" + BlogPostURL.LIMIT;
		assertEquals(expected, resultUrl);
	}


	@Test
	public void testBuildURL_withLimit() {
		int limit;
		String baseUrl			= BlogPostURL.WEB_SERVICE;
		String resultUrl;
		String expected;

		limit	= 0;
		blogPostURL.setLimit(limit);
		resultUrl	= blogPostURL.buildURL();

		expected	= baseUrl;
		assertEquals(expected, resultUrl);

		limit	= 12;
		blogPostURL.setLimit(limit);
		resultUrl	= blogPostURL.buildURL();

		expected	= baseUrl + "?limit=" + limit;
		assertEquals(expected, resultUrl);

		limit	= BlogPostURL.LIMIT + 3;
		blogPostURL.setLimit(limit);
		resultUrl	= blogPostURL.buildURL();

		expected	= baseUrl;
		assertEquals(expected, resultUrl);
	}
}
