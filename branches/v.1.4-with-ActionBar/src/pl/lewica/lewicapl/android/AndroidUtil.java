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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;


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


	/**
	 * Very limited shim for enabling the action bar's up button on devices that support it.
	 * Based on com.example.android.support.appnavigation.app.ActionBarCompat
	 */
	public static class ActionBarCompat {

		private static boolean isActionBarSupported	= Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

		/**
		 * This class will only ever be loaded if the version check succeeds,
		 * keeping the verifier from rejecting the use of framework classes that
		 * don't exist on older platform versions.
		 */
		@SuppressLint("NewApi")
		static class ActionBarCompatImpl {
			static void setDisplayHomeAsUpEnabled(Activity activity, boolean enable) {
				activity.getActionBar().setDisplayHomeAsUpEnabled(enable);
			}

			// Added by Chris
			static void setBackgroundDrawable(Activity activity, Drawable drawable) {
					activity.getActionBar().setBackgroundDrawable(drawable);
			}
		}

		public static void setDisplayHomeAsUpEnabled(Activity activity, boolean enable) {
			if (isActionBarSupported) {
				ActionBarCompatImpl.setDisplayHomeAsUpEnabled(activity, enable);
			}
		}

		// Added by Chris
		public static void setBackgroundDrawable(Activity activity, Drawable drawable) {
			if (isActionBarSupported) {
				ActionBarCompatImpl.setBackgroundDrawable(activity, drawable);
			}
		}
	}
}