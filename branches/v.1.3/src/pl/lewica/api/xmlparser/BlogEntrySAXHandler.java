/*
 Copyright 2012 lewica.pl

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.lewica.api.model.BlogEntry;
import pl.lewica.api.model.DataModel;
import pl.lewica.util.DateUtil;

/**
 * BlogEntrys feed XML parser using the SAX engine.
 * @author Krzysztof Kobrzak
 */
public class BlogEntrySAXHandler extends DefaultHandler implements SAXParserDelegate {
	// XML nodes, see documentation under http://lewica.pl/api/
	static final  String BLOG_ENTRY				= "publikacja";
	static final  String BLOG_ENTRY_ID			= "id";
	static final  String BLOG_ENTRY_BLOG_ID		= "id_blog";
	static final  String BLOG_ENTRY_AUTHOR_ID	= "id_autor";
	static final  String BLOG_ENTRY_PUB_DATE	= "data";
	static final  String BLOG_ENTRY_BLOG_TITLE	= "blog";
	static final  String BLOG_ENTRY_AUTHOR		= "autor";
	static final  String BLOG_ENTRY_TITLE		= "tytul";
	static final  String BLOG_ENTRY_TEXT		= "text";

	// In case any parsing problems the date falls back to this value:
	public String defaultDate	= "2000-01-01";

	private List<DataModel> blogEntries;
	private BlogEntry currentBlogEntry;
	private StringBuilder builder;


	/**
	 * Returns an array populated with data parsed by SAX.
	 * @see pl.lewica.api.xmlparser.SAXParserDelegate#getElements()
	 */
	public List<DataModel> getElements() {
		return this.blogEntries;
	}


	@Override
	public void startDocument()
			throws SAXException {
		super.startDocument();
		blogEntries	= new ArrayList<DataModel>();
		builder				= new StringBuilder();
	}


	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (name.equalsIgnoreCase(BLOG_ENTRY) ) {
			this.currentBlogEntry	= new BlogEntry();
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

		if (this.currentBlogEntry == null) {
			builder.setLength(0);
			return;
		}

		if (name.equalsIgnoreCase(BLOG_ENTRY_ID) ) {
			int ID	= Integer.parseInt(builder.toString() );
			currentBlogEntry.setID(ID);
		}
		else if (name.equalsIgnoreCase(BLOG_ENTRY_BLOG_ID) ) {
			int blogID	= Integer.parseInt(builder.toString() );
			currentBlogEntry.setBlogID(blogID);
		}
		else if (name.equalsIgnoreCase(BLOG_ENTRY_AUTHOR_ID) ) {
			int authorID	= Integer.parseInt(builder.toString() );
			currentBlogEntry.setAuthorID(authorID);
		}
		else if (name.equalsIgnoreCase(BLOG_ENTRY_PUB_DATE) ) {
			DateFormat df	= new SimpleDateFormat(DateUtil.DATE_MASK_SQL);
			Date pubDate	;
			
			try {
				pubDate	= df.parse(builder.toString() );
			} catch (ParseException e) {
				// We don't want to let an invalid date crash the application so let's just use any date
				pubDate	= java.sql.Date.valueOf(defaultDate);
			}
			currentBlogEntry.setDatePublished(pubDate);
		}
		else if (name.equalsIgnoreCase(BLOG_ENTRY_BLOG_TITLE) ) {
			currentBlogEntry.setBlogTitle(builder.toString() );
		}
		else if (name.equalsIgnoreCase(BLOG_ENTRY_AUTHOR) ) {
			currentBlogEntry.setAuthor(builder.toString() );
		}
		else if (name.equalsIgnoreCase(BLOG_ENTRY_TITLE) ) {
			currentBlogEntry.setTitle(builder.toString() );
		}
		else if (name.equalsIgnoreCase(BLOG_ENTRY_TEXT) ) {
			currentBlogEntry.setText(builder.toString() );
		}
		else if (name.equalsIgnoreCase(BLOG_ENTRY) ) {
			currentBlogEntry.setWasRead(false);

			blogEntries.add(currentBlogEntry);
		}
		builder.setLength(0);
	}
}
