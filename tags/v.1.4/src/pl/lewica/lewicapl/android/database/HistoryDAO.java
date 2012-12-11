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

import pl.lewica.api.model.History;


/**
 * Collection of Data Access method for interacting with the calendar/history entity.
 * @author Krzysztof Kobrzak
 */
public class HistoryDAO extends BaseLewicaPLDAO {

	private static final String DATABASE_TABLE	= "ZCalendar";
	// Database fields
	public static final String FIELD_ID					= "_id";
	public static final String FIELD_YEAR				= "ZYear";
	public static final String FIELD_MONTH			= "ZMonth";
	public static final String FIELD_DAY				= "ZDay";
	public static final String FIELD_EVENT			= "ZEvent";

	private static String[] fieldsForSingleRecord	= new String[] {FIELD_ID, FIELD_YEAR, FIELD_EVENT };

	public static final int LIMIT_ROWS					= 100;


	public HistoryDAO(Context context) {
		super(context);
	}


	// String wasRead,
	public long insert(History history) {
		ContentValues cv	= new ContentValues();
		
		cv.put(FIELD_YEAR,		history.getYear() );
		cv.put(FIELD_MONTH,	history.getMonth() );
		cv.put(FIELD_DAY,		history.getDay() );
		cv.put(FIELD_EVENT,	history.getEvent() );

		return database.insert(DATABASE_TABLE, null, cv);
	}


	public Cursor select(int month, int day, int limit) 
			throws SQLException {
		StringBuilder sb	= new StringBuilder();
		sb.append(FIELD_MONTH);
		sb.append("= ? AND ");
		sb.append(FIELD_DAY);
		sb.append("= ?");
		
		Cursor cursor = database.query(
				true, 
				DATABASE_TABLE, 						// FROM
				fieldsForSingleRecord,				// SELECT
				sb.toString(),								// WHERE 
				new String[] { Integer.toString(month), Integer.toString(day) }, 
				null, 											// GROUP BY
				null, 											// HAVING
				FIELD_YEAR + " ASC", 				// ORDER BY
				Integer.toString(limit) );				// LIMIT

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}


	public boolean hasEntriesForDate(int month, int day) 
			throws SQLException {
		StringBuilder sb	= new StringBuilder();
		sb.append(FIELD_MONTH);
		sb.append("= ? AND ");
		sb.append(FIELD_DAY);
		sb.append("= ?");

		// All we need to know if there is at least one entry for the given day in the database hence LIMIT = 1.
		Cursor cursor = database.query(
				true, 
				DATABASE_TABLE, 				// FROM
				new String[] { FIELD_ID },	// SELECT
				sb.toString(),						// WHERE 
				new String[] { Integer.toString(month), Integer.toString(day) }, 
				null, 									// GROUP BY
				null, 									// HAVING
				null,							 		// ORDER BY
				"1");									// LIMIT

		if (cursor == null) {
			return false;
		}

		if (cursor.getCount() == 0) {
			cursor.close();
			return false;
		}

		cursor.close();
		return true;
	}

}
