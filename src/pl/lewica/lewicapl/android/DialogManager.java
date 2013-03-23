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


public class DialogManager {


	public interface SliderEventHandler {
		public void changeValue(int points);
	}


	public static void showDialogWithSlider(SliderDialog slider, Activity activityContext, final SliderEventHandler sliderProgressDelegate) {
		View layout	= getDialogLayout(activityContext);

		configureSlider(layout, slider, sliderProgressDelegate);

		AlertDialog dialog	= buildAlertDialogWithOneButton(slider, layout, activityContext);

		dialog.show();
	}


	private static void configureSlider(View layout, SliderDialog slider, final SliderEventHandler sliderProgressDelegate) {
		SeekBar sb		= (SeekBar) layout.findViewById(R.id.dialog_slider);
		sb.setMax(slider.getSliderMax() );
		sb.setProgress(slider.getSliderValue() );
		sb.setPadding(90, 20, 90, 20);
		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				sliderProgressDelegate.changeValue(progress);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {}
		});
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
