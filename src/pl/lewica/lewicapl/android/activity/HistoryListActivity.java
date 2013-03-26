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
import android.view.LayoutInflater;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.ContentUpdateManager;
import pl.lewica.lewicapl.android.UserPreferencesManager;
import pl.lewica.lewicapl.android.database.HistoryDAO;
import pl.lewica.lewicapl.android.theme.Theme;


/**
 * @author Krzysztof Kobrzak
 */
public class HistoryListActivity extends Activity {
	
	public static final String RELOAD_VIEW	= "pl.lewica.lewicapl.android.activity.historylistactivity.RELOAD";

	private static Typeface categoryTypeface;
	private HistoryDAO historyDAO;
	private ListAdapter listAdapter;
	private ListView listView;
	private HistoryUpdateBroadcastReceiver receiver;
	private 	int month;
	private 	int day;
	private int limitRows		= 100;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_history);	// This comes from this file's name /res/list_history.xml

		// Load a list view container from list_history.xml
		listView					= (ListView) findViewById(R.id.list_history_events);

		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		LayoutInflater inflater	= getLayoutInflater();
		LinearLayout barLayout	= (LinearLayout) inflater.inflate(R.layout.bar_layout, null);
		listView.addHeaderView(barLayout);

		Calendar cal		= Calendar.getInstance();
		month				= cal.get(Calendar.MONTH) + 1;
		day					= cal.get(Calendar.DATE);
		loadView(month, day);

		// Access data
		historyDAO			= new HistoryDAO(this);
		historyDAO.open();
		Cursor cursor			= historyDAO.select(month, day, limitRows);
		startManagingCursor(cursor);

		int layout;
		if (UserPreferencesManager.isLightTheme() ) {
			layout	= R.layout.list_history_item;
		} else {
			layout	= R.layout.list_history_item_dark;
		}
		// Set list view adapter - this links the view with the data
		listAdapter				= new SimpleCursorAdapter(
				this, 
				layout,
				cursor, 
				new String[] { HistoryDAO.FIELD_YEAR, HistoryDAO.FIELD_EVENT },
				new int[] { R.id.history_year, R.id.history_event }
		) {
			// Disables the onClick event attached to the rows (and also the default, orange background colour).
			// In order to get rid of lines dividing the rows, you might want to implement areAllItemsEnabled() as well.
			@Override
			public boolean isEnabled(int position) { 
				return false; 
			} 
		}; 
		listView.setAdapter(listAdapter);
		Theme appTheme	= UserPreferencesManager.getThemeInstance(getApplicationContext() );
		appTheme.setListViewDividerColour(listView, this);

		// Register to receive content update messages
		IntentFilter filter		= new IntentFilter();
		filter.addAction(RELOAD_VIEW);
		receiver					= new HistoryUpdateBroadcastReceiver();	// Instance of an inner class
		registerReceiver(receiver, filter);
	}


	/**
	 * When this activity is created, it saves the current day and month.  We compare the current date
	 * with these values to work out if we should update the content.
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		Calendar cal		= Calendar.getInstance();
		int monthNow	= cal.get(Calendar.MONTH) + 1;
		int dayNow		= cal.get(Calendar.DATE);
		
		if (day == dayNow && month == monthNow) {
			return;
		}
		// Update current date
		day		= dayNow;
		month	= monthNow;

		ContentUpdateManager updateManager	= ContentUpdateManager.getInstance(getApplicationContext(), null);
		if (! updateManager.isRunning() ) {
			updateManager.manageAndBroadcastUpdates(ContentUpdateManager.CommandType.INIT_HISTORY, false);
		}
	}


	public void loadView(int month, int day) {
		// Orange bar
		TextView tv			= (TextView) findViewById(R.id.bar_category);
		tv.setTypeface(categoryTypeface);
		tv.setText(this.getString(R.string.heading_history) );

		StringBuilder sb		= new StringBuilder();
		sb.append(day);
		sb.append("/");
		sb.append(month);
		sb.append(" ");
		sb.append(this.getString(R.string.heading_history_extra) );

		tv							= (TextView) findViewById(R.id.bar_date);
		tv.setText(sb.toString() );
	}


	public void reloadRows() {
		Calendar cal			= Calendar.getInstance();
		CursorAdapter ca	= (CursorAdapter) listAdapter;
		// Reload rows
		Cursor newCursor	= historyDAO.select(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE), limitRows);
		ca.changeCursor(newCursor);

		// Make sure the top bar data is up-to-date
		loadView(month, day);
	}


	// INNER CLASSES
	private class HistoryUpdateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			reloadRows();
		}
	}
}
