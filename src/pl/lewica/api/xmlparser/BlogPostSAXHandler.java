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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.lewica.api.model.BlogPost;
import pl.lewica.api.model.DataModel;
import pl.lewica.util.DateUtil;

/**
 * Blog posts feed XML parser using the SAX engine.
 * @author Krzysztof Kobrzak
 */
public class BlogPostSAXHandler extends DefaultHandler implements SAXParserDelegate {
	// XML nodes, see documentation under http://lewica.pl/api/
	static final  String BLOG_POST				= "publikacja";
	static final  String BLOG_POST_ID			= "id";
	static final  String BLOG_POST_BLOG_ID		= "id_blog";
	static final  String BLOG_POST_AUTHOR_ID	= "id_autor";
	static final  String BLOG_POST_PUB_DATE	= "data";
	static final  String BLOG_POST_BLOG_TITLE	= "blog";
	static final  String BLOG_POST_AUTHOR		= "autor";
	static final  String BLOG_POST_TITLE		= "tytul";
	static final  String BLOG_POST_TEXT		= "tekst";

	private List<DataModel> blogPosts;
	private BlogPost currentBlogPost;
	private StringBuilder builder;


	/**
	 * Returns an array populated with data parsed by SAX.
	 * @see pl.lewica.api.xmlparser.SAXParserDelegate#getElements()
	 */
	public List<DataModel> getElements() {
		return this.blogPosts;
	}


	@Override
	public void startDocument()
			throws SAXException {
		super.startDocument();
		blogPosts	= new ArrayList<DataModel>();
		builder				= new StringBuilder();
	}


	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (name.equalsIgnoreCase(BLOG_POST) ) {
			this.currentBlogPost	= new BlogPost();
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

		if (this.currentBlogPost == null) {
			builder.setLength(0);
			return;
		}

		if (name.equalsIgnoreCase(BLOG_POST_ID) ) {
			int ID	= Integer.parseInt(builder.toString() );
			currentBlogPost.setId(ID);
		}
		else if (name.equalsIgnoreCase(BLOG_POST_BLOG_ID) ) {
			int blogID	= Integer.parseInt(builder.toString() );
			currentBlogPost.setBlogId(blogID);
		}
		else if (name.equalsIgnoreCase(BLOG_POST_AUTHOR_ID) ) {
			int authorID	= Integer.parseInt(builder.toString() );
			currentBlogPost.setAuthorId(authorID);
		}
		else if (name.equalsIgnoreCase(BLOG_POST_PUB_DATE) ) {
			Date pubDate = DateUtil.parseDateString(builder.toString() );
			currentBlogPost.setDatePublished(pubDate);
		}
		else if (name.equalsIgnoreCase(BLOG_POST_BLOG_TITLE) ) {
			currentBlogPost.setBlogTitle(builder.toString() );
		}
		else if (name.equalsIgnoreCase(BLOG_POST_AUTHOR) ) {
			currentBlogPost.setAuthor(builder.toString() );
		}
		else if (name.equalsIgnoreCase(BLOG_POST_TITLE) ) {
			currentBlogPost.setTitle(builder.toString() );
		}
		else if (name.equalsIgnoreCase(BLOG_POST_TEXT) ) {
			currentBlogPost.setText(builder.toString() );
		}
		else if (name.equalsIgnoreCase(BLOG_POST) ) {
			currentBlogPost.setWasRead(false);

			blogPosts.add(currentBlogPost);
		}
		builder.setLength(0);
	}
}
