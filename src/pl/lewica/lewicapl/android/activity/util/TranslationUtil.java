package pl.lewica.lewicapl.android.activity.util;

import android.content.Context;
import pl.lewica.api.model.Article;
import pl.lewica.lewicapl.R;


public class TranslationUtil {

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
}
