/*
 Copyright 2013 lewica.pl

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

public class LanguageUtil {

	// PL Biernik: 2-5, 22-24
	public static boolean isPolishAccusative(int number) {
		if ( (number > 1 && number < 5) || (number > 21 && number < 25) ) {
			return true;
		}
		return false;
	}


	// PL Dopełniacz: 6-21, 25
	public static boolean isPolishGenitive(int number) {
		if ( (number > 5 && number < 21) || number == 25) {
			return true;
		}
		return false;
	}
}
