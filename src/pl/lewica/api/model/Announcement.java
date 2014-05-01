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
package pl.lewica.api.model;

import java.util.Date;

/**
 * Represents the data structure of the announcements feed (ogłoszenia) available through the lewica.pl REST-like Web Service.
 * @author Krzysztof Kobrzak
 */
public class Announcement implements DataModel {
	private int id;
	private boolean wasRead;
	private Date dateExpiry;
	private String what;
	private String where;
	private String when;
	private String content;
	private String publishedBy;
	private String publishedByEmail;


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean wasRead() {
		return wasRead;
	}
	public void setAsNotRead() {
		this.wasRead = false;
	}
	public Date getDateExpiry() {
		return dateExpiry;
	}
	public void setDateExpiry(Date dateExpiry) {
		this.dateExpiry = dateExpiry;
	}
	public String getWhat() {
		return what;
	}
	public void setWhat(String what) {
		this.what = what;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public String getWhen() {
		return when;
	}
	public void setWhen(String when) {
		this.when = when;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String text) {
		this.content = text;
	}
	public String getPublishedBy() {
		return publishedBy;
	}
	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
	}
	public String getPublishedByEmail() {
		return publishedByEmail;
	}
	public void setPublishedByEmail(String publishedByEmail) {
		this.publishedByEmail = publishedByEmail;
	}
}