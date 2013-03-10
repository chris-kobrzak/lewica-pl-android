package pl.lewica.lewicapl.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPreferences {

	public enum TextSizeAction	{
		INCREASE,
		DECREASE
	};

	public static void changeUserTextSize(TextSizeAction action, Activity activity) {
		float textSize;
		float change	= 2.5f;	// increase by two points

		if (action == TextSizeAction.DECREASE) {
			change	= -2.5f;
		}
		textSize		= getUserTextSizeHeading(activity);
		textSize	+= change;
		setUserTextSizeHeading(textSize, activity);

		textSize		= getUserTextSizeStandard(activity);
		textSize	+= change;
		setUserTextSizeStandard(textSize, activity);
	}


	public static float getUserTextSizeStandard(Activity activity) {
		SharedPreferences prefs	= activity.getPreferences(activity.MODE_PRIVATE);

		return prefs.getFloat("textSizeStandard", 15.f);
	}


	private static void setUserTextSizeStandard(float textSizeStandard, Activity activity) {
		if (textSizeStandard < 8.f) {
			return;
		}
		SharedPreferences prefs	= activity.getPreferences(activity.MODE_PRIVATE);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat("textSizeStandard", textSizeStandard);
		prefsEditor.commit();
	}


	public static float getUserTextSizeHeading(Activity activity) {
		SharedPreferences prefs	= activity.getPreferences(activity.MODE_PRIVATE);
		
		return prefs.getFloat("textSizeHeading", 22.f);
	}
	
	
	private static void setUserTextSizeHeading(float textSizeHeading, Activity activity) {
		if (textSizeHeading < 10.f) {
			return;
		}
		SharedPreferences prefs	= activity.getPreferences(activity.MODE_PRIVATE);
		Editor prefsEditor			= prefs.edit();
		prefsEditor.putFloat("textSizeHeading", textSizeHeading);
		prefsEditor.commit();
	}
}
