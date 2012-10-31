package pl.lewica.api.url.test;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import pl.lewica.api.url.ArticleURL;

public class ArticleURLTest extends TestCase {

	private ArticleURL articleURL;


	@Before
	public void setUp() throws Exception {
		articleURL	= new ArticleURL();
	}


	@Test
	public void testClassFields() {
		assertEquals("http://lewica.pl/api/publikacje.php",	ArticleURL.WEB_SERVICE);
		assertEquals("http://lewica.pl/im/",				ArticleURL.PATH_IMAGE);
		assertEquals("http://lewica.pl/im/thumbs/",			ArticleURL.PATH_THUMBNAIL);
		assertEquals("th_",		ArticleURL.PREFIX_THUMBNAIL);
		assertEquals(5,			ArticleURL.LIMIT);
		assertEquals("od",		ArticleURL.PARAM_NEWER_THAN);
		assertEquals("limit",	ArticleURL.PARAM_LIMIT);
		assertEquals("dzialy",	ArticleURL.PARAM_SECTIONS);
		assertEquals("format",	ArticleURL.PARAM_FORMAT);
	}


	@Test
	public void testBuildURL() {
		String baseUrl			= ArticleURL.WEB_SERVICE;
		String resultUrl;
		String expected;

		resultUrl	= this.articleURL.buildURL();

		expected	= baseUrl + "?limit=5&format=xml";
		assertEquals(expected, resultUrl);

		resultUrl	= articleURL.buildURL();

		// Even if you try building a Url with the limit exceeding the allowed value, you should still get the default param in the URL.
		articleURL.setLimit(ArticleURL.LIMIT + 1);

		assertEquals(expected, resultUrl);

		articleURL.setNewerThan(0);
		resultUrl	= articleURL.buildURL();
		
		expected	= baseUrl + "?format=xml";
		assertEquals(expected, resultUrl);
	}


	@Test
	public void testBuildURLImage() {
		long id	= 12345;
		String ext	= "png";

		String expected		= "http://lewica.pl/im/" + 12345 + "." + ext;
		String resultUrl	= ArticleURL.buildURLImage(id, ext);

		assertEquals(expected, resultUrl);
	}


	@Test
	public void testBuildURLThumbnail() {
		long id	= 12345;
		String ext	= "png";

		String expected		= "http://lewica.pl/im/thumbs/th_" + 12345 + "." + ext;
		String resultUrl	= ArticleURL.buildURLThumbnail(id, ext);

		assertEquals(expected, resultUrl);
	}


	@Test
	public void testBuildNameThumbnail() {
		long id	= 12345;
		String ext	= "png";

		String expected		= "th_" + 12345 + "." + ext;
		String resultUrl	= ArticleURL.buildNameThumbnail(id, ext);

		assertEquals(expected, resultUrl);
	}

}
