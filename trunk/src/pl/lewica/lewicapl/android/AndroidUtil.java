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

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.ViewParent;
import android.widget.ScrollView;


public class AndroidUtil {

	private static File storageDir	= null;


	public static File getStorageDir() {
		return storageDir;
	}


	protected static void setStorageDir(String path) {
		File sdDir		= Environment.getExternalStorageDirectory();
		storageDir		= new File(sdDir + path);
		if (! storageDir.exists() ) {
			storageDir.mkdirs();
		}
	}


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


	/**
	 * Sets custom title background colour,
	 * see http://stackoverflow.com/questions/2251714/set-title-background-color
	 * @param colour
	 */
	public static void setApplicationTitleBackgroundColour(int colour, Activity activity) {
		View titleView = activity.getWindow().findViewById(android.R.id.title);
		if (titleView == null) {
			return;
		}
		ViewParent parent	= titleView.getParent();
		if (parent == null || ! (parent instanceof View) ) {
			return;
		}
		View parentView	= (View)parent;
		parentView.setBackgroundColor(colour);
	}


	/**
	 * E.g. when using previous-next facility you need to make sure the scroll view's position is back at the top of the screen
	 */
	public static void scrollToTop(int scrollViewId, Activity activity) {
		ScrollView sv	= (ScrollView) activity.findViewById(scrollViewId);
		sv.fullScroll(View.FOCUS_UP);
		sv.setSmoothScrollingEnabled(true);
	}


	/**
	 * Fix for carriage returns displayed as rectangle characters in Android 1.6
	 * @param str
	 * @return
	 */
	public static String removeCarriageReturns(String str) {
		return str.replace("\r", "");
	}

	/**
	 * Activities on Android are invoked with a Uri string.  This method captures and returns the last bit of this Uri
	 * which it assumes to be a numeric ID of the current article/announcements etc.
	 * @param intent
	 * @return
	 */
	public static long filterIDFromUri(Uri uri) {
		String id	= uri.getLastPathSegment();

		return Long.valueOf(id);
	}
}