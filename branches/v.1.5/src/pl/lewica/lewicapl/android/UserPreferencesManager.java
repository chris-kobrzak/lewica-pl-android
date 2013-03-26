package pl.lewica.lewicapl.android;

import pl.lewica.lewicapl.android.theme.ApplicationTheme;
import pl.lewica.lewicapl.android.theme.DarkTheme;
import pl.lewica.lewicapl.android.theme.LightTheme;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class UserPreferencesManager {

	// TODO Consider converting these constants into enum types
	public static final int THEME_LIGHT		= 1;
	public static final int THEME_DARK		= 2;

	public static final String USER_SETTING_THEME		= "colourTheme";
	public static final String USER_SETTING_TEXT_SIZE		= "textSize";
	public static final float DEFAULT_TEXT_SIZE					= 16.f;
	public static final float MIN_TEXT_SIZE							=  8.f;
	public static final float MAX_TEXT_SIZE							= 52.f;
	public static final float DEFAULT_TEXT_SIZE_HEADING	= 25.f;
	public static final float MIN_TEXT_SIZE_HEADING			= 17.f;
	public static final float MAX_TEXT_SIZE_HEADING			= 65.f;
	public static final float TEXT_SIZE_INCREMENT				=  1.f;
	public static final int TEXT_SIZES_TOTAL		= (int) ( (MAX_TEXT_SIZE - MIN_TEXT_SIZE) / TEXT_SIZE_INCREMENT) + 1;
	public static final float HEADING_TEXT_DIFF		= DEFAULT_TEXT_SIZE_HEADING - DEFAULT_TEXT_SIZE;

	private static float sTextSize	= -1.f;
	private static int sCurrentTheme	= -1;


	/**
	 * Checks the current theme in the cache,
	 * caches the other theme (we only have two of them at the moment)
	 * and calls a method that caches it using Android routines.
	 * @param context
	 * @return
	 */
	public static int switchUserTheme(Context context) {
		switch (getTheme(context) ) {
			case THEME_LIGHT:
				setTheme(THEME_DARK, context);
				return THEME_DARK;

			case THEME_DARK:
				setTheme(THEME_LIGHT, context);
				return THEME_LIGHT;
		}

		return -1;
	}


	/**
	 * Factory method that returns an instance of a class that implements the ApplicationTheme interface
	 * @param context
	 * @return
	 */
	public static ApplicationTheme getThemeInstance(Context context) {
		if (sCurrentTheme == -1) {
			setTheme(getUserTheme(context), context);
		}
		switch (sCurrentTheme) {
			case THEME_LIGHT:
				return LightTheme.getInstance(context);

			case THEME_DARK:
				return DarkTheme.getInstance(context);
		}
		return null;
	}


	/**
	 * Returns the current theme from the class's cache
	 * or, if cache is empty, reads it from Android preferences file
	 * and adds it to the cache.
	 * @param context
	 * @return
	 */
	public static int getTheme(Context context) {
		if (sCurrentTheme > -1) {
			return sCurrentTheme;
		}
		sCurrentTheme	= getUserTheme(context); 
		return sCurrentTheme;
	}


	/**
	 * Saves new theme ID in the class variable (local cache)
	 * and saves it in the Android preferences file
	 * @param theme
	 * @param context
	 */
	public static void setTheme(int theme, Context context) {
		sCurrentTheme	= theme;

		setUserTheme(theme, context);
	}


	private static int getUserTheme(Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getInt(USER_SETTING_THEME, THEME_LIGHT);
	}


	private static void setUserTheme(int theme, Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putInt(USER_SETTING_THEME, theme);
		prefsEditor.commit();
	}


	/**
	 * Returns the value cached inside this class
	 * and if the cache is empty, reads it from the Android preferences file
	 * and adds this value to the cache.
	 * @param context
	 * @return
	 */
	public static float getTextSize(Context context) {
		if (sTextSize > -1.f) {
			return sTextSize;
		}
		sTextSize	= getUserTextSize(context);
		return sTextSize;
	}


	/**
	 * Saves new text size value in the class variable
	 * and saves it in the Android preferences file
	 * @param textSize
	 * @param context
	 */
	public static void setTextSize(float textSize, Context context) {
		sTextSize = textSize;
		// TODO This operation should be run on UI thread
		setUserTextSize(textSize, context);
	}


	/**
	 * Opens up the preferences file via PreferenceManager
	 * and reads the user text size value
	 * @param context
	 * @return
	 */
	private static float getUserTextSize(Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getFloat(USER_SETTING_TEXT_SIZE, DEFAULT_TEXT_SIZE);
	}


	/**
	 * Opens up the preferences file via PreferenceManager
	 * and saves the new values on UI thread
	 * @param size
	 * @param context
	 */
	private static void setUserTextSize(float size, Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat(USER_SETTING_TEXT_SIZE, size);
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
}
