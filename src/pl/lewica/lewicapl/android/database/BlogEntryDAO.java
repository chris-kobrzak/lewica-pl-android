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

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import pl.lewica.api.model.BlogEntry;
import pl.lewica.util.DateUtil;


/**
 * Collection of Data Access method for interacting with the blogEntry entity.
 * @author Krzysztof Kobrzak
 */
public class BlogEntryDAO {

	public static final int LIMIT_LATEST_ENTRIES			= 15;

	private static final String DATABASE_TABLE				= "ZBlogEntry";
	// Database fields
	public static final String FIELD_ID							= "_id";
	public static final String FIELD_BLOG_ID					= "ZIdBlog";
	public static final String FIELD_AUTHOR_ID				= "ZIdAuthor";
	public static final String FIELD_DATE_PUBLISHED		= "ZDatePublished";
	public static final String FIELD_WAS_READ				= "ZWasRead";
	public static final String FIELD_BLOG_TITLE				= "ZBlog";
	public static final String FIELD_AUTHOR					= "ZAuthor";
	public static final String FIELD_TITLE						= "ZTitle";
	public static final String FIELD_TEXT						= "ZText";

	public static final String MAP_KEY_PREVIOUS			= "Previous";
	public static final String MAP_KEY_NEXT					= "Next";

	private Context context;
	private SQLiteDatabase database;
	private LewicaPLSQLiteOpenHelper dbHelper;


	public BlogEntryDAO(Context context) {
		this.context	= context;
	}


	public BlogEntryDAO open() 
			throws SQLException {
		dbHelper	= new LewicaPLSQLiteOpenHelper(context);
		database	= dbHelper.getWritableDatabase();
		
		return this;
	}


	public void close() {
		dbHelper.close();
	}

	// String wasRead,
	public long insert(BlogEntry blogEntry) {
		ContentValues cv	= new ContentValues();
		
		cv.put(FIELD_ID,							blogEntry.getID() );
		cv.put(FIELD_BLOG_ID,				blogEntry.getBlogID() );
		cv.put(FIELD_AUTHOR_ID,			blogEntry.getAuthorID() );
		cv.put(FIELD_WAS_READ,				0);	// It's a new blogEntry so it couldn't be read yet
		cv.put(FIELD_DATE_PUBLISHED,	DateUtil.convertDateToString(blogEntry.getDatePublished(), DateUtil.DATE_MASK_SQL) );
		cv.put(FIELD_BLOG_TITLE,			blogEntry.getBlogTitle() );
		cv.put(FIELD_AUTHOR,				blogEntry.getAuthor() );
		cv.put(FIELD_TITLE,						blogEntry.getTitle() );
		cv.put(FIELD_TEXT,						blogEntry.getText() );

		return database.insert(DATABASE_TABLE, null, cv);
	}


	/**
	 * Meant to be called in a separate thread to avoid blocking UI.
	 * @param articleID
	 * @return
	 */
	public int updateMarkAsRead(long articleID) {
		ContentValues cv	= new ContentValues();

		cv.put(FIELD_WAS_READ, 1);

		SQLiteDatabase databaseWritable	= dbHelper.getWritableDatabase();

		int totalUpdates	= databaseWritable.update(
			DATABASE_TABLE, 
			cv, 
			FIELD_ID + "=" + articleID, 
			null
		);
		databaseWritable.close();
		
		return totalUpdates;
	}


	public int updateMarkAllAsRead() {
		ContentValues cv	= new ContentValues();
		
		cv.put(FIELD_WAS_READ, 1);
		
		SQLiteDatabase databaseWritable	= dbHelper.getWritableDatabase();
		
		int totalUpdates	= databaseWritable.update(
				DATABASE_TABLE, 
				cv, 
				FIELD_WAS_READ + "=" + 0, 
				null
				);
		databaseWritable.close();
		
		return totalUpdates;
	}


	public Cursor selectOne(long blogEntryID) throws SQLException {
		Cursor cursor = database.query(true, DATABASE_TABLE, new String[] {
				FIELD_ID, FIELD_DATE_PUBLISHED, FIELD_BLOG_TITLE,  FIELD_AUTHOR, FIELD_TITLE, FIELD_TEXT },
				FIELD_ID + "=" + blogEntryID, null, null, null, null, "1");
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}


	public Cursor selectLatest()
			 throws SQLException {
		Cursor cursor = database.query(
			true, 
			DATABASE_TABLE, 
			new String[] {	FIELD_ID, FIELD_WAS_READ, FIELD_DATE_PUBLISHED, FIELD_BLOG_TITLE, FIELD_AUTHOR, FIELD_TITLE },
			null, null, null, null, 
			FIELD_ID + " DESC", 
			Integer.toString(LIMIT_LATEST_ENTRIES)
		);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * Returns the latest blogEntry ID from the database.
	 * @return
	 */
	public int fetchLastID() {
		Cursor cursor = database.query(
			DATABASE_TABLE,
			new String[] { "MAX(" + FIELD_ID + ")" },
			null, null, null, null, null, null
		);

		if (cursor == null) {
			return 0;
		}

		cursor.moveToFirst();

		int result	= cursor.getInt(0);
		cursor.close();
		return result;
	}


	public Map<String,Long> fetchPreviousNextID(long ID) {
		Map<String,Long> map	= new HashMap<String,Long>();
		StringBuilder sb				= new StringBuilder();
		
		// Building SQL query consisting of two "unioned" parts like this one:
		// SELECT COALESCE(MAX(_id), 0)AS id, 'Previous' AS type FROM ZBlogEntry WHERE _id < 1365
		sb.append("SELECT COALESCE(MAX(");
		sb.append(FIELD_ID);
		sb.append("), 0) AS id, '");
		sb.append(MAP_KEY_PREVIOUS);
		sb.append("' AS type FROM ");
		sb.append(DATABASE_TABLE);
		sb.append(" WHERE ");
		sb.append(FIELD_ID);
		sb.append(" < ? ");
		sb.append(" UNION ");
		sb.append("SELECT COALESCE(MIN(");
		sb.append(FIELD_ID);
		sb.append("), 0) AS id, '");
		sb.append(MAP_KEY_NEXT);
		sb.append("' AS type FROM ");
		sb.append(DATABASE_TABLE);
		sb.append(" WHERE ");
		sb.append(FIELD_ID);
		sb.append(" > ? ");

		String idString			= Long.toString(ID); 

		if (! database.isOpen() ) {
			database				= dbHelper.getReadableDatabase();
		}
		Cursor cursor	= database.rawQuery(
			sb.toString(),
			new String[] { idString, idString }
		);

		if (cursor == null) {
			return map;
		}

		// First row = previous article ID, see the UNION query above
		cursor.moveToFirst();
		int colIndID		= cursor.getColumnIndex("id");
		int colIndType	= cursor.getColumnIndex("type");
		
		map.put(cursor.getString(colIndType), cursor.getLong(colIndID) );
		
		// Second row = next article ID, see the UNION query above
		cursor.moveToLast();
		
		map.put(cursor.getString(colIndType), cursor.getLong(colIndID) );
		
		cursor.close();

		return map;
	}


	/**
	 * Not in use yet.  Convert this method to public when the delete functionality is implemented.
	 * @param articleID
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean delete(long articleID) {
		return database.delete(DATABASE_TABLE, FIELD_ID + "=" + articleID, null) > 0;
	}
}
