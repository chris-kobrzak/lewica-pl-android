package pl.lewica.lewicapl.android;

import pl.lewica.lewicapl.android.theme.Theme;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class UserPreferencesManager {

	public static final String USER_SETTING_THEME		= "colourTheme";

	public static final String USER_SETTING_TEXT_SIZE		= "textSizeStandard";
	public static final String USER_SETTING_TEXT_SIZE_HEADING		= "textSizeHeading";
	public static final float DEFAULT_TEXT_SIZE					= 16.f;
	public static final float MIN_TEXT_SIZE							=  8.f;
	public static final float MAX_TEXT_SIZE							= 52.f;
	public static final float DEFAULT_TEXT_SIZE_HEADING	= 25.f;
	public static final float MIN_TEXT_SIZE_HEADING			= 17.f;
	public static final float MAX_TEXT_SIZE_HEADING			= 65.f;
	public static final float TEXT_SIZE_INCREMENT				=  1.f;
	public static final int TEXT_SIZES_TOTAL		= (int) ( (MAX_TEXT_SIZE - MIN_TEXT_SIZE) / TEXT_SIZE_INCREMENT) + 1;
	public static final float HEADING_TEXT_DIFF		= DEFAULT_TEXT_SIZE_HEADING - DEFAULT_TEXT_SIZE;


	public static int switchUserTheme(Context context) {
		switch (getUserTheme(context) ) {
			case Theme.THEME_LIGHT:
				setUserTheme(Theme.THEME_DARK, context);
				return Theme.THEME_DARK;

			case Theme.THEME_DARK:
				setUserTheme(Theme.THEME_LIGHT, context);
				return Theme.THEME_LIGHT;
		}

		return -1;
	}


	public static int getUserTheme(Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getInt(USER_SETTING_THEME, Theme.THEME_LIGHT);
	}


	private static void setUserTheme(int theme, Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putInt(USER_SETTING_THEME, theme);
		prefsEditor.commit();
	}


	public static int convertTextSize(float textSize) {
		int textSizeInt	= Math.round(textSize);
		int minSize		= Math.round(MIN_TEXT_SIZE);
		int increment		= Math.round(TEXT_SIZE_INCREMENT);

		return (textSizeInt - minSize) / increment;
	}


	public static float convertTextSize(int textSize) {
		int increment	= Math.round(TEXT_SIZE_INCREMENT);
		int minSize	= Math.round(MIN_TEXT_SIZE);
		return (float) (textSize * increment) + minSize;
	}


	/**
	 * Opens up the preferences file via PreferenceManager
	 * and reads the user text size value
	 * TODO Make sure this method is not invoked too often as it reads XML
	 *   consider caching its result in a similar way to how we cache theme info 
	 * @param context
	 * @return
	 */
	public static float getUserTextSize(Context context) {
//android.util.Log.i("prefs", "read");
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getFloat(USER_SETTING_TEXT_SIZE, DEFAULT_TEXT_SIZE);
	}


	/**
	 * Opens up the preferences file via PreferenceManager
	 * and saves the new values on UI thread
	 * @param size
	 * @param context
	 */
	public static void setUserTextSize(float size, Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat(USER_SETTING_TEXT_SIZE, size);
		prefsEditor.commit();
	}
}
