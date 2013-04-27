package pl.lewica.lewicapl.android.theme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ListView;
import pl.lewica.lewicapl.R;


public class LightTheme implements Theme {

	private static Theme instance;
	private Resources res;


	public static Theme getInstance(Context context) {
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
			return res.getColor(R.color.grey_dark);
		} else {
			return res.getColor(R.color.read);
		}
	}

	@Override
	public int getListTextColour(boolean read) {
		if (read) {
			return res.getColor(R.color.grey_dark);
		} else {
			return res.getColor(R.color.grey_darker);
		}
	}


	private int getListViewDividerColour() {
		return res.getColor(R.color.grey);
	}

	@Override
	public void setListViewDividerColour(ListView listView, Context context) {
		listView.setDivider(new ColorDrawable(getListViewDividerColour() ) );
		listView.setDividerHeight(1);
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