package pl.lewica.lewicapl.android.theme;

import android.graphics.drawable.Drawable;


public interface ApplicationTheme {

	public int getBackgroundColour();

	public int getListHeadingColour(boolean read);

	public int getListTextColour();

	public int getListViewDividerColour();

	public int getHeadingColour();

	public int getTextColour();

	public int getEditorsCommentTextColour();

	public int getEditorsCommentBackgroundColour();

	public Drawable getEditorsCommentBackground();
}