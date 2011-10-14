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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.lewica.api.model.Article;
import pl.lewica.api.model.IModel;

/**
 * XML parser using SAX engine.
 * @author Krzysztof Kobrzak
 */
public class ArticleSAXParserDelegate extends DefaultHandler implements ISAXParserDelegate {
	// XML nodes as per http://lewica.pl/api/ docs.
	static final  String ARTICLE									= "publikacja";
	static final  String ARTICLE_ID							= "id";
	static final  String ARTICLE_CATEGORY_ID			= "id_dzial";
	static final  String ARTICLE_RELATED_IDS			= "id_pokrewne";
	static final  String ARTICLE_PUB_DATE				= "data";
	static final  String ARTICLE_URL							= "url";
	static final  String ARTICLE_IMAGE						= "obrazek";
	static final  String ARTICLE_TITLE						= "tytul";
	static final  String ARTICLE_CONTENT					= "tekst";
	static final  String ARTICLE_EDITOR_COMMENT	= "opinia";
	static final String DATE_MASK							= "yyyy-MM-dd HH:mm:ss";

	// In case any parsing problems the date falls back to this value:
	public String defaultDate	= "2000-01-01";

	private List<IModel>	articles;
	private Article			currentArticle;
	private StringBuilder	builder;


	public List<IModel> getElements() {
		return this.articles;
	}


	@Override
	public void startDocument()
			throws SAXException {
		super.startDocument();
		articles	= new ArrayList<IModel>();
		builder		= new StringBuilder();
	}


	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (name.equalsIgnoreCase(ARTICLE) ) {
			this.currentArticle	= new Article();
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
		if (this.currentArticle == null) {
			builder.setLength(0);
			return;
		}

		if (name.equalsIgnoreCase(ARTICLE_ID) ) {
			int ID	= Integer.parseInt(builder.toString() );
			currentArticle.setID(ID);
		}
		else if (name.equalsIgnoreCase(ARTICLE_CATEGORY_ID) ) {
			int categoryID	= Integer.parseInt(builder.toString() );
			currentArticle.setArticleCategoryID(categoryID);
		}
		else if (name.equalsIgnoreCase(ARTICLE_RELATED_IDS) ) {
			List<Integer> articleIDs	= new ArrayList<Integer>();
			Scanner s						= new Scanner(builder.toString() ).useDelimiter(",");

			while (s.hasNextInt() ) {
				articleIDs.add(s.nextInt() );
			}
			currentArticle.setRelatedIDs(articleIDs);
		}
		else if (name.equalsIgnoreCase(ARTICLE_PUB_DATE) ) {
			DateFormat df	= new SimpleDateFormat(ArticleSAXParserDelegate.DATE_MASK);
			Date pubDate	;

			try {
				pubDate	= df.parse(builder.toString() );
			} catch (ParseException e) {
				// We don't want to let an invalid date crash the application so let's just use any date
				pubDate	= java.sql.Date.valueOf(defaultDate);
			}
			currentArticle.setDatePublished(pubDate);
		}
		else if (name.equalsIgnoreCase(ARTICLE_IMAGE) ) {
			String imgExt	= builder.toString();
			
			if (imgExt.length() > 0) {
				currentArticle.setImageExtension(imgExt);
				currentArticle.setHasThumbnail(true);
			} else {
				currentArticle.setHasThumbnail(false);
			}
		}
		else if (name.equalsIgnoreCase(ARTICLE_URL) ) {
			URL url;
			try {
				url = new URL(builder.toString() );
				currentArticle.setURL(url);
			} catch (MalformedURLException e) {
				// We don't want to let an invalid date crash the application so let's just ignore the article's URL
				currentArticle.setURL(null);
			}
		}
		else if (name.equalsIgnoreCase(ARTICLE_TITLE) ) {
			currentArticle.setTitle(builder.toString() );
		}
		else if (name.equalsIgnoreCase(ARTICLE_CONTENT) ) {
			currentArticle.setText(builder.toString() );
		}
		else if (name.equalsIgnoreCase(ARTICLE_EDITOR_COMMENT) ) {
			currentArticle.setEditorComment(builder.toString() );
		}
		else if (name.equalsIgnoreCase(ARTICLE) ) {
			currentArticle.setWasRead(false);

			articles.add(currentArticle);
		}
		builder.setLength(0);
	}
}