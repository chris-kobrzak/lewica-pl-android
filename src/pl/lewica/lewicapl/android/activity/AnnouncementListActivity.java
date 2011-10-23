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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import pl.lewica.lewicapl.android.database.AnnouncementDAO;


/**
 * @author Krzysztof Kobrzak
 */
public class AnnouncementListActivity extends Activity {
	
	public static final String BROADCAST_UPDATE_AVAILABLE	= "pl.lewica.lewicapl.android.activity.announcementslistactivity.reload";

	private static final String TAG = "LewicaPL:AnnouncementListActivity";

	private AnnouncementDAO annDAO;
	private ListAdapter listAdapter;
	private ListView listView;
	private AnnouncementsUpdateBroadcastReceiver receiver;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_announcements);	// This comes from this file's name /res/list_announcements.xml

		// Load a list view container from list_announcements.xml
		listView					= (ListView) findViewById(R.id.list_announcements);

		// Register to content update messages
		IntentFilter filter		= new IntentFilter();
		filter.addAction(BROADCAST_UPDATE_AVAILABLE);
		receiver					= new AnnouncementsUpdateBroadcastReceiver();	// Instance of an inner class
		registerReceiver(receiver, filter);

		// Access data
		annDAO					= new AnnouncementDAO(this);
		annDAO.open();
		Cursor cursor			= annDAO.selectLatest();

		// Set list view adapter - this links the view with the data
		listAdapter				= new AnnouncementsCursorAdapter(this, cursor, false);
		listView.setAdapter(listAdapter);

		// Clicking on an item should redirect to the details view
		listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tv;
				Context context				= getApplicationContext();
				Resources mResources	= context.getResources();

				// Redirect to article details screen
				Intent intent	= new Intent(context, AnnouncementActivity.class);
				// Builds a uri in the following format: content://lewicapl/articles/article/[0-9]+
				Uri uri			= Uri.parse(AnnouncementActivity.BASE_URI + Long.toString(id) );
				// Passes activity Uri as parameter that can be used to work out ID of requested article.
				intent.setData(uri);
		        startActivity(intent);

		        // Mark current announcement as read by changing its colour
		        int colour	= mResources.getColor(R.color.read);
		        tv				= (TextView) view.findViewById(R.id.announcement_item_title);
		        tv.setTextColor(colour);

		        return;
			}
		});
	}


	public void reloadRows() {
		CursorAdapter ca	= (CursorAdapter) listAdapter;
		// Reload rows
		Cursor newCursor	= annDAO.selectLatest();
		ca.changeCursor(newCursor);
	}


	// INNER CLASSES
	private class AnnouncementsUpdateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "AnnouncementsUpdateBroadcastReceiver got a message!");
			reloadRows();
		}
	}


	private static final class AnnouncementsCursorAdapter extends CursorAdapter {
		
		public LayoutInflater mInflater;
		private static Resources mResources;

//		private int colIndex_ID;
		private int colIndex_WasRead;
		private int colIndex_Title;
		private int colIndex_Where;
		private int colIndex_When;


		AnnouncementsCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
			super(context, cursor, autoRequery);

			// Get the layout inflater
			mInflater			= LayoutInflater.from(context);

			// Get and cache column indices
//			colIndex_ID					= cursor.getColumnIndex(AnnouncementDAO.FIELD_ID);
			colIndex_WasRead			= cursor.getColumnIndex(AnnouncementDAO.FIELD_WAS_READ);
			colIndex_Title				= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHAT);
			colIndex_Where				= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHERE);
			colIndex_When				= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHEN);
			
			mResources		= context.getResources();
		}


		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return mInflater.inflate(R.layout.list_announcements_item, parent, false);
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
			if (cursor.getInt(colIndex_WasRead) == 0) {
				colour	= mResources.getColor(R.color.unread);
			} else {
				colour	= mResources.getColor(R.color.read);
			}
			tv.setTextColor(colour);
			tv.setText(cursor.getString(colIndex_Title) );
			// Where and when?
			tv	= (TextView) view.findViewById(R.id.announcement_item_details);
			String where	= cursor.getString(colIndex_Where);
			String when	= cursor.getString(colIndex_When);

			if (where.length() > 0 && when.length() > 0) {
				StringBuilder sb	= new StringBuilder();
				sb.append(where);
				sb.append(" | ");
				sb.append(when);

				tv.setTextKeepState(sb.toString() );
				return;
			}

			if (where.length() > 0) {
				tv.setTextKeepState(where);
				return;
			}
			if (when.length() > 0) {
				tv.setTextKeepState(when);
				return;
			}
			// We are still here - that means both where and when info is empty.
			tv.setText("");
		}
	}
	// End of NewsCursorAdapter
}
