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
	 * Reads contents of SQL script and splits it into separate queries.
	 * This is required by Android that is unable to e.g. create multiple tables in one go.
	 * The regular expression that searches for query delimiters is pretty fragile, i.e.: 
	 * it assumes queries are ended with ; followed by a new line character.
	 */
	public static List<String> importSQL(InputStream is) {
		List<String> result		= new ArrayList<String>();
		Scanner s					= new Scanner(is);
		s.useDelimiter("(; *(\r)?\n)|(--\n)");

		while (s.hasNext()) {
			String line		= s.next();

			if (line.startsWith("/*!") && line.endsWith("*/")) {
				int i	= line.indexOf(" ");
				line	= line.substring(i + 1, line.length() - " */".length());
			}

			if (line.trim().length() > 0) {
				result.add(line);
			}
		}

		return result;
    }
}
