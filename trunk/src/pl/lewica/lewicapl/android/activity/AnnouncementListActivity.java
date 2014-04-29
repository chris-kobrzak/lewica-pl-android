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

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.UserPreferencesManager;
import pl.lewica.lewicapl.android.database.AnnouncementDAO;
import pl.lewica.lewicapl.android.database.BaseTextDAO;
import pl.lewica.lewicapl.android.theme.Theme;


/**
 * @author Krzysztof Kobrzak
 */
public class AnnouncementListActivity extends Activity {

	public static final String RELOAD_VIEW	= "pl.lewica.lewicapl.android.activity.announcementslistactivity.RELOAD";

	// When users select a new article, navigate back to the list and start scrolling up and down, the cursor won't know this article should be marked as read.
	// That results in articles still being marked as unread (titles in red rather than blue).
	// That's why we need to cache the list of clicked articles.  Please note, it is down to ArcticleActivity to flag articles as read in the database.
	private static Set<Long> clicked	= new HashSet<Long>();

	private BaseTextDAO annDAO;
	private ListAdapter listAdapter;
	private ListView listView;
	private static Theme appTheme;

	private int limitRows		= 15;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_announcements);	// This comes from this file's name /res/list_announcements.xml

		// Load a list view container from list_announcements.xml
		listView					= (ListView) findViewById(R.id.list_announcements);

		// Register to receive content update messages
		IntentFilter filter		= new IntentFilter();
		filter.addAction(RELOAD_VIEW);
		AnnouncementsUpdateBroadcastReceiver receiver = new AnnouncementsUpdateBroadcastReceiver();	// Instance of an inner class
		registerReceiver(receiver, filter);

		// Access data
		annDAO					= new AnnouncementDAO(this);
		annDAO.open();
		Cursor cursor			= annDAO.selectLatest(limitRows);

		// Set list view adapter - this links the view with the data
		listAdapter				= new AnnouncementsCursorAdapter(this, cursor, false);
		listView.setAdapter(listAdapter);

		// Clicking on an item should redirect to the details view
		listView.setOnItemClickListener(new AnnouncementClickListener() );

		appTheme	= UserPreferencesManager.getThemeInstance(getApplicationContext() );
		appTheme.setListViewDividerColour(listView, this);
	}


	private void reloadRows() {
		CursorAdapter ca	= (CursorAdapter) listAdapter;
		// Reload rows
		Cursor newCursor	= annDAO.selectLatest(limitRows);
		ca.changeCursor(newCursor);
	}


	private static void addToClickedItems(Long id) {
		clicked.add(id);
	}


	private static boolean wasItemClicked(Long id) {
		return clicked.contains(id);
	}


	// INNER CLASSES
	private class AnnouncementClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Context context		= getApplicationContext();

			// Redirect to article details screen
			Intent intent	= new Intent(context, AnnouncementActivity.class);
			// Builds a uri in the following format: content://lewicapl/articles/article/[0-9]+
			Uri uri			= Uri.parse(AnnouncementActivity.BASE_URI + Long.toString(id) );
			// Passes activity Uri as parameter that can be used to work out ID of requested article.
			intent.setData(uri);
			startActivity(intent);

			// Mark current announcement as read by changing its colour
			TextView tv					= (TextView) view.findViewById(R.id.announcement_item_title);
			appTheme	= UserPreferencesManager.getThemeInstance(context);
			tv.setTextColor(appTheme.getListHeadingColour(true) );
			addToClickedItems(id);

			return;
		}
	}


	private class AnnouncementsUpdateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			appTheme	= UserPreferencesManager.getThemeInstance(getApplicationContext() );
			reloadRows();
			appTheme.setListViewDividerColour(listView, context);
		}
	}


	/**
	 * Populates the list and makes sure announcements that have already been read are marked accordingly.
	 * It is static nested class, see http://download.oracle.com/javase/tutorial/java/javaOO/nested.html
	 * @author Krzysztof Kobrzak
	 */
	private static final class AnnouncementsCursorAdapter extends CursorAdapter {

		public LayoutInflater inflater;

		private int inxID;
		private int inxWasRead;
		private int inxTitle;
		private int inxWhere;
		private int inxWhen;


		AnnouncementsCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
			super(context, cursor, autoRequery);

			// Get the layout inflater
			inflater				= LayoutInflater.from(context);

			// Get and cache column indices
			inxID					= cursor.getColumnIndex(AnnouncementDAO.FIELD_ID);
			inxWasRead			= cursor.getColumnIndex(AnnouncementDAO.FIELD_WAS_READ);
			inxTitle				= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHAT);
			inxWhere				= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHERE);
			inxWhen				= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHEN);
		}

		/**
		 * Responsible for providing views with content and formatting it.
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			appTheme	= UserPreferencesManager.getThemeInstance(context);

			TextView tvTitle	= (TextView) view.findViewById(R.id.announcement_item_title);
			tvTitle.setText(cursor.getString(inxTitle) );

			String dateAndPlace	= getDateAndPlaceText(cursor.getString(inxWhere), cursor.getString(inxWhen) );
			TextView tvWhereWhen	= (TextView) view.findViewById(R.id.announcement_item_details);
			if (dateAndPlace != null) {
				tvWhereWhen.setText(dateAndPlace);
				tvWhereWhen.setVisibility(View.VISIBLE);
			} else {
				tvWhereWhen.setText("");
				tvWhereWhen.setVisibility(View.GONE);
			}

			boolean unread	= cursor.getInt(inxWasRead) == 0 && ! wasItemClicked(cursor.getLong(inxID) );
			loadTheme(! unread, view, tvTitle, tvWhereWhen);
		}


		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.list_announcements_item, parent, false);
		}


		/**
		 * @param where
		 * @param when
		 */
		private String getDateAndPlaceText(String where, String when) {
			if (where.length() > 0 && when.length() > 0) {
				StringBuilder sb	= new StringBuilder();
				sb.append(where);
				sb.append(" | ");
				sb.append(when);

				return sb.toString();
			}
			if (where.length() > 0) {
				return where;
			}
			if (when.length() > 0) {
				return when;
			}
			// We are still here - that means both where and when info is empty.
			return null;
		}


		/**
		 * @param read
		 * @param view
		 * @param tvTitle
		 * @param tvWhereWhen
		 */
		private void loadTheme(boolean read, View view, TextView tvTitle, TextView tvWhereWhen) {
			tvTitle.setTextColor(appTheme.getListHeadingColour(read) );
			tvWhereWhen.setTextColor(appTheme.getListTextColour(read) );
			view.setBackgroundColor(appTheme.getBackgroundColour() );
		}
	}
	// End of NewsCursorAdapter
}
