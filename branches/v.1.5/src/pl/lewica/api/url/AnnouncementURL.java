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
 * Provides the lewica.pl announcements Web Service URL. 
 * You may set the parameters and then request the URL string from the buildURL method.
 * If you do not call the setters, buildURL will return an address that can be used 
 * to retrieve a default number of the most recent announcements.
 * 
 * @author Krzysztof Kobrzak
 */
public class AnnouncementURL implements WebServiceURL {

	// The maximum number of entries that can be downloaded is limited to 10 by the server anyway.
	public static final int LIMIT								= 10;

	public static final String PARAM_NEWER_THAN	= "od"; 
	public static final String PARAM_LIMIT				= "limit"; 

	// URL parameters
	private int newerThan	= 0;
	private int limit				= LIMIT;


	public String buildURL() {
		StringBuilder sb	= new StringBuilder(URLDictionary.API.ANNOUNCEMENTS);

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

		// If the query string has length, that means that the first char is "&" and it has to be replaced with "?".
		int qsStart	= URLDictionary.API.ANNOUNCEMENTS.length();
		if (sb.length() > qsStart) {
			sb.replace(qsStart, qsStart + 1, "?");
		}

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


	/**
	 * @param limit Maximum value: LIMIT
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}
}
