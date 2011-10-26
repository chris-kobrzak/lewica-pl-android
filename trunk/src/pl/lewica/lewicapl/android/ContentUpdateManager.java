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
/**
 * 
 */
package pl.lewica.lewicapl.android;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import pl.lewica.api.FeedDownloadManager;
import pl.lewica.api.model.Announcement;
import pl.lewica.api.model.Article;
import pl.lewica.api.model.History;
import pl.lewica.api.model.DataModel;
import pl.lewica.api.model.DataModelType;
import pl.lewica.api.url.AnnouncementURL;
import pl.lewica.api.url.ArticleURL;
import pl.lewica.api.url.HistoryURL;
import pl.lewica.lewicapl.android.activity.AnnouncementListActivity;
import pl.lewica.lewicapl.android.activity.HistoryListActivity;
import pl.lewica.lewicapl.android.activity.NewsListActivity;
import pl.lewica.lewicapl.android.activity.PublicationListActivity;
import pl.lewica.lewicapl.android.database.AnnouncementDAO;
import pl.lewica.lewicapl.android.database.ArticleDAO;
import pl.lewica.lewicapl.android.database.HistoryDAO;


/**
 * Collection of methods the coordinate the process of downloading feeds from the server 
 * and saving them in the device's database.
 * The class uses the singleton design pattern.
 * @author Krzysztof Kobrzak
 */
public class ContentUpdateManager {

	private static final String TAG	= "LewicaPL:ContentUpdateManager";

	private static ContentUpdateManager _instance;
	
	private File storageDir;
	private Context context;
	private boolean isRunning;

	public static enum StatusMessageType {
		INIT,
		INIT_HISTORY,
		NEW_PUBLICATIONS,
		NO_PUBLICATIONS,
		NEW_PUBLICATION_IMAGES,
		NO_PUBLICATION_IMAGES,
		NEW_ANNOUNCEMENTS,
		NO_ANNOUNCEMENTS,
		NEW_HISTORY,
		NO_HISTORY
	}


	/**
	 * Private constructor, see singleton design pattern.
	 * @param context
	 * @param storageDir
	 */
	private ContentUpdateManager(Context context, File storageDir) {
		this.context		= context;
		this.storageDir	= storageDir;
	}


	/**
	 * Standard singleton constructor.
	 * @param context
	 * @param storageDir
	 * @return
	 */
	public static synchronized ContentUpdateManager getInstance(Context context, File storageDir) {
		if (_instance == null) {
			_instance	= new ContentUpdateManager(context, storageDir);
		}

		return _instance;
	}

	public boolean isRunning() {
		return isRunning;
	}


	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}


	/**
	 * Coordinates the process of downloading the articles feed and inserting data to the database.
	 * @param context
	 * @return
	 */
	private UpdateStatus fetchAndSaveArticles(Context context) {
		ArticleUpdateStatus status		= new ArticleUpdateStatus();
		FeedDownloadManager fdm	= new FeedDownloadManager();
		ArticleURL articleURL				= new ArticleURL();
		ArticleDAO articleDAO			= new ArticleDAO(context);

		articleDAO.open();
		int lastArticleID						= articleDAO.fetchLastID();

		articleURL.setSectionList("1,2,3,4,5");	// TODO This should be done using an array, not a list.
		articleURL.setNewerThan(lastArticleID);
		articleURL.setLimit(5);
		
		List<DataModel> articles		= fdm.fetchAndParse(DataModelType.ARTICLE, articleURL.buildURL() );
		int totalArticles						= articles.size();

		if (totalArticles == 0) {
			Log.i(TAG, "fetchAndSaveArticles: NO NEW ARTICLES.  Last ID: " + Integer.toString(lastArticleID) );
			articleDAO.close();
			status.setTotalUpdated(0);

			return status;
		}

		// A container for images to be downloaded.  Order of downloads doesn't matter hence Set instead of ArrayList.
		Set<Map<String, String>> set	= new HashSet<Map<String, String>>();
		Article article;

		// Loop through downloaded articles and insert them to the database
		for (DataModel element: articles) {
			article	= (Article) element;
			articleDAO.insert(article);

			if (! article.hasThumbnail ) {
				continue;
			}
			
			// We are not downloading corresponding thumbnails straight away.
			// We're merely listing them here so we can download them in another background thread once this AsyncTask has completed.
			// This is to be able to show them updated content as soon as possible since text downloads are much faster than images.
			Map<String,String> imageMeta	= new HashMap<String,String>();
			imageMeta.put("ID", Integer.toString(article.getID() ) );
			imageMeta.put("Ext", article.imageExtension );
			set.add(imageMeta);
		}
		Log.i(TAG, "fetchAndSaveArticles: total downloaded: " + Integer.toString(totalArticles ) );

		articleDAO.close();
		
		status.setTotalUpdated(totalArticles);
		status.setImages(set);

		return status;
	}


	/**
	 * Coordinates the process of downloading images and saving them in the file system.
	 * 
	 * @param imageSet
	 * @param storageDir
	 * @return
	 */
	private boolean fetchAndSaveArticleThumbnails(Set<Map<String,String>> imageSet, File storageDir) {
		if (! storageDir.canWrite() ) {
			return false;
		}

		Iterator<Map<String, String>> iter	= imageSet.iterator();
		
		Map<String,String> imageMeta;
		long ID;
		String imageName;
		String imageURL;
		String imagePath;
		File image;
		URL url;
		
		BufferedInputStream bis;
		FileOutputStream fos;
		ByteArrayBuffer bab;
		int current;
		// See http://stackoverflow.com/questions/3498643/dalvik-message-default-buffer-size-used-in-bufferedinputstream-constructor-it/7516554#7516554
		int bufferSize	= 8192;

		while (iter.hasNext() ) {
			imageMeta	= (Map<String,String>) iter.next();
			ID									= Long.parseLong(imageMeta.get("ID") );
			
			imageName					= ArticleURL.buildNameThumbnail(ID, imageMeta.get("Ext") );
			// Source image (on the server)
			imageURL						= ArticleURL.PATH_THUMBNAIL + imageName;
			// Destination image (on phone's SD card)
			imagePath						= storageDir.getPath() + "/" + imageName; 

			image							= new File(imagePath);
			if (image.exists() ) {
				continue;
			}

			try {
				url				= new URL(imageURL);
				bis			= new BufferedInputStream(url.openStream(), bufferSize);
				bab			= new ByteArrayBuffer(50);
				
				current	= 0;
				
				while ( (current = bis.read() ) != -1) {
					bab.append( (byte) current);
				}
				
				fos	= new FileOutputStream(image);
				fos.write(bab.toByteArray() );
				fos.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.i(TAG, "fetchAndSaveThumbnails: total: " + imageSet.size() );

		return true;
	}


	/**
	 * Coordinates the process of downloading the news feed and inserting data to the database.
	 * @param context
	 * @return
	 */
	private UpdateStatus fetchAndSaveAnnouncements(Context context) {
		UpdateStatus status						= new UpdateStatus();
		FeedDownloadManager fdm			= new FeedDownloadManager();
		AnnouncementURL annURL				= new AnnouncementURL();
		AnnouncementDAO annDAO			= new AnnouncementDAO(context);

		annDAO.open();
		int lastAnnID					= annDAO.fetchLastID();

		annURL.setNewerThan(lastAnnID);
		annURL.setLimit(10);
		
		List<DataModel> anns	= fdm.fetchAndParse(DataModelType.ANNOUNCEMENT, annURL.buildURL() );
		int totalAnns					= anns.size();

		if (totalAnns == 0) {
			Log.i(TAG, "fetchAndSaveAnnnouncements: NO NEW ANNOUNCEMENTS.  Last ID: " + Integer.toString(lastAnnID) );
			annDAO.close();
			status.setTotalUpdated(0);

			return status;
		}

		Announcement ann;
		// Loop through downloaded articles and insert them to the database
		for (DataModel element: anns) {
			ann	= (Announcement) element;
			annDAO.insert(ann);
		}
		Log.i(TAG, "fetchAndSaveAnnnouncements: total downloaded: " + Integer.toString(totalAnns) );

		annDAO.close();
		
		status.setTotalUpdated(totalAnns);

		return status;
	}


	private UpdateStatus fetchAndSaveHistoryEvents(Context context) {
		UpdateStatus status				= new UpdateStatus();
		FeedDownloadManager fdm	= new FeedDownloadManager();
		HistoryURL historyURL			= new HistoryURL();
		HistoryDAO historyDAO			= new HistoryDAO(context);
		
		historyDAO.open();
		
		// We are only going to be pulling new records if the local database doesn't have any entries for a given day.
		// This is to avoid duplicates in the database.
		Calendar cal			= Calendar.getInstance();
		int month				= cal.get(Calendar.MONTH) + 1;
		int day					= cal.get(Calendar.DATE);
		if (historyDAO.hasEntries(month, day) ) {
			Log.i(TAG, "fetchAndSaveHistoryEvents: fetching update not required");
			historyDAO.close();
			status.setTotalUpdated(0);
			
			return status;
		}
		
		historyURL.setLimit(50);
		
		List<DataModel> entries	= fdm.fetchAndParse(DataModelType.HISTORY, historyURL.buildURL() );
		int totalAnns						= entries.size();
		
		if (totalAnns == 0) {
			Log.i(TAG, "fetchAndSaveHistoryEvents: NO NEW HISTORY EVENTS");
			historyDAO.close();
			status.setTotalUpdated(0);
			
			return status;
		}
		
		History history;
		// Loop through downloaded articles and insert them to the database
		for (DataModel element: entries) {
			history	= (History) element;
			historyDAO.insert(history);
		}
		Log.i(TAG, "fetchAndSaveHistoryEvents: total downloaded: " + Integer.toString(totalAnns) );
		
		historyDAO.close();
		
		status.setTotalUpdated(totalAnns);
		
		return status;
	}


	/**
	 * Broadcasts RELOAD_VIEW messages to all activities that display content from the database.
	 */
	public void broadcastDataReload() {
		Intent intent	= new Intent();

		// Notify the news listing screen
		intent.setAction(NewsListActivity.RELOAD_VIEW);
		context.sendBroadcast(intent);

		// Notify the publications listing screen
		intent.setAction(PublicationListActivity.RELOAD_VIEW);
		context.sendBroadcast(intent);

		intent.setAction(AnnouncementListActivity.RELOAD_VIEW);
		context.sendBroadcast(intent);

		intent.setAction(HistoryListActivity.RELOAD_VIEW);
		context.sendBroadcast(intent);
	}


	public void broadcastDataReload_News() {
		Intent intent	= new Intent();
		intent.setAction(NewsListActivity.RELOAD_VIEW);
		context.sendBroadcast(intent);
	}
	
	
	public void broadcastDataReload_Publications() {
		Intent intent	= new Intent();
		intent.setAction(PublicationListActivity.RELOAD_VIEW);
		context.sendBroadcast(intent);
	}
	
	
	public void broadcastDataReload_Announcements() {
		Intent intent	= new Intent();
		intent.setAction(AnnouncementListActivity.RELOAD_VIEW);
		context.sendBroadcast(intent);
	}
	
	
	public void broadcastDataReload_History() {
		Intent intent	= new Intent();
		intent.setAction(HistoryListActivity.RELOAD_VIEW);
		context.sendBroadcast(intent);
	}


	/**
	 * Attempts to switch the top bar network activity indicator on. 
	 */
	public void broadcastNetworkActivity_On() {
		Intent intent	= new Intent();
		intent.setAction(ApplicationRootActivity.START_INDETERMINATE_PROGRESS);
		context.sendBroadcast(intent);
	}
	
	
	/**
	 * Attempts to switch the top bar network activity indicator off. 
	 */
	public void broadcastNetworkActivity_Off() {
		Intent intent	= new Intent();
		intent.setAction(ApplicationRootActivity.STOP_INDETERMINATE_PROGRESS);
		context.sendBroadcast(intent);
	}


	/**
	 * Convenience method that can be used to trigger the update process.
	 */
	public void run() {
		manageAndBroadcastUpdates(StatusMessageType.INIT);
	}

	/**
	 * Manages the process of updating content in a sequential manner.
	 * 
	 * If null is passed as an argument, the publications update is requested.
	 * Every single AsyncTask called by this method is expected to call it once their completed.
	 * And when this happens, the method triggers another update and also sends a message that activities can listen to 
	 * to update their data, e.g. list views.
	 * The order of tasks is as follows:
	 * 1. publications,
	 * 2. announcements,
	 * 3. history
	 */
	public void manageAndBroadcastUpdates(StatusMessageType status) {
		setRunning(true);

		switch (status) {
			case INIT:
				broadcastNetworkActivity_On();
				// Fetch news and opinions updates from the server.
				new UpdateArticlesTask().execute();
				break;

			case INIT_HISTORY:
				broadcastNetworkActivity_On();
				new UpdateHistoryTask().execute();
				break;
				
			case NEW_PUBLICATIONS:
				// Notify the news listing screen
				broadcastDataReload_News();
				
				// Notify the publications listing screen
				broadcastDataReload_Publications();

				// New publications have been downloaded, now request new announcements
				new UpdateAnnouncementsTask().execute();
				break;
			
			case NO_PUBLICATIONS:
				new UpdateAnnouncementsTask().execute();
				break;
				
				
			case NEW_PUBLICATION_IMAGES:
				// Notify the news listing screen
				broadcastDataReload_News();
				
				// Notify the publications listing screen
				broadcastDataReload_Publications();

				// We "know" the image update is actually triggered by UpdateArticlesTask so no actions required here.
				break;

			case NEW_ANNOUNCEMENTS:
				broadcastDataReload_Announcements();

				// New announcements have been downloaded, now request new history events
				new UpdateHistoryTask().execute();
				break;

			case NO_ANNOUNCEMENTS:
				new UpdateHistoryTask().execute();
				break;

			case NEW_HISTORY:
				broadcastDataReload_History();

				broadcastNetworkActivity_Off();
				setRunning(false);
				break;

			case NO_HISTORY:
				broadcastNetworkActivity_Off();
				setRunning(false);
				break;
		}
	}


	/**
	 * Manages publications update and calls the thumbnails update once completed.
	 * This is an <em>inner class</em>.
	 * @author Krzysztof Kobrzak
	 */
	private class UpdateArticlesTask extends AsyncTask<Void, Void, UpdateStatus> {

		@Override
		protected UpdateStatus doInBackground(Void... params) {
			UpdateStatus status					= (UpdateStatus) fetchAndSaveArticles(context);

			return status;
		}

		protected void onPostExecute(UpdateStatus status) {
			if (status.getTotalUpdated() == 0) {
				manageAndBroadcastUpdates(StatusMessageType.NO_PUBLICATIONS);
				return;
			}
			// Notify the update manager straight away so it can kick off the other update tasks.
			manageAndBroadcastUpdates(StatusMessageType.NEW_PUBLICATIONS);

			// We are still here and that means there is at least one thumbnail to be downloaded.
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

			if (fetchAndSaveArticleThumbnails(set, storageDir) ) {
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
			manageAndBroadcastUpdates(StatusMessageType.NEW_PUBLICATION_IMAGES);
		}
	}


	private class UpdateAnnouncementsTask extends AsyncTask<Void, Integer, UpdateStatus> {

		@Override
		protected UpdateStatus doInBackground(Void... params) {
			UpdateStatus status					= (UpdateStatus) fetchAndSaveAnnouncements(context);

			return status;
		}

		protected void onPostExecute(UpdateStatus status) {
			if (status.getTotalUpdated() == 0) {
				manageAndBroadcastUpdates(StatusMessageType.NO_ANNOUNCEMENTS);
				return;
			}
			manageAndBroadcastUpdates(StatusMessageType.NEW_ANNOUNCEMENTS);
		}
	}


	private class UpdateHistoryTask extends AsyncTask<Void, Integer, UpdateStatus> {

		@Override
		protected UpdateStatus doInBackground(Void... params) {
			UpdateStatus status					= (UpdateStatus) fetchAndSaveHistoryEvents(context);

			return status;
		}

		protected void onPostExecute(UpdateStatus status) {
			if (status.getTotalUpdated() == 0) {
				manageAndBroadcastUpdates(StatusMessageType.NO_HISTORY);
				return;
			}
			manageAndBroadcastUpdates(StatusMessageType.NEW_HISTORY);
		}
	}
}
