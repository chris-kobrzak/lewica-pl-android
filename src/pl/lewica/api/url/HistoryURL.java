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

import java.util.Calendar;

/**
 * Provides lewica.pl API URL based on the parameters set.
 * @author Krzysztof Kobrzak
 */
public class HistoryURL implements WebServiceURL {

	public static final String WEB_SERVICE			= "http://lewica.pl/api/kalendarium.php";
	public static final int LIMIT						= 100;

	public static final String PARAM_DAY			= "dzien"; 
	public static final String PARAM_MONTH		= "miesiac"; 
	public static final String PARAM_LIMIT		= "limit"; 

	private Calendar cal;
	// URL parameters
	private int day		= 0;
	private int month	= 0;
	private int limit		= 0;


	public HistoryURL() {
		cal			= Calendar.getInstance();
	}


	public String buildURL() {
		StringBuilder sb	= new StringBuilder(WEB_SERVICE);

		if (day == 0) {
			day		= cal.get(Calendar.DATE);
		}
		if (month == 0) {
			month	= cal.get(Calendar.MONTH) + 1;
		}
		if (limit > 0 && limit <= LIMIT) {
			sb.append("&");
			sb.append(PARAM_LIMIT);
			sb.append("=");
			sb.append(limit);
		}

		sb.append("&");
		sb.append(PARAM_DAY);
		sb.append("=");
		sb.append(day);

		sb.append("&");
		sb.append(PARAM_MONTH);
		sb.append("=");
		sb.append(month);

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


	/**
	 * @param limit Cannot exceed LIMIT
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}
}
