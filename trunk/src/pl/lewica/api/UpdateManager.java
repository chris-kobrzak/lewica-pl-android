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

import org.xml.sax.helpers.DefaultHandler;

import pl.lewica.api.model.IModel;
import pl.lewica.api.model.ModelType;
import pl.lewica.api.xmlparser.ISAXParserDelegate;
import pl.lewica.api.xmlparser.SAXParserHandlerFactory;
import pl.lewica.util.HTTPUtil;

/**
 * @author Krzysztof Kobrzak, kobrzak@lewica.pl
 */
public class UpdateManager {
	
	private SAXParserFactory factory;


	/**
	 * Class constructor
	 */
	public UpdateManager(){
		factory		= SAXParserFactory.newInstance();
	}


	/**
	 * Loads a SAX parser, sets up its handler based on the argument passed to the method, 
	 * waits until it finishes downloading and parsing and returns a list of entries.
	 * @param modelType Entity to be updated as per ModelType enum.
	 * @param URL
	 * @return List
	 */
	public List<IModel> fetchAndParseRemoteData(ModelType modelType, String URL) {
		List<IModel> elements				= null;
		HTTPUtil httpUtil						= new HTTPUtil(URL);
		SAXParser saxp;

		try {
			DefaultHandler handler			= SAXParserHandlerFactory.create(modelType);

			saxp = factory.newSAXParser();
			saxp.parse(httpUtil.getInputStream(), handler);

			ISAXParserDelegate delegate	= (ISAXParserDelegate) handler; 
			elements								= delegate.getElements();

			return elements;
		} catch (Exception e) {
			// Typically, it's just URL connection time-out so let's just ignore it and return an empty list (see the statements below).
		}

		// Return empty list
		elements	= new ArrayList<IModel>();
		return elements;
	}
}
