/*
 Copyright 2012 lewica.pl

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
package pl.lewica.lewicapl.android;

import java.io.File;
import java.io.IOException;


public class AndroidUtil {


	/**
	 * Adds a special, hidden file to the cache directory to prevent images from being indexed by Android Gallery
	 * @param storageDir
	 */
	public static void setUpResourcesHiddenFromAndroidGallery(File storageDir)
			throws IOException {
		File hideGallery	= new File(storageDir + "/.nomedia");
		if (hideGallery.exists() ) {
			return;
		}
		hideGallery.createNewFile();
	}
}