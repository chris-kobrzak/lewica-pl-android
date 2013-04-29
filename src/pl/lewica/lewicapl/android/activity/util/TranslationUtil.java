package pl.lewica.lewicapl.android.activity.util;

import android.content.Context;
import pl.lewica.api.model.Article;
import pl.lewica.lewicapl.R;
import pl.lewica.lewicapl.android.ApplicationRootActivity.Tab;
import pl.lewica.util.LanguageUtil;


public class TranslationUtil {

	private enum GrammaticalForm {
		SINGLE, ACCUSATIVE, GENITIVE
	}


	public static String getArticleCategoryLabel(int categoryId, Context context) {
		switch (categoryId) {
			case Article.SECTION_POLAND:
				return context.getString(R.string.heading_poland);

			case Article.SECTION_WORLD:
				return context.getString(R.string.heading_world);

			// TODO Confirm we need the case statements below
			case Article.SECTION_OPINIONS:
				return context.getString(R.string.heading_texts);

			case Article.SECTION_REVIEWS:
				return context.getString(R.string.heading_reviews);

			case Article.SECTION_CULTURE:
				return context.getString(R.string.heading_culture);
		}
		return null;
	}


	public static String getPolishUpdateStatusMessage(Tab tab, int totalUpdated, Context context) {
		GrammaticalForm form	= GrammaticalForm.SINGLE;

		if (totalUpdated == 1) {

		} else if (LanguageUtil.isPolishAccusative(totalUpdated) ) {
			form	= GrammaticalForm.ACCUSATIVE;
		} else if (LanguageUtil.isPolishGenitive(totalUpdated) ) {
			form	= GrammaticalForm.GENITIVE;
		}

		switch (tab) {
			case NEWS:
			case ARTICLES:
				switch (form) {
					case SINGLE:
						return context.getString(R.string.updated_1_article);
					case ACCUSATIVE:
						return context.getString(R.string.updated_2_articles).replace("%s", Integer.toString(totalUpdated) );
					case GENITIVE:
						return context.getString(R.string.updated_5_articles).replace("%s", Integer.toString(totalUpdated) );
				}
			break;
			case BLOGS:
				switch (form) {
					case SINGLE:
						return context.getString(R.string.updated_1_blog_entry);
					case ACCUSATIVE:
						return context.getString(R.string.updated_2_blog_entries).replace("%s", Integer.toString(totalUpdated) );
					case GENITIVE:
						return context.getString(R.string.updated_5_blog_entries).replace("%s", Integer.toString(totalUpdated) );
				}
			break;
			case ANNOUNCEMENTS:
				switch (form) {
					case SINGLE:
						return context.getString(R.string.updated_1_announcement);
					case ACCUSATIVE:
						return context.getString(R.string.updated_2_announcements).replace("%s", Integer.toString(totalUpdated) );
					case GENITIVE:
						return context.getString(R.string.updated_5_announcements).replace("%s", Integer.toString(totalUpdated) );
				}
			break;
			case HISTORY:
				switch (form) {
					case SINGLE:
						return context.getString(R.string.updated_1_history_item);
					case ACCUSATIVE:
						return context.getString(R.string.updated_2_history_items).replace("%s", Integer.toString(totalUpdated) );
					case GENITIVE:
						return context.getString(R.string.updated_5_history_items).replace("%s", Integer.toString(totalUpdated) );
				}
			break;
		}
		return null;
	}
}