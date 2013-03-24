package pl.lewica.lewicapl.android.theme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import pl.lewica.lewicapl.R;


public class LightTheme implements ApplicationTheme {

	private static LightTheme instance;
	private Resources res;


	public static LightTheme getInstance(Context context) {
		if (instance != null) {
			return instance;
		}

		instance	= new LightTheme(context);
		return instance;
	}


	private LightTheme(Context context) {
		res	= context.getResources();
	}


	@Override
	public int getBackgroundColour() {
		return res.getColor(android.R.color.transparent);
	}

	@Override
	public int getListHeadingColour(boolean read) {
		if (read) {
			return res.getColor(R.color.read);
		} else {
			return res.getColor(R.color.unread);
		}
	}

	@Override
	public int getListTextColour() {
		return res.getColor(R.color.grey_darker);
	}

	@Override
	public int getListViewDividerColour() {
		return res.getColor(R.color.grey);
	}

	@Override
	public int getHeadingColour() {
		return res.getColor(R.color.read);
	}

	@Override
	public int getTextColour() {
		return res.getColor(R.color.grey_darker);
	}

	@Override
	public int getEditorsCommentTextColour() {
		return res.getColor(R.color.grey_darker);
	}

	@Override
	public Drawable getEditorsCommentBackground() {
		return res.getDrawable(R.drawable.background_comment);
	}

	@Override
	public int getEditorsCommentBackgroundColour() {
		return 0;
	}
}