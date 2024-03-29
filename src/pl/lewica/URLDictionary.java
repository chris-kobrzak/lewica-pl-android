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
/**
 * 
 */
package pl.lewica;

/**
 * @author Krzysztof Kobrzak
 */
public class URLDictionary {

	public static final String HOMEPAGE	= "http://lewica.pl/";
	public static final String FACEBOOK	= "http://www.facebook.com/pages/Lewicapl/245985269840";
	public static final String BLOGS			= "http://lewica.pl/blog/";
	public static final String LINKS			= "http://lewica.pl/index.php?s=katalog";
	public static final String SEARCH		= "http://lewica.pl/index.php?s=szukaj";
	public static final String TEAM			= "http://lewica.pl/index.php?s=redakcja";

	public static final String IMAGE			= "http://lewica.pl/im/";
	public static final String THUMBNAIL	= "http://lewica.pl/im/thumbs/";

	public static class API {
		public static final String PUBLICATIONS		= "http://lewica.pl/api/publikacje.php";
		public static final String ANNOUNCEMENTS	= "http://lewica.pl/api/ogloszenia.php";
		public static final String BLOG_POSTS		= "http://lewica.pl/api/blog-posty.php";
		public static final String HISTORY			= "http://lewica.pl/api/kalendarium.php";
	}


	public static String buildURL_BlogPost(Integer blogId, Integer Id) {
		StringBuilder sb	= new StringBuilder(BLOGS);
		sb.append("?blog=");
		sb.append(Integer.toString(blogId) );
		sb.append("&id=");
		sb.append(Integer.toString(Id) );

		return sb.toString();
	}


	public static String buildURL_ReadersComments(Integer id, int textSize, int theme) {
		StringBuilder sb	= new StringBuilder("http://lewica.pl/forum/index.php?fuse=messages.");
		sb.append(Integer.toString(id) );
		sb.append("&format=minimal");
		sb.append("&textSize=");
		sb.append(Integer.toString(textSize) );
		sb.append("&theme=");
		sb.append(Integer.toString(theme) );

		return sb.toString();
	}
}
