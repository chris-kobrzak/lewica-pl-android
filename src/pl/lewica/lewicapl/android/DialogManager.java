package pl.lewica.lewicapl.android;

import pl.lewica.lewicapl.R;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;


/**
 * Set of utilities facilitating standard Android dialogs and pop-up windows
 * @author Krzysztof Kobrzak
 */
public class DialogManager {

	/**
	 * Displays a standard dialog box with the slider widget and one button.
	 * User sliding events handling is delegated to the sliderProgressDelegate object
	 * passed to this method.
	 * @param slider
	 * @param activityContext
	 * @param seekBarChangeListener
	 */
	public static void showDialogWithSlider(SliderDialog slider, Activity activityContext, final SeekBar.OnSeekBarChangeListener seekBarChangeListener) {
		View layout	= getDialogLayout(activityContext);

		configureSlider(layout, slider, seekBarChangeListener);

		AlertDialog dialog	= buildAlertDialogWithOneButton(slider, layout, activityContext);

		dialog.show();
	}


	private static void configureSlider(View layout, SliderDialog slider, final SeekBar.OnSeekBarChangeListener seekBarChangeListener) {
		SeekBar sb		= (SeekBar) layout.findViewById(R.id.dialog_slider);
		sb.setMax(slider.getSliderMax() );
		sb.setProgress(slider.getSliderValue() );
		sb.setPadding(90, 20, 90, 20);
		sb.setOnSeekBarChangeListener(seekBarChangeListener);
	}


	private static View getDialogLayout(Activity activity) {
		LayoutInflater inflater	= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.dialog_slider, (ViewGroup) activity.findViewById(R.id.dialog_slider_layout) );
	}


	private static AlertDialog buildAlertDialogWithOneButton(SliderDialog slider, View layout, Activity activity) {
		AlertDialog.Builder builder	= new AlertDialog.Builder(activity).setView(layout);
		builder.setTitle(slider.getTitleResource() );
		builder.setPositiveButton(slider.getOkButtonResource(), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {}
		});

		return builder.create();
	}
}
