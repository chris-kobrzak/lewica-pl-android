package pl.lewica.lewicapl.android.database;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


/**
 * Collection of methods standard list-details sections should be able to subclass.
 * Apart from open(), you'd typically want to define an insert method in your subclass.
 * @author Krzysztof Kobrzak
 */
public abstract class BaseTextDAO extends BaseLewicaPLDAO implements BaseColumns {
	
	public static final String MAP_KEY_PREVIOUS	= "Previous";
	public static final String MAP_KEY_NEXT			= "Next";

	// Only these two fields are common to articles, announcements and blog entries:
	public static final String FIELD_ID					= "_id";
	public static final String FIELD_WAS_READ		= "ZWasRead";

	// Typically, these would be "overridden" in the subclass.
	protected static String[] fieldsForSingleRecord	= new String[] {FIELD_ID };
	protected static String[] fieldsForRecordSet			= new String[] {FIELD_ID, FIELD_WAS_READ };

	private String databaseTable;


	public BaseTextDAO(Context context, String dbRoutine) {
		super(context);

		this.databaseTable			= dbRoutine;
	}


	/**
	 * Meant to be called in a separate thread to avoid blocking UI.
	 * @param recordId
	 * @return Number of records updated
	 */
	public int updateMarkRecordAsRead(int recordId) {
		ContentValues cv	= new ContentValues();

		cv.put(FIELD_WAS_READ, 1);

		SQLiteDatabase databaseWritable	= dbHelper.getWritableDatabase();

		int totalUpdates	= databaseWritable.update(
			databaseTable, 
			cv, 
			FIELD_ID + "=" + recordId,
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
			databaseTable, 
			cv, 
			FIELD_WAS_READ + "=" + 0, 
			null
		);
		databaseWritable.close();

		return totalUpdates;
	}


	/**
	 * Fetches one record for a given ID.
	 */
	public Cursor selectOne(int recordId) throws SQLException {
		Cursor cursor = database.query(true, databaseTable, getFieldsForSingleRecord(),
				FIELD_ID + "=" + recordId, null, null, null, null, "1");
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}


	public Cursor selectLatest(int limit)
			throws SQLException {
		Cursor cursor = database.query(
			true, 
			databaseTable, 
			getFieldsForRecordSet(),
			null, null, null, null, 
			FIELD_ID + " DESC", 
			Integer.toString(limit)
		);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}


	/**
	 * Returns the latest announcement ID from the database.
	 * @return
	 */
	public int fetchLastId() {
		Cursor cursor = database.query(
			databaseTable,
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


	public Map<String,Integer> fetchPreviousNextId(int recordId) {
		Map<String,Integer> map	= new HashMap<String,Integer>();
		StringBuilder sb				= new StringBuilder();

		// Building SQL query consisting of two "unioned" parts like this one:
		// SELECT COALESCE(MAX(_id), 0)AS id, 'Previous' AS type FROM ZAnnouncement WHERE _id < 1365
		// SELECT COALESCE(MAX(_id), 0)AS id, 'Previous' AS type FROM ZBlogPost WHERE _id < 1365
		sb.append("SELECT COALESCE(MAX(");
		sb.append(FIELD_ID);
		sb.append("), 0) AS id, '");
		sb.append(MAP_KEY_PREVIOUS);
		sb.append("' AS type FROM ");
		sb.append(databaseTable);
		sb.append(" WHERE ");
		sb.append(FIELD_ID);
		sb.append(" < ? ");
		sb.append(" UNION ");
		sb.append("SELECT COALESCE(MIN(");
		sb.append(FIELD_ID);
		sb.append("), 0) AS id, '");
		sb.append(MAP_KEY_NEXT);
		sb.append("' AS type FROM ");
		sb.append(databaseTable);
		sb.append(" WHERE ");
		sb.append(FIELD_ID);
		sb.append(" > ? ");

		String idString = Integer.toString(recordId);

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
		int colIndId		= cursor.getColumnIndex("id");
		int colIndType	= cursor.getColumnIndex("type");

		map.put(cursor.getString(colIndType), cursor.getInt(colIndId) );

		// Second row = next article ID, see the UNION query above
		cursor.moveToLast();

		map.put(cursor.getString(colIndType), cursor.getInt(colIndId) );

		cursor.close();

		return map;
	}


	protected String[] getFieldsForSingleRecord() {
		return fieldsForSingleRecord;
	}


	protected String[] getFieldsForRecordSet() {
		return fieldsForRecordSet;
	}
}