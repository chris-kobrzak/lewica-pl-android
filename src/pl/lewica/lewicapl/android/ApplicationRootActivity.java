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
import java.util.Map;
import java.util.Set;

import pl.lewica.api.model.DataModelType;
import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.activity.AnnouncementListActivity;
import pl.lewica.lewicapl.android.activity.HistoryListActivity;
import pl.lewica.lewicapl.android.activity.MoreActivity;
import pl.lewica.lewicapl.android.activity.NewsListActivity;
import pl.lewica.lewicapl.android.activity.PublicationsListActivity;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.TabHost;

/**
 * @author Krzysztof Kobrzak
 */
public class ApplicationRootActivity extends TabActivity {

	private static File storageDir;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		intent	= new Intent(this, PublicationsListActivity.class);
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

		// TODO Make sure these tasks run in a sequence or at least there is a few seconds gap between them.
		// Fetch updates from the server
		new UpdateArticlesTask().execute();

		// Wait a few seconds before running the announcements update
		new UpdateAnnouncementsTask().execute();

		new UpdateHistoryTask().execute();
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
			default :
				return super.onOptionsItemSelected(item);
		}
	}


	/**
	 * Sends a message that activities can listen to to update their data, e.g. list views.
	 */
	public void broadcastUpdate(DataModelType type) {
		Context context		= getApplicationContext();
		Intent intent			= new Intent();

		switch (type) {
			case ARTICLE:
				// Notify the news listing screen
				intent.setAction(NewsListActivity.BROADCAST_UPDATE_AVAILABLE);
				context.sendBroadcast(intent);
				
				// Notify the publications listing screen
				intent.setAction(PublicationsListActivity.BROADCAST_UPDATE_AVAILABLE);
				context.sendBroadcast(intent);
			break;

			case ANNOUNCEMENT:
				intent.setAction(AnnouncementListActivity.BROADCAST_UPDATE_AVAILABLE);
				context.sendBroadcast(intent);
			break;

			case HISTORY:
				intent.setAction(HistoryListActivity.BROADCAST_UPDATE_AVAILABLE);
				context.sendBroadcast(intent);
			break;
		}
	}



	/**
	 * TODO This is temporary code demonstrating how to update the application content.
	 * Manages data update and refreshes the list of items.
	 * This is an <em>inner class</em>.
	 * @author Krzysztof Kobrzak
	 */
	private class UpdateArticlesTask extends AsyncTask<Void, Integer, UpdateStatus> {

		@Override
		protected UpdateStatus doInBackground(Void... params) {
			ContentUpdateManager updater	= new ContentUpdateManager();
			UpdateStatus status					= (UpdateStatus) updater.fetchAndSaveArticles(getApplicationContext() );

			return status;
		}

		protected void onPostExecute(UpdateStatus status) {
			if (status.getTotalUpdated() == 0) {
				return;
			}
			broadcastUpdate(DataModelType.ARTICLE);

			new DownloadArticleThumbnailsTask().execute(status);
		}
	}


	private class DownloadArticleThumbnailsTask extends AsyncTask<UpdateStatus, Integer, Integer> {
		private static final String TAG	= "DownloadArticleThumbnailsTask";

		@Override
		protected Integer doInBackground(UpdateStatus... statuses) {
			UpdateStatus status						= statuses[0];
			ArticleUpdateStatus articleStatus		= (ArticleUpdateStatus) status;
			Set<Map<String,String>> set			= articleStatus.getImages();

			ContentUpdateManager updater		= new ContentUpdateManager();

			if (updater.fetchAndSaveArticleThumbnails(set, storageDir) ) {
				return set.size();
			}

			return -1;
		}

		protected void onProgressUpdate(Integer... progress) {
			Log.i(TAG, "Downloaded image " + Integer.toString(progress[0]) );
		}

		protected void onPostExecute(Integer status) {
			if (status == 0) {
				return;
			}
			broadcastUpdate(DataModelType.ARTICLE);
		}
	}


	private class UpdateAnnouncementsTask extends AsyncTask<Void, Integer, UpdateStatus> {

		@Override
		protected UpdateStatus doInBackground(Void... params) {
			ContentUpdateManager updater	= new ContentUpdateManager();
			UpdateStatus status					= (UpdateStatus) updater.fetchAndSaveAnnouncements(getApplicationContext() );

			return status;
		}

		protected void onPostExecute(UpdateStatus status) {
			if (status.getTotalUpdated() == 0) {
				return;
			}
			broadcastUpdate(DataModelType.ANNOUNCEMENT);
		}
	}


	private class UpdateHistoryTask extends AsyncTask<Void, Integer, UpdateStatus> {

		@Override
		protected UpdateStatus doInBackground(Void... params) {
			ContentUpdateManager updater	= new ContentUpdateManager();
			UpdateStatus status					= (UpdateStatus) updater.fetchAndSaveHistoryEvents(getApplicationContext() );

			return status;
		}

		protected void onPostExecute(UpdateStatus status) {
			if (status.getTotalUpdated() == 0) {
				return;
			}
			broadcastUpdate(DataModelType.HISTORY);
		}
	}
}
