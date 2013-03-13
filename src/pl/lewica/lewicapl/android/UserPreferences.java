package pl.lewica.lewicapl.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPreferences {

	public static final float DEFAULT_FONT_SIZE_HEADING	= 24.f;
	public static final float DEFAULT_FONT_SIZE_STANDARD	= 15.f;
	public static final float MIN_FONT_SIZE_HEADING			= 12.f;
	public static final float MIN_FONT_SIZE_STANDARD		= 10.f;
	public static final float MAX_FONT_SIZE_HEADING			= 44.f;
	public static final float MAX_FONT_SIZE_STANDARD		= 32.f;

	public enum TextSizeAction	{
		INCREASE,
		DECREASE
	};

	public static void changeUserTextSize(TextSizeAction action, Activity activity) {
		float textSize;
		float change	= 4.f;

		if (action == TextSizeAction.DECREASE) {
			change	= -4.f;
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

		return prefs.getFloat("textSizeStandard", DEFAULT_FONT_SIZE_STANDARD);
	}


	private static void setUserTextSizeStandard(float size, Activity activity) {
		SharedPreferences prefs	= activity.getPreferences(Context.MODE_PRIVATE);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat("textSizeStandard", size);
		prefsEditor.commit();
	}


	public static float getUserTextSizeHeading(Activity activity) {
		SharedPreferences prefs	= activity.getPreferences(Context.MODE_PRIVATE);
		
		return prefs.getFloat("textSizeHeading", DEFAULT_FONT_SIZE_HEADING);
	}


	private static void setUserTextSizeHeading(float size, Activity activity) {
		if (size < MIN_FONT_SIZE_HEADING || size > MAX_FONT_SIZE_HEADING) {
			return;
		}
		SharedPreferences prefs	= activity.getPreferences(Context.MODE_PRIVATE);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat("textSizeHeading", size);
		prefsEditor.commit();
	}
	
	
	public static boolean canIncreaseTextSize(Activity activity) {
		float size	= getUserTextSizeStandard(activity);
		return size <= MAX_FONT_SIZE_STANDARD;
	}


	public static boolean canDecreaseTextSize(Activity activity) {
		float size	= getUserTextSizeStandard(activity);
		return size >= MIN_FONT_SIZE_STANDARD;
	}
}
