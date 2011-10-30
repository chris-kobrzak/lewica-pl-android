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
package pl.lewica.api;

import java.util.ArrayList; 
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import pl.lewica.api.model.DataModel;
import pl.lewica.api.model.DataModelType;
import pl.lewica.api.xmlparser.SAXParserDelegate;
import pl.lewica.api.xmlparser.SAXParserHandlerFactory;
import pl.lewica.util.HTTPUtil;

/**
 * Manages the process of downloading and parsing data from the lewica.pl server.
 * @author Krzysztof Kobrzak, kobrzak@lewica.pl
 */
public class FeedDownloadManager {
	
	private SAXParserFactory factory;


	/**
	 * Class constructor
	 */
	public FeedDownloadManager() {
		factory		= SAXParserFactory.newInstance();

		// Android 1.6 fix.  Without it the parser would not work at all.
		try {
			factory.setFeature("http://xml.org/sax/features/namespaces", false);
			factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
		} catch (Exception e) {}
	}


	/**
	 * Loads a SAX parser, sets up its handler based on the argument passed to the method, 
	 * waits until it finishes downloading and parsing and returns a list of entries.
	 * Does not currently provide any progress monitoring facilities.
	 * @param modelType Entity to be updated as per ModelType enum.
	 * @param URL
	 * @return List
	 */
	public List<DataModel> fetchAndParse(DataModelType modelType, String URL) {
		List<DataModel> elements;
		HTTPUtil httpUtil;
		SAXParser saxp;

		try {
			saxp										= factory.newSAXParser();
			httpUtil									= new HTTPUtil(URL);
			final InputSource source			= new InputSource(httpUtil.getInputStream() );
			final XMLReader xmlreader		= saxp.getXMLReader();
			final DefaultHandler handler	= SAXParserHandlerFactory.create(modelType);

			xmlreader.setContentHandler(handler);
			xmlreader.parse(source);

			SAXParserDelegate delegate	= (SAXParserDelegate) handler; 
			elements								= delegate.getElements();

			return elements;
		} catch (Exception e) {
			// Typically, it's just URL connection time-out so let's just ignore it and return an empty list (see the statements below).
			e.printStackTrace();
		}

		// We are still here and that means there's been a problem in the try block.  Just return a empty list.
		elements	= new ArrayList<DataModel>();
		return elements;
	}
}
