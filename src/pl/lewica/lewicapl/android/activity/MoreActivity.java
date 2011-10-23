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

import pl.lewica.lewicapl.R;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;


/**
 * @author Krzysztof Kobrzak
 *
 */
public class MoreActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView textview	= new TextView(this);
		textview.setText("Więcej");
		setContentView(textview);

		// Custom title background colour, http://stackoverflow.com/questions/2251714/set-title-background-color
		Resources res		= getResources();
		View titleView = getWindow().findViewById(android.R.id.title);
		if (titleView != null) {
			ViewParent parent	= titleView.getParent();
			if (parent != null && (parent instanceof View)) {
				View parentView	= (View)parent;
				parentView.setBackgroundColor(res.getColor(R.color.red));
			}
		}
	}
}
