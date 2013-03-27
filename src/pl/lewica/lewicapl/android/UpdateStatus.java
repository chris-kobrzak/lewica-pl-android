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
package pl.lewica.lewicapl.android;


/**
 * Matrix for common attributes of the data update status object.
 * @author Krzysztof Kobrzak
 */
public class UpdateStatus {
	private int totalUpdated;


	public int getTotalUpdated() {
		return totalUpdated;
	}

	public void setTotalUpdated(int totalUpdated) {
		this.totalUpdated = totalUpdated;
	}
}
