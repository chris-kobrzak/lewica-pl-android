package pl.lewica.lewicapl.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TextSize {

	public static final String USER_SETTING							= "textSizeStandard";
	public static final String USER_SETTING_HEADING			= "textSizeHeading";
	public static final float DEFAULT_TEXT_SIZE					= 16.f;
	public static final float MIN_TEXT_SIZE							=  8.f;
	public static final float MAX_TEXT_SIZE							= 36.f;
	public static final float DEFAULT_TEXT_SIZE_HEADING	= 25.f;
	public static final float MIN_TEXT_SIZE_HEADING			= 17.f;
	public static final float MAX_TEXT_SIZE_HEADING			= 45.f;
	public static final float TEXT_SIZE_INCREMENT				=  1.f;
	public static final int TEXT_SIZES_TOTAL		= (int) ( (MAX_TEXT_SIZE - MIN_TEXT_SIZE) / TEXT_SIZE_INCREMENT) + 1;


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


	public static float getUserTextSize(Activity activity) {
		SharedPreferences prefs	= activity.getPreferences(Context.MODE_PRIVATE);

		return prefs.getFloat(USER_SETTING, DEFAULT_TEXT_SIZE);
	}


	public static void setUserTextSize(float size, Activity activity) {
		SharedPreferences prefs	= activity.getPreferences(Context.MODE_PRIVATE);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat(USER_SETTING, size);
		prefsEditor.commit();
	}


	public static float getUserTextSizeHeading(Activity activity) {
		SharedPreferences prefs	= activity.getPreferences(Context.MODE_PRIVATE);
		
		return prefs.getFloat(USER_SETTING_HEADING, DEFAULT_TEXT_SIZE_HEADING);
	}


	public static void setUserTextSizeHeading(float size, Activity activity) {
		if (size < MIN_TEXT_SIZE_HEADING || size > MAX_TEXT_SIZE_HEADING) {
			return;
		}
		SharedPreferences prefs	= activity.getPreferences(Context.MODE_PRIVATE);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat(USER_SETTING_HEADING, size);
		prefsEditor.commit();
	}
}
