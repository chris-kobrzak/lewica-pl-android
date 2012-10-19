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
package pl.lewica.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Collection of file and IO related utilities.
 * 
 * Sample usage of this class methods: 
 * InputStream is			= FileUtil.read("/path_to_sql_script");
 * List<String> queries	= FileUtil.importSQL(is);
 * @author Krzysztof Kobrzak
 *
 */
public class FileUtil {

	public static InputStream read(String path)
			throws FileNotFoundException {
		return new FileInputStream(path);
	}


	/**
	 * Reads contents of a stream (e.g. SQL script) and splits it into separate statements.
	 * IMPORTANT: The assumption is the statements are delimited by semicolons followed by new lines.
	 * 
	 * If you are using this method to convert a string with multiple SQL queries to individual statements,
	 * make sure the semicolon-new line sequence doesn't exist anywhere inside the SQL statements, perhaps
	 * somewhere in the middle of long varchars or text fields as they would be treated as SQL statement
	 * delimiters and you would therefore get unexpected results. 
	 * 
	 * The method might be useful on Android that is unable to e.g. create multiple tables in one go.
	 */
	public static List<String> convertStreamToStrings(InputStream is, String delimiter) {
		List<String> result		= new ArrayList<String>();
		Scanner s					= new Scanner(is);
		s.useDelimiter(delimiter);

		while (s.hasNext() ) {
			String line		= s.next().trim();
			if (line.length() > 0) {
				result.add(line);
			}
		}

		return result;
	}

}
