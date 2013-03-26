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
import android.os.Environment;
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
import pl.lewica.api.model.Article;
import pl.lewica.api.url.ArticleURL;
import pl.lewica.lewicapl.android.database.ArticleDAO;
import pl.lewica.lewicapl.android.theme.ApplicationTheme;
import pl.lewica.lewicapl.android.theme.Theme;


/**
 * @author Krzysztof Kobrzak
 */
public class NewsListActivity extends Activity {

	public static final String RELOAD_VIEW	= "pl.lewica.lewicapl.android.activity.newslistactivity.RELOAD";

	private static Typeface categoryTypeface;
	private static File storageDir;
	private ArticleDAO articleDAO;
	private ListAdapter listAdapter;
	private ListView listView;
	private BroadcastReceiver receiver;
	private static ApplicationTheme appTheme;
	// When users select a new article, navigate back to the list and start scrolling up and down, the cursor won't know this article should be marked as read.
	// That results in articles still being marked as unread (titles in red rather than blue).
	// That's why we need to cache the list of clicked articles.  Please note, it is down to ArcticleActivity to flag articles as read in the database.
	private static Set<Long> clicked	= new HashSet<Long>();
//	private SharedPreferences.OnSharedPreferenceChangeListener prefsListener;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_articles);	// This comes from this file's name /res/list_articles.xml

		// Load a list view container from list_articles.xml
		listView					= (ListView) findViewById(R.id.list_articles);

		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		// Thumbnails store
		File sdDir				= Environment.getExternalStorageDirectory();
		storageDir				= new File(sdDir + getResources().getString(R.string.path_images) );
		storageDir.mkdirs();

		// Register to receive content update messages
		IntentFilter filter		= new IntentFilter();
		filter.addAction(RELOAD_VIEW);
		receiver					= new NewsUpdateBroadcastReceiver();	// Instance of an inner class
		registerReceiver(receiver, filter);

		// Access data
		articleDAO				= new ArticleDAO(this);
		articleDAO.open();
		Cursor cursor			= articleDAO.selectLatestNews();

		// Set list view adapter - this links the view with the data
		listAdapter				= new NewsCursorAdapter(this, cursor, false);
		listView.setAdapter(listAdapter);

		// Clicking on an item should redirect to the details view
		listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Context context		= getApplicationContext();

				// Redirect to article details screen
				Intent intent	= new Intent(context, ArticleActivity.class);
				// Builds a uri in the following format: content://lewicapl/articles/article/[0-9]+
				Uri uri			= Uri.parse(ArticleActivity.URI_BASE + Long.toString(id) );
				// Passes activity Uri as parameter that can be used to work out ID of requested article.
				intent.setData(uri);
				startActivity(intent);

				// Mark current article as read by changing its colour...
				TextView tv		= (TextView) view.findViewById(R.id.article_item_title);
				appTheme	= Theme.getTheme(context);
				tv.setTextColor(appTheme.getListHeadingColour(true) );
				// ... and flagging it in the database accordingly
				clicked.add(id);

				return;
			}
		});

		appTheme	= Theme.getTheme(getApplicationContext() );
		appTheme.setListViewDividerColour(listView, this);
	}


	public void reloadRows() {
		CursorAdapter ca	= (CursorAdapter) listAdapter;
		// Reload rows
		Cursor newCursor	= articleDAO.selectLatestNews();
		ca.changeCursor(newCursor);
	}


	// INNER CLASSES
	public class NewsUpdateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			appTheme	= Theme.getTheme(getApplicationContext() );
			reloadRows();
			appTheme.setListViewDividerColour(listView, context);
		}
	}



	/**
	 * Populates list items, adds category headings, shows/hides images.
	 * It is static nested class, see http://download.oracle.com/javase/tutorial/java/javaOO/nested.html
	 * @author Krzysztof Kobrzak
	 *
	 */
	private static final class NewsCursorAdapter extends CursorAdapter {
		// We have two list item view types
		private static final int VIEW_TYPE_GROUP_START			= 0;
		private static final int VIEW_TYPE_GROUP_CONTINUE	= 1;
		private static final int VIEW_TYPE_COUNT					= 2;
		
		public LayoutInflater inflater;

		private int colIndex_ID;
		private int colIndex_CategoryID;
		private int colIndex_Title;
		private int colIndex_DatePub;
		private int colIndex_WasRead;
		private int colIndex_HasComment;
		private int colIndex_HasThumb;
		private int colIndex_ThumbExt;

		private static SimpleDateFormat dateFormat	= new SimpleDateFormat("dd/MM/yyyy HH:mm");


		NewsCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
			super(context, cursor, autoRequery);

			// Get the layout inflater
			inflater				= LayoutInflater.from(context);

			// Get and cache column indices
			colIndex_ID					= cursor.getColumnIndex(ArticleDAO.FIELD_ID);
			colIndex_CategoryID		= cursor.getColumnIndex(ArticleDAO.FIELD_CATEGORY_ID);
			colIndex_Title				= cursor.getColumnIndex(ArticleDAO.FIELD_TITLE);
			colIndex_DatePub			= cursor.getColumnIndex(ArticleDAO.FIELD_DATE_PUBLISHED);
			colIndex_WasRead			= cursor.getColumnIndex(ArticleDAO.FIELD_WAS_READ);
			colIndex_HasComment	= cursor.getColumnIndex(ArticleDAO.FIELD_HAS_EDITOR_COMMENT);
			colIndex_HasThumb		= cursor.getColumnIndex(ArticleDAO.FIELD_HAS_IMAGE);
			colIndex_ThumbExt		= cursor.getColumnIndex(ArticleDAO.FIELD_IMAGE_EXTENSION);
		}

		/**
		 * Responsible for providing views with content and formatting it.
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			appTheme	= Theme.getTheme(context);

			// Editor's comments icon
			int hasComment	= cursor.getInt(colIndex_HasComment);
			ImageView iv	= (ImageView) view.findViewById(R.id.ico_pencil);
			if (hasComment == 0) {
				iv.setVisibility(View.INVISIBLE);
			} else {
				iv.setVisibility(View.VISIBLE);
			}
			// Title
			boolean unread	= cursor.getInt(colIndex_WasRead) == 0 && ! clicked.contains(cursor.getLong(colIndex_ID) );
			TextView tvTitle	= (TextView) view.findViewById(R.id.article_item_title);
			tvTitle.setTextColor(appTheme.getListHeadingColour(! unread) );
			tvTitle.setText(cursor.getString(colIndex_Title) );
			// Datetime
			TextView tvDate	= (TextView) view.findViewById(R.id.article_item_date);
			long unixTime	= cursor.getLong(colIndex_DatePub);	// Dates are stored as Unix timestamps
			Date d				= new Date(unixTime);
			tvDate.setText(dateFormat.format(d) );

			// Thumbnail
			iv	= (ImageView) view.findViewById(R.id.article_item_icon);
			iv.setImageBitmap(null);
			if (cursor.getInt(colIndex_HasThumb) == 0) {
				iv.setVisibility(View.INVISIBLE);
			} else {
				String imgPath	= storageDir.getPath() + "/" + ArticleURL.buildNameThumbnail(cursor.getLong(colIndex_ID), cursor.getString(colIndex_ThumbExt) );
				Bitmap bMap		= BitmapFactory.decodeFile(imgPath);
				iv.setImageBitmap(bMap);
				// Reset image to avoid issues when navigating between previous and next articles
				iv.setVisibility(View.VISIBLE);
			}

			// If there is a group header, set their values
			TextView tv	= (TextView) view.findViewById(R.id.article_items_heading);
			if (tv != null) {
				tv.setTypeface(categoryTypeface);

				switch (cursor.getInt(colIndex_CategoryID) ) {
					case Article.SECTION_POLAND:
						tv.setText(context.getString(R.string.heading_poland) );
						break;
						
					case Article.SECTION_WORLD:
						tv.setText(context.getString(R.string.heading_world) );
						break;
				}
			}

			tvDate.setTextColor(appTheme.getListTextColour() );
			view.setBackgroundColor(appTheme.getBackgroundColour() );
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
			if (isNewGroup(cursor, position) ) {
				return VIEW_TYPE_GROUP_START;
			} else {
				return VIEW_TYPE_GROUP_CONTINUE;
			}
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
//				@Override
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
			int categoryID		= cursor.getInt(colIndex_CategoryID);
			cursor.moveToPosition(position - 1);
			int categoryIDPrev	= cursor.getInt(colIndex_CategoryID);
			// Restore cursor position
			cursor.moveToPosition(position);
			
			if (categoryID == categoryIDPrev) {
				return false;
			} else {
				return true;
			}
		}
	}
	// End of NewsCursorAdapter
}