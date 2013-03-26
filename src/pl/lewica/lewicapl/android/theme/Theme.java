package pl.lewica.lewicapl.android.theme;

import pl.lewica.lewicapl.android.UserPreferencesManager;
import android.content.Context;


public class Theme {

	public static final int THEME_LIGHT		= 1;
	public static final int THEME_DARK		= 2;

	private static int currentTheme	= -1;


	/**
	 * Factory method that returns an instance of a class that implements the ApplicationTheme interface
	 * @param context
	 * @return
	 */
	public static ApplicationTheme getTheme(Context context) {
		if (currentTheme == -1) {
			setCurrentTheme(UserPreferencesManager.getUserTheme(context) );
		}
		switch (currentTheme) {
			case THEME_LIGHT:
				return LightTheme.getInstance(context);

			case THEME_DARK:
				return DarkTheme.getInstance(context);
		}
		return null;
	}


	public static int getCurrentTheme(Context context) {
		if (currentTheme > -1) {
			return currentTheme;
		}
		setCurrentTheme(UserPreferencesManager.getUserTheme(context) );
		return currentTheme;
	}


	public static void setCurrentTheme(int theme) {
		currentTheme	= theme;
	}
}