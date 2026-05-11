package pl.lewica.lewicapl.android.parsing.xml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import pl.lewica.lewicapl.android.parsing.FeedParser

abstract class XmlFeedParser<T> : FeedParser<T> {
  protected abstract val entryTag: String

  protected open fun onTagOutsideEntry(name: String, text: String) {}
  protected abstract fun onField(name: String, text: String)
  protected abstract fun buildEntry(): T?
  protected abstract fun resetEntry()

  override fun parse(data: ByteArray): List<T> {
    val xmlParser = Xml.newPullParser()
    xmlParser.setInput(data.inputStream(), "UTF-8")
    val results = mutableListOf<T>()
    val textBuffer = StringBuilder()
    var insideEntry = false

    while (xmlParser.eventType != XmlPullParser.END_DOCUMENT) {
      when (xmlParser.eventType) {
        XmlPullParser.START_TAG -> {
          textBuffer.clear()
          if (xmlParser.name == entryTag) insideEntry = true
        }
        XmlPullParser.TEXT -> textBuffer.append(xmlParser.text)
        XmlPullParser.END_TAG -> {
          val text = textBuffer.toString().trim()
          if (insideEntry) {
            if (xmlParser.name == entryTag) {
              buildEntry()?.let { results.add(it) }
              resetEntry()
              insideEntry = false
            } else {
              onField(xmlParser.name, text)
            }
          } else {
            onTagOutsideEntry(xmlParser.name, text)
          }
          textBuffer.clear()
        }
      }
      xmlParser.next()
    }
    return results
  }
}
