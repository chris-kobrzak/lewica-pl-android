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

import pl.lewica.URLDictionary;
import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.UserPreferencesManager;
import pl.lewica.lewicapl.android.theme.Theme;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * @author Krzysztof Kobrzak
 *
 */
public class MoreActivity extends Activity {

	private static Typeface categoryTypeface;
	private TextView tvIntro;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_more);

		// Custom font used by the category headings
		categoryTypeface	= Typeface.createFromAsset(getAssets(), "Impact.ttf");

		loadView();
		loadTheme(getApplicationContext() );

		// Custom title background colour, http://stackoverflow.com/questions/2251714/set-title-background-color
		Resources res		= getResources();
		View titleView = getWindow().findViewById(android.R.id.title);
		if (titleView != null) {
			ViewParent parent	= titleView.getParent();
			if (parent != null && (parent instanceof View)) {
				View parentView	= (View)parent;
				parentView.setBackgroundColor(res.getColor(R.color.red) );
			}
		}
	}


	private void loadView() {
		TextView tv;
		
		tvIntro = (TextView) findViewById(R.id.more_introduction);
		tvIntro.setText(this.getString(R.string.paragraph_more_intro) );

		tv	= (TextView) findViewById(R.id.more_homepage);
		tv.setTypeface(categoryTypeface);
		tv.setText(this.getString(R.string.heading_homepage) );
		tv.setClickable(true);
		tv.setOnClickListener(new TextClickListener() );
		
		tv	= (TextView) findViewById(R.id.more_facebook);
		tv.setTypeface(categoryTypeface);
		tv.setText(this.getString(R.string.heading_facebook) );
		tv.setOnClickListener(new TextClickListener() );
		
		tv	= (TextView) findViewById(R.id.more_blogs);
		tv.setTypeface(categoryTypeface);
		tv.setText(this.getString(R.string.heading_blogs) );
		tv.setOnClickListener(new TextClickListener() );
		
		tv	= (TextView) findViewById(R.id.more_links);
		tv.setTypeface(categoryTypeface);
		tv.setText(this.getString(R.string.heading_links) );
		tv.setOnClickListener(new TextClickListener() );
		
		tv	= (TextView) findViewById(R.id.more_search);
		tv.setTypeface(categoryTypeface);
		tv.setText(this.getString(R.string.heading_search) );
		tv.setOnClickListener(new TextClickListener() );
		
		tv	= (TextView) findViewById(R.id.more_team);
		tv.setTypeface(categoryTypeface);
		tv.setText(this.getString(R.string.heading_team) );
		tv.setOnClickListener(new TextClickListener() );
	}


	private void loadTheme(Context context) {
		Theme theme	= UserPreferencesManager.getThemeInstance(context);
		ScrollView layout		= (ScrollView) findViewById(R.id.list_more_scroll_view);
		layout.setBackgroundColor(theme.getBackgroundColour() );

		tvIntro.setTextColor(theme.getTextColour() );
	}


	class TextClickListener implements View.OnClickListener {
		public void onClick(View v) {
			String urlString;
			switch (v.getId() ) {
				case R.id.more_homepage:
					urlString	= URLDictionary.HOMEPAGE;
					break;

				case R.id.more_facebook:
					urlString	= URLDictionary.FACEBOOK;
					break;

				case R.id.more_blogs:
					urlString	= URLDictionary.BLOGS;
					break;

				case R.id.more_links:
					urlString	= URLDictionary.LINKS;
					break;

				case R.id.more_search:
					urlString	= URLDictionary.SEARCH;
					break;

				case R.id.more_team:
					urlString	= URLDictionary.TEAM;
					break;

				default:
					urlString	= URLDictionary.HOMEPAGE;
			}
			
			Uri uri			= Uri.parse(urlString);
			Intent intent	= new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}
}
