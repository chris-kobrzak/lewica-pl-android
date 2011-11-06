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
package pl.lewica.api.xmlparser;

import org.xml.sax.helpers.DefaultHandler;
import pl.lewica.api.model.DataModelType;

/**
 * @author Krzysztof Kobrzak
 */
public class SAXParserHandlerFactory {
	
	// Default factory method
	public static DefaultHandler create() {
		return new DefaultHandler();
	}


	/**
	 * Class constructor using the factory design pattern.
	 * @param modelType
	 * @return
	 */
	public static DefaultHandler create(DataModelType modelType) {
		switch (modelType) {
			case ARTICLE:
				return (DefaultHandler) new ArticleSAXHandler();

			case ANNOUNCEMENT:
				return (DefaultHandler) new AnnouncementSAXHandler();

			case HISTORY:
				return (DefaultHandler) new HistorySAXHandler();

			default :
				return new DefaultHandler();
		}
	}
}
