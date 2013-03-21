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


import java.util.Map;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.ScrollView;
import android.widget.TextView;

import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.AndroidUtil;
import pl.lewica.lewicapl.android.ApplicationRootActivity;
import pl.lewica.lewicapl.android.BroadcastSender;
import pl.lewica.lewicapl.android.database.AnnouncementDAO;
import pl.lewica.lewicapl.android.database.BaseTextDAO;


public class AnnouncementActivity extends Activity {
	// This intent's base Uri.  It should have a numeric ID appended to it.
	public static final String BASE_URI	= "content://lewicapl/announcements/announcement/";

	private static Typeface categoryTypeface;

	private long annID;
	private BaseTextDAO annDAO;
	private Map<String,Long> nextPrevID;

//	private int colIndex_ID;
	private int colIndex_WasRead;
	private int colIndex_Title;
	private int colIndex_Where;
	private int colIndex_When;
	private int colIndex_Content;
	private int colIndex_PublishedBy;
	private int colIndex_PublishedByEmail;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_announcement);

		Resources res		= getResources();
		
		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		// Access data
		annDAO				= new AnnouncementDAO(this);
		annDAO.open();

		// When user changes the orientation, Android restarts the activity.  Say, users navigated through articles using
		// the previous-next facility; if they subsequently changed the screen orientation, they would've ended up on the original
		// article that was loaded through the intent.  In other words, changing the orientation would change the article displayed...
		// The logic below fixes this issue and it's using the ID set by onRetainNonConfigurationInstance (see docs for details).
		final Long ID	= (Long) getLastNonConfigurationInstance();
		if (ID == null) {
			annID			= filterIDFromUri(getIntent() );
		} else {
			annID			= ID;
		}

		// Fill views with data
		loadContent(annID, this);

		// Custom title background colour, http://stackoverflow.com/questions/2251714/set-title-background-color
		View titleView = getWindow().findViewById(android.R.id.title);
		if (titleView != null) {
			ViewParent parent	= titleView.getParent();
			if (parent != null && (parent instanceof View) ) {
				View parentView	= (View)parent;
				parentView.setBackgroundColor(res.getColor(R.color.red) );
			}
		}
		AndroidUtil.ActionBarCompat.setBackgroundDrawable(this, new ColorDrawable(Color.RED) );
	}


	/**
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		
		annDAO.close();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater infl	= getMenuInflater();
		infl.inflate(R.menu.menu_announcement, menu);
		
		return true;
	}


	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		long id;
		nextPrevID		= annDAO.fetchPreviousNextID(annID);
		
		menu.getItem(0).setEnabled(true);
		menu.getItem(1).setEnabled(true);

		id	= nextPrevID.get(AnnouncementDAO.MAP_KEY_PREVIOUS);
		if (id == 0) {
			menu.getItem(0).setEnabled(false);
		}
		id	= nextPrevID.get(AnnouncementDAO.MAP_KEY_NEXT);
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
				id	= nextPrevID.get(AnnouncementDAO.MAP_KEY_PREVIOUS);
				// If there are no newer announcements the ID is set to zero by fetchPreviousNextID()
				if (id > 0) {
					loadContent(nextPrevID.get(AnnouncementDAO.MAP_KEY_PREVIOUS), this);
				}
				return true;

			case R.id.menu_next:
				id	= nextPrevID.get(AnnouncementDAO.MAP_KEY_NEXT);
				// If there are no older announcements the ID is set to zero by fetchPreviousNextID()
				if (id > 0) {
					loadContent(nextPrevID.get(AnnouncementDAO.MAP_KEY_NEXT), this);
				}
				return true;

			default :
				return super.onOptionsItemSelected(item);
		}
	}


	/**
	 * Caches the current article ID.  Called when device orientation changes.
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
	    final Long ID = annID;
		return ID;
	}




	/**
	 * Responsible for loading content to the views and marking the current announcement as read.
	 * It is meant to be called every time user accesses this activity, either by selecting an announcement from the list
	 * or navigating between announcements using the previous-next facility (not yet implemented).
	 * @param id
	 */
	public void loadContent(long ID, Context context) {
		// Save it in this object's field
		annID	= ID;
		// Fetch database record
		Cursor cursor				= annDAO.selectOne(ID);

		startManagingCursor(cursor);

		// In order to capture a cell, you need to work what their index
		colIndex_Title					= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHAT);
		colIndex_Content				= cursor.getColumnIndex(AnnouncementDAO.FIELD_TEXT);
		colIndex_Where					= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHERE);
		colIndex_When					= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHEN);
		colIndex_PublishedBy			= cursor.getColumnIndex(AnnouncementDAO.FIELD_PUBLISHED_BY);
		colIndex_PublishedByEmail	= cursor.getColumnIndex(AnnouncementDAO.FIELD_PUBLISHED_EMAIL);

		// When using previous-next facility you need to make sure the scroll view's position is at the top of the screen
		ScrollView sv	= (ScrollView) findViewById(R.id.announcement_scroll_view);
		sv.fullScroll(View.FOCUS_UP);
		sv.setSmoothScrollingEnabled(true);

		// Now start populating all views with data
		TextView tv, tvLabel;
		tv							= (TextView) findViewById(R.id.announcement_title);
		tv.setText(cursor.getString(colIndex_Title) );

		tv							= (TextView) findViewById(R.id.announcement_category);
		tv.setTypeface(categoryTypeface);
		tv.setText(context.getString(R.string.heading_announcements) );

		tv							= (TextView) findViewById(R.id.announcement_content);
		// Fix for carriage returns displayed as rectangle characters in Android 1.6 
		tv.setText(cursor.getString(colIndex_Content).replace("\r", "") );

		// Where
		tv							= (TextView) findViewById(R.id.announcement_where);
		tvLabel					= (TextView) findViewById(R.id.announcement_where_label);
		String where			= cursor.getString(colIndex_Where);
		if (where != null && where.length() > 0) {
			tv.setText(where);
			// Reset visibility, may be useful when users navigate between announcements (previous-next facility to be added in the future)
			tv.setVisibility(View.VISIBLE);
			
			tvLabel.setText(getString(R.string.label_where) );
			tvLabel.setVisibility(View.VISIBLE);
		} else {
			tv.setText("");
			tv.setVisibility(View.GONE);
			// Hide top, dark grey bar
			tvLabel.setText("");
			tvLabel.setVisibility(View.GONE);
		}
		// When
		tv							= (TextView) findViewById(R.id.announcement_when);
		tvLabel					= (TextView) findViewById(R.id.announcement_when_label);
		String when			= cursor.getString(colIndex_When);
		if (when != null && when.length() > 0) {
			tv.setText(when);
			// Reset visibility, may be useful when users navigate between announcements (previous-next facility to be added in the future)
			tv.setVisibility(View.VISIBLE);
			
			tvLabel.setText(getString(R.string.label_when) );
			tvLabel.setVisibility(View.VISIBLE);
		} else {
			tv.setText("");
			tv.setVisibility(View.GONE);
			// Hide top, dark grey bar
			tvLabel.setText("");
			tvLabel.setVisibility(View.GONE);
		}

		tv							= (TextView) findViewById(R.id.announcement_author);
		String author			= cursor.getString(colIndex_PublishedBy);
		String authorEmail	= cursor.getString(colIndex_PublishedByEmail);

		if (author.length() == 0 && authorEmail.length() > 0) {
			author				= authorEmail;
		}
		if (author.length() > 0) {
			if (authorEmail.length() > 0) {
				tv.setText(Html.fromHtml("<a href=\"mailto:" + authorEmail + "?subject=" + context.getString(R.string.email_subject_announcement) + "\">" + author + "</a>") );
				tv.setMovementMethod(LinkMovementMethod.getInstance() );
			} else {
				tv.setText(author);
			}
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setText("");
			tv.setVisibility(View.INVISIBLE);
		}
		// Only mark the announcement as read once.  If it's already marked as such - just stop here.
		if (cursor.getInt(colIndex_WasRead) == 1) {
			cursor.close();
			return;
		}
		// Tidy up
		cursor.close();

		// Mark this announcement as read without blocking the UI thread
		// Java threads require variables to be declared as final
		final long announcementIDThread	= ID;
		final Context contextThread			= context;
		new Thread(new Runnable() {
			public void run() {
				annDAO.updateMarkAsRead(announcementIDThread);
				BroadcastSender.getInstance(contextThread).reloadTab(ApplicationRootActivity.Tab.ANNOUNCEMENTS);
			}
		}).start();
	}


	/**
	 * Activities on Android are invoked with a Uri string.  This method captures and returns the last bit of this Uri
	 * which it assumes to be a numeric ID of the current announcement.
	 * @param intent
	 * @return
	 */
	public long filterIDFromUri(Intent intent) {
		Uri uri						= intent.getData();
		String announcementIDString	= uri.getLastPathSegment();
		
		// TODO Make sure announcementID is a number
		Long announcementID			= Long.valueOf(announcementIDString);
		return announcementID;
	}
}