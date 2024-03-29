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
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.AndroidUtil;
import pl.lewica.lewicapl.android.ApplicationRootActivity;
import pl.lewica.lewicapl.android.BroadcastSender;
import pl.lewica.lewicapl.android.UserPreferencesManager;
import pl.lewica.lewicapl.android.activity.util.StandardTextScreen;
import pl.lewica.lewicapl.android.activity.util.TextSizeChangeListener;
import pl.lewica.lewicapl.android.activity.util.TextSizeDialog;
import pl.lewica.lewicapl.android.database.AnnouncementDAO;
import pl.lewica.lewicapl.android.database.BaseTextDAO;
import pl.lewica.lewicapl.android.theme.Theme;


public class AnnouncementActivity extends Activity implements StandardTextScreen {
	// This intent's base Uri.  It should have a numeric ID appended to it.
	public static final String BASE_URI	= "content://lewicapl/announcements/announcement/";

	private static Typeface categoryTypeface;

	private int annId;
	private BaseTextDAO annDAO;
	private Map<String,Integer> nextPrevId;
	private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;

	private TextView tvTitle;
	private TextView tvWhereLbl;
	private TextView tvWhenLbl;
	private TextView tvWhere;
	private TextView tvWhen;
	private TextView tvContent;
	private TextView tvAuthor;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_announcement);

		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		// Access data
		annDAO				= new AnnouncementDAO(this);
		annDAO.open();

		mSeekBarChangeListener	= new TextSizeChangeListener(this, this);

		// When user changes the orientation, Android restarts the activity.  Say, users navigated through articles using
		// the previous-next facility; if they subsequently changed the screen orientation, they would've ended up on the original
		// article that was loaded through the intent.  In other words, changing the orientation would change the article displayed...
		// The logic below fixes this issue and it's using the ID set by onRetainNonConfigurationInstance (see docs for details).
		final Integer id = (Integer) getLastNonConfigurationInstance();
		if (id == null) {
			annId = AndroidUtil.filterIdFromUri(getIntent().getData());
		} else {
			annId = id;
		}

		// Fill views with data
		loadContent(annId, this);
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
		MenuItem showPrevious	= menu.getItem(0);
		MenuItem showNext		= menu.getItem(1);

		showPrevious.setEnabled(true);
		showNext.setEnabled(true);

		nextPrevId = annDAO.fetchPreviousNextId(annId);
		int id	= nextPrevId.get(AnnouncementDAO.MAP_KEY_PREVIOUS);
		if (id == 0) {
			showPrevious.setEnabled(false);
		}
		id	= nextPrevId.get(AnnouncementDAO.MAP_KEY_NEXT);
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
		int id;

		switch (item.getItemId()) {
			case R.id.menu_previous:
				id	= nextPrevId.get(AnnouncementDAO.MAP_KEY_PREVIOUS);
				// If there are no newer announcements the ID is set to zero by fetchPreviousNextID()
				if (id > 0) {
					loadContent(nextPrevId.get(AnnouncementDAO.MAP_KEY_PREVIOUS), this);
				}
				return true;

			case R.id.menu_next:
				id	= nextPrevId.get(AnnouncementDAO.MAP_KEY_NEXT);
				// If there are no older announcements the ID is set to zero by fetchPreviousNextID()
				if (id > 0) {
					loadContent(nextPrevId.get(AnnouncementDAO.MAP_KEY_NEXT), this);
				}
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
	 * Caches the current article ID.  Called when device orientation changes.
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		final int id = annId;
		return id;
	}




	/**
	 * Responsible for loading content to the views and marking the current announcement as read.
	 * It is meant to be called every time user accesses this activity, either by selecting an announcement from the list
	 * or navigating between announcements using the previous-next facility (not yet implemented).
	 */
	private void loadContent(int id, Context context) {
		// Save it in this object's field
		annId = id;
		// Fetch database record
		Cursor cursor				= annDAO.selectOne(id);

		startManagingCursor(cursor);

		// In order to capture a cell, you need to work what their index
		int idxWasRead				= cursor.getColumnIndex(AnnouncementDAO.FIELD_WAS_READ);
		int idxTitle					= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHAT);
		int idxContent				= cursor.getColumnIndex(AnnouncementDAO.FIELD_TEXT);
		int idxWhere					= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHERE);
		int idxWhen					= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHEN);
		int idxPublishedBy			= cursor.getColumnIndex(AnnouncementDAO.FIELD_PUBLISHED_BY);
		int idxPublishedByEmail	= cursor.getColumnIndex(AnnouncementDAO.FIELD_PUBLISHED_EMAIL);

		// When using previous-next facility you need to make sure the scroll view's position is at the top of the screen
		AndroidUtil.scrollToTop(R.id.announcement_scroll_view, this);

		// Now start populating all views with data
		tvTitle			= (TextView) findViewById(R.id.announcement_title);
		tvTitle.setText(cursor.getString(idxTitle) );

		TextView tv	= (TextView) findViewById(R.id.announcement_category);
		tv.setTypeface(categoryTypeface);
		tv.setText(context.getString(R.string.heading_announcements) );

		tvContent		= (TextView) findViewById(R.id.announcement_content);
		// Fix for carriage returns displayed as rectangle characters in Android 1.6 
		tvContent.setText(AndroidUtil.removeCarriageReturns(cursor.getString(idxContent) ) );

		// Where
		loadEventLocation(cursor.getString(idxWhere) );
		// When
		loadEventTime(cursor.getString(idxWhen) );

		loadAnnouncementAuthor(cursor.getString(idxPublishedBy), cursor.getString(idxPublishedByEmail) );

		// Only mark the announcement as read once.  If it's already marked as such - just stop here.
		if (cursor.getInt(idxWasRead) == 1) {
			cursor.close();
			return;
		}
		// Tidy up
		cursor.close();

		reloadListingTabAndMarkAsRead(id, context);
	}


	private void loadTheme(Context context) {
		Theme theme	= UserPreferencesManager.getThemeInstance(context);
		ScrollView layout		= (ScrollView) findViewById(R.id.announcement_scroll_view);

		layout.setBackgroundColor(theme.getBackgroundColour() );
		tvTitle.setTextColor(theme.getHeadingColour() );
		tvWhere.setTextColor(theme.getTextColour() );
		tvWhereLbl.setTextColor(theme.getHeadingColour() );
		tvWhen.setTextColor(theme.getTextColour() );
		tvWhenLbl.setTextColor(theme.getHeadingColour() );
		tvContent.setTextColor(theme.getTextColour() );
		tvAuthor.setTextColor(theme.getTextColour() );
	}


	@Override
	public void loadTextSize(float textSize) {
		tvTitle.setTextSize(textSize + UserPreferencesManager.HEADING_TEXT_DIFF);
		tvWhere.setTextSize(textSize);
		tvWhereLbl.setTextSize(textSize);
		tvWhen.setTextSize(textSize);
		tvWhenLbl.setTextSize(textSize);
		tvContent.setTextSize(textSize);
		tvAuthor.setTextSize(textSize);
	}


	private void loadEventLocation(String location) {
		tvWhere			= (TextView) findViewById(R.id.announcement_where);
		tvWhereLbl	= (TextView) findViewById(R.id.announcement_where_label);

		if (location != null && location.length() > 0) {
			tvWhere.setText(location);
			// Reset visibility, may be useful when users navigate between announcements (previous-next facility to be added in the future)
			tvWhere.setVisibility(View.VISIBLE);
			
			tvWhereLbl.setText(getString(R.string.label_where) );   // TODO Move it to resource bundle
			tvWhereLbl.setVisibility(View.VISIBLE);
			return;
		}

		tvWhere.setText("");
		tvWhere.setVisibility(View.GONE);
		// Hide top, dark grey bar
		tvWhereLbl.setText("");
		tvWhereLbl.setVisibility(View.GONE);
	}


	private void loadEventTime(String dateTime) {
		tvWhen					= (TextView) findViewById(R.id.announcement_when);
		tvWhenLbl				= (TextView) findViewById(R.id.announcement_when_label);

		if (dateTime != null && dateTime.length() > 0) {
			tvWhen.setText(dateTime);
			// Reset visibility, may be useful when users navigate between announcements (previous-next facility to be added in the future)
			tvWhen.setVisibility(View.VISIBLE);

			tvWhenLbl.setText(getString(R.string.label_when) ); // TODO Move it to resource bundle
			tvWhenLbl.setVisibility(View.VISIBLE);
			return;
		}

		tvWhen.setText("");
		tvWhen.setVisibility(View.GONE);
		// Hide top, dark grey bar
		tvWhenLbl.setText("");
		tvWhenLbl.setVisibility(View.GONE);
	}


	/**
	 * @param author
	 * @param authorEmail
	 */
	private void loadAnnouncementAuthor(String author, String authorEmail) {
		tvAuthor		= (TextView) findViewById(R.id.announcement_author);

		if (author.length() == 0 && authorEmail.length() > 0) {
			author		= authorEmail;
		}
		if (author.length() > 0) {
			if (authorEmail.length() > 0) {
				String emailSubject	= getString(R.string.email_subject_announcement);  // TODO Move it to resource bundle
				tvAuthor.setText(Html.fromHtml("<a href=\"mailto:" + authorEmail + "?subject=" + emailSubject + "\">" + author + "</a>") );
				tvAuthor.setMovementMethod(LinkMovementMethod.getInstance() );
			} else {
				tvAuthor.setText(author);
			}
			tvAuthor.setVisibility(View.VISIBLE);
			return;
		}

		tvAuthor.setText("");
		tvAuthor.setVisibility(View.INVISIBLE);
	}

	/**
	 * 1. Marks this article as read without blocking the UI thread (Java threads require variables to be declared as final)
	 * 2. Asks listing screens to refresh
	 * @param articleId
	 * @param context
	 */
	private void reloadListingTabAndMarkAsRead(final int articleId, final Context context) {
		// Mark this announcement as read without blocking the UI thread
		// Java threads require variables to be declared as final
		new Thread(new Runnable() {
			public void run() {
				annDAO.updateMarkRecordAsRead(articleId);
				BroadcastSender.getInstance(context).reloadTab(ApplicationRootActivity.Tab.ANNOUNCEMENTS);
			}
		}).start();
	}
}
