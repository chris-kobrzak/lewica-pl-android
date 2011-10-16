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

/**
 * Provides lewica.pl API URL based on the parameters set.
 * @author Krzysztof Kobrzak
 */
public class CalendarEntryURL implements IWebServiceURL {

	public static final String WEB_SERVICE		= "http://lewica.pl/api/kalendarium.php";

	public static final String PARAM_DAY			= "dzien"; 
	public static final String PARAM_MONTH	= "miesiac"; 
	public static final String PARAM_LIMIT		= "limit"; 

	// URL parameters
	private int day		= 0;
	private int month	= 0;
	private int limit		= 0;


	public CalendarEntryURL() {}


	public String buildURL() {
		StringBuilder sb	= new StringBuilder(WEB_SERVICE);

		if (day > 0) {
			sb.append("&");
			sb.append(PARAM_DAY);
			sb.append("=");
			sb.append(day);
		}

		if (month > 0) {
			sb.append("&");
			sb.append(PARAM_MONTH);
			sb.append("=");
			sb.append(month);
		}

		if (limit > 0 && limit <= 100) {
			sb.append("&");
			sb.append(PARAM_LIMIT);
			sb.append("=");
			sb.append(limit);
		}

		// If the query string has length, that means that the first char is "&" and it has to be replaced with "?".
		int qsStart	= WEB_SERVICE.length();
		sb.replace(qsStart, qsStart + 1, "?");

		return sb.toString();
	}


	public int getDay() {
		return day;
	}


	public void setDay(int day) {
		this.day = day;
	}


	public int getMonth() {
		return month;
	}


	public void setMonth(int month) {
		this.month = month;
	}


	public int getLimit() {
		return limit;
	}


	public void setLimit(int limit) {
		this.limit = limit;
	}
}
