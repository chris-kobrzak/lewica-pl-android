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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import pl.lewica.lewicapl.android.UserPreferencesManager;
import pl.lewica.lewicapl.android.database.BlogPostDAO;
import pl.lewica.lewicapl.android.theme.Theme;


/**
 * @author Krzysztof Kobrzak
 */
public class BlogPostListActivity extends Activity {

	public static final String RELOAD_VIEW	= "pl.lewica.lewicapl.android.activity.blogpostlistactivity.RELOAD";


	// Currently it's only possible to filter the list by blog ID.
	public static enum dataFilters {
		BLOG_ID
	}

	// When users select a new post, navigate back to the list and start scrolling up and down, the cursor won't know this article should be marked as read.
	// That results in articles still being marked as unread (titles in red rather than blue).
	// That's why we need to cache the list of clicked articles.  Please note, it is down to BlogPostActivity to flag articles as read in the database.
	private static Set<Long> clicked	= new HashSet<Long>();

	private BlogPostDAO blogPostDAO;
	private ListAdapter listAdapter;
	private ListView listView;
	private BroadcastReceiver receiver;
	private static Theme appTheme;

	private int limitRows		= 15;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_blog_posts);	// This comes from this file's name /res/list_blog_posts.xml

		// Load a list view container from list_blog_posts.xml
		listView					= (ListView) findViewById(R.id.list_blog_posts);

		// Register to receive content update messages
		IntentFilter filter		= new IntentFilter();
		filter.addAction(RELOAD_VIEW);
		receiver					= new BlogPostsUpdateBroadcastReceiver();	// Instance of an inner class
		registerReceiver(receiver, filter);

		// Access data
		blogPostDAO					= new BlogPostDAO(this);
		blogPostDAO.open();
		Cursor cursor			= blogPostDAO.selectLatest(limitRows);

		// Set list view adapter - this links the view with the data
		listAdapter				= new BlogPostsCursorAdapter(this, cursor, false);
		listView.setAdapter(listAdapter);

		// Clicking on an item should redirect to the details view
		listView.setOnItemClickListener(new BlogPostClickListener() );

		appTheme	= UserPreferencesManager.getThemeInstance(getApplicationContext() );
		appTheme.setListViewDividerColour(listView, this);
	}


	private void reloadRows() {
		CursorAdapter ca	= (CursorAdapter) listAdapter;
		// Reload rows
		Cursor newCursor	= blogPostDAO.selectLatest(limitRows);
		ca.changeCursor(newCursor);
	}


	private void reloadRowsFilterByBlogID(int blogID) {
		CursorAdapter ca	= (CursorAdapter) listAdapter;
		// Reload rows
		Cursor newCursor	= blogPostDAO.selectLatestByBlogID(blogID, limitRows * 2);
		ca.changeCursor(newCursor);
	}


	// INNER CLASSES
	private class BlogPostClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Context context		= getApplicationContext();

			// Redirect to article details screen
			Intent intent	= new Intent(context, BlogPostActivity.class);
			// Builds a uri in the following format: content://lewicapl/articles/article/[0-9]+
			Uri uri			= Uri.parse(BlogPostActivity.BASE_URI + Long.toString(id) );
			// Passes activity Uri as parameter that can be used to work out ID of requested article.
			intent.setData(uri);
			startActivity(intent);

			// Mark current blog post as read by changing its colour...
			TextView tv			= (TextView) view.findViewById(R.id.blog_post_item_title);
			appTheme	= UserPreferencesManager.getThemeInstance(context);
			tv.setTextColor(appTheme.getListHeadingColour(true) );
			// ... and flagging it in local cache accordingly
			clicked.add(id);

			return;
		}
	}


	private class BlogPostsUpdateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			appTheme	= UserPreferencesManager.getThemeInstance(getApplicationContext() );
			if (! intent.hasExtra(BlogPostListActivity.dataFilters.BLOG_ID.name() ) ) {
				reloadRows();
			} else {
				int blogID		= intent.getIntExtra(BlogPostListActivity.dataFilters.BLOG_ID.name(), 0);
				reloadRowsFilterByBlogID(blogID);
			}
			appTheme.setListViewDividerColour(listView, context);
		}
	}



	/**
	 * Populates the list and makes sure blog posts that have already been read are marked accordingly.
	 * It is a static nested class, see http://download.oracle.com/javase/tutorial/java/javaOO/nested.html
	 * @author Krzysztof Kobrzak
	 */
	private static final class BlogPostsCursorAdapter extends CursorAdapter {

		public LayoutInflater inflater;

		private int inxID;
		private int inxWasRead;
		private int inxDatePub;
		private int inxTitle;
		private int inxAuthor;
		private int inxBlog;

		private static SimpleDateFormat dateFormat	= new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);



		BlogPostsCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
			super(context, cursor, autoRequery);

			// Get the layout inflater
			inflater				= LayoutInflater.from(context);

			// Get and cache column indices
			inxID					= cursor.getColumnIndex(BlogPostDAO.FIELD_ID);
			inxWasRead			= cursor.getColumnIndex(BlogPostDAO.FIELD_WAS_READ);
			inxDatePub			= cursor.getColumnIndex(BlogPostDAO.FIELD_DATE_PUBLISHED);
			inxTitle				= cursor.getColumnIndex(BlogPostDAO.FIELD_TITLE);
			inxAuthor				= cursor.getColumnIndex(BlogPostDAO.FIELD_AUTHOR);
			inxBlog				= cursor.getColumnIndex(BlogPostDAO.FIELD_BLOG_TITLE);
		}

		/**
		 * Responsible for providing views with content and formatting it.
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			appTheme	= UserPreferencesManager.getThemeInstance(context);

			boolean unread	= cursor.getInt(inxWasRead) == 0 && ! clicked.contains(cursor.getLong(inxID) );
			TextView tvTitle	= (TextView) view.findViewById(R.id.blog_post_item_title);
			tvTitle.setTextColor(appTheme.getListHeadingColour(! unread) );
			tvTitle.setText(cursor.getString(inxAuthor) + ": " + cursor.getString(inxTitle) );

			TextView tvDate	= (TextView) view.findViewById(R.id.blog_post_item_date);
			long unixTime	= cursor.getLong(inxDatePub);
			Date d				= new Date(unixTime);
			tvDate.setText(dateFormat.format(d) );

			TextView tvBlog	= (TextView) view.findViewById(R.id.blog_post_item_blog_title);
			String blogTitle	= cursor.getString(inxBlog);
			if (blogTitle.length() > 0) {
				tvBlog.setText(blogTitle);
				tvBlog.setVisibility(View.VISIBLE);
			} else {
				tvBlog.setText("");
				tvBlog.setVisibility(View.GONE);
			}

			tvDate.setTextColor(appTheme.getListTextColour(! unread) );
			tvBlog.setTextColor(appTheme.getListTextColour(! unread) );
			view.setBackgroundColor(appTheme.getBackgroundColour() );
		}


		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.list_blog_posts_item, parent, false);
		}
	}
	// End of NewsCursorAdapter
}
