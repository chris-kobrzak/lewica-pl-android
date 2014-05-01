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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.lewica.api.model.Article;
import pl.lewica.api.model.DataModel;
import pl.lewica.util.DateUtil;
import pl.lewica.util.ListUtil;

/**
 * Articles feed XML parser using the SAX engine.
 * @author Krzysztof Kobrzak
 */
public class ArticleSAXHandler extends DefaultHandler implements SAXParserDelegate {
	// XML nodes as per http://lewica.pl/api/ docs.
	public static final  String ARTICLE								= "publikacja";
	public static final  String ARTICLE_ID							= "id";
	public static final  String ARTICLE_CATEGORY_ID			= "id_dzial";
	public static final  String ARTICLE_RELATED_IDS			= "id_pokrewne";
	public static final  String ARTICLE_PUB_DATE				= "data";
	public static final  String ARTICLE_URL						= "url";
	public static final  String ARTICLE_IMAGE					= "obrazek";
	public static final  String ARTICLE_TITLE						= "tytul";
	public static final  String ARTICLE_CONTENT				= "tekst";
	public static final  String ARTICLE_EDITOR_COMMENT	= "opinia";

	private List<DataModel> articles;
	private Article currentArticle;
	private StringBuilder builder;


	/**
	 * Returns an array populated with data parsed by SAX.
	 * @see pl.lewica.api.xmlparser.SAXParserDelegate#getElements()
	 */
	public List<DataModel> getElements() {
		return this.articles;
	}


	@Override
	public void startDocument()
			throws SAXException {
		super.startDocument();
		articles	= new ArrayList<DataModel>();
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
			int id	= Integer.parseInt(builder.toString() );
			currentArticle.setId(id);
		}
		else if (name.equalsIgnoreCase(ARTICLE_CATEGORY_ID) ) {
			int categoryId	= Integer.parseInt(builder.toString() );
			currentArticle.setArticleCategoryId(categoryId);
		}
		else if (name.equalsIgnoreCase(ARTICLE_RELATED_IDS) ) {
			List<Integer> articleIds = ListUtil.parseIntegersList(builder.toString() );
			currentArticle.setRelatedIds(articleIds);
		}
		else if (name.equalsIgnoreCase(ARTICLE_PUB_DATE) ) {
			Date pubDate = DateUtil.parseDateString(builder.toString() );
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
			currentArticle.setAsNotRead();

			articles.add(currentArticle);
		}
		builder.setLength(0);
	}
}