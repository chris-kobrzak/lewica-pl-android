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
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;

import pl.lewica.util.FileUtil;


/**
 * This class is related to the database set-up scripts.
 * It knows everything about their paths and format.
 * 
 * @author Krzysztof Kobrzak
 */
public class Schema {

	/**
	 * Returns SQL scripts necessary to initialise the database.
	 * Since SQLiteDatabse.execSQL() can only run individual queries, this method returns a list queries that can be looped through.
	 * @param context Android application context
	 * @return
	 * @throws IOException
	 */
	public static List<String> getDatabaseInitSQL(Context context)
			throws IOException {
		AssetManager am = context.getAssets();
		InputStream is = am.open("LewicaPL.sql");

		return extractSqlStatements(is);
	}


	/**
	 * Returns SQL scripts required to initialise the database.
	 * Since SQLiteDatabse.execSQL() can only run individual queries, this method returns a list queries that can be looped through.
	 * @param context
	 * @param oldVersion
	 * @param newVersion
	 * @return
	 * @throws IOException
	 */
	public static List<String> getDatabaseUpgradeSQL(Context context, int oldVersion, int newVersion) 
			throws IOException {
		AssetManager am	= context.getAssets();
		InputStream is		= am.open(getUpgradeFileName(oldVersion, newVersion) );

		return extractSqlStatements(is);
	}


	/**
	 * Returns SQL upgrade script file name. 
	 * A sample file name could be LewicaPLUpgrade1To2.sql for a file upgrading from version 1 to 2.
	 * @param oldVersion
	 * @param newVersion
	 * @return
	 */
	private static String getUpgradeFileName(int oldVersion, int newVersion) {
		return "LewicaPLUpgrade" + oldVersion + "To" + newVersion + ".sql";
	}


	private static List<String> extractSqlStatements(InputStream is) {
		String semicolonNewLineRegEx	= "; *\r?\n";
		
		return FileUtil.convertStreamToStrings(is, semicolonNewLineRegEx);
	}
}
