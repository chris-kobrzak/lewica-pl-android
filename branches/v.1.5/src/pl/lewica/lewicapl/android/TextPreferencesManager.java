package pl.lewica.lewicapl.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class TextPreferencesManager {

	public static final String USER_SETTING_THEME		= "colourTheme";
	public static final int THEME_WHITE_ON_BLACK		= 1;
	public static final int THEME_BLACK_ON_WHITE		= 2;

	public static final String USER_SETTING_TEXT_SIZE		= "textSizeStandard";
	public static final String USER_SETTING_TEXT_SIZE_HEADING		= "textSizeHeading";
	public static final float DEFAULT_TEXT_SIZE					= 16.f;
	public static final float MIN_TEXT_SIZE							=  8.f;
	public static final float MAX_TEXT_SIZE							= 36.f;
	public static final float DEFAULT_TEXT_SIZE_HEADING	= 25.f;
	public static final float MIN_TEXT_SIZE_HEADING			= 17.f;
	public static final float MAX_TEXT_SIZE_HEADING			= 45.f;
	public static final float TEXT_SIZE_INCREMENT				=  1.f;
	public static final int TEXT_SIZES_TOTAL		= (int) ( (MAX_TEXT_SIZE - MIN_TEXT_SIZE) / TEXT_SIZE_INCREMENT) + 1;


	public interface ThemeHandler {
		public void setThemeDark();

		public void setThemeLight();
	}


	public static void switchTheme(ThemeHandler themeHandler, Context context) {
		switch (getUserTheme(context) ) {
			case THEME_BLACK_ON_WHITE:
				themeHandler.setThemeDark();

				setUserTheme(THEME_WHITE_ON_BLACK, context);
				break;

			case THEME_WHITE_ON_BLACK:
				themeHandler.setThemeLight();

				setUserTheme(THEME_BLACK_ON_WHITE, context);
				break;
		}
	}


	public static void loadTheme(ThemeHandler themeHandler, Context context) {
		switch (getUserTheme(context) ) {
			case THEME_BLACK_ON_WHITE:
				themeHandler.setThemeLight();
				break;
				
			case THEME_WHITE_ON_BLACK:
				themeHandler.setThemeDark();
				break;
		}
	}


	public static int getUserTheme(Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getInt(USER_SETTING_THEME, THEME_BLACK_ON_WHITE);
	}


	private static void setUserTheme(int theme, Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putInt(USER_SETTING_THEME, theme);
		prefsEditor.commit();
	}


	public static int convertTextSizeToPoint(float textSize) {
		int textSizeInt	= Math.round(textSize);
		int minSize		= Math.round(MIN_TEXT_SIZE);
		int increment		= Math.round(TEXT_SIZE_INCREMENT);

		return (textSizeInt - minSize) / increment;
	}


	public static float convertTextSizeToFloat(int textSize) {
		int increment	= Math.round(TEXT_SIZE_INCREMENT);
		int minSize	= Math.round(MIN_TEXT_SIZE);
		return (float) (textSize * increment) + minSize;
	}


	public static float getUserTextSize(Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getFloat(USER_SETTING_TEXT_SIZE, DEFAULT_TEXT_SIZE);
	}


	public static void setUserTextSize(float size, Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat(USER_SETTING_TEXT_SIZE, size);
		prefsEditor.commit();
	}


	public static float getUserTextSizeHeading(Context context) {
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);
		
		return prefs.getFloat(USER_SETTING_TEXT_SIZE_HEADING, DEFAULT_TEXT_SIZE_HEADING);
	}


	public static void setUserTextSizeHeading(float size, Context context) {
		if (size < MIN_TEXT_SIZE_HEADING || size > MAX_TEXT_SIZE_HEADING) {
			return;
		}
		SharedPreferences prefs	= PreferenceManager.getDefaultSharedPreferences(context);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat(USER_SETTING_TEXT_SIZE_HEADING, size);
		prefsEditor.commit();
	}
}
