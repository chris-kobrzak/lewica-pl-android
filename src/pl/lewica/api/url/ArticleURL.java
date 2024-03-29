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
package pl.lewica.api.url;

import pl.lewica.URLDictionary;

/**
 * Provides the lewica.pl articles Web Service URL. 
 * You may set the parameters and then request the URL string from the buildURL method.
 * If you do not call the setters, buildURL will return an address that can be used 
 * to retrieve the default number of the most recent articles.
 * @author Krzysztof Kobrzak
 */
public class ArticleURL implements WebServiceURL {

	public static final String PREFIX_THUMBNAIL		= "th_";
	public static final int LIMIT								= 5;

	public static final String PARAM_NEWER_THAN	= "od";
	public static final String PARAM_LIMIT				= "limit";
	public static final String PARAM_SECTIONS			= "dzialy";
	public static final String PARAM_FORMAT			= "format";

	// Web Service URL parameters
	private int newerThan		= 0;
	private int limit					= LIMIT;	// Maximum number of entries per section.
	private String sectionList	= "";	// Comma-separated list of sections, as per the Article model constants.
	private String format			= "xml";


	public String buildURL() {
		StringBuilder sb	= new StringBuilder(URLDictionary.API.PUBLICATIONS);

		if (newerThan > 0) {
			sb.append("&");
			sb.append(PARAM_NEWER_THAN);
			sb.append("=");
			sb.append(newerThan);
		}

		if (limit > 0 && limit <= LIMIT) {
			sb.append("&");
			sb.append(PARAM_LIMIT);
			sb.append("=");
			sb.append(limit);
		}

		if (sectionList.length() > 0) {
			sb.append("&");
			sb.append(PARAM_SECTIONS);
			sb.append("=");
			sb.append(sectionList);
		}

		if (format.length() > 0) {
			sb.append("&");
			sb.append(PARAM_FORMAT);
			sb.append("=");
			sb.append(format);
		}

		// If the query string has length, that means that the first char is "&" and it has to be replaced with "?".
		int qsStart	= URLDictionary.API.PUBLICATIONS.length();
		sb.replace(qsStart, qsStart + 1, "?");

		return sb.toString();
	}


	/**
	 * Returns a URL for a given image, e.g. http://lewica.pl/im/25365.jpg
	 * @param id
	 * @param extension
	 * @return
	 */
	public static String buildURLImage(int id, String extension) {
		StringBuilder sb	= new StringBuilder(URLDictionary.IMAGE);

		sb.append(id);
		sb.append(".");
		sb.append(extension);

		return sb.toString();
	}


	public static String buildNameThumbnail(int id, String extension) {
		// Thumbnails use the th_ prefix
		StringBuilder sb	= new StringBuilder(PREFIX_THUMBNAIL);

		sb.append(id);
		sb.append(".");
		sb.append(extension);

		return sb.toString();
	}


	public int getNewerThan() {
		return newerThan;
	}


	public void setNewerThan(int newerThan) {
		this.newerThan = newerThan;
	}


	public int getLimit() {
		return limit;
	}


	public void setLimit(int limit) {
		this.limit = limit;
	}


	public String getSectionList() {
		return sectionList;
	}


	public void setSectionList(String sectionList) {
		this.sectionList = sectionList;
	}


	public String getFormat() {
		return format;
	}


	public void setFormat(String format) {
		this.format = format;
	}
}
