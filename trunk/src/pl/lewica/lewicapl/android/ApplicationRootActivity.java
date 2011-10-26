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
package pl.lewica.lewicapl.android;

import java.io.File;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.widget.TabHost;

import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.activity.AnnouncementListActivity;
import pl.lewica.lewicapl.android.activity.HistoryListActivity;
import pl.lewica.lewicapl.android.activity.MoreActivity;
import pl.lewica.lewicapl.android.activity.NewsListActivity;
import pl.lewica.lewicapl.android.activity.PublicationListActivity;
import pl.lewica.lewicapl.android.database.AnnouncementDAO;
import pl.lewica.lewicapl.android.database.ArticleDAO;

/**
 * @author Krzysztof Kobrzak
 */
public class ApplicationRootActivity extends TabActivity {

	public static final String START_INDETERMINATE_PROGRESS		= "pl.lewica.lewicapl.android.applicationrootactivity.reloadon";
	public static final String STOP_INDETERMINATE_PROGRESS		= "pl.lewica.lewicapl.android.applicationrootactivity.reloadoff";

	private static File storageDir;
	private ContentUpdateManager updateManager;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// This allows to show and hide the progress indicator in the top bar.
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.tab_layout);
		
		// Tabs layout based on http://developer.android.com/resources/tutorials/views/hello-tabwidget.html
		Resources res			= getResources(); // Resource object to get Drawables
		TabHost tabHost	= getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab

		File sdDir		= Environment.getExternalStorageDirectory();
		storageDir		= new File(sdDir + res.getString(R.string.path_images) );
		storageDir.mkdirs();

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent	= new Intent(this, NewsListActivity.class);
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec		= tabHost.newTabSpec("news").setIndicator(res.getString(R.string.tab_news),
				res.getDrawable(R.drawable.ic_70tv) ).setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent	= new Intent(this, PublicationListActivity.class);
		spec		= tabHost.newTabSpec("texts").setIndicator(res.getString(R.string.tab_texts),
				res.getDrawable(R.drawable.ic_96book) ).setContent(intent);
		tabHost.addTab(spec);

		intent	= new Intent(this, AnnouncementListActivity.class);
		spec		= tabHost.newTabSpec("announcements").setIndicator(res.getString(R.string.tab_announcements),
				res.getDrawable(R.drawable.ic_124bullhorn) ).setContent(intent);
		tabHost.addTab(spec);

		intent	= new Intent(this, HistoryListActivity.class);
		spec		= tabHost.newTabSpec("history").setIndicator(res.getString(R.string.tab_history),
				res.getDrawable(R.drawable.ic_83calendar) ).setContent(intent);
		tabHost.addTab(spec);

		// Custom title background colour, http://stackoverflow.com/questions/2251714/set-title-background-color
		View titleView = getWindow().findViewById(android.R.id.title);
		if (titleView != null) {
			ViewParent parent	= titleView.getParent();
			if (parent != null && (parent instanceof View) ) {
				View parentView	= (View)parent;
				parentView.setBackgroundColor(res.getColor(R.color.red) );
			}
		}

		// By default, the first tab is selected
		tabHost.setCurrentTab(0);

		// Register to receive notifications
		IntentFilter filter		= new IntentFilter();
		filter.addAction(START_INDETERMINATE_PROGRESS);
		filter.addAction(STOP_INDETERMINATE_PROGRESS);
		ApplicationBroadcastReceiver receiver	= new ApplicationBroadcastReceiver();	// Instance of an inner class
		registerReceiver(receiver, filter);

		// Trigger content update
		updateManager	= new ContentUpdateManager(getApplicationContext(), storageDir);
		updateManager.run();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater infl	= getMenuInflater();
		infl.inflate(R.menu.menu_common, menu);
		
		return true;
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId() ) {
			case R.id.menu_more:
				Intent intent	= new Intent(this, MoreActivity.class);
		        this.startActivity(intent);
				return true;
	
			case R.id.menu_refresh:
				updateManager.run();
				return true;
	
			case R.id.menu_mark_as_read:
				ArticleDAO articleDAO			= new ArticleDAO(this);
				articleDAO.open();
				articleDAO.updateMarkAllAsRead();
				articleDAO.close();
				
				AnnouncementDAO annDAO	= new AnnouncementDAO(this);
				annDAO.open();
				annDAO.updateMarkAllAsRead();
				annDAO.close();

				updateManager.broadcastDataReload_News();
				updateManager.broadcastDataReload_Publications();
				updateManager.broadcastDataReload_Announcements();
				return true;
	
				default :
					return super.onOptionsItemSelected(item);
		}
	}


	// INNER CLASSES
	private class ApplicationBroadcastReceiver extends BroadcastReceiver {
		@SuppressWarnings("unused")
		private static final String TAG = "ApplicationBroadcastReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(START_INDETERMINATE_PROGRESS) ) {
				setProgressBarIndeterminateVisibility(true);
			}
			
			if (intent.getAction().equals(STOP_INDETERMINATE_PROGRESS) ) {
				setProgressBarIndeterminateVisibility(false);
			}
		}
	}
}
