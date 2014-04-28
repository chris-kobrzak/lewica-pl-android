package pl.lewica.lewicapl.android.activity.util;

import android.content.Intent;

public class MessageSharingChooser {

	public static Intent getIntent(CharSequence heading, CharSequence message) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, message);
		return Intent.createChooser(intent, heading);
	}
}
