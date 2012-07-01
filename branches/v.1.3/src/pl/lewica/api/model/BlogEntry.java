/*
 Copyright 2012 lewica.pl

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
 * Represents the data structure of the blog entry feed available through the lewica.pl REST-like Web Service.
 * @author Krzysztof Kobrzak
 */
public class BlogEntry implements DataModel {
	public int ID;
	public int blogID;
	public int authorID;
	public Date datePublished;
	public boolean wasRead;
	public String blogTitle;
	public String author;
	public String title;
	public String text;


	public int getID() {
		return ID;
	}
	public void setID(int id) {
		ID = id;
	}
	public boolean wasRead() {
		return wasRead;
	}
	public void setWasRead(boolean wasRead) {
		this.wasRead = wasRead;
	}
	public int getBlogID() {
		return blogID;
	}
	public void setBlogID(int blogID) {
		this.blogID = blogID;
	}
	public int getAuthorID() {
		return authorID;
	}
	public void setAuthorID(int authorID) {
		this.authorID = authorID;
	}
	public Date getDatePublished() {
		return datePublished;
	}
	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}
	public String getBlogTitle() {
		return blogTitle;
	}
	public void setBlogTitle(String blogTitle) {
		this.blogTitle = blogTitle;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}