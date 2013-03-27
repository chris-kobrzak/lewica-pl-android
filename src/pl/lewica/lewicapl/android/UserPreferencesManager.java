package pl.lewica.lewicapl.android;

import pl.lewica.lewicapl.android.theme.Theme;
import pl.lewica.lewicapl.android.theme.DarkTheme;
import pl.lewica.lewicapl.android.theme.LightTheme;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class UserPreferencesManager {

	public static enum ThemeType {
		LIGHT, DARK
	}

	private static String sSettingTheme					= "colourTheme";
	private static String sSettingTextSize					= "textSize";
	private static float sDefaultTextSize					= 16.f;
	private static float sMinTextSize						=  8.f;
	private static float sMaxTextSize						= 52.f;
	private static float sTextSizeIncrement				=  1.f;

	// This is utilised by the text size slider widget
	public static final int TEXT_SIZES_TOTAL			= (int) ( (sMaxTextSize - sMinTextSize) / sTextSizeIncrement) + 1;
	public static final float HEADING_TEXT_DIFF		= 9.f;	// Difference between text and heading sizes

	private static float sTextSize			= -1.f;
	private static ThemeType sTheme	= null;


	/**
	 * Factory method that returns an instance of a class that implements the ApplicationTheme interface
	 * @param context
	 * @return
	 */
	public static Theme getThemeInstance(Context context) {
		switch (getTheme(context) ) {
			case LIGHT:
				return LightTheme.getInstance(context);

			case DARK:
				return DarkTheme.getInstance(context);
		}
		return null;
	}


	/**
	 * Checks the current theme in the cache,
	 * caches the other theme (we only have two of them at the moment)
	 * and calls a method that caches it using Android routines.
	 * @param context
	 * @return
	 */
	public static ThemeType switchUserTheme(Context context) {
		switch (getTheme(context) ) {
			case LIGHT:
				setTheme(ThemeType.DARK, context);
				break;
			case DARK:
				setTheme(ThemeType.LIGHT, context);
				break;
		}

		return getTheme(context);
	}


	/**
	 * @return True if the currently selected theme is light (dark text on white background)
	 */
	public static boolean isLightTheme() {
		return sTheme == ThemeType.LIGHT;
	}


	/**
	 * Returns the current theme from the class's cache
	 * or, if cache is empty, reads it from Android preferences file
	 * and adds it to the cache.
	 * @param context
	 * @return
	 */
	public static ThemeType getTheme(Context context) {
		if (sTheme != null) {
			return sTheme;
		}
		sTheme	= convertThemeId(getUserTheme(context) );
		return sTheme;
	}


	/**
	 * Saves new theme ID in the class variable (local cache)
	 * and saves it in the Android preferences file in a separate thread
	 * @param theme
	 * @param context
	 */
	public static void setTheme(final ThemeType theme, final Context context) {
		sTheme	= theme;

		new Thread(new Runnable() {
			public void run() {
				setUserTheme(convertThemeId(theme), context);
			}
		}).start();
	}


	private static int getUserTheme(Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getInt(sSettingTheme, ThemeType.LIGHT.ordinal() );
	}


	private static void setUserTheme(int theme, Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putInt(sSettingTheme, theme);
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
	public static void setTextSize(final float textSize, final Context context) {
		sTextSize = textSize;

		new Thread(new Runnable() {
			public void run() {
				setUserTextSize(textSize, context);
			}
		}).start();
	}


	/**
	 * Opens up the preferences file via PreferenceManager
	 * and reads the user text size value
	 * @param context
	 * @return
	 */
	private static float getUserTextSize(Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getFloat(sSettingTextSize, sDefaultTextSize);
	}


	/**
	 * Opens up the preferences file via PreferenceManager
	 * and saves the new values on main thread
	 * @param size
	 * @param context
	 */
	private static void setUserTextSize(float size, Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat(sSettingTextSize, size);
		prefsEditor.commit();
	}


	/**
	 * Converts text size in float values to points "understood" by the slider widget
	 * @param textSize
	 * @return Text size in points
	 */
	public static int convertTextSize(float textSize) {
		int textSizeInt	= Math.round(textSize);
		int minSize		= Math.round(sMinTextSize);
		int increment		= Math.round(sTextSizeIncrement);

		return (textSizeInt - minSize) / increment;
	}


	/**
	 * Converts text size expressed in point units "understood" by the slider widget
	 * to float values that can be used directly by Android TextView methods
	 * or stored in user preferences files via the Android PreferenceManager.
	 * @param textSize
	 * @return
	 */
	public static float convertTextSize(int textSize) {
		int increment	= Math.round(sTextSizeIncrement);
		int minSize	= Math.round(sMinTextSize);
		return (float) (textSize * increment) + minSize;
	}


	private static int convertThemeId(ThemeType theme) {
		return theme.ordinal();
	}


	private static ThemeType convertThemeId(int theme) {
		for (ThemeType type: ThemeType.values() ) {
			if (type.ordinal() == theme) {
				return type;
			}
		}
		// Default value
		return ThemeType.LIGHT;
	}
}
