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
import pl.lewica.api.model.ModelType;

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
	 * @param typeID
	 * @return
	 */
	public static DefaultHandler create(ModelType typeID) {
		switch (typeID) {
			case ARTICLE:
				return (DefaultHandler) new ArticleSAXParserDelegate();

			case ANNOUNCEMENT:
				return (DefaultHandler) new AnnouncementSAXParserDelegate();

			case CALENDAR_ENTRY:
				return (DefaultHandler) new CalendarEntrySAXParserDelegate();

			default :
				return new DefaultHandler();
		}
	}
}
