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
import pl.lewica.lewicapl.android.AndroidUtil;
import pl.lewica.lewicapl.android.UserPreferencesManager;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


/**
 * @author Krzysztof Kobrzak
 */
public class ReadersCommentsActivity extends Activity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.web_readers_comments);

		int themeId	= UserPreferencesManager.ThemeType.LIGHT.ordinal();
		if (! UserPreferencesManager.isLightTheme() ) {
			themeId		= UserPreferencesManager.ThemeType.DARK.ordinal();
		}

		long articleId			= AndroidUtil.filterIDFromUri(getIntent().getData() );
		WebView wv			= (WebView) findViewById(R.id.web_comments);
		wv.loadUrl(URLDictionary.buildURL_ReadersComments(articleId, themeId) );
	}
}
