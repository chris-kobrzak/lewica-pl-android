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
public class ArticleURL implements IWebServiceURL {

	public static final String webService	= "http://lewica.pl/api/publikacje.php";
	// URL parameters
	private int newerThan		= 0;
	private int limit					= 0;
	private String sectionList	= "";
	private String format			= "xml";

	static final String PARAM_NEWER_THAN	= "od"; 
	static final String PARAM_LIMIT				= "limit"; 
	static final String PARAM_SECTIONS		= "dzialy"; 
	static final String PARAM_FORMAT			= "format"; 


	public ArticleURL() {}


	public String buildURL() {
		StringBuilder sb	= new StringBuilder(webService);

		if (newerThan > 0) {
			sb.append("&");
			sb.append(PARAM_NEWER_THAN);
			sb.append("=");
			sb.append(newerThan);
		}

		if (limit > 0 && limit <= 10) {
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
		int qsStart	= webService.length();
		sb.replace(qsStart, qsStart + 1, "?");

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
