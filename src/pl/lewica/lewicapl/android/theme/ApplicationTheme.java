package pl.lewica.lewicapl.android.theme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ListView;


public interface ApplicationTheme {

	public int getBackgroundColour();

	public int getListHeadingColour(boolean read);

	public int getListTextColour();

	public void setListViewDividerColour(ListView listView, Context context);

	public int getHeadingColour();

	public int getTextColour();

	public int getEditorsCommentTextColour();

	public int getEditorsCommentBackgroundColour();

	public Drawable getEditorsCommentBackground();
}