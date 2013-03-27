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
package pl.lewica.lewicapl.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import pl.lewica.api.model.BlogPost;


/**
 * Collection of Data Access method for interacting with the blogPost entity.
 * @author Krzysztof Kobrzak
 */
public class BlogPostDAO extends BaseTextDAO {

	public static final String DATABASE_TABLE				= "ZBlogPost";

	// Database fields
	public static final String FIELD_BLOG_ID					= "ZIdBlog";
	public static final String FIELD_AUTHOR_ID				= "ZIdAuthor";
	public static final String FIELD_DATE_PUBLISHED		= "ZDatePublished";
	public static final String FIELD_BLOG_TITLE				= "ZBlog";
	public static final String FIELD_AUTHOR					= "ZAuthor";
	public static final String FIELD_TITLE						= "ZTitle";
	public static final String FIELD_TEXT						= "ZText";

	private static String[] fieldsForSingleRecord		= new String[] {FIELD_ID, FIELD_DATE_PUBLISHED, FIELD_BLOG_TITLE,  FIELD_AUTHOR, FIELD_TITLE, FIELD_WAS_READ, FIELD_BLOG_ID, FIELD_TEXT };
	private static String[] fieldsForRecordSet				= new String[] {FIELD_ID, FIELD_DATE_PUBLISHED, FIELD_BLOG_TITLE, FIELD_AUTHOR, FIELD_TITLE, FIELD_WAS_READ };


	public BlogPostDAO(Context context) {
		super(context, DATABASE_TABLE);
	}


	// String wasRead,
	public long insert(BlogPost blogPost) {
		ContentValues cv	= new ContentValues();

		cv.put(FIELD_ID,							blogPost.getID() );
		cv.put(FIELD_BLOG_ID,				blogPost.getBlogID() );
		cv.put(FIELD_AUTHOR_ID,			blogPost.getAuthorID() );
		cv.put(FIELD_WAS_READ,				0);	// It's a new blogPost so it couldn't be read yet
		cv.put(FIELD_DATE_PUBLISHED,	blogPost.getDatePublished().getTime() );
		cv.put(FIELD_BLOG_TITLE,			blogPost.getBlogTitle() );
		cv.put(FIELD_AUTHOR,				blogPost.getAuthor() );
		cv.put(FIELD_TITLE,					blogPost.getTitle() );
		cv.put(FIELD_TEXT,						blogPost.getText() );

		return database.insert(DATABASE_TABLE, null, cv);
	}


	public Cursor selectLatestByBlogID(int blogID, int limit)
			throws SQLException {
		Cursor cursor = database.query(
			true, 
			DATABASE_TABLE, 
			getFieldsForRecordSet(),
			FIELD_BLOG_ID + "=" + blogID,
			null, null, null, 
			FIELD_ID + " DESC", 
			Integer.toString(limit)
		);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}


	@Override
	protected String[] getFieldsForSingleRecord() {
		return fieldsForSingleRecord;
	}


	@Override
	protected String[] getFieldsForRecordSet() {
		return fieldsForRecordSet;
	}
}
