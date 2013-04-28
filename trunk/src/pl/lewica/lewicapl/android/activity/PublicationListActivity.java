/*
 Copyright 2011 lewica.pl

 Licensed under the Apache Licence, Version 2.0 (the "Licence");
 you may not use this file except in compliance with the Licence.
 You may obtain a copy of the Licence at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the Licence is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the Licence for the specific language governing permissions and
 limitations under the Licence. 
*/
package pl.lewica.lewicapl.android.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import pl.lewica.lewicapl.R;
import pl.lewica.api.url.ArticleURL;
import pl.lewica.lewicapl.android.AndroidUtil;
import pl.lewica.lewicapl.android.UserPreferencesManager;
import pl.lewica.lewicapl.android.activity.util.ArticleUtil;
import pl.lewica.lewicapl.android.database.ArticleDAO;
import pl.lewica.lewicapl.android.theme.Theme;


/**
 * @author Krzysztof Kobrzak
 */
public class PublicationListActivity extends Activity {

	public static final String RELOAD_VIEW	= "pl.lewica.lewicapl.android.activity.publicationslistactivity.RELOAD";

	private static Typeface categoryTypeface;
	private ArticleDAO articleDAO;
	private ListAdapter listAdapter;
	private ListView listView;
	private static Theme appTheme;
	// When users select a new article, navigate back to the list and start scrolling up and down, the cursor won't know this article should be marked as read.
	// That results in articles still being marked as unread (titles in red rather than blue).
	// That's why we need to cache the list of clicked articles.  Please note, it is down to ArcticleActivity to flag articles as read in the database.
	private static Set<Long> clicked	= new HashSet<Long>();



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_articles);	// This comes from this file's name /res/list_articles.xml

		// Load a list view container from list_articles.xml
		listView					= (ListView) findViewById(R.id.list_articles);

		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		// Register to receive content update messages
		IntentFilter filter		= new IntentFilter();
		filter.addAction(RELOAD_VIEW);
		BroadcastReceiver receiver		= new PublicationsUpdateBroadcastReceiver();	// Instance of an inner class
		registerReceiver(receiver, filter);

		// Access data
		articleDAO				= new ArticleDAO(this);
		articleDAO.open();
		Cursor cursor			= articleDAO.selectLatestTexts();

		// Set list view adapter - this links the view with the data
		listAdapter				= new PublicationsCursorAdapter(this, cursor, false);
		listView.setAdapter(listAdapter);

		// Clicking on an item should redirect to the details view
		listView.setOnItemClickListener(new ArticlesClickListener() );

		appTheme	= UserPreferencesManager.getThemeInstance(getApplicationContext() );
		appTheme.setListViewDividerColour(listView, this);
	}


	private void reloadRows() {
		CursorAdapter ca	= (CursorAdapter) listAdapter;
		// Reload rows
		Cursor newCursor	= articleDAO.selectLatestTexts();
		ca.changeCursor(newCursor);
	}


	private static void addToClickedItems(Long id) {
		clicked.add(id);
	}


	private static boolean wasItemClicked(Long id) {
		return clicked.contains(id);
	}


	private class ArticlesClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Context context		= getApplicationContext();

			// Redirect to article details screen
			Intent intent	= new Intent(context, ArticleActivity.class);
			// Builds a uri in the following format: content://lewicapl/articles/article/[0-9]+
			Uri uri			= Uri.parse(ArticleActivity.URI_BASE + Long.toString(id) );
			// Passes activity Uri as parameter that can be used to work out ID of requested article.
			intent.setData(uri);
			startActivity(intent);

			// Mark current article as read by changing its colour
			appTheme	= UserPreferencesManager.getThemeInstance(context);
			TextView tv		= (TextView) view.findViewById(R.id.article_item_title);
			tv.setTextColor(appTheme.getListHeadingColour(true) );
			addToClickedItems(id);

			return;
		}
	}



	// INNER CLASSES
	private class PublicationsUpdateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			appTheme	= UserPreferencesManager.getThemeInstance(getApplicationContext() );
			reloadRows();
			appTheme.setListViewDividerColour(listView, context);
		}
	}



	/**
	 * Populates list items, adds category headings, shows/hides images.
	 * It is static nested class, see http://download.oracle.com/javase/tutorial/java/javaOO/nested.html
	 * @author Krzysztof Kobrzak
	 */
	private static final class PublicationsCursorAdapter extends CursorAdapter {
		// We have two list item view types
		private static final int VIEW_TYPE_GROUP_START			= 0;
		private static final int VIEW_TYPE_GROUP_CONTINUE	= 1;
		private static final int VIEW_TYPE_COUNT					= 3;

		private LayoutInflater inflater;

		private int inxID;
		private int inxCategoryID;
		private int inxTitle;
		private int inxDatePub;
		private int inxWasRead;
		private int inxHasThumb;
		private int inxThumbExt;

		private static SimpleDateFormat dateFormat	= new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);


		PublicationsCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
			super(context, cursor, autoRequery);

			// Get the layout inflater
			inflater				= LayoutInflater.from(context);

			// Get and cache column indices
			inxID					= cursor.getColumnIndex(ArticleDAO.FIELD_ID);
			inxCategoryID		= cursor.getColumnIndex(ArticleDAO.FIELD_CATEGORY_ID);
			inxTitle				= cursor.getColumnIndex(ArticleDAO.FIELD_TITLE);
			inxDatePub			= cursor.getColumnIndex(ArticleDAO.FIELD_DATE_PUBLISHED);
			inxWasRead			= cursor.getColumnIndex(ArticleDAO.FIELD_WAS_READ);
			inxHasThumb		= cursor.getColumnIndex(ArticleDAO.FIELD_HAS_IMAGE);
			inxThumbExt		= cursor.getColumnIndex(ArticleDAO.FIELD_IMAGE_EXTENSION);
		}

		/**
		 * Responsible for providing views with content and formatting it.
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			appTheme	= UserPreferencesManager.getThemeInstance(context);

			TextView tvTitle	= (TextView) view.findViewById(R.id.article_item_title);
			tvTitle.setText(cursor.getString(inxTitle) );

			Date d				= new Date(cursor.getLong(inxDatePub) );	// Dates are stored as Unix timestamps
			TextView tvDate	= (TextView) view.findViewById(R.id.article_item_date);
			tvDate.setText(dateFormat.format(d) );

			loadImage(cursor, view);

			// If there is a group header, set their values
			loadCategoryLabel(cursor.getInt(inxCategoryID), view, context);

			// Publications do not have editor's comments so no need for the pencil icon here
			ImageView iv	= (ImageView) view.findViewById(R.id.ico_pencil);
			iv.setVisibility(View.GONE);

			boolean unread	= cursor.getInt(inxWasRead) == 0 && ! wasItemClicked(cursor.getLong(inxID) );
			loadTheme(! unread, view, tvTitle, tvDate);
		}

		@Override
		public int getViewTypeCount() {
			return VIEW_TYPE_COUNT;
		}

		@Override
		public int getItemViewType(int position) {
			// There is always a group header for the first data item
			if (position == 0) {
				return VIEW_TYPE_GROUP_START;
			}

			// For other items, decide based on current data
			Cursor cursor	= getCursor();
			cursor.moveToPosition(position);

			// Check item grouping
			if (! isNewGroup(cursor, position) ) {
				return VIEW_TYPE_GROUP_CONTINUE;
			}

			return VIEW_TYPE_GROUP_START;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			int position = cursor.getPosition();
			int nViewType;

			if (position > 0) {
				// For other positions, decide based on data
				if (isNewGroup(cursor, position) ) {
					nViewType	= VIEW_TYPE_GROUP_START;
				} else {
					nViewType	= VIEW_TYPE_GROUP_CONTINUE;
				}
			} else {
				// Group header for position 0
				nViewType		= VIEW_TYPE_GROUP_START;
			}

			View v;

			if (nViewType != VIEW_TYPE_GROUP_START) {
				// Inflate a layout for "regular" items
				v	= inflater.inflate(R.layout.list_articles_item, parent, false);
				return v; 
			}

			// Else, inflate the layout to start a new group
			v	= inflater.inflate(R.layout.list_articles_item_with_heading, parent, false);

			// Ignore clicks on the list header
			View vHeader	= v.findViewById(R.id.article_items_heading);
			vHeader.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {}
			});
			return v;
		}

		/**
		 * Determines whether the current data item (at the current position
		 * within the cursor) starts a new group, based on the group column.
		 *
		 * @param cursor
		 *            SQLite database cursor, the current data item position is
		 *            assumed to have been set by the caller
		 * @param position
		 *            The current data item's position within the cursor
		 * @return True if the current data item starts a new group
		 */
		private boolean isNewGroup(Cursor cursor, int position) {
			if (position == 0) {
				return false;
			}
			// Get date values for current and previous data items
			int categoryID		= cursor.getInt(inxCategoryID);
			cursor.moveToPosition(position - 1);
			int categoryIDPrev	= cursor.getInt(inxCategoryID);
			// Restore cursor position
			cursor.moveToPosition(position);
			
			if (categoryID == categoryIDPrev) {
				return false;
			}
			return true;
		}


		/**
		 * @param view
		 * @param context
		 * @param cursor
		 */
		private void loadCategoryLabel(int categoryId, View view, Context context) {
			TextView tv	= (TextView) view.findViewById(R.id.article_items_heading);
			if (tv == null) {
				return;
			}

			tv.setTypeface(categoryTypeface);
			tv.setText(ArticleUtil.getCategoryLabel(categoryId, context) );
		}


		/**
		 * @param view
		 * @param cursor
		 */
		private void loadImage(Cursor cursor, View view) {
			ImageView iv	= (ImageView) view.findViewById(R.id.article_item_icon);
			iv.setImageBitmap(null);
			iv.setVisibility(View.INVISIBLE);
			if (cursor.getInt(inxHasThumb) == 0) {
				return;
			}
			String imgPath	= AndroidUtil.getStorageDir().getPath() + "/" + ArticleURL.buildNameThumbnail(cursor.getLong(inxID), cursor.getString(inxThumbExt) );
			File img	= new File(imgPath);
			if (! img.exists() ) {
				return;
			}
			Bitmap bMap		= BitmapFactory.decodeFile(imgPath);
			iv.setImageBitmap(bMap);
			iv.setVisibility(View.VISIBLE);
		}


		/**
		 * @param read
		 * @param view
		 * @param tvTitle
		 * @param tvDate
		 * @param tvBlog
		 */
		private void loadTheme(boolean read, View view, TextView tvTitle, TextView tvDate) {
			tvTitle.setTextColor(appTheme.getListHeadingColour(read) );
			tvDate.setTextColor(appTheme.getListTextColour(read) );
			view.setBackgroundColor(appTheme.getBackgroundColour() );
		}
	}
	// End of NewsCursorAdapter
}
