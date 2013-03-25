package pl.lewica.lewicapl.android.theme;

import android.content.Context;


public class Theme {

	public static enum Themes {
		LIGHT,
		DARK
	}

	private static Theme themeInstance;
	private static Context mContext;
	private ApplicationTheme currentTheme;


	public static Theme getInstance(Context context) {
		if (themeInstance != null) {
			return themeInstance;
		}

		themeInstance	= new Theme(context);
		mContext		= context;
		return themeInstance;
	}


	private Theme(Context context) {}


	public ApplicationTheme getCurrentTheme() {
		return currentTheme;
	}


	public void setCurrentTheme(Themes theme) {
		switch (theme) {
			case LIGHT:
				currentTheme	= LightTheme.getInstance(mContext);
				break;

			case DARK:
				currentTheme	= DarkTheme.getInstance(mContext);
				break;
		}
	}
}
