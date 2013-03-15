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


public class DialogHandler {

	public interface TextSizeSliderEventHandler {
		public void updateTextSize(int points);
	}


	public static void showDialogWithTextSizeSlider(int sliderValue, int sliderMax, Activity activity, final TextSizeSliderEventHandler sliderProgressDelegate) {
		LayoutInflater inflater		= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout					= inflater.inflate(R.layout.dialog_text_size, (ViewGroup) activity.findViewById(R.id.dialog_text_size_root) );
		AlertDialog.Builder builder	= new AlertDialog.Builder(activity).setView(layout);

		builder.setTitle(R.string.heading_change_text_size);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {}
		});

		AlertDialog dialog	= builder.create();
		SeekBar slider		= (SeekBar)layout.findViewById(R.id.dialog_text_size_seekbar);
		slider.setMax(sliderMax);
		slider.setProgress(sliderValue);
		slider.setPadding(50, 20, 50, 20);
		slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				sliderProgressDelegate.updateTextSize(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {}
		});
		dialog.show();
	}
}
