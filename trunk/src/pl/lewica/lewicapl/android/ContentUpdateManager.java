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
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import pl.lewica.URLDictionary;
import pl.lewica.api.FeedDownloadManager;
import pl.lewica.api.model.Announcement;
import pl.lewica.api.model.Article;
import pl.lewica.api.model.BlogPost;
import pl.lewica.api.model.History;
import pl.lewica.api.model.DataModel;
import pl.lewica.api.model.DataModelType;
import pl.lewica.api.url.AnnouncementURL;
import pl.lewica.api.url.ArticleURL;
import pl.lewica.api.url.BlogPostURL;
import pl.lewica.api.url.HistoryURL;
import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.database.AnnouncementDAO;
import pl.lewica.lewicapl.android.database.ArticleDAO;
import pl.lewica.lewicapl.android.database.BlogPostDAO;
import pl.lewica.lewicapl.android.database.HistoryDAO;
import pl.lewica.util.DateUtil;
import pl.lewica.util.FileUtil;
import pl.lewica.util.LanguageUtil;


/**
 * Collection of methods that coordinate the process of downloading feeds from the server,
 * saving them in the device's database and sending notifications about its progress.
 * The class uses the singleton design pattern.
 * @author Krzysztof Kobrzak
 */
public class ContentUpdateManager {
	private static final String TAG	= "ContentUpdateManager";

	private static ContentUpdateManager instance;

	private File storageDir;
	private Context context;
	private BroadcastSender broadcastSender;
	private boolean isRunning;
	private int lastUpdated	= 1;		// This actually stores a time stamp the action was triggered on, not completed at.
	private int timeout			= 20;	// Seconds

	public static enum CommandType {
		INIT,
		INIT_HISTORY,
		NEW_PUBLICATIONS,
		NO_PUBLICATIONS,
		NEW_PUBLICATION_IMAGES,
		NEW_ANNOUNCEMENTS,
		NO_ANNOUNCEMENTS,
		NEW_HISTORY,
		NO_HISTORY,
		NEW_BLOG_POSTS,
		NO_BLOG_POSTS
	}


	/**
	 * Private constructor, see singleton design pattern description.
	 * @param context
	 * @param storageDir
	 */
	private ContentUpdateManager(Context context, File storageDir) {
		this.context		= context;
		this.storageDir	= storageDir;
		this.broadcastSender	= BroadcastSender.getInstance(context);
	}


	/**
	 * Standard singleton constructor.
	 * @param context
	 * @param storageDir
	 * @return ContentUpdateManager
	 */
	public static synchronized ContentUpdateManager getInstance(Context context, File storageDir) {
		if (instance == null) {
			instance	= new ContentUpdateManager(context, storageDir);
		}

		return instance;
	}


	/**
	 * This method patches a bug that prevents the object from notifying ApplicationRootActivity that it completed the update process.
	 * As a result of this bug the toolbar indicator keeps spinning around and users are unable to restart the update process manually.
	 * An assumption here is the update should never take more than [timeout] seconds and if it does we treat it as if it timed out
	 * and mark the update as not running, reset lastUpdated and also notify the application activity to stop any visual update indicators.
	 * @return boolean
	 */
	public boolean isRunning() {
		int sinceLastUpdate	= DateUtil.getCurrentUnixTime() % lastUpdated; 
		if (isRunning && sinceLastUpdate > timeout) {
			broadcastSender.indicateDeviceNetworkActivity(false);
			this.lastUpdated		= 1;
			this.isRunning		= false;
		}
		return isRunning;
	}


	/**
	 * Sets the isRunning boolean.
	 * Before it does it it checks if the current value is set to false and the new - to true, meaning they are starting the update process, 
	 * not running one of their chained parts.
	 * If this is the case the lastUpdated time stamp is also set to the current time.
	 * @param isRunning
	 */
	private void setRunning(boolean isRunning) {
		if (! this.isRunning && isRunning) {
			setLastUpdated(DateUtil.getCurrentUnixTime() );
		}
		this.isRunning = isRunning;
	}


	/**
	 * Standard getter for lastUpdated.
	 * @return int
	 */
	public int getLastUpdated() {
		return lastUpdated;
	}


	/**
	 * Standard setter for lastUpdated.
	 * @param lastUpdated Unix time stamp
	 */
	public void setLastUpdated(int lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	
	/**
	 * If lastUpdated is set to its default value i.e. 1 (meaning the update's never run) the method returns 
	 * the current Unix timestamp.  That should be good enough an indicator that it's time to run another update.
	 * Otherwise it just returns the number of seconds since the last update that is calculated on the basis of lastUpdated.
	 * @return
	 */
	public int getIntervalSinceLastUpdate() {
		int currentTimestamp	= DateUtil.getCurrentUnixTime();
		if (lastUpdated == 1) {
			return currentTimestamp;
		}
		return currentTimestamp % lastUpdated;
	}


	/**
	 * Coordinates the process of downloading the articles feed and inserting data to the database.
	 * @param context
	 * @return UpdateStatus
	 */
	private UpdateStatus fetchAndSaveArticles(Context context) {
		ArticleDAO articleDAO	= new ArticleDAO(context);
		articleDAO.open();
		int lastArticleID		= articleDAO.fetchLastID();

		ArticleURL articleURL	= new ArticleURL();
		articleURL.setSectionList("1,2,3,4,5");	// TODO This should be done using an array, not a list.
		articleURL.setNewerThan(lastArticleID);
		articleURL.setLimit(5);

		FeedDownloadManager fdm	= new FeedDownloadManager();
		List<DataModel> articles	= fdm.fetchAndParse(DataModelType.ARTICLE, articleURL.buildURL() );
		int totalArticles			= articles.size();

		ArticleUpdateStatus status	= new ArticleUpdateStatus();
		if (totalArticles == 0) {
			articleDAO.close();
			status.setTotalUpdated(0);

			return status;
		}

		// A container for images to be downloaded.  Order of downloads doesn't matter hence Set instead of ArrayList.
		Set<Map<String, String>> set	= new HashSet<Map<String, String>>();

		// Loop through downloaded articles and insert them to the database
		for (DataModel element: articles) {
			Article article	= (Article) element;
			articleDAO.insert(article);

			if (! article.hasThumbnail() ) {
				continue;
			}

			// We are not downloading thumbnails straight away.
			// We're merely listing them here so we can download them in a background thread once this AsyncTask has completed.
			// This is to be able to show them updated content as soon as possible since text downloads are much faster than images.
			Map<String,String> imageMeta	= new HashMap<String,String>();
			imageMeta.put("ID", Integer.toString(article.getID() ) );
			imageMeta.put("Ext", article.getImageExtension() );
			set.add(imageMeta);
		}

		articleDAO.close();

		status.setTotalUpdated(totalArticles);
		status.setImages(set);

		return status;
	}


	/**
	 * Coordinates the process of downloading images linked with publications and saving them in the file system.
	 * 
	 * @param imageSet
	 * @param storageDir
	 * @return boolean
	 */
	private boolean fetchAndSaveArticleThumbnails(Set<Map<String,String>> imageSet, File storageDir) {
		if (! storageDir.canWrite() ) {
			return false;
		}

		Iterator<Map<String, String>> iter	= imageSet.iterator();
		while (iter.hasNext() ) {
			Map<String,String> imageMeta	= iter.next();
			long ID	= Long.parseLong(imageMeta.get("ID") );

			String imageName		= ArticleURL.buildNameThumbnail(ID, imageMeta.get("Ext") );
			// Source image (on the server)
			String imageURL			= URLDictionary.THUMBNAIL + imageName;
			// Destination image (on phone's storage)
			String imagePath		= storageDir.getPath() + "/" + imageName;

			try {
				return FileUtil.fetchAndSaveImage(imageURL, imagePath, false);
			} catch (MalformedURLException e) {
				Log.w(TAG, "MalformedURLException: " + e.getMessage() );
			} catch (IOException e) {
				Log.w(TAG, "IOException: " + e.getMessage() );
			}
		}

		return true;
	}


	/**
	 * Coordinates the process of downloading the blog entries feed and inserting data to the database.
	 * @param context
	 * @return UpdateStatus
	 */
	private UpdateStatus fetchAndSaveBlogPosts(Context context) {
		BlogPostDAO blogPostDAO		= new BlogPostDAO(context);
		blogPostDAO.open();
		int lastBlogPostID			= blogPostDAO.fetchLastID();

		BlogPostURL blogPostURL		= new BlogPostURL();
		blogPostURL.setNewerThan(lastBlogPostID);
		blogPostURL.setLimit(15);

		FeedDownloadManager fdm		= new FeedDownloadManager();
		List<DataModel> blogPosts	= fdm.fetchAndParse(DataModelType.BLOG_POST, blogPostURL.buildURL() );
		int totalBlogPosts			= blogPosts.size();

		UpdateStatus status			= new UpdateStatus();
		if (totalBlogPosts == 0) {
			blogPostDAO.close();
			status.setTotalUpdated(0);

			return status;
		}

		BlogPost blogPost;
		// Loop through downloaded blog entries and insert them to the database
		for (DataModel element: blogPosts) {
			blogPost	= (BlogPost) element;
			blogPostDAO.insert(blogPost);
		}

		blogPostDAO.close();
		status.setTotalUpdated(totalBlogPosts);

		return status;
	}


	/**
	 * Coordinates the process of downloading the announcements feed and inserting data to the database.
	 * @param context
	 * @return UpdateStatus
	 */
	private UpdateStatus fetchAndSaveAnnouncements(Context context) {
		AnnouncementDAO annDao	= new AnnouncementDAO(context);
		annDao.open();
		int lastAnnID			= annDao.fetchLastID();

		AnnouncementURL annUrl	= new AnnouncementURL();
		annUrl.setNewerThan(lastAnnID);
		annUrl.setLimit(10);

		FeedDownloadManager fdm	= new FeedDownloadManager();
		List<DataModel> anns	= fdm.fetchAndParse(DataModelType.ANNOUNCEMENT, annUrl.buildURL() );
		int totalAnns			= anns.size();

		UpdateStatus status		= new UpdateStatus();
		if (totalAnns == 0) {
			annDao.close();
			status.setTotalUpdated(0);

			return status;
		}

		Announcement ann;
		// Loop through downloaded articles and insert them to the database
		for (DataModel element: anns) {
			ann	= (Announcement) element;
			annDao.insert(ann);
		}

		annDao.close();
		status.setTotalUpdated(totalAnns);

		return status;
	}


	/**
	 * Coordinates the process of downloading the history feed and inserting data to the database.
	 * @param context
	 * @return UpdateStatus
	 */
	private UpdateStatus fetchAndSaveHistoryEvents(Context context) {
		HistoryDAO historyDao	= new HistoryDAO(context);
		historyDao.open();

		// We are only going to be pulling new records if the local database doesn't have any entries for a given day.
		// This is to avoid duplicates in the database.
		Calendar cal			= Calendar.getInstance();
		int month				= cal.get(Calendar.MONTH) + 1;
		int day					= cal.get(Calendar.DATE);
		UpdateStatus status		= new UpdateStatus();
		if (historyDao.hasEntriesForDate(month, day) ) {
			historyDao.close();
			status.setTotalUpdated(0);

			return status;
		}

		HistoryURL historyUrl	= new HistoryURL();
		historyUrl.setLimit(50);

		FeedDownloadManager fdm	= new FeedDownloadManager();
		List<DataModel> entries	= fdm.fetchAndParse(DataModelType.HISTORY, historyUrl.buildURL() );
		int totalEntries		= entries.size();

		if (totalEntries == 0) {
			historyDao.close();
			status.setTotalUpdated(0);

			return status;
		}

		History history;
		// Loop through downloaded articles and insert them to the database
		for (DataModel element: entries) {
			history	= (History) element;
			historyDao.insert(history);
		}

		historyDao.close();

		status.setTotalUpdated(totalEntries);

		return status;
	}


	/**
	 * Convenience method that can be used to trigger the update process.
	 */
	public void run() {
		manageAndBroadcastUpdates(CommandType.INIT, true);
	}


	public void runSingle(CommandType command) {
		manageAndBroadcastUpdates(command, false);
	}


	/**
	 * Manages the process of updating content in a sequential manner.
	 * 
	 * The method calls one subclass of AsyncTask at a time and expects their onPostExecute methods to report back whether there are any updates or not.
	 * If there are, broadcastDataReload methods are called and if the second argument is set to true, the next task is executed.
	 * broadcastDataReload methods send messages that activities can listen to in order to update their views.
	 * The order of tasks is as follows:
	 *   1. publications,
	 *   2. announcements,
	 *   3. history
	 * @param command The action which the method starts the updates from.
	 * @param chainReaction Tells the method if it should continue running other updates as per the order of tasks specified in the method description.
	 */
	private void manageAndBroadcastUpdates(CommandType command, boolean chainReaction) {
		setRunning(true);

		// Please note, the order of the case statements matters!
		switch (command) {
			case INIT:
				broadcastSender.indicateDeviceNetworkActivity(true);
				// Fetch news and opinions updates from the server.
				new UpdateArticlesTask().execute();
				break;

			case INIT_HISTORY:
				broadcastSender.indicateDeviceNetworkActivity(true);
				new UpdateHistoryTask().execute();
				break;

			case NEW_PUBLICATIONS:
				// Notify the news and publication listing screens
				broadcastSender.reloadTabsOnDataUpdate(DataModelType.ARTICLE);

				// No break statement here, just let it jump to the next NO_PUBLICATIONS case that will take care of deciding what to do next.

			case NO_PUBLICATIONS:
				// After publications come the blog stuff
				if (chainReaction) {
					new UpdateBlogPostsTask().execute();
				} else {
					broadcastSender.indicateDeviceNetworkActivity(false);
					setRunning(false);
				}
				break;

			case NEW_PUBLICATION_IMAGES:
				// Notify the news and articles listing screens
				broadcastSender.reloadTabsOnDataUpdate(DataModelType.ARTICLE);

				// We "know" the image update is actually triggered by UpdateArticlesTask so no actions required here.
				break;

			case NEW_BLOG_POSTS:
				broadcastSender.reloadTabsOnDataUpdate(DataModelType.BLOG_POST);

				// No break statement here, just let it jump to the next NO_BLOG_ENTRIES case that will take care of deciding what to do next.

			case NO_BLOG_POSTS:
				// After blog come the announcements
				if (chainReaction) {
					new UpdateAnnouncementsTask().execute();
				} else {
					broadcastSender.indicateDeviceNetworkActivity(false);
					setRunning(false);
				}
				break;

			case NEW_ANNOUNCEMENTS:
				broadcastSender.reloadTabsOnDataUpdate(DataModelType.ANNOUNCEMENT);

				// No break statement here, just let it jump to the next NO_ANNOUNCEMENTS case that will take care of deciding what to do next.

			case NO_ANNOUNCEMENTS:
				// After announcements come the history
				if (chainReaction) {
					new UpdateHistoryTask().execute();
				} else {
					broadcastSender.indicateDeviceNetworkActivity(false);
					setRunning(false);
				}
				break;

			case NEW_HISTORY:
				broadcastSender.reloadTabsOnDataUpdate(DataModelType.HISTORY);

				// No break statement here, just let it jump to the next NO_HISTORY case that will take care of deciding what to do next.

			case NO_HISTORY:
				broadcastSender.indicateDeviceNetworkActivity(false);
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
			UpdateStatus status					= fetchAndSaveArticles(context);

			return status;
		}

		@Override
		protected void onPostExecute(UpdateStatus status) {
			int totalUpdates	= status.getTotalUpdated();
			if (totalUpdates == 0) {
				manageAndBroadcastUpdates(CommandType.NO_PUBLICATIONS, true);
				return;
			}
			// Notify the update manager straight away so it can kick off the other update tasks.
			manageAndBroadcastUpdates(CommandType.NEW_PUBLICATIONS, true);

			Toast.makeText(context, getArticlesUpdateMessage(totalUpdates), Toast.LENGTH_SHORT).show();

			// We are still here and that means there is at least one thumbnail to be downloaded.
			new DownloadArticleThumbnailsTask().execute(status);
		}


		private String getArticlesUpdateMessage(int totalUpdated) {
			int messageId	= R.string.updated_1_article;

			if (totalUpdated == 1) {
				return context.getString(messageId);
			}
			if (LanguageUtil.isPolishAccusative(totalUpdated) ) {
				messageId		= R.string.updated_2_articles;
			} else if (LanguageUtil.isPolishGenitive(totalUpdated) ) {
				messageId		= R.string.updated_5_articles;
			}
			return context.getString(messageId).replace("%s", Integer.toString(totalUpdated) );
		}
	}


	private class DownloadArticleThumbnailsTask extends AsyncTask<UpdateStatus, Void, Integer> {

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

		@Override
		protected void onPostExecute(Integer status) {
			if (status == 0) {
				return;
			}
			// Images task is branched out by the articles task so we want to be strict and set the chainReaction argument to false.
			manageAndBroadcastUpdates(CommandType.NEW_PUBLICATION_IMAGES, false);
		}
	}
	
	
	private class UpdateBlogPostsTask extends AsyncTask<Void, Integer, UpdateStatus> {
		
		@Override
		protected UpdateStatus doInBackground(Void... params) {
			UpdateStatus status					= fetchAndSaveBlogPosts(context);
			
			return status;
		}
		
		@Override
		protected void onPostExecute(UpdateStatus status) {
			int totalUpdates	= status.getTotalUpdated();
			if (totalUpdates == 0) {
				manageAndBroadcastUpdates(CommandType.NO_BLOG_POSTS, true);
				return;
			}
			manageAndBroadcastUpdates(CommandType.NEW_BLOG_POSTS, true);

			Toast.makeText(context, getBlogPostsUpdateMessage(totalUpdates), Toast.LENGTH_SHORT).show();
		}


		private String getBlogPostsUpdateMessage(int totalUpdated) {
			int messageId	= R.string.updated_1_blog_entry;
			
			if (totalUpdated == 1) {
				return context.getString(messageId);
			}
			if (LanguageUtil.isPolishAccusative(totalUpdated) ) {
				messageId		= R.string.updated_2_blog_entries;
			} else if (LanguageUtil.isPolishGenitive(totalUpdated) ) {
				messageId		= R.string.updated_5_blog_entries;
			}
			return context.getString(messageId).replace("%s", Integer.toString(totalUpdated) );
		}
	}


	private class UpdateAnnouncementsTask extends AsyncTask<Void, Integer, UpdateStatus> {

		@Override
		protected UpdateStatus doInBackground(Void... params) {
			UpdateStatus status					= fetchAndSaveAnnouncements(context);

			return status;
		}

		@Override
		protected void onPostExecute(UpdateStatus status) {
			int totalUpdates	= status.getTotalUpdated();
			if (totalUpdates == 0) {
				manageAndBroadcastUpdates(CommandType.NO_ANNOUNCEMENTS, true);
				return;
			}
			manageAndBroadcastUpdates(CommandType.NEW_ANNOUNCEMENTS, true);

			Toast.makeText(context, getAnnouncementsUpdateMessage(totalUpdates), Toast.LENGTH_SHORT).show();
		}


		private String getAnnouncementsUpdateMessage(int totalUpdated) {
			int messageId	= R.string.updated_1_announcement;
			
			if (totalUpdated == 1) {
				return context.getString(messageId);
			}
			if (LanguageUtil.isPolishAccusative(totalUpdated) ) {
				messageId		= R.string.updated_2_announcements;
			} else if (LanguageUtil.isPolishGenitive(totalUpdated) ) {
				messageId		= R.string.updated_5_announcements;
			}
			return context.getString(messageId).replace("%s", Integer.toString(totalUpdated) );
		}
	}


	private class UpdateHistoryTask extends AsyncTask<Void, Integer, UpdateStatus> {

		@Override
		protected UpdateStatus doInBackground(Void... params) {
			UpdateStatus status					= fetchAndSaveHistoryEvents(context);

			return status;
		}

		@Override
		protected void onPostExecute(UpdateStatus status) {
			if (status.getTotalUpdated() == 0) {
				manageAndBroadcastUpdates(CommandType.NO_HISTORY, true);
				return;
			}
			manageAndBroadcastUpdates(CommandType.NEW_HISTORY, true);
		}
	}
}
