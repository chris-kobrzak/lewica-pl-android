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
/**
 * 
 */
package pl.lewica.lewicapl.android;

import java.util.Map;
import java.util.Set;

/**
 * Helper class used by AsyncTask subclasses responsible for coordinating data updates.
 * @author Krzysztof Kobrzak
 */
public class ArticleUpdateStatus extends UpdateStatus {
	private int totalUpdated;
	// Each item should have the following keys: ID and Ext (file extension)
	private Set<Map<String, String>> images;
	
	
	@Override
	public int getTotalUpdated() {
		return totalUpdated;
	}
	@Override
	public void setTotalUpdated(int totalUpdated) {
		this.totalUpdated = totalUpdated;
	}
	public Set<Map<String, String>> getImages() {
		return images;
	}
	public void setImages(Set<Map<String, String>> images) {
		this.images = images;
	}
}
