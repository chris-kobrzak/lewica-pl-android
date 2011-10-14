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

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.lewica.api.model.CalendarEntry;
import pl.lewica.api.model.IModel;

/**
 *
 * @author Krzysztof Kobrzak
 */
public class CalendarEntrySAXParserDelegate extends DefaultHandler implements ISAXParserDelegate {
	List<IModel> calendarEntries;
	private CalendarEntry currentEntry;
	private int	 entryDay;
	private int	 entryMonth;
	private StringBuilder builder;

	// XML nodes as per http://lewica.pl/api/ docs.
	static final  String CALENDAR_ENTRY					= "wydarzenie";
	static final  String CALENDAR_ENTRY_DAY			= "dzien";
	static final  String CALENDAR_ENTRY_MONTH		= "miesiac";
	static final  String CALENDAR_ENTRY_YEAR			= "rok";
	static final  String CALENDAR_ENTRY_CONTENT	= "tytul";


	public List<IModel> getElements() {
		return this.calendarEntries;
	}


	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		calendarEntries	= new ArrayList<IModel>();
		builder			= new StringBuilder();
		entryDay		= 0;
		entryMonth		= 0;
	}


	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, name, attributes);

		if (name.equalsIgnoreCase(CALENDAR_ENTRY) ) {
			this.currentEntry	= new CalendarEntry();
		}
		else if (name.equalsIgnoreCase(CALENDAR_ENTRY_DAY) || name.equalsIgnoreCase(CALENDAR_ENTRY_MONTH) ) {
			builder.setLength(0);
		}
	}


	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}


	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);

		// These two nodes are in the header node, not inside the calendar entry so need to be treated in a special way.
		if (name.equalsIgnoreCase(CALENDAR_ENTRY_DAY) ) {
			entryDay	= Integer.parseInt(builder.toString() );
		}
		else if (name.equalsIgnoreCase(CALENDAR_ENTRY_MONTH) ) {
			entryMonth	= Integer.parseInt(builder.toString() );
		}

		if (this.currentEntry == null) {
			builder.setLength(0);
			return;
		}

		if (name.equalsIgnoreCase(CALENDAR_ENTRY_YEAR) ) {
			int entryYear	= Integer.parseInt(builder.toString() );
			currentEntry.setYear(entryYear);
		}
		else if (name.equalsIgnoreCase(CALENDAR_ENTRY_CONTENT) ) {
			currentEntry.setEvent(builder.toString() );
		}
		else if (name.equalsIgnoreCase(CALENDAR_ENTRY) ) {
			currentEntry.setDay(entryDay);
			currentEntry.setMonth(entryMonth);

			calendarEntries.add(currentEntry);
		}

		builder.setLength(0);
	}
}
