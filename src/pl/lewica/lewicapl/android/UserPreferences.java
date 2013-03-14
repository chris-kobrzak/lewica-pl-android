package pl.lewica.lewicapl.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPreferences {

	public static final float DEFAULT_TEXT_SIZE_HEADING	= 25.f;
	public static final float DEFAULT_TEXT_SIZE_STANDARD	= 16.f;
	public static final float MIN_TEXT_SIZE_HEADING			= 17.f;
	public static final float MIN_TEXT_SIZE_STANDARD			=  8.f;
	public static final float MAX_TEXT_SIZE_HEADING			= 45.f;
	public static final float MAX_TEXT_SIZE_STANDARD		= 36.f;
	public static final float TEXT_SIZE_INCREMENT				=  2.f;
	public static final int TEXT_SIZES_TOTAL		= (int) ( (MAX_TEXT_SIZE_STANDARD - MIN_TEXT_SIZE_STANDARD) / TEXT_SIZE_INCREMENT) + 1;
	

	public enum TextSizeAction	{
		INCREASE,
		DECREASE
	};

	public static void changeUserTextSize(TextSizeAction action, Activity activity) {
		float textSize;
		float change	= TEXT_SIZE_INCREMENT;

		if (action == TextSizeAction.DECREASE) {
			change	= -TEXT_SIZE_INCREMENT;
		}
		textSize		= getUserTextSizeHeading(activity);
		textSize	+= change;
		setUserTextSizeHeading(textSize, activity);

		textSize		= getUserTextSizeStandard(activity);
		textSize	+= change;
		setUserTextSizeStandard(textSize, activity);
	}


	public static float getUserTextSizeStandard(Activity activity) {
		SharedPreferences prefs	= activity.getPreferences(Context.MODE_PRIVATE);

		return prefs.getFloat("textSizeStandard", DEFAULT_TEXT_SIZE_STANDARD);
	}


	public static void setUserTextSizeStandard(float size, Activity activity) {
		SharedPreferences prefs	= activity.getPreferences(Context.MODE_PRIVATE);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat("textSizeStandard", size);
		prefsEditor.commit();
	}


	public static float getUserTextSizeHeading(Activity activity) {
		SharedPreferences prefs	= activity.getPreferences(Context.MODE_PRIVATE);
		
		return prefs.getFloat("textSizeHeading", DEFAULT_TEXT_SIZE_HEADING);
	}


	public static void setUserTextSizeHeading(float size, Activity activity) {
		if (size < MIN_TEXT_SIZE_HEADING || size > MAX_TEXT_SIZE_HEADING) {
			return;
		}
		SharedPreferences prefs	= activity.getPreferences(Context.MODE_PRIVATE);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat("textSizeHeading", size);
		prefsEditor.commit();
	}
	
	
	public static boolean canIncreaseTextSize(Activity activity) {
		float size	= getUserTextSizeStandard(activity);
		return size <= MAX_TEXT_SIZE_STANDARD;
	}


	public static boolean canDecreaseTextSize(Activity activity) {
		float size	= getUserTextSizeStandard(activity);
		return size >= MIN_TEXT_SIZE_STANDARD;
	}
}
