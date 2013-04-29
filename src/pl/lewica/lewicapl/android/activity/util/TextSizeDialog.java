package pl.lewica.lewicapl.android.activity.util;

import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.DialogManager;
import pl.lewica.lewicapl.android.SliderDialog;
import pl.lewica.lewicapl.android.UserPreferencesManager;
import android.app.Activity;
import android.widget.SeekBar;


public class TextSizeDialog {

	/**
	 * @param listener
	 * @param activity
	 */
	public static void showDefaultTextSizeWidget(SeekBar.OnSeekBarChangeListener listener, Activity activity) {
		int sizeInPoints	= UserPreferencesManager.convertTextSize(UserPreferencesManager.getTextSize(activity) );
		SliderDialog sd	= new SliderDialog();
		sd.setSliderValue(sizeInPoints);
		sd.setSliderMax(UserPreferencesManager.TEXT_SIZES_TOTAL);
		sd.setTitleResource(R.string.heading_change_text_size);
		sd.setOkButtonResource(R.string.ok);

		DialogManager.showDialogWithSlider(sd, activity, listener);
	}
}
