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
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;


/**
 * @author Krzysztof Kobrzak
 */
public class ReadersCommentsActivity extends Activity {

	private long articleID;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.web_readers_comments);

		articleID					= filterIDFromUri(getIntent() );
		
		WebView tv			= (WebView) findViewById(R.id.web_comments);
		tv.loadUrl(URLDictionary.BASE_READERS_COMMENTS + articleID);
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
		
		// TODO Make sure articleID is indeed a number
		Long articleID			= Long.valueOf(articleIDString);
		return articleID;
	}
}
