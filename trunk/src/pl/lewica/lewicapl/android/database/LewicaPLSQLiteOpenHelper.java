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

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Standard Android SQLite database management class responsible for creating and updating the schema.
 * @author Krzysztof Kobrzak
 */
public class LewicaPLSQLiteOpenHelper extends SQLiteOpenHelper {
	private static final String TAG						= "LewicaPLSQLiteOpenHelper";

	private static final String DATABASE_NAME		= "LewicaPL.db";
	private static final int DATABASE_VERSION		= 1;
	
	private Context context;


	/**
	 * Class constructor
	 * @param context
	 */
	public LewicaPLSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		this.context	= context;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		List<String> queries;

		try {
			queries = Schema.getDatabaseInitSQL(context, DATABASE_VERSION);

			for (String query: queries) {
				db.execSQL(query);
			}
		} catch (IOException e) {
			// This is unlikely to happen but if it does, there's not much you can do as the application
			// cannot run without the database.  So let's at least make sure this event is logged.
			Log.e(TAG, "Failed to create database");
			e.printStackTrace();
		}
	}


	/**
	 *  Method is called during the upgrade of the database, e.g. if you increase the database version.  Not in use yet.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}
