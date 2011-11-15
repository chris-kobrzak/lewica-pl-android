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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import pl.lewica.api.model.Article;
import pl.lewica.api.url.ArticleURL;
import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.database.ArticleDAO;


public class ArticleActivity extends Activity {
	// This intent's base Uri.  It should have a numeric ID appended to it.
	public static final String URI_BASE						= "content://lewicapl/articles/article/";
	public static final String URI_BASE_COMMENTS	= "content://lewicapl/articles/article/comments/";

	private static Typeface categoryTypeface;
	private static Map<Long,Bitmap> images	= new HashMap<Long,Bitmap>();

	private long articleID;
	private int categoryID;
	private String articleURL;
	private ArticleDAO articleDAO;
	private Map<String,Long> nextPrevID;
	private ImageLoadTask imageTask;

	private int colIndex_CategoryID;
	private int colIndex_WasRead;
	private int colIndex_Title;
	private int colIndex_DatePub;
	private int colIndex_URL;
	private int colIndex_Content;
	private int colIndex_Comment;
	private int colIndex_HasThumb;
	private int colIndex_ThumbExt;

	private static SimpleDateFormat dateFormat	= new SimpleDateFormat("dd/MM/yyyy HH:mm");



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_article);

		Resources res			= getResources();
		
		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		articleID					= filterIDFromUri(getIntent() );
		categoryID				= 0;

		// Access data
		articleDAO				= new ArticleDAO(this);
		articleDAO.open();
		
		// Fill views with data
		loadContent(articleID, this);

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

			default :
				return super.onOptionsItemSelected(item);
		}
	}




	/**
	 * Responsible for loading content to the views and marking the current article as read.
	 * It is meant to be called every time user accesses this activity, either by selecting an article from the list
	 * or navigating between articles using the previous-next facility (not yet implemented).
	 * @param id
	 */
	public void loadContent(long ID, Context context) {
		// Save it in this object's field
		articleID	= ID;
		// Fetch database record
		Cursor cursor				= articleDAO.selectOne(ID);

		startManagingCursor(cursor);

		// In order to capture a cell, you need to work what their index
		colIndex_URL				= cursor.getColumnIndex(ArticleDAO.FIELD_URL);
		colIndex_CategoryID	= cursor.getColumnIndex(ArticleDAO.FIELD_CATEGORY_ID);
		colIndex_Title			= cursor.getColumnIndex(ArticleDAO.FIELD_TITLE);
		colIndex_DatePub		= cursor.getColumnIndex(ArticleDAO.FIELD_DATE_PUBLISHED);
		colIndex_Content		= cursor.getColumnIndex(ArticleDAO.FIELD_TEXT);
		colIndex_Comment		= cursor.getColumnIndex(ArticleDAO.FIELD_EDITOR_COMMENT);
		colIndex_WasRead		= cursor.getColumnIndex(ArticleDAO.FIELD_WAS_READ);
		colIndex_HasThumb	= cursor.getColumnIndex(ArticleDAO.FIELD_HAS_IMAGE);
		colIndex_ThumbExt	= cursor.getColumnIndex(ArticleDAO.FIELD_IMAGE_EXTENSION);

		// When using previous-next facility you need to make sure the scroll view's position is at the top of the screen
		ScrollView sv	= (ScrollView) findViewById(R.id.article_scroll_view);
		sv.fullScroll(View.FOCUS_UP);
		sv.setSmoothScrollingEnabled(true);

		// On the same token, make sure an image belonging to another article never appears here as a result of an async process.
		if (imageTask != null) {
			imageTask.cancel(true);
		}

		// Save the URL in memory so the "share link" option in menu can access it easily
		articleURL				= cursor.getString(colIndex_URL);

		// Now start populating all views with data
		TextView tv, tvComment;
		tv							= (TextView) findViewById(R.id.article_title);
		tv.setText(cursor.getString(colIndex_Title) );

		tv							= (TextView) findViewById(R.id.article_category);
		categoryID				= cursor.getInt(colIndex_CategoryID);
		tv.setTypeface(categoryTypeface);
		switch (categoryID) {
			case Article.SECTION_POLAND:
				tv.setText(context.getString(R.string.heading_poland) );
				break;

			case Article.SECTION_WORLD:
				tv.setText(context.getString(R.string.heading_world) );
				break;

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
		long unixTime		= cursor.getLong(colIndex_DatePub);	// Dates are stored as Unix timestamps
		Date d					= new Date(unixTime);
		tv.setText(dateFormat.format(d) );

		tv							= (TextView) findViewById(R.id.article_content);
		// Fix for carriage returns displayed as rectangle characters in Android 1.6 
		tv.setText(cursor.getString(colIndex_Content).replace("\r", "") );

		tv							= (TextView) findViewById(R.id.article_editor_comment);
		tvComment			= (TextView) findViewById(R.id.article_editor_comment_top);
		String commentString		= cursor.getString(colIndex_Comment);
		if (commentString != null && commentString.length() > 0) {
			tv.setText(commentString.replace("\r", "") );
			// Reset visibility, may be useful when users navigate between articles (previous-next facility to be added in the future)
			tv.setVisibility(View.VISIBLE);
			tvComment.setVisibility(View.VISIBLE);
		} else {
			tv.setText("");
			tv.setVisibility(View.GONE);
			// Hide top, dark grey bar
			tvComment.setVisibility(View.GONE);
		}

		// Reset image to avoid issues when navigating between previous and next articles
		ImageView iv			= (ImageView) findViewById(R.id.article_image);
		iv.setImageBitmap(null);
		if (cursor.getInt(colIndex_HasThumb) == 1) {
			if (images.containsKey(articleID) ) {
				loadImage(images.get(articleID) );
			} else {
				String imageUrl		= ArticleURL.buildURLImage(ID, cursor.getString(colIndex_ThumbExt) );
	
				// Images need to be downloaded in a separate thread as we cannot block the UI thread.
				// Note: we do not currently cache images on this screen and they are downloaded from the Internet on every request.
				imageTask	= new ImageLoadTask();
				imageTask.execute(imageUrl);
			}
		}
		

		// Only mark the article as read once.  If it's already marked as such - just stop here.
		if (cursor.getInt(colIndex_WasRead) == 1) {
			cursor.close();
			return;
		}
		// Tidy up
		cursor.close();

		// Mark this article as read without blocking the UI thread
		// Java threads require variables to be declared as final
		final long articleIDThread	= ID;
		new Thread(new Runnable() {
			public void run() {
				articleDAO.updateMarkAsRead(articleIDThread);
			}
		}).start();
	}


	/**
	 * Puts a bitmap in the image view.  Adds the bitmap to the local cache.
	 * @param bm
	 */
	public void loadImage(Bitmap bm) {
		ImageView iv			= (ImageView) findViewById(R.id.article_image);

		iv.setImageBitmap(bm);
	}


	/**
	 * Activities on Android are invoked with a Uri string.  This method captures and returns the last bit of this Uri
	 * which it assumes to be a numeric ID of the current article.
	 * @param intent
	 * @return
	 */
	public long filterIDFromUri(Intent intent) {
		Uri uri						= intent.getData();
		String articleIDString	= uri.getLastPathSegment();
		
		// TODO Make sure articleID is a number
		Long articleID			= Long.valueOf(articleIDString);
		return articleID;
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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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
			images.put(articleID, bitmap);
		}
	}
}
