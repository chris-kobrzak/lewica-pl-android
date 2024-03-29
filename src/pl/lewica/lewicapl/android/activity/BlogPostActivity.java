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
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import pl.lewica.URLDictionary;
import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.AndroidUtil;
import pl.lewica.lewicapl.android.ApplicationRootActivity;
import pl.lewica.lewicapl.android.BroadcastSender;
import pl.lewica.lewicapl.android.UserPreferencesManager;
import pl.lewica.lewicapl.android.activity.util.StandardTextScreen;
import pl.lewica.lewicapl.android.activity.util.TextSizeChangeListener;
import pl.lewica.lewicapl.android.activity.util.TextSizeDialog;
import pl.lewica.lewicapl.android.activity.util.MessageSharingChooser;
import pl.lewica.lewicapl.android.database.BlogPostDAO;
import pl.lewica.lewicapl.android.theme.Theme;


public class BlogPostActivity extends Activity implements StandardTextScreen {
	// This intent's base Uri.  It should have a numeric ID appended to it.
	public static final String BASE_URI	= "content://lewicapl/blog_posts/blog_post/";

	private static Typeface categoryTypeface;

	private int blogPostId;
	private int blogId;
	private BlogPostDAO blogPostDAO;
	private Map<String,Integer> nextPrevId;
	private String blogPostURL;
	private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;

	private TextView tvTitle;
	private TextView tvContent;
	private TextView tvAuthor;

	private static SimpleDateFormat dateFormat	= new SimpleDateFormat("dd/MM/yyyy HH:mm");



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_blog_post);

		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		// Access data
		blogPostDAO				= new BlogPostDAO(this);
		blogPostDAO.open();

		mSeekBarChangeListener	= new TextSizeChangeListener(this, this);

		// When user changes the orientation, Android restarts the activity.  Say, users navigated through articles using
		// the previous-next facility; if they subsequently changed the screen orientation, they would've ended up on the original
		// article that was loaded through the intent.  In other words, changing the orientation would change the article displayed...
		// The logic below fixes this issue and it's using the ID set by onRetainNonConfigurationInstance (see docs for details).
		final Integer id	= (Integer) getLastNonConfigurationInstance();
		if (id == null) {
			blogPostId = AndroidUtil.filterIdFromUri(getIntent().getData());
		} else {
			blogPostId = id;
		}

		// Fill views with data
		loadContent(blogPostId, this);
		loadTextSize(UserPreferencesManager.getTextSize(this) );
		loadTheme(getApplicationContext() );

		AndroidUtil.setApplicationTitleBackgroundColour(getResources().getColor(R.color.red), this);
	}


	/**
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		
		blogPostDAO.close();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater infl	= getMenuInflater();
		infl.inflate(R.menu.menu_blog_post, menu);
		
		return true;
	}


	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		MenuItem showPrevious	= menu.getItem(0);
		MenuItem showNext		= menu.getItem(1);

		showPrevious.setEnabled(true);
		showNext.setEnabled(true);

		nextPrevId = blogPostDAO.fetchPreviousNextId(blogPostId);

		long id	= nextPrevId.get(BlogPostDAO.MAP_KEY_PREVIOUS);
		if (id == 0) {
			showPrevious.setEnabled(false);
		}
		id	= nextPrevId.get(BlogPostDAO.MAP_KEY_NEXT);
		if (id == 0) {
			showNext.setEnabled(false);
		}

		return true;
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		long id;

		switch (item.getItemId()) {
			case R.id.menu_previous:
				id	= nextPrevId.get(BlogPostDAO.MAP_KEY_PREVIOUS);
				// If there are no newer blog posts the ID is set to zero by fetchPreviousNextID()
				if (id > 0) {
					loadContent(nextPrevId.get(BlogPostDAO.MAP_KEY_PREVIOUS), this);
				}
				return true;

			case R.id.menu_next:
				id	= nextPrevId.get(BlogPostDAO.MAP_KEY_NEXT);
				// If there are no older blog posts the ID is set to zero by fetchPreviousNextID()
				if (id > 0) {
					loadContent(nextPrevId.get(BlogPostDAO.MAP_KEY_NEXT), this);
				}
				return true;

			case R.id.menu_share:
				Intent chooserIntent = MessageSharingChooser.getIntent(getString(R.string.label_share_link), blogPostURL);
				startActivity(chooserIntent);
				return true;

			case R.id.menu_other_posts:
				BroadcastSender broadcastSender	= BroadcastSender.getInstance(this);
				broadcastSender.reloadTab_BlogPostListFilteredByBlogId(blogId);
				finish();
				return true;

			case R.id.menu_change_text_size:
				TextSizeDialog.showDefaultTextSizeWidget(mSeekBarChangeListener, this);

				return true;

			case R.id.menu_change_background:
				UserPreferencesManager.switchUserTheme(getApplicationContext() );
				loadTheme(getApplicationContext() );
				ApplicationRootActivity.reloadAllTabsInBackground(getApplicationContext() );

				return true;

			default :
				return super.onOptionsItemSelected(item);
		}
	}


	/**
	 * Caches the current blog post ID.  Called when device orientation changes.
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		final int id = blogPostId;
		return id;
	}




	/**
	 * Responsible for loading content to the views and marking the current blog post as read.
	 * It is meant to be called every time user accesses this activity, either by selecting an blog post from the list
	 * or navigating between blog posts using the previous-next facility (not yet implemented).
	 */
	private void loadContent(int id, Context context) {
		// Save it in this object's field
		blogPostId = id;
		// Fetch database record
		Cursor cursor				= blogPostDAO.selectOne(id);

		startManagingCursor(cursor);

		// In order to capture a cell, you need to work what their index
		int inxWasRead		= cursor.getColumnIndex(BlogPostDAO.FIELD_WAS_READ);
		int inxTitle			= cursor.getColumnIndex(BlogPostDAO.FIELD_TITLE);
		int inxText			= cursor.getColumnIndex(BlogPostDAO.FIELD_TEXT);
		int inxBlogId			= cursor.getColumnIndex(BlogPostDAO.FIELD_BLOG_ID);
		int inxBlog				= cursor.getColumnIndex(BlogPostDAO.FIELD_BLOG_TITLE);
		int inxDatePub		= cursor.getColumnIndex(BlogPostDAO.FIELD_DATE_PUBLISHED);
		int inxPublishedBy	= cursor.getColumnIndex(BlogPostDAO.FIELD_AUTHOR);

		// When using previous-next facility you need to make sure the scroll view's position is at the top of the screen
		AndroidUtil.scrollToTop(R.id.blog_post_scroll_view, this);

		blogId = cursor.getInt(inxBlogId);
		blogPostURL = URLDictionary.buildURL_BlogPost(cursor.getInt(inxBlogId), id);

		// Now start populating all views with data
		tvTitle			= (TextView) findViewById(R.id.blog_post_title);
		tvTitle.setText(cursor.getString(inxTitle) );

		TextView tv	= (TextView) findViewById(R.id.blog_post_category);
		loadBlogName(cursor.getString(inxBlog), cursor.getString(inxPublishedBy), tv, context);

		Date d			= new Date(cursor.getLong(inxDatePub));	// Dates are stored as Unix timestamps
		TextView tvDate	= (TextView) findViewById(R.id.blog_post_date);
		tvDate.setText(dateFormat.format(d) );

		tvContent		= (TextView) findViewById(R.id.blog_post_content);
		// Fix for carriage returns displayed as rectangle characters in Android 1.6 
		tvContent.setText(AndroidUtil.removeCarriageReturns(cursor.getString(inxText) ) );

		tvAuthor = (TextView) findViewById(R.id.blog_post_author);
		String author	= cursor.getString(inxPublishedBy);

		if (author.length() > 0) {
			tvAuthor.setText(author);
			tvAuthor.setVisibility(View.VISIBLE);
		} else {
			tvAuthor.setText("");
			tvAuthor.setVisibility(View.INVISIBLE);
		}
		// Only mark the blog_post as read once.  If it's already marked as such - just stop here.
		if (cursor.getInt(inxWasRead) == 1) {
			cursor.close();
			return;
		}
		// Tidy up
		cursor.close();

		reloadListingTabAndMarkAsRead(id, context);
	}


	@Override
	public void loadTextSize(float textSize) {
		tvTitle.setTextSize(textSize + UserPreferencesManager.HEADING_TEXT_DIFF);
		tvContent.setTextSize(textSize);
		tvAuthor.setTextSize(textSize);
	}


	private void loadTheme(Context context) {
		Theme theme	= UserPreferencesManager.getThemeInstance(context);
		ScrollView layout		= (ScrollView) findViewById(R.id.blog_post_scroll_view);

		layout.setBackgroundColor(theme.getBackgroundColour() );
		tvTitle.setTextColor(theme.getHeadingColour() );
		tvContent.setTextColor(theme.getTextColour() );
		tvAuthor.setTextColor(theme.getTextColour() );
	}


	/**
	 * Loads the blog title view.  By default, the blog name is displayed but if it's empty it is replaced by the blogger's name
	 * @param blog
	 * @param publishedBy
	 * @param tv
	 * @param context
	 */
	private void loadBlogName(String blog, String publishedBy, TextView tv, Context context) {
		StringBuilder sb	= new StringBuilder(context.getString(R.string.heading_blog) );
		sb.append(": ");

		if (blog.length() > 0) {
			sb.append(blog.toLowerCase() );
		} else if (publishedBy.length() > 0) {
			sb.append(publishedBy.toLowerCase() );
		}
		tv.setTypeface(categoryTypeface);
		tv.setText(sb.toString() );
	}


	/**
	 * 1. Marks this blog post as read without blocking the UI thread (Java threads require variables to be declared as final)
	 * 2. Asks listing screens to refresh
	 * @param id
	 * @param context
	 */
	private void reloadListingTabAndMarkAsRead(final int id, final Context context) {
		new Thread(new Runnable() {
			public void run() {
				blogPostDAO.updateMarkRecordAsRead(id);
				BroadcastSender.getInstance(context).reloadTab(ApplicationRootActivity.Tab.BLOGS);
			}
		}).start();
	}
}
