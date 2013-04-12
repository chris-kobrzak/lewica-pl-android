package pl.lewica.lewicapl.android.activity;

import pl.lewica.lewicapl.android.UserPreferencesManager;
import android.app.Activity;
import android.widget.SeekBar;


class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

	private Activity mActivity;
	private StandardTextScreen mDetailsPage;

	public SeekBarChangeListener(Activity activity, StandardTextScreen detailsPage) {
		mActivity		= activity;
		mDetailsPage	= detailsPage;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		float textSize		= UserPreferencesManager.convertTextSize(progress);

		mDetailsPage.loadTextSize(textSize);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		float textSize		= UserPreferencesManager.convertTextSize(seekBar.getProgress() );

		UserPreferencesManager.setTextSize(textSize, mActivity);
	}
}