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

import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Represents the data structure of the articles feed (artykuły) available through the lewica.pl REST-like Web Service.
 * @author Krzysztof Kobrzak
 */
public class Article implements DataModel {
	// Article sections dictionary
	public static final int SECTION_POLAND		= 1;
	public static final int SECTION_WORLD		= 2;
	public static final int SECTION_OPINIONS	= 3;
	public static final int SECTION_REVIEWS		= 4;
	public static final int SECTION_CULTURE		= 5;

	private int id;
	private int articleCategoryId;
	private List<Integer> relatedIds;
	private Date datePublished;
	private boolean wasRead;
	private boolean hasThumbnail;
	private String imageExtension;
	private URL URL;
	private String title;
	private String text;
	private String editorComment;
	private int totalComments;


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getArticleCategoryId() {
		return articleCategoryId;
	}
	public void setArticleCategoryId(int articleCategoryId) {
		this.articleCategoryId = articleCategoryId;
	}
	public List<Integer> getRelatedIds() {
		return relatedIds;
	}
	public void setRelatedIds(List<Integer> relatedIds) {
		this.relatedIds = relatedIds;
	}
	public Date getDatePublished() {
		return datePublished;
	}
	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}
	public boolean wasRead() {
		return wasRead;
	}
	public void setWasRead(boolean wasRead) {
		this.wasRead = wasRead;
	}
	public boolean hasThumbnail() {
		return hasThumbnail;
	}
	public void setHasThumbnail(boolean hasThumbnail) {
		this.hasThumbnail = hasThumbnail;
	}
	public String getImageExtension() {
		return imageExtension;
	}
	public void setImageExtension(String imageExtension) {
		this.imageExtension = imageExtension;
	}
	public URL getURL() {
		return URL;
	}
	public void setURL(URL url) {
		URL = url;
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
	public String getEditorComment() {
		return editorComment;
	}
	public void setEditorComment(String editorComment) {
		this.editorComment = editorComment;
	}
	public int getTotalComments() {
		return totalComments;
	}
	public void setTotalComments(int totalComments) {
		this.totalComments = totalComments;
	}
}
