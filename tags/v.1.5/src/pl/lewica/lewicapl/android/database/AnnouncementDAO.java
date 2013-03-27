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
package pl.lewica.lewicapl.android.database;


import android.content.ContentValues;
import android.content.Context;
import pl.lewica.api.model.Announcement;
import pl.lewica.util.DateUtil;


/**
 * Collection of Data Access method for interacting with the announcement entity.
 * @author Krzysztof Kobrzak
 */
public class AnnouncementDAO extends BaseTextDAO {

	private static final String DATABASE_TABLE			= "ZAnnouncement";

	public static final String FIELD_DATE_EXPIRY			= "ZDateExpiry";
	public static final String FIELD_WHAT						= "ZWhat";
	public static final String FIELD_WHERE					= "ZWhere";
	public static final String FIELD_WHEN						= "ZWhen";
	public static final String FIELD_PUBLISHED_BY			= "ZPublishedBy";
	public static final String FIELD_PUBLISHED_EMAIL	= "ZPublishedByEmail";
	public static final String FIELD_TEXT						= "ZText";

	protected static String[] fieldsForSingleRecord		= new String[] {FIELD_ID, FIELD_DATE_EXPIRY, FIELD_WHAT, FIELD_WHERE,  FIELD_WHEN, FIELD_WAS_READ, FIELD_PUBLISHED_BY, FIELD_PUBLISHED_EMAIL, FIELD_TEXT };
	protected static String[] fieldsForRecordSet				= new String[] {FIELD_ID, FIELD_DATE_EXPIRY, FIELD_WHAT, FIELD_WHERE, FIELD_WHEN, FIELD_WAS_READ };


	public AnnouncementDAO(Context context) {
		super(context, DATABASE_TABLE);
	}


	// String wasRead,
	public long insert(Announcement announcement) {
		ContentValues cv	= new ContentValues();

		cv.put(FIELD_ID,							announcement.getID() );
		cv.put(FIELD_WAS_READ,				0);	// It's a new announcement so it couldn't be read yet
		cv.put(FIELD_DATE_EXPIRY,			DateUtil.convertDateToString(announcement.getDateExpiry(), DateUtil.DATE_MASK_SQL) );
		cv.put(FIELD_WHAT,					announcement.getWhat() );
		cv.put(FIELD_WHERE,					announcement.getWhere() );
		cv.put(FIELD_WHEN,					announcement.getWhen() );
		cv.put(FIELD_PUBLISHED_BY,		announcement.getPublishedBy() );
		cv.put(FIELD_PUBLISHED_EMAIL,	announcement.getPublishedByEmail() );
		cv.put(FIELD_TEXT,						announcement.getContent() );

		return database.insert(DATABASE_TABLE, null, cv);
	}


	@Override
	protected String[] getFieldsForSingleRecord() {
		return fieldsForSingleRecord;
	}


	@Override
	protected String[] getFieldsForRecordSet() {
		return fieldsForRecordSet;
	}
}
