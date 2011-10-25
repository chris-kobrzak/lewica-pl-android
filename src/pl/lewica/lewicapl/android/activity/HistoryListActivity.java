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

import java.util.Calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.database.HistoryDAO;


/**
 * @author Krzysztof Kobrzak
 */
public class HistoryListActivity extends Activity {
	
	public static final String BROADCAST_UPDATE_AVAILABLE	= "pl.lewica.lewicapl.android.activity.historylistactivity.reload";

	private static final String TAG = "LewicaPL:HistoryEventListActivity";

	private static Typeface categoryTypeface;
	private HistoryDAO historyDAO;
	private ListAdapter listAdapter;
	private ListView listView;
	private Calendar cal			= Calendar.getInstance();
	private HistoryUpdateBroadcastReceiver receiver;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_history);	// This comes from this file's name /res/list_history.xml

		// Load a list view container from list_articles.xml
		listView					= (ListView) findViewById(R.id.list_history_events);

		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		// Register to receive content update messages
		IntentFilter filter		= new IntentFilter();
		filter.addAction(BROADCAST_UPDATE_AVAILABLE);
		receiver					= new HistoryUpdateBroadcastReceiver();	// Instance of an inner class
		registerReceiver(receiver, filter);
		
		int month				= cal.get(Calendar.MONTH) + 1;
		int day					= cal.get(Calendar.DATE);
		loadView(month, day);

		// Access data
		historyDAO			= new HistoryDAO(this);
		historyDAO.open();
		Cursor cursor			= historyDAO.select(month, day);
		startManagingCursor(cursor);
		
		// Set list view adapter - this links the view with the data
		listAdapter				= new SimpleCursorAdapter(
				this, 
				R.layout.list_history_item,
				cursor, 
				new String[] { HistoryDAO.FIELD_YEAR, HistoryDAO.FIELD_EVENT },
				new int[] { R.id.history_year, R.id.history_event }
		);
		listView.setAdapter(listAdapter);
	}


	public void loadView(int month, int day) {
		// Orange bar
		TextView tv			= (TextView) findViewById(R.id.history_category);
		tv.setTypeface(categoryTypeface);
		tv.setText(this.getString(R.string.heading_history) );
		
		StringBuilder sb		= new StringBuilder();
		sb.append(day);
		sb.append("/");
		sb.append(month);
		sb.append(" w historii");
		
		tv							= (TextView) findViewById(R.id.history_date);
		tv.setText(sb.toString() );
	}


	public void reloadRows() {
		CursorAdapter ca	= (CursorAdapter) listAdapter;
		// Reload rows
		Cursor newCursor	= historyDAO.select(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE) );
		ca.changeCursor(newCursor);
	}


	// INNER CLASSES
	private class HistoryUpdateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "HistoryUpdateBroadcastReceiver got a message!");
			reloadRows();
		}
	}
}
