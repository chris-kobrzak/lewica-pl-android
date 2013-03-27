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

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import pl.lewica.api.model.Article;
import pl.lewica.api.url.ArticleURL;
import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.AndroidUtil;
import pl.lewica.lewicapl.android.ApplicationRootActivity;
import pl.lewica.lewicapl.android.BroadcastSender;
import pl.lewica.lewicapl.android.DialogManager;
import pl.lewica.lewicapl.android.SliderDialog;
import pl.lewica.lewicapl.android.DialogManager.SliderEventHandler;
import pl.lewica.lewicapl.android.UserPreferencesManager;
import pl.lewica.lewicapl.android.database.ArticleDAO;
import pl.lewica.lewicapl.android.theme.Theme;


public class ArticleActivity extends Activity {
	// This intent's base Uri.  It should have a numeric ID appended to it.
	public static final String URI_BASE						= "content://lewicapl/articles/article/";
	public static final String URI_BASE_COMMENTS	= "content://lewicapl/articles/article/comments/";

	private static Typeface categoryTypeface;
	private static Map<Long, SoftReference<Bitmap>> images	= new HashMap<Long,SoftReference<Bitmap>>();

	private long articleID;
	private int categoryID;
	private String articleURL;
	private ArticleDAO articleDAO;
	private Map<String,Long> nextPrevID;
	private ImageLoadTask imageTask;
	private ImageCache imageCache;
	private SliderEventHandler mTextSizeHandler;

	private TextView tvTitle;
	private TextView tvContent;
	private TextView tvComment;

	private static SimpleDateFormat dateFormat	= new SimpleDateFormat("dd/MM/yyyy HH:mm");



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_article);

		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		// Image caching service
		imageCache			= new ImageCache();

		// Access data
		articleDAO				= new ArticleDAO(this);
		articleDAO.open();

		mTextSizeHandler	= new TextSizeHandler(this);

		// When user changes the orientation, Android restarts the activity.  Say, users navigated through articles using
		// the previous-next facility; if they subsequently changed the screen orientation, they would've ended up on the original
		// article that was loaded through the intent.  In other words, changing the orientation would change the article displayed...
		// The logic below fixes this issue and it's using the ID set by onRetainNonConfigurationInstance (see docs for details).
		final Long ID	= (Long) getLastNonConfigurationInstance();
		if (ID == null) {
			articleID				= AndroidUtil.filterIDFromUri(getIntent().getData() );
		} else {
			articleID				= ID;
		}
		// Fill views with data
		loadContent(articleID, this);
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

		articleDAO.close();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater infl	= getMenuInflater();
		infl.inflate(R.menu.menu_article, menu);

		return true;
	}


	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		long id;
		nextPrevID		= articleDAO.fetchPreviousNextID(articleID, categoryID);

		menu.getItem(0).setEnabled(true);
		menu.getItem(1).setEnabled(true);

		id	= nextPrevID.get(ArticleDAO.MAP_KEY_PREVIOUS);
		if (id == 0) {
			menu.getItem(0).setEnabled(false);
		}
		id	= nextPrevID.get(ArticleDAO.MAP_KEY_NEXT);
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
		Intent intent;

		switch (item.getItemId()) {
			case R.id.menu_previous:
				id	= nextPrevID.get(ArticleDAO.MAP_KEY_PREVIOUS);
				// If there are no newer articles the ID is set to zero by fetchPreviousNextID()
				if (id > 0) {
					loadContent(nextPrevID.get(ArticleDAO.MAP_KEY_PREVIOUS), this);
				}
				return true;

			case R.id.menu_next:
				id	= nextPrevID.get(ArticleDAO.MAP_KEY_NEXT);
				// If there are no older articles the ID is set to zero by fetchPreviousNextID()
				if (id > 0) {
					loadContent(nextPrevID.get(ArticleDAO.MAP_KEY_NEXT), this);
				}
				return true;

			case R.id.menu_share:
				intent	= new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, articleURL);
				startActivity(Intent.createChooser(intent, getString(R.string.label_share_link) ) );
				return true;

			case R.id.menu_comments:
				// Redirect to article details screen
				intent	= new Intent(this, ReadersCommentsActivity.class);
				// Builds a uri in the following format: content://lewicapl/articles/article/[0-9]+
				Uri uri			= Uri.parse(ArticleActivity.URI_BASE_COMMENTS + Long.toString(articleID) );
				// Passes activity Uri as parameter that can be used to work out ID of requested article.
				intent.setData(uri);
				startActivity(intent);
				return true;

			case R.id.menu_change_text_size:
				int sizeInPoints	= UserPreferencesManager.convertTextSize(UserPreferencesManager.getTextSize(this) );
				SliderDialog sd		= new SliderDialog();
				sd.setSliderValue(sizeInPoints);
				sd.setSliderMax(UserPreferencesManager.TEXT_SIZES_TOTAL);
				sd.setTitleResource(R.string.heading_change_text_size);
				sd.setOkButtonResource(R.string.ok);

				DialogManager.showDialogWithSlider(sd, this, mTextSizeHandler);
				return true;

			case R.id.menu_change_background:
				// Read existing and write new theme to xml file (done by Android) and store in memory
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
		final Long ID = articleID;
		return ID;
	}


	/**
	 * Responsible for loading content to the views and marking the current article as read.
	 * It is meant to be called every time user accesses this activity, either by selecting an article from the list
	 * or navigating between articles using the previous-next facility (not yet implemented).
	 * TODO Break this method down into smaller parts
	 * @param id
	 */
	private void loadContent(long ID, Context context) {
		// Save it in this object's field
		articleID	= ID;
		// Fetch database record
		Cursor cursor				= articleDAO.selectOne(ID);

		startManagingCursor(cursor);

		// In order to capture a cell, you need to work what their index
		int inxURL				= cursor.getColumnIndex(ArticleDAO.FIELD_URL);
		int inxCategoryID	= cursor.getColumnIndex(ArticleDAO.FIELD_CATEGORY_ID);
		int inxTitle			= cursor.getColumnIndex(ArticleDAO.FIELD_TITLE);
		int inxDatePub		= cursor.getColumnIndex(ArticleDAO.FIELD_DATE_PUBLISHED);
		int inxContent		= cursor.getColumnIndex(ArticleDAO.FIELD_TEXT);
		int inxComment		= cursor.getColumnIndex(ArticleDAO.FIELD_EDITOR_COMMENT);
		int inxWasRead		= cursor.getColumnIndex(ArticleDAO.FIELD_WAS_READ);
		int inxHasThumb	= cursor.getColumnIndex(ArticleDAO.FIELD_HAS_IMAGE);
		int inxThumbExt	= cursor.getColumnIndex(ArticleDAO.FIELD_IMAGE_EXTENSION);

		// When using previous-next facility you need to make sure the scroll view's position is at the top of the screen
		ScrollView sv	= (ScrollView) findViewById(R.id.article_scroll_view);
		sv.fullScroll(View.FOCUS_UP);
		sv.setSmoothScrollingEnabled(true);

		// On the same token, make sure an image belonging to another article never appears here as a result of an async process.
		if (imageTask != null) {
			imageTask.cancel(true);
		}

		// Save the URL in memory so the "share link" option in menu can access it easily
		articleURL				= cursor.getString(inxURL);

		// Now start populating all views with data
		tvTitle					= (TextView) findViewById(R.id.article_title);
		tvTitle.setText(cursor.getString(inxTitle) );

		TextView tv;
		tv							= (TextView) findViewById(R.id.article_category);
		categoryID				= cursor.getInt(inxCategoryID);
		tv.setTypeface(categoryTypeface);
		switch (categoryID) {
			case Article.SECTION_POLAND:
				tv.setText(context.getString(R.string.heading_poland) );
				break;

			case Article.SECTION_WORLD:
				tv.setText(context.getString(R.string.heading_world) );
				break;

			// TODO Confirm we need the case statements below
			case Article.SECTION_OPINIONS:
				tv.setText(context.getString(R.string.heading_texts) );
				break;

			case Article.SECTION_REVIEWS:
				tv.setText(context.getString(R.string.heading_reviews) );
				break;

			case Article.SECTION_CULTURE:
				tv.setText(context.getString(R.string.heading_culture) );
				break;
		}

		tv							= (TextView) findViewById(R.id.article_date);
		long unixTime		= cursor.getLong(inxDatePub);	// Dates are stored as Unix timestamps
		Date d					= new Date(unixTime);
		tv.setText(dateFormat.format(d) );

		tvContent	= (TextView) findViewById(R.id.article_content);
		// Fix for carriage returns displayed as rectangle characters in Android 1.6 
		tvContent.setText(cursor.getString(inxContent).replace("\r", "") );

		tvComment				= (TextView) findViewById(R.id.article_editor_comment);
		tv			= (TextView) findViewById(R.id.article_editor_comment_top);
		String commentString		= cursor.getString(inxComment);
		if (commentString != null && commentString.length() > 0) {
			tvComment.setText(commentString.replace("\r", "") );
			// Reset visibility, may be useful when users navigate between articles (previous-next facility to be added in the future)
			tv.setVisibility(View.VISIBLE);
			tvComment.setVisibility(View.VISIBLE);
		} else {
			tvComment.setText("");
			tv.setVisibility(View.GONE);
			// Hide top, dark grey bar
			tvComment.setVisibility(View.GONE);
		}

		// Reset image to avoid issues when navigating between previous and next articles
		ImageView iv			= (ImageView) findViewById(R.id.article_image);
		iv.setImageBitmap(null);
		if (cursor.getInt(inxHasThumb) == 1) {
			boolean downloadImage		= true;
			// Checking if this image is available in our local cache.
			if (imageCache.isCached(articleID) ) {
				Bitmap bitmap	= imageCache.get(articleID);
				if (bitmap != null) {
					loadImage(bitmap);
					downloadImage		= false;
				}
			}
			// Image needs to downloaded from the server
			if (downloadImage) {
				String imageUrl		= ArticleURL.buildURLImage(ID, cursor.getString(inxThumbExt) );
	
				// Images need to be downloaded in a separate thread as we cannot block the UI thread.
				// Note: we do not currently cache images on this screen and they are downloaded from the Internet on every request.
				imageTask	= new ImageLoadTask();
				imageTask.execute(imageUrl);
			}
		}
		

		// Only mark the article as read once.  If it's already marked as such - just stop here.
		if (cursor.getInt(inxWasRead) == 1) {
			cursor.close();
			return;
		}
		// Tidy up
		cursor.close();

		reloadListingTabAndMarkAsRead(ID, context);
	}


	private void loadTextSize(float textSize) {
		tvTitle.setTextSize(textSize + UserPreferencesManager.HEADING_TEXT_DIFF);
		tvContent.setTextSize(textSize);
		tvComment.setTextSize(textSize);
	}


	private void loadTheme(Context context) {
		Theme theme	= UserPreferencesManager.getThemeInstance(context);
		ScrollView layout		= (ScrollView) findViewById(R.id.article_scroll_view);

		layout.setBackgroundColor(theme.getBackgroundColour() );
		tvTitle.setTextColor(theme.getHeadingColour() );
		tvContent.setTextColor(theme.getTextColour() );
		tvComment.setTextColor(theme.getEditorsCommentTextColour() );

		if (UserPreferencesManager.isLightTheme() ) {
			tvComment.setBackgroundDrawable(theme.getEditorsCommentBackground() );
		} else {
			tvComment.setBackgroundColor(theme.getEditorsCommentBackgroundColour() );
		}
	}


	/**
	 * Puts a bitmap in the image view.  Adds the bitmap to the local cache.
	 * @param bm
	 */
	private void loadImage(Bitmap bm) {
		ImageView iv			= (ImageView) findViewById(R.id.article_image);

		iv.setImageBitmap(bm);
	}


	/**
	 * 1. Marks this article as read without blocking the UI thread (Java threads require variables to be declared as final)
	 * 2. Asks listing screens to refresh
	 * @param articleId
	 * @param context
	 */
	private void reloadListingTabAndMarkAsRead(final long articleId, final Context context) {
		final int categoryIdFinal		= categoryID;

		new Thread(new Runnable() {
			public void run() {
				articleDAO.updateMarkAsRead(articleId);
				switch (categoryIdFinal) {
					case Article.SECTION_POLAND:
					case Article.SECTION_WORLD:
						BroadcastSender.getInstance(context).reloadTab(ApplicationRootActivity.Tab.NEWS);
					break;
					case Article.SECTION_OPINIONS:
					case Article.SECTION_REVIEWS:
					case Article.SECTION_CULTURE:
						BroadcastSender.getInstance(context).reloadTab(ApplicationRootActivity.Tab.ARTICLES);
					break;
				}
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
			float textSize		= UserPreferencesManager.convertTextSize(points);

			loadTextSize(textSize);
		}


		@Override
		public void finishSliding(int points) {
			float textSize		= UserPreferencesManager.convertTextSize(points);

			UserPreferencesManager.setTextSize(textSize, mActivity);
		}
	}


	/**
	 * Inner class that abstracts out the publication image attachment caching.
	 * Images are cached in a parent class member called images.
	 * Other methods should use this class rather than directly reference the images object methods.
	 * @author Krzysztof Kobrzak
	 */
	private class ImageCache {

		public boolean isCached(Long articleID) {
			return images.containsKey(articleID);
		}


		public Bitmap get(Long articleID) {
			SoftReference<Bitmap> sf		= images.get(articleID);
			if (sf != null) {
				return sf.get();
			}
			return null;
		}


		public void put(Long articleID, Bitmap bitmap) {
			images.put(articleID, new SoftReference<Bitmap>(bitmap) );
		}
	}


	private class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... imageURLs) {
			InputStream is;
			URL url;
			Bitmap bitmap	= null;

			try {
				url		= new URL(imageURLs[0]);
				is	= (InputStream) url.getContent();

				bitmap 	= BitmapFactory.decodeStream(is);
			} catch (MalformedURLException e) {
				// Ignore it
			} catch (IOException e) {
				// Ignore it
			}
			// The parent process might have requested this thread to be stopped.
			if (isCancelled() ) {
				return null;
			}
			return bitmap;
		}

		/*protected void onProgressUpdate(Integer... progress) {
			setProgressPercent(progress[0]);
		}*/


		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap == null) {
				return;
			}

			loadImage(bitmap);
			// Even though relying on articleID might seem risky, loading a wrong image shouldn't ever be the case.
			// This is because we cancel image loading operations every time loadArticle is called, ie. when users 
			// navigate between articles using the previous-next facility.
			imageCache.put(articleID, bitmap);
		}
	}
}
