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
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
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
import pl.lewica.lewicapl.android.DialogManager;
import pl.lewica.lewicapl.android.SliderDialog;
import pl.lewica.lewicapl.android.DialogManager.SliderEventHandler;
import pl.lewica.lewicapl.android.TextPreferencesManager;
import pl.lewica.lewicapl.android.TextPreferencesManager.ThemeHandler;
import pl.lewica.lewicapl.android.database.AnnouncementDAO;
import pl.lewica.lewicapl.android.database.BaseTextDAO;


public class AnnouncementActivity extends Activity {
	// This intent's base Uri.  It should have a numeric ID appended to it.
	public static final String BASE_URI	= "content://lewicapl/announcements/announcement/";

	private static Typeface categoryTypeface;

	private long annID;
	private BaseTextDAO annDAO;
	private Map<String,Long> nextPrevID;
	private SliderEventHandler mTextSizeHandler;
	private ThemeHandler mThemeHandler;

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

		Resources res		= getResources();
		
		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		// Access data
		annDAO				= new AnnouncementDAO(this);
		annDAO.open();

		mTextSizeHandler	= new TextSizeHandler(this);
		mThemeHandler	= new ApplicationThemeHandler();

		// When user changes the orientation, Android restarts the activity.  Say, users navigated through articles using
		// the previous-next facility; if they subsequently changed the screen orientation, they would've ended up on the original
		// article that was loaded through the intent.  In other words, changing the orientation would change the article displayed...
		// The logic below fixes this issue and it's using the ID set by onRetainNonConfigurationInstance (see docs for details).
		final Long ID	= (Long) getLastNonConfigurationInstance();
		if (ID == null) {
			annID			= AndroidUtil.filterIDFromUri(getIntent().getData() );
		} else {
			annID			= ID;
		}

		// Fill views with data
		loadContent(annID, this);
		TextPreferencesManager.loadTheme(mThemeHandler, this);

		// Custom title background colour, http://stackoverflow.com/questions/2251714/set-title-background-color
		View titleView = getWindow().findViewById(android.R.id.title);
		if (titleView != null) {
			ViewParent parent	= titleView.getParent();
			if (parent != null && (parent instanceof View) ) {
				View parentView	= (View)parent;
				parentView.setBackgroundColor(res.getColor(R.color.red) );
			}
		}
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

			case R.id.menu_change_text_size:
				int sizeInPoints	= TextPreferencesManager.convertTextSizeToPoint(TextPreferencesManager.getUserTextSize(this) );
				SliderDialog sd		= new SliderDialog();
				sd.setSliderValue(sizeInPoints);
				sd.setSliderMax(TextPreferencesManager.TEXT_SIZES_TOTAL);
				sd.setTitleResource(R.string.heading_change_text_size);
				sd.setOkButtonResource(R.string.ok);

				DialogManager.showDialogWithSlider(sd, this, mTextSizeHandler);

				return true;

			case R.id.menu_change_background:
				TextPreferencesManager.switchTheme(mThemeHandler, this);
				ApplicationRootActivity.reloadAllTabsInBackground(this);

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

		float userTextSize	= TextPreferencesManager.getUserTextSize(this);

		// In order to capture a cell, you need to work what their index
		int idxWasRead				= cursor.getColumnIndex(AnnouncementDAO.FIELD_WAS_READ);
		int idxTitle					= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHAT);
		int idxContent				= cursor.getColumnIndex(AnnouncementDAO.FIELD_TEXT);
		int idxWhere					= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHERE);
		int idxWhen					= cursor.getColumnIndex(AnnouncementDAO.FIELD_WHEN);
		int idxPublishedBy			= cursor.getColumnIndex(AnnouncementDAO.FIELD_PUBLISHED_BY);
		int idxPublishedByEmail	= cursor.getColumnIndex(AnnouncementDAO.FIELD_PUBLISHED_EMAIL);

		// When using previous-next facility you need to make sure the scroll view's position is at the top of the screen
		ScrollView sv	= (ScrollView) findViewById(R.id.announcement_scroll_view);
		sv.fullScroll(View.FOCUS_UP);
		sv.setSmoothScrollingEnabled(true);

		// Now start populating all views with data
		tvTitle					= (TextView) findViewById(R.id.announcement_title);
		tvTitle.setTextSize(userTextSize + TextPreferencesManager.HEADING_TEXT_DIFF);
		tvTitle.setText(cursor.getString(idxTitle) );

		TextView tv;
		tv							= (TextView) findViewById(R.id.announcement_category);
		tv.setTypeface(categoryTypeface);
		tv.setText(context.getString(R.string.heading_announcements) );

		tvContent				= (TextView) findViewById(R.id.announcement_content);
		tvContent.setTextSize(userTextSize);
		// Fix for carriage returns displayed as rectangle characters in Android 1.6 
		tvContent.setText(cursor.getString(idxContent).replace("\r", "") );

		// Where
		tvWhere					= (TextView) findViewById(R.id.announcement_where);
		tvWhereLbl			= (TextView) findViewById(R.id.announcement_where_label);
		String where			= cursor.getString(idxWhere);
		if (where != null && where.length() > 0) {
			tvWhere.setText(where);
			// Reset visibility, may be useful when users navigate between announcements (previous-next facility to be added in the future)
			tvWhere.setVisibility(View.VISIBLE);
			
			tvWhereLbl.setText(getString(R.string.label_where) );
			tvWhereLbl.setVisibility(View.VISIBLE);
		} else {
			tvWhere.setText("");
			tvWhere.setVisibility(View.GONE);
			// Hide top, dark grey bar
			tvWhereLbl.setText("");
			tvWhereLbl.setVisibility(View.GONE);
		}
		// When
		tvWhen					= (TextView) findViewById(R.id.announcement_when);
		tvWhenLbl				= (TextView) findViewById(R.id.announcement_when_label);
		String when			= cursor.getString(idxWhen);
		if (when != null && when.length() > 0) {
			tvWhen.setText(when);
			// Reset visibility, may be useful when users navigate between announcements (previous-next facility to be added in the future)
			tvWhen.setVisibility(View.VISIBLE);
			
			tvWhenLbl.setText(getString(R.string.label_when) );
			tvWhenLbl.setVisibility(View.VISIBLE);
		} else {
			tvWhen.setText("");
			tvWhen.setVisibility(View.GONE);
			// Hide top, dark grey bar
			tvWhenLbl.setText("");
			tvWhenLbl.setVisibility(View.GONE);
		}

		tvAuthor				= (TextView) findViewById(R.id.announcement_author);
		tvAuthor.setTextSize(userTextSize);
		String author			= cursor.getString(idxPublishedBy);
		String authorEmail	= cursor.getString(idxPublishedByEmail);

		if (author.length() == 0 && authorEmail.length() > 0) {
			author				= authorEmail;
		}
		if (author.length() > 0) {
			if (authorEmail.length() > 0) {
				tvAuthor.setText(Html.fromHtml("<a href=\"mailto:" + authorEmail + "?subject=" + context.getString(R.string.email_subject_announcement) + "\">" + author + "</a>") );
				tvAuthor.setMovementMethod(LinkMovementMethod.getInstance() );
			} else {
				tvAuthor.setText(author);
			}
			tvAuthor.setVisibility(View.VISIBLE);
		} else {
			tvAuthor.setText("");
			tvAuthor.setVisibility(View.INVISIBLE);
		}
		// Only mark the announcement as read once.  If it's already marked as such - just stop here.
		if (cursor.getInt(idxWasRead) == 1) {
			cursor.close();
			return;
		}
		// Tidy up
		cursor.close();

		reloadListingTabAndMarkAsRead(ID, context);
	}


	/**
	 * 1. Marks this article as read without blocking the UI thread (Java threads require variables to be declared as final)
	 * 2. Asks listing screens to refresh
	 * @param articleId
	 * @param context
	 */
	private void reloadListingTabAndMarkAsRead(final long articleId, final Context context) {
		// Mark this announcement as read without blocking the UI thread
		// Java threads require variables to be declared as final
		new Thread(new Runnable() {
			public void run() {
				annDAO.updateMarkAsRead(articleId);
				BroadcastSender.getInstance(context).reloadTab(ApplicationRootActivity.Tab.ANNOUNCEMENTS);
			}
		}).start();
	}


	private class TextSizeHandler implements DialogManager.SliderEventHandler {

		private Activity mActivity;

		public TextSizeHandler(Activity activity) {
			mActivity	= activity;
		}


		@Override
		public void changeValue(int points) {
			float textSize		= TextPreferencesManager.convertTextSizeToFloat(points);

			tvTitle.setTextSize(textSize + TextPreferencesManager.HEADING_TEXT_DIFF);
			tvWhere.setTextSize(textSize);
			tvWhereLbl.setTextSize(textSize);
			tvWhen.setTextSize(textSize);
			tvWhenLbl.setTextSize(textSize);
			tvContent.setTextSize(textSize);
			tvAuthor.setTextSize(textSize);

			TextPreferencesManager.setUserTextSize(textSize, mActivity);
		}


		@Override
		public void finishSliding(int points) {
			float textSize		= TextPreferencesManager.convertTextSizeToFloat(points);

			TextPreferencesManager.setUserTextSize(textSize, mActivity);
		}
	}


	private class ApplicationThemeHandler implements TextPreferencesManager.ThemeHandler {

		@Override
		public void setThemeDark() {
			ScrollView layout		= (ScrollView) findViewById(R.id.announcement_scroll_view);
			int black			= getResources().getColor(R.color.black);
			int light		= getResources().getColor(R.color.grey_light);
			int lightBlue	= getResources().getColor(R.color.blue_light);

			layout.setBackgroundColor(black);
			tvTitle.setTextColor(lightBlue);
			tvWhere.setTextColor(light);
			tvWhereLbl.setTextColor(light);
			tvWhen.setTextColor(light);
			tvWhenLbl.setTextColor(light);
			tvContent.setTextColor(light);
			tvAuthor.setTextColor(light);
		}


		@Override
		public void setThemeLight() {
			ScrollView layout		= (ScrollView) findViewById(R.id.announcement_scroll_view);
			int dark			= getResources().getColor(R.color.grey_darker);
			int white		= getResources().getColor(R.color.white);
			int blue			= getResources().getColor(R.color.read);

			layout.setBackgroundColor(white);
			tvTitle.setTextColor(blue);
			tvWhere.setTextColor(dark);
			tvWhereLbl.setTextColor(dark);
			tvWhen.setTextColor(dark);
			tvWhenLbl.setTextColor(dark);
			tvContent.setTextColor(dark);
			tvAuthor.setTextColor(dark);
		}
	}
}
