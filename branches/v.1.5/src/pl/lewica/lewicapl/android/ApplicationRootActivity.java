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
import java.io.IOException;

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
import pl.lewica.lewicapl.android.activity.BlogPostListActivity;
import pl.lewica.lewicapl.android.activity.HistoryListActivity;
import pl.lewica.lewicapl.android.activity.MoreActivity;
import pl.lewica.lewicapl.android.activity.NewsListActivity;
import pl.lewica.lewicapl.android.activity.PublicationListActivity;
import pl.lewica.lewicapl.android.database.AnnouncementDAO;
import pl.lewica.lewicapl.android.database.ArticleDAO;
import pl.lewica.lewicapl.android.database.BaseTextDAO;
import pl.lewica.lewicapl.android.database.BlogPostDAO;

/**
 * @author Krzysztof Kobrzak
 */
public class ApplicationRootActivity extends TabActivity {

	public static final String START_INDETERMINATE_PROGRESS		= "pl.lewica.lewicapl.android.applicationrootactivity.RELOADON";
	public static final String STOP_INDETERMINATE_PROGRESS		= "pl.lewica.lewicapl.android.applicationrootactivity.RELOADOFF";

	// Order of tabs
	public static enum Tab {
		NEWS, ARTICLES, BLOGS, ANNOUNCEMENTS, HISTORY
	}
	// This is to be able switch by enum ordinal
	public static final Tab[] tabs	= Tab.values();

	private IntentFilter filter;
	private BroadcastReceiver receiver;
	private BroadcastSender broadcastSender;
	private ContentUpdateManager updateManager;
	// Number of seconds after which the application will attempt to run an update. 
	private int updateInterval	= 5 * 60;


	public void init() {
		// This allows to show and hide the progress indicator in the top bar and needs to be done before content view set-up
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		File sdDir		= Environment.getExternalStorageDirectory();
		File storageDir		= new File(sdDir + getResources().getString(R.string.path_images) );
		if (! storageDir.exists() ) {
			storageDir.mkdirs();
		}
		// it's not a big deal if this operation fails so let's just try-catch it.
		try {
			AndroidUtil.setUpResourcesHiddenFromAndroidGallery(storageDir);
		} catch (IOException err) {}

		filter		= new IntentFilter();
		filter.addAction(START_INDETERMINATE_PROGRESS);
		filter.addAction(STOP_INDETERMINATE_PROGRESS);
		receiver	= new ApplicationBroadcastReceiver();
		registerReceiver(receiver, filter);

		// Required by the content update module
		updateManager		= ContentUpdateManager.getInstance(getApplicationContext(), storageDir);
		broadcastSender	= BroadcastSender.getInstance(this);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init();

		setContentView(R.layout.tab_layout);

		// Tabs layout based on http://developer.android.com/resources/tutorials/views/hello-tabwidget.html
		Resources res			= getResources(); // Resource object to get Drawables
		TabHost tabHost	= getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Reusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab
		String tag;

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent	= new Intent(this, NewsListActivity.class);
		tag		= Tab.NEWS.name();
		// Initialise a TabSpec for each tab and add it to the TabHost
		spec		= tabHost.newTabSpec(tag).setIndicator(res.getString(R.string.tab_news), res.getDrawable(R.drawable.ic_tab_tv) ).setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent	= new Intent(this, PublicationListActivity.class);
		tag		= Tab.ARTICLES.name();
		spec		= tabHost.newTabSpec(tag).setIndicator(res.getString(R.string.tab_texts), res.getDrawable(R.drawable.ic_tab_book) ).setContent(intent);
		tabHost.addTab(spec);
		
		intent	= new Intent(this, BlogPostListActivity.class);
		tag		= Tab.BLOGS.name();
		spec		= tabHost.newTabSpec(tag).setIndicator(res.getString(R.string.tab_blog), res.getDrawable(R.drawable.ic_tab_person) ).setContent(intent);
		tabHost.addTab(spec);

		intent	= new Intent(this, AnnouncementListActivity.class);
		tag		= Tab.ANNOUNCEMENTS.name();
		spec		= tabHost.newTabSpec(tag).setIndicator(res.getString(R.string.tab_announcements), res.getDrawable(R.drawable.ic_tab_bullhorn) ).setContent(intent);
		tabHost.addTab(spec);

		intent	= new Intent(this, HistoryListActivity.class);
		tag		= Tab.HISTORY.name();
		spec		= tabHost.newTabSpec(tag).setIndicator(res.getString(R.string.tab_history), res.getDrawable(R.drawable.ic_tab_calendar) ).setContent(intent);
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

		tabHost.setCurrentTab(Tab.NEWS.ordinal() );
	}


	@Override
	protected void onStart() {
		super.onStart();

		registerReceiver(receiver, filter);

		if (! updateManager.isRunning() ) {
			setProgressBarIndeterminateVisibility(false);
		}

		runUpdate();
	}


	@Override
	protected void onPause() {
		super.onPause();

		// Android API doesn't provide an "isRegisteredReciever" method so need to use a hack to avoid random exceptions thrown when application gets closed.
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {}
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater	= getMenuInflater();
		int tab	= getTabHost().getCurrentTab();
		if (tab != Tab.HISTORY.ordinal() ) {
			inflater.inflate(R.menu.menu_common, menu); 
		} else {
			inflater.inflate(R.menu.menu_history, menu);
		}

		return true;
	}


	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		super.onPrepareOptionsMenu(menu);

		// Don't let them run the refresh if it's already running.
		menu.getItem(0).setEnabled(true);

		if (updateManager.isRunning() ) {
			menu.getItem(0).setEnabled(false);
		}

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId() ) {
			case R.id.menu_more:
				Intent intent	= new Intent(this, MoreActivity.class);
				this.startActivity(intent);
				return true;

			case R.id.menu_refresh:
				// The only thing that should prevent it from happening is the fact the update is running already. 
				if (updateManager.isRunning() ) {
					return true;
				}

				// Don't call runUpdate() here as this is an action triggered by the user.
				updateManager.run();
				return true;

			case R.id.menu_mark_as_read:
				int tab	= getTabHost().getCurrentTab();
				
				ArticleDAO articleDAO;

				// Switching over an integer representing the ordinal of the current tab
				switch (tabs[tab]) {
					case NEWS:
						articleDAO		= new ArticleDAO(this);
						articleDAO.open();
						articleDAO.updateMarkNewsAsRead();
						articleDAO.close();

						broadcastSender.reloadTab(Tab.NEWS);
						break;

					case ARTICLES:
						articleDAO		= new ArticleDAO(this);
						articleDAO.open();
						articleDAO.updateMarkTextsAsRead();
						articleDAO.close();

						broadcastSender.reloadTab(Tab.ARTICLES);
						break;

					case BLOGS:
						BlogPostDAO blogDAO		= new BlogPostDAO(this);
						blogDAO.open();
						blogDAO.updateMarkAllAsRead();
						blogDAO.close();

						broadcastSender.reloadTab(Tab.BLOGS);
						break;

					case ANNOUNCEMENTS:
						BaseTextDAO annDAO	= new AnnouncementDAO(this);
						annDAO.open();
						annDAO.updateMarkAllAsRead();
						annDAO.close();

						broadcastSender.reloadTab(Tab.HISTORY);
						break;

					case HISTORY:
						break;
				}
				return true;

				default :
					return super.onOptionsItemSelected(item);
		}
	}


	/**
	 * Checks if the update is not running already and also compares the current time with the one it last ran 
	 * and makes a decision whether to run the update on this basis.  See updateInterval to check what value is used for comparison. 
	 * This method is meant to be called automatically, not triggered by the user as the time interval check might prevent it from execution.
	 */
	private synchronized void runUpdate() {
		if (updateManager.isRunning() ) {
			return;
		}

		if (updateManager.getIntervalSinceLastUpdate() < updateInterval) {
			return;
		}

		updateManager.run();
	}



	// INNER CLASSES
	private class ApplicationBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ApplicationRootActivity.START_INDETERMINATE_PROGRESS) ) {
				setProgressBarIndeterminateVisibility(true);
			}

			if (intent.getAction().equals(ApplicationRootActivity.STOP_INDETERMINATE_PROGRESS) ) {
				setProgressBarIndeterminateVisibility(false);
			}
		}
	}
}
