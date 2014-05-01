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
import java.util.Date;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.lewica.api.model.Announcement;
import pl.lewica.api.model.DataModel;

/**
 * Announcements feed XML parser using the SAX engine.
 * @author Krzysztof Kobrzak
 */
public class AnnouncementSAXHandler extends DefaultHandler implements SAXParserDelegate {
	// XML nodes, see documentation under http://lewica.pl/api/
	static final  String ANNOUNCEMENT									= "ogloszenie";
	static final  String ANNOUNCEMENT_ID								= "id";
	static final  String ANNOUNCEMENT_WHAT							= "co";
	static final  String ANNOUNCEMENT_WHERE							= "gdzie";
	static final  String ANNOUNCEMENT_WHEN							= "kiedy";
	static final  String ANNOUNCEMENT_CONTENT					= "opis";
	static final  String ANNOUNCEMENT_PUBLISHED_BY				= "autor";
	static final  String ANNOUNCEMENT_PUBLISHED_BY_EMAIL	= "autor_email";

	private List<DataModel> announcements;
	private Announcement currentAnnouncement;
	private StringBuilder builder;


	/**
	 * Returns an array populated with data parsed by SAX.
	 * @see pl.lewica.api.xmlparser.SAXParserDelegate#getElements()
	 */
	public List<DataModel> getElements() {
		return this.announcements;
	}


	@Override
	public void startDocument()
			throws SAXException {
		super.startDocument();
		announcements	= new ArrayList<DataModel>();
		builder				= new StringBuilder();
	}


	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (name.equalsIgnoreCase(ANNOUNCEMENT) ) {
			this.currentAnnouncement	= new Announcement();
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

		if (this.currentAnnouncement == null) {
			builder.setLength(0);
			return;
		}

		if (name.equalsIgnoreCase(ANNOUNCEMENT_ID) ) {
			int id = Integer.parseInt(builder.toString() );
			currentAnnouncement.setId(id);
		}
		else if (name.equalsIgnoreCase(ANNOUNCEMENT_WHAT) ) {
			currentAnnouncement.setWhat(builder.toString() );
		}
		else if (name.equalsIgnoreCase(ANNOUNCEMENT_WHERE) ) {
			currentAnnouncement.setWhere(builder.toString() );
		}
		else if (name.equalsIgnoreCase(ANNOUNCEMENT_WHEN) ) {
			currentAnnouncement.setWhen(builder.toString() );
		}
		else if (name.equalsIgnoreCase(ANNOUNCEMENT_CONTENT) ) {
			currentAnnouncement.setContent(builder.toString() );
		}
		else if (name.equalsIgnoreCase(ANNOUNCEMENT_PUBLISHED_BY) ) {
			currentAnnouncement.setPublishedBy(builder.toString() );
		}
		else if (name.equalsIgnoreCase(ANNOUNCEMENT_PUBLISHED_BY_EMAIL) ) {
			currentAnnouncement.setPublishedByEmail(builder.toString() );
		}
		else if (name.equalsIgnoreCase(ANNOUNCEMENT) ) {
			Date dateNow	= new Date();

			currentAnnouncement.setAsNotRead();
			currentAnnouncement.setDateExpiry(dateNow);

			announcements.add(currentAnnouncement);
		}
		builder.setLength(0);
	}
}
