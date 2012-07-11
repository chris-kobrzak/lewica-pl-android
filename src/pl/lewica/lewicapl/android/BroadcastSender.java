package pl.lewica.lewicapl.android;

import pl.lewica.api.model.DataModelType;
import pl.lewica.lewicapl.android.ApplicationRootActivity.Tab;
import pl.lewica.lewicapl.android.activity.AnnouncementListActivity;
import pl.lewica.lewicapl.android.activity.BlogPostListActivity;
import pl.lewica.lewicapl.android.activity.HistoryListActivity;
import pl.lewica.lewicapl.android.activity.NewsListActivity;
import pl.lewica.lewicapl.android.activity.PublicationListActivity;
import android.content.Context;
import android.content.Intent;


public class BroadcastSender {

	private static BroadcastSender _instance;
	private Context context;


	private BroadcastSender(Context context) {
		this.context		= context;
	}


	/**
	 * Standard singleton constructor.
	 * @param context
	 * @return BroadcastSender
	 */
	public static synchronized BroadcastSender getInstance(Context context) {
		if (_instance == null) {
			_instance	= new BroadcastSender(context);
		}

		return _instance;
	}


	/**
	 * Attempts to switch the top bar network activity indicator on or off by notifying the application activity.  
	 */
	public void indicateDeviceNetworkActivity(boolean show) {
		String action;

		if (show) {
			action 	= ApplicationRootActivity.START_INDETERMINATE_PROGRESS;
		} else {
			action	= ApplicationRootActivity.STOP_INDETERMINATE_PROGRESS;
		}

		Intent intent	= new Intent();
		intent.setAction(action);
		context.sendBroadcast(intent);
	}


	/**
	 * This should be used when you know what tab should be reloaded.
	 * This might be triggered by the user.
	 * @param tab
	 */
	public void reloadTab(ApplicationRootActivity.Tab tab) {
		String action;

		switch (tab) {
			case NEWS:
				action	= NewsListActivity.RELOAD_VIEW;
			break;

			case ARTICLES:
				action	= PublicationListActivity.RELOAD_VIEW;
			break;

			case BLOGS:
				action	= BlogPostListActivity.RELOAD_VIEW;
			break;

			case ANNOUNCEMENTS:
				action	= AnnouncementListActivity.RELOAD_VIEW;
			break;

			case HISTORY:
				action	= HistoryListActivity.RELOAD_VIEW;
			break;

			default:
				return;
		}

		Intent intent	= new Intent();
		intent.setAction(action);
		context.sendBroadcast(intent);
	}


	/**
	 * Broadcasts RELOAD_VIEW messages to all activities that display content from the database.
	 */
	public void reloadAllTabs() {
		reloadTab(Tab.NEWS);
		reloadTab(Tab.ARTICLES);
		reloadTab(Tab.BLOGS);
		reloadTab(Tab.ANNOUNCEMENTS);
		reloadTab(Tab.HISTORY);
	}


	/**
	 * This should be used when you know what has been updated in the database.
	 * @param dataType
	 */
	public void reloadTabsOnDataUpdate(DataModelType dataType) {
		switch (dataType) {
			case ARTICLE:
				reloadTab(Tab.NEWS);
				reloadTab(Tab.ARTICLES);
				break;
				
			case BLOG_POST:
				reloadTab(Tab.BLOGS);
				break;
				
			case ANNOUNCEMENT:
				reloadTab(Tab.ANNOUNCEMENTS);
				break;
				
			case HISTORY:
				reloadTab(Tab.HISTORY);
				break;
				
			default:
				return;
		}
	}

}
