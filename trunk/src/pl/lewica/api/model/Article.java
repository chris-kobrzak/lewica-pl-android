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
 * @author Krzysztof Kobrzak
 */
public class Article implements IModel {
	public int ID;
	public int articleCategoryID;
	public List<Integer> relatedIDs;
	public Date datePublished;
	public boolean wasRead;
	public boolean hasThumbnail;
	public String imageExtension;
	public URL URL;
	public String title;
	public String text;
	public String editorComment;
	public int totalComments;


	public int getID() {
		return ID;
	}
	public void setID(int id) {
		ID = id;
	}
	public int getArticleCategoryID() {
		return articleCategoryID;
	}
	public void setArticleCategoryID(int articleCategoryID) {
		this.articleCategoryID = articleCategoryID;
	}
	public List<Integer> getRelatedIDs() {
		return relatedIDs;
	}
	public void setRelatedIDs(List<Integer> relatedIDs) {
		this.relatedIDs = relatedIDs;
	}
	public Date getDatePublished() {
		return datePublished;
	}
	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}
	public boolean isWasRead() {
		return wasRead;
	}
	public void setWasRead(boolean wasRead) {
		this.wasRead = wasRead;
	}
	public boolean isHasThumbnail() {
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
