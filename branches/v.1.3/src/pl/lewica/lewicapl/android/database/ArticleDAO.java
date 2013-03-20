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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
import pl.lewica.api.model.Article;


/**
 * Collection of Data Access method for interacting with the Article entity.
 * @author Krzysztof Kobrzak
 */
public class ArticleDAO extends BaseTextDAO {

	// A view using UNION statements pulling data from other views.  See LewicaPL.sql for details.
	public static final String DATABASE_VIEW_NEWS				= "VLatestNews";
	public static final String DATABASE_VIEW_PUBLICATIONS	= "VLatestPublications";

	// Database fields
	public static final String FIELD_CATEGORY_ID					= "ZIDArticleCategory";
	public static final String FIELD_RELATED_IDS					= "ZRelatedIDs";
	public static final String FIELD_DATE_PUBLISHED				= "ZDatePublished";
	public static final String FIELD_HAS_IMAGE						= "ZHasThumbnail";
	public static final String FIELD_IMAGE_EXTENSION			= "ZImageExtension";
	public static final String FIELD_URL								= "ZURL";
	public static final String FIELD_TITLE								= "ZTitle";
	public static final String FIELD_TEXT								= "ZText";
	public static final String FIELD_EDITOR_COMMENT			= "ZEditorComment";
	public static final String FIELD_HAS_EDITOR_COMMENT	= "ZHasEditorComment";

	protected static String[] fieldsForSingleRecord				= new String[] {FIELD_ID, FIELD_CATEGORY_ID, FIELD_DATE_PUBLISHED, FIELD_WAS_READ, FIELD_HAS_IMAGE, FIELD_IMAGE_EXTENSION, FIELD_URL, FIELD_TITLE, FIELD_TEXT, FIELD_EDITOR_COMMENT, FIELD_HAS_EDITOR_COMMENT };

	private static String databaseTable									= "ZArticle";

	// Changing this value would have database ramifications as the views have hard-coded limits set to 5!
	protected int limitLatestRecords										= 5;


	public ArticleDAO(Context context) {
		this.context	= context;
	}


	public ArticleDAO open()
			throws SQLException {
		dbHelper	= new LewicaPLSQLiteOpenHelper(context);
		database	= dbHelper.getReadableDatabase();

		return this;
	}


	/**
	 * Adds a new article to the database.
	 * @param article
	 * @return
	 */
	public long insert(Article article) {
		ContentValues cv	= new ContentValues();

//		String ID, String categoryID, String relatedIDs, String datePublished, String hasImage, String imageExt, String URL, String title, String content, String editorComment
		URL articleURL		= article.getURL();
		String url;
		if (articleURL != null) {
			url		= articleURL.toString();
		} else {
			url		= "";
		}
		boolean hasImage	= false;
		if (article.getImageExtension() != null && article.getImageExtension().length() > 0) {
			hasImage				= true;
		}
		boolean hasEditorComment	= false;
		if (article.getEditorComment() != null && article.getEditorComment().length() > 0) {
			hasEditorComment	= true;
		}

		String relatedIDs	= "";
		ArrayList<Integer> relatedIDList	= (ArrayList<Integer>) article.getRelatedIDs();
		if (relatedIDList.size() > 0) {
			StringBuilder sb	= new StringBuilder(); 
			for (Integer i: relatedIDList) {
				sb.append(i.toString() );
			}
			relatedIDs	= sb.toString();
		}

		cv.put(FIELD_ID,							article.getID() );
		cv.put(FIELD_CATEGORY_ID,		article.getArticleCategoryID() );
		cv.put(FIELD_RELATED_IDS,			relatedIDs);
		cv.put(FIELD_DATE_PUBLISHED,	article.getDatePublished().getTime() );	// Storing date as Unix timestamp for better performance
		cv.put(FIELD_WAS_READ,				0);	// It's a new article so it couldn't be read yet
		cv.put(FIELD_HAS_IMAGE,			hasImage);
		cv.put(FIELD_IMAGE_EXTENSION,	article.getImageExtension() );
		cv.put(FIELD_URL,						url);
		cv.put(FIELD_TITLE,						article.getTitle() );
		cv.put(FIELD_TEXT,						article.getText() );
		cv.put(FIELD_HAS_EDITOR_COMMENT, hasEditorComment);
		cv.put(FIELD_EDITOR_COMMENT,	article.getEditorComment() );

		SQLiteDatabase databaseWritable	= dbHelper.getWritableDatabase();
		return databaseWritable.insert(databaseTable, null, cv);
	}


	public int updateMarkNewsAsRead() {
		StringBuilder sb		= new StringBuilder();
		ContentValues cv	= new ContentValues();

		// UPDATE operation
		cv.put(FIELD_WAS_READ, 1);

		// WHERE clause
		sb.append(FIELD_CATEGORY_ID);
		sb.append(" IN (");
		sb.append(Article.SECTION_POLAND);
		sb.append(",");
		sb.append(Article.SECTION_WORLD);
		sb.append(") AND ");
		sb.append(FIELD_WAS_READ);
		sb.append(" = 0");

		SQLiteDatabase databaseWritable	= dbHelper.getWritableDatabase();

		int totalUpdates	= databaseWritable.update(
			getDatabaseTable(), 
			cv, 
			sb.toString(), 
			null
		);
		databaseWritable.close();

		return totalUpdates;
	}


	public int updateMarkTextsAsRead() {
		StringBuilder sb		= new StringBuilder();
		ContentValues cv	= new ContentValues();

		// UPDATE operation
		cv.put(FIELD_WAS_READ, 1);

		// WHERE clause
		sb.append(FIELD_CATEGORY_ID);
		sb.append(" IN (");
		sb.append(Article.SECTION_OPINIONS);
		sb.append(",");
		sb.append(Article.SECTION_REVIEWS);
		sb.append(",");
		sb.append(Article.SECTION_CULTURE);
		sb.append(") AND ");
		sb.append(FIELD_WAS_READ);
		sb.append(" = 0");

		SQLiteDatabase databaseWritable	= dbHelper.getWritableDatabase();

		int totalUpdates	= databaseWritable.update(
			getDatabaseTable(), 
			cv, 
			sb.toString(), 
			null
		);
		databaseWritable.close();

		return totalUpdates;
	}


	/**
	 * A wrapper method that fetches LIMIT_LATEST_ENTRIES articles from the database for the TWO news sections.
	 * @return
	 * @throws SQLException
	 */
	public Cursor selectLatestNews() throws SQLException {
		// Requesting LIMIT_LATEST_ENTRIES entries per section.  Total news sections is 2.
		return selectLatest(
			DATABASE_VIEW_NEWS,
			new String[] { FIELD_ID, FIELD_CATEGORY_ID, FIELD_DATE_PUBLISHED, FIELD_WAS_READ, FIELD_HAS_IMAGE, FIELD_IMAGE_EXTENSION, FIELD_TITLE, FIELD_HAS_EDITOR_COMMENT },
			new String[] {
				Integer.toString(Article.SECTION_POLAND),
				Integer.toString(Article.SECTION_WORLD)
			},
			limitLatestRecords * 2);
	}


	/**
	 * Fetches LIMIT_LATEST_ENTRIES articles from the database for the THREE commentary sections.
	 * @return
	 * @throws SQLException
	 */
	public Cursor selectLatestTexts() throws SQLException {
		// Requesting LIMIT_LATEST_ENTRIES entries per section.  Total publications sections is 3.
		return selectLatest(
			DATABASE_VIEW_PUBLICATIONS,
			new String[] { FIELD_ID, FIELD_CATEGORY_ID, FIELD_DATE_PUBLISHED, FIELD_WAS_READ, FIELD_HAS_IMAGE, FIELD_IMAGE_EXTENSION, FIELD_TITLE },
			new String[] { 
				Integer.toString(Article.SECTION_OPINIONS),
				Integer.toString(Article.SECTION_REVIEWS),
				Integer.toString(Article.SECTION_CULTURE)
			},
			limitLatestRecords * 3);
	}


	/**
	 * Fetches latest articles in one or more categories ordered by category ID (ASC) and article ID (DESC)
	 * @param categoryIDs This array needs to have at least one element.
	 * @param limit
	 * @return
	 * @throws SQLException
	 */
	private Cursor selectLatest(String table, String[] columns, String[] categoryIDs, int limit)
			throws SQLException {
		StringBuilder sbWhere	= new StringBuilder();
		StringBuilder sbOrderBy	= new StringBuilder();

		String listCatID	= Arrays.toString(categoryIDs);
		// SQL IN statement that goes to the WHERE clause
		if (categoryIDs.length > 0 && listCatID.length() > 0) {
			sbWhere.append(FIELD_CATEGORY_ID);
			sbWhere.append(" IN ( ");
			// At this stage the string can look like this: [1, 2].  So we need to rid square brackets.
			sbWhere.append( listCatID.substring( 1, listCatID.length() - 1) );
			sbWhere.append(") ");
		}

		sbOrderBy.append(FIELD_CATEGORY_ID);
		sbOrderBy.append( " ASC, ");
		sbOrderBy.append(FIELD_ID);
		sbOrderBy.append( " DESC");

		Cursor cursor = database.query(
			true, 
			table, 
			columns,
			sbWhere.toString(), 
			null, 
			null, 
			null, 
			sbOrderBy.toString(), 
			Integer.toString(limit)
		);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}


	public Map<String,Long> fetchPreviousNextID(long recordID, int categoryID) {
		Map<String,Long> map	= new HashMap<String,Long>();
		StringBuilder sb				= new StringBuilder();

		// Building SQL query consisting of two "unioned" parts like this one:
		// SELECT COALESCE(MAX(_id), 0)AS id, 'Previous' AS type FROM ZArticle WHERE _id < 25365 AND ZIDArticleCategory = 1
		sb.append("SELECT COALESCE(MAX(");
		sb.append(FIELD_ID);
		sb.append("), 0) AS id, '");
		sb.append(MAP_KEY_PREVIOUS);
		sb.append("' AS type FROM ");
		sb.append(getDatabaseTable());
		sb.append(" WHERE ");
		sb.append(FIELD_ID);
		sb.append(" < ? AND ");
		sb.append(FIELD_CATEGORY_ID);
		sb.append(" = ? UNION ");
		sb.append("SELECT COALESCE(MIN(");
		sb.append(FIELD_ID);
		sb.append("), 0) AS id, '");
		sb.append(MAP_KEY_NEXT);
		sb.append("' AS type FROM ");
		sb.append(getDatabaseTable());
		sb.append(" WHERE ");
		sb.append(FIELD_ID);
		sb.append(" > ? AND ");
		sb.append(FIELD_CATEGORY_ID);
		sb.append(" = ?");

		String idString			= Long.toString(recordID); 
		String idCategString	= Integer.toString(categoryID);

		if (! database.isOpen() ) {
			database				= dbHelper.getReadableDatabase();
		}
		Cursor cursor	= database.rawQuery(
			sb.toString(),
			new String[] { idString, idCategString, idString, idCategString }
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


	@Override
	protected String getDatabaseTable() {
		return databaseTable;
	}


	@Override
	protected String[] getFieldsForSingleRecord() {
		return fieldsForSingleRecord;
	}


	@Override
	protected String[] getFieldsForRecordSet() {
		return fieldsForRecordSet;
	}


	@Override
	protected int getLimitLatestRecords() {
		return limitLatestRecords;
	}
}