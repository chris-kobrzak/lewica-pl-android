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
import android.content.res.Resources;
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
import pl.lewica.lewicapl.android.TextPreferencesManager;
import pl.lewica.lewicapl.android.database.AnnouncementDAO;
import pl.lewica.lewicapl.android.database.BaseTextDAO;


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
	private AnnouncementsUpdateBroadcastReceiver receiver;

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
		receiver					= new AnnouncementsUpdateBroadcastReceiver();	// Instance of an inner class
		registerReceiver(receiver, filter);

		// Access data
		annDAO					= new AnnouncementDAO(this);
		annDAO.open();
		Cursor cursor			= annDAO.selectLatest(limitRows);

		// Set list view adapter - this links the view with the data
		listAdapter				= new AnnouncementsCursorAdapter(this, cursor, false);
		listView.setAdapter(listAdapter);

		// Clicking on an item should redirect to the details view
		listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tv;
				Context context		= getApplicationContext();
				Resources res			= context.getResources();

				// Redirect to article details screen
				Intent intent	= new Intent(context, AnnouncementActivity.class);
				// Builds a uri in the following format: content://lewicapl/articles/article/[0-9]+
				Uri uri			= Uri.parse(AnnouncementActivity.BASE_URI + Long.toString(id) );
				// Passes activity Uri as parameter that can be used to work out ID of requested article.
				intent.setData(uri);
				startActivity(intent);

				// Mark current announcement as read by changing its colour...
				int colour		= res.getColor(R.color.read);
				tv					= (TextView) view.findViewById(R.id.announcement_item_title);
				tv.setTextColor(colour);
				// ... and flagging it in local cache accordingly
				clicked.add(id);

				return;
			}
		});
	}


	public void reloadRows() {
		CursorAdapter ca	= (CursorAdapter) listAdapter;
		// Reload rows
		Cursor newCursor	= annDAO.selectLatest(limitRows);
		ca.changeCursor(newCursor);
	}


	// INNER CLASSES
	private class AnnouncementsUpdateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			reloadRows();
		}
	}



	/**
	 * Populates the list and makes sure announcements that have already been read are marked accordingly.
	 * It is static nested class, see http://download.oracle.com/javase/tutorial/java/javaOO/nested.html
	 * @author Krzysztof Kobrzak
	 */
	private static final class AnnouncementsCursorAdapter extends CursorAdapter {

		public LayoutInflater inflater;
		private static Resources res;

		private int colIndex_ID;
		private int colIndex_WasRead;
		private int colIndex_Title;
		private int colIndex_Where;
		private int colIndex_When;


		AnnouncementsCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
			super(context, cursor, autoRequery);

			// Get the layout inflater
			inflater				= LayoutInflater.from(context);

			res					= context.getResources();

			// Get and cache column indices
			colIndex_ID					= cursor.getColumnIndex(AnnouncementDAO.FIELD_ID);
			colIndex_WasRead			= cursor.getColumnIndex(AnnouncementDAO.FIELD_WAS_READ);
			colIndex_Title				= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHAT);
			colIndex_Where				= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHERE);
			colIndex_When				= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHEN);
		}

		/**
		 * Responsible for providing views with content and formatting it.
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tv;
			int colour;
			// Title
			tv	= (TextView) view.findViewById(R.id.announcement_item_title);
			if (cursor.getInt(colIndex_WasRead) == 0 && ! clicked.contains(cursor.getLong(colIndex_ID) ) ) {
				colour	= res.getColor(R.color.unread);
			} else {
				colour	= res.getColor(R.color.read);
			}
			tv.setTextColor(colour);
			tv.setText(cursor.getString(colIndex_Title) );
			// Where and when?
			TextView tvWhereWhen	= (TextView) view.findViewById(R.id.announcement_item_details);
			String where	= cursor.getString(colIndex_Where);
			String when	= cursor.getString(colIndex_When);
			tvWhereWhen.setVisibility(View.VISIBLE);

			if (where.length() > 0 && when.length() > 0) {
				StringBuilder sb	= new StringBuilder();
				sb.append(where);
				sb.append(" | ");
				sb.append(when);

				tvWhereWhen.setText(sb.toString() );
			} else if (where.length() > 0) {
				tvWhereWhen.setText(where);
			} else if (when.length() > 0) {
				tvWhereWhen.setText(when);
			} else {
				// We are still here - that means both where and when info is empty.
				tvWhereWhen.setText("");
				tvWhereWhen.setVisibility(View.GONE);
			}

			if (TextPreferencesManager.getUserTheme(context) == TextPreferencesManager.THEME_WHITE_ON_BLACK) {
				tvWhereWhen.setTextColor(res.getColor(R.color.white) );
				view.setBackgroundColor(res.getColor(R.color.black) );
			} else {
				tvWhereWhen.setTextColor(res.getColor(R.color.grey_darker) );
				view.setBackgroundColor(res.getColor(android.R.color.transparent) );
			}
		}


		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.list_announcements_item, parent, false);
		}
	}
	// End of NewsCursorAdapter
}
