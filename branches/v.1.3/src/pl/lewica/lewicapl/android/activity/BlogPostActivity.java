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
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.ScrollView;
import android.widget.TextView;

import pl.lewica.URLDictionary;
import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.database.BlogPostDAO;


public class BlogPostActivity extends Activity {
	// This intent's base Uri.  It should have a numeric ID appended to it.
	public static final String BASE_URI	= "content://lewicapl/blog_posts/blog_post/";

	private static Typeface categoryTypeface;

	private long blogPostID;
	private BlogPostDAO blogPostDAO;
	private Map<String,Long> nextPrevID;
	private String blogPostURL;

//	private int colIndex_ID;
	private int colIndex_WasRead;
	private int colIndex_DatePub;
	private int colIndex_BlogID;
	private int colIndex_Blog;
	private int colIndex_PublishedBy;
	private int colIndex_Title;
	private int colIndex_Text;

	private static SimpleDateFormat dateFormat	= new SimpleDateFormat("dd/MM/yyyy HH:mm");


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_blog_post);

		Resources res		= getResources();
		
		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		// Access data
		blogPostDAO				= new BlogPostDAO(this);
		blogPostDAO.open();

		// When user changes the orientation, Android restarts the activity.  Say, users navigated through articles using
		// the previous-next facility; if they subsequently changed the screen orientation, they would've ended up on the original
		// article that was loaded through the intent.  In other words, changing the orientation would change the article displayed...
		// The logic below fixes this issue and it's using the ID set by onRetainNonConfigurationInstance (see docs for details).
		final Long ID	= (Long) getLastNonConfigurationInstance();
		if (ID == null) {
			blogPostID			= filterIDFromUri(getIntent() );
		} else {
			blogPostID			= ID;
		}

		// Fill views with data
		loadContent(blogPostID, this);

		// Custom title background colour, http://stackoverflow.com/questions/2251714/set-title-background-color
		View titleView = getWindow().findViewById(android.R.id.title);
		if (titleView == null) {
			return;
		}
		ViewParent parent	= titleView.getParent();
		if (parent == null || ! (parent instanceof View) ) {
			return;
		}
		View parentView	= (View)parent;
		parentView.setBackgroundColor(res.getColor(R.color.red) );
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
		long id;
		nextPrevID		= blogPostDAO.fetchPreviousNextID(blogPostID);

		menu.getItem(0).setEnabled(true);
		menu.getItem(1).setEnabled(true);

		id	= nextPrevID.get(BlogPostDAO.MAP_KEY_PREVIOUS);
		if (id == 0) {
			menu.getItem(0).setEnabled(false);
		}
		id	= nextPrevID.get(BlogPostDAO.MAP_KEY_NEXT);
		if (id == 0) {
			menu.getItem(1).setEnabled(false);
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
				id	= nextPrevID.get(BlogPostDAO.MAP_KEY_PREVIOUS);
				// If there are no newer blog posts the ID is set to zero by fetchPreviousNextID()
				if (id > 0) {
					loadContent(nextPrevID.get(BlogPostDAO.MAP_KEY_PREVIOUS), this);
				}
				return true;

			case R.id.menu_next:
				id	= nextPrevID.get(BlogPostDAO.MAP_KEY_NEXT);
				// If there are no older blog posts the ID is set to zero by fetchPreviousNextID()
				if (id > 0) {
					loadContent(nextPrevID.get(BlogPostDAO.MAP_KEY_NEXT), this);
				}
				return true;

			case R.id.menu_share:
				Intent intent	= new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, blogPostURL);
				startActivity(Intent.createChooser(intent, getString(R.string.label_share_link) ) );
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
	    final Long ID = blogPostID;
		return ID;
	}




	/**
	 * Responsible for loading content to the views and marking the current blog post as read.
	 * It is meant to be called every time user accesses this activity, either by selecting an blog post from the list
	 * or navigating between blog posts using the previous-next facility (not yet implemented).
	 * @param id
	 */
	public void loadContent(long ID, Context context) {
		// Save it in this object's field
		blogPostID	= ID;
		// Fetch database record
		Cursor cursor				= blogPostDAO.selectOne(ID);

		startManagingCursor(cursor);

		// In order to capture a cell, you need to work what their index
		colIndex_Title				= cursor.getColumnIndex(BlogPostDAO.FIELD_TITLE);
		colIndex_Text				= cursor.getColumnIndex(BlogPostDAO.FIELD_TEXT);
		colIndex_BlogID				= cursor.getColumnIndex(BlogPostDAO.FIELD_BLOG_ID);
		colIndex_Blog				= cursor.getColumnIndex(BlogPostDAO.FIELD_BLOG_TITLE);
		colIndex_DatePub			= cursor.getColumnIndex(BlogPostDAO.FIELD_DATE_PUBLISHED);
		colIndex_PublishedBy		= cursor.getColumnIndex(BlogPostDAO.FIELD_AUTHOR);

		// When using previous-next facility you need to make sure the scroll view's position is at the top of the screen
		ScrollView sv	= (ScrollView) findViewById(R.id.blog_post_scroll_view);
		sv.fullScroll(View.FOCUS_UP);
		sv.setSmoothScrollingEnabled(true);

		blogPostURL			= URLDictionary.buildURL_BlogPost(cursor.getInt(colIndex_BlogID), ID);

		// Now start populating all views with data
		TextView tv;
		tv							= (TextView) findViewById(R.id.blog_post_title);
		tv.setText(cursor.getString(colIndex_Title) );

		tv							= (TextView) findViewById(R.id.blog_post_category);
		tv.setTypeface(categoryTypeface);
		tv.setText(context.getString(R.string.heading_blogs) + ": " + cursor.getString(colIndex_Blog).toLowerCase() );

		tv							= (TextView) findViewById(R.id.blog_post_date);
		long unixTime		= cursor.getLong(colIndex_DatePub);	// Dates are stored as Unix timestamps
		Date d					= new Date(unixTime);
		tv.setText(dateFormat.format(d) );

		tv							= (TextView) findViewById(R.id.blog_post_content);
		// Fix for carriage returns displayed as rectangle characters in Android 1.6 
		tv.setText(cursor.getString(colIndex_Text).replace("\r", "") );

		tv							= (TextView) findViewById(R.id.blog_post_author);
		String author			= cursor.getString(colIndex_PublishedBy);

		if (author.length() > 0) {
			tv.setText(author);
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setText("");
			tv.setVisibility(View.INVISIBLE);
		}
		// Only mark the blog_post as read once.  If it's already marked as such - just stop here.
		if (cursor.getInt(colIndex_WasRead) == 1) {
			cursor.close();
			return;
		}
		// Tidy up
		cursor.close();

		// Mark this blog_post as read without blocking the UI thread
		// Java threads require variables to be declared as final
		final long blogPostIDThread	= ID;
		new Thread(new Runnable() {
			public void run() {
				blogPostDAO.updateMarkAsRead(blogPostIDThread);
			}
		}).start();
	}


	/**
	 * Activities on Android are invoked with a Uri string.  This method captures and returns the last bit of this Uri
	 * which it assumes to be a numeric ID of the current blog post.
	 * @param intent
	 * @return
	 */
	public long filterIDFromUri(Intent intent) {
		Uri uri						= intent.getData();
		String blogPostIDString	= uri.getLastPathSegment();
		
		// TODO Make sure blogPostID is a number
		Long blogPostID			= Long.valueOf(blogPostIDString);
		return blogPostID;
	}
}