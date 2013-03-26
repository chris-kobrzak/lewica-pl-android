package pl.lewica.lewicapl.android.theme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ListView;
import pl.lewica.lewicapl.R;


public class DarkTheme implements ApplicationTheme {

	private static DarkTheme instance;
	private Resources res;


	public static DarkTheme getInstance(Context context) {
		if (instance != null) {
			return instance;
		}

		instance	= new DarkTheme(context);
		return instance;
	}


	private DarkTheme(Context context) {
		res	= context.getResources();
	}


	@Override
	public int getBackgroundColour() {
		return res.getColor(R.color.black);
	}


	public int getListHeadingColour(boolean read) {
		if (read) {
			return res.getColor(R.color.blue_light);
		} else {
			return res.getColor(R.color.red_light);
		}
	}

	@Override
	public int getListTextColour() {
		return res.getColor(R.color.grey);
	}

	private int getListViewDividerColour() {
		return res.getColor(R.color.grey_darker);
	}

	@Override
	public void setListViewDividerColour(ListView listView, Context context) {
		listView.setDivider(new ColorDrawable(getListViewDividerColour() ) );
		listView.setDividerHeight(1);
	}

	@Override
	public int getHeadingColour() {
		return res.getColor(R.color.blue_light);
	}

	@Override
	public int getTextColour() {
		return res.getColor(R.color.grey_light);
	}

	@Override
	public int getEditorsCommentTextColour() {
		return res.getColor(R.color.grey_light);
	}

	@Override
	public Drawable getEditorsCommentBackground() {
		return null;
	}

	@Override
	public int getEditorsCommentBackgroundColour() {
		return res.getColor(R.color.grey_darker);
	}
}
