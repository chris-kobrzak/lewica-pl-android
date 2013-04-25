package pl.lewica.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ListUtil {

	/**
	 * @return
	 */
	public static List<Integer> parseIntegersList(String intList) {
		List<Integer> ints	= new ArrayList<Integer>();
		Scanner s			= new Scanner(intList);
		s.useDelimiter(",");

		while (s.hasNextInt() ) {
			ints.add(s.nextInt() );
		}
		s.close();
		return ints;
	}
}
