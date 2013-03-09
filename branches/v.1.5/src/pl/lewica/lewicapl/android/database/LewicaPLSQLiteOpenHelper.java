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
	private static final String TAG	= "LewicaPLSQLiteOpenHelper";
	
	private static final String DATABASE_NAME		= "LewicaPL.db";
	private static final int DATABASE_VERSION		= 2;
	
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
		int i, prevVersion;

		// Version 1
		initDatabase(db, DATABASE_VERSION);

		// Versions 2+
		i	= 2;
		for ( ; i <= DATABASE_VERSION; i++) {
			prevVersion	= i - 1;
			upgradeDatabase(db, prevVersion, i);
		}
	}



	/**
	 * This method is called during the upgrade of the database, e.g. if you increase the database version.
	 * 
	 * What it does is it calls updateDatabase as many times as the difference between newVersion and oldVersion is.
	 * This is because we want to run incremental upgrades so if e.g. you upgrade from version 1 to 3,
	 * we run a version 1 to version 2 upgrade script and then version 2 to 3. 
	 *  
	 * Not in use yet.
	 *  
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		int i	= oldVersion;

		for ( ; i <= newVersion - oldVersion; i++) {
			upgradeDatabase(db, i, i + 1);
		}
	}


	private boolean initDatabase(SQLiteDatabase db, int version) {
		boolean result	= true;
		List<String> queries;

		Log.i(TAG, "Creating DB version 1");
		try {
			queries = Schema.getDatabaseInitSQL(context, DATABASE_VERSION);

			db.beginTransaction();
			for (String query: queries) {
				db.execSQL(query);
			}
			db.setTransactionSuccessful();
		} catch (IOException e) {
			result	= false;
		} finally {
			db.endTransaction();
		}

		if (result) {
			return true;
		}

		// This is unlikely to happen but if it does, there's not much you can do as the application
		// cannot run without the database.  So let's at least make sure this event is logged.
		Log.e(TAG, "Failed to create DB version " + DATABASE_VERSION);
		return false;
	}


	private boolean upgradeDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
		boolean result	= true;
		List<String> queries;

		Log.i(TAG, "Upgrading DB from version " + oldVersion + " to " + newVersion);
		try {
			queries = Schema.getDatabaseUpgradeSQL(context, oldVersion, newVersion);

			db.beginTransaction();
			for (String query: queries) {
				db.execSQL(query);
			}
			db.setTransactionSuccessful();
		} catch (IOException e) {
			result	= false;
		} finally {
			db.endTransaction();
		}

		if (result) {
			return true;
		}
		
		// This is unlikely to happen but if it does, there's not much you can do as the application
		// cannot run without the database.  So let's at least make sure this event is logged.
		Log.e(TAG, "Failed to upgrade DB from version " + oldVersion + " to " + newVersion);
		return false;
	}
}
