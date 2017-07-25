package org.lightsys.crmapp.data;

import android.net.Uri;

/**
 * Created by nathan on 3/9/16.
 *
 * Edited by Ca2br and Judah on 7/26/16
 * this is a thing
 * this thing store table information for the local database
 * the local database that, at the moment, doesn't work :(
 * this class doesn't actually do anything with the actual database
 * it just holds all the constants used when talking to the broken local database
 */
public class CRMContract {
    public static final String providerAuthority = "org.lightsys.crmapp.provider";
    public static final String accountType = "org.lightsys.crmapp";

    public static final class StaffTable {
        public static final String TABLE_NAME = "staff";
        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority("org.lightsys.crmapp.provider").path("staff").build();

        public static final String PARTNER_ID = "partnerId";
        public static final String KARDIA_LOGIN = "kardiaLogin";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                        PARTNER_ID + ", " +
                        KARDIA_LOGIN +
                        ")";
    }

    public static final class CollaborateeTable {
        public static final String TABLE_NAME = "collaboratee";
        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority("org.lightsys.crmapp.provider").path(TABLE_NAME).build();

        public static final String COLLABORATER_ID = "collaboraterId";
        public static final String PARTNER_ID = "partnerId";
        public static final String PARTNER_NAME = "partnerName";
        public static final String SURNAME = "surname";
        public static final String GIVEN_NAMES = "givenNames";
        public static final String PHONE = "phone";
        public static final String PHONE_ID = "phoneId";
        public static final String CELL = "cell";
        public static final String CELL_ID = "cellId";
        public static final String EMAIL = "email";
        public static final String EMAIL_ID = "emailId";
        public static final String ADDRESS_1 = "address1";
        public static final String CITY = "city";
        public static final String STATE_PROVINCE = "stateProvince";
        public static final String POSTAL_CODE = "postalCode";
        public static final String PHONE_JSON_ID = "phoneJsonId";
        public static final String CELL_JSON_ID = "cellJsonId";
        public static final String EMAIL_JSON_ID = "emailJsonId";
        public static final String ADDRESS_JSON_ID = "addressJsonId";
        public static final String PARTNER_JSON_ID = "partnerJsonId";
        public static final String PROFILE_PICTURE = "profilePicture";
        
        public static final String[] ALL_COLUMNS = {
                COLLABORATER_ID, PARTNER_ID, PARTNER_NAME, EMAIL, PHONE, ADDRESS_1, CITY, STATE_PROVINCE,
                POSTAL_CODE, CELL, SURNAME, GIVEN_NAMES, PHONE_ID, CELL_ID, EMAIL_ID, PHONE_JSON_ID,
                CELL_JSON_ID, EMAIL_JSON_ID, ADDRESS_JSON_ID, PARTNER_JSON_ID };

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLLABORATER_ID + ", " +
                PARTNER_ID + " INTEGER PRIMARY KEY, " +
                PARTNER_NAME + ", " +
                SURNAME + ", " +
                GIVEN_NAMES + ", " +
                PHONE + ", " +
                PHONE_ID + ", " +
                CELL + ", " +
                CELL_ID + ", " +
                EMAIL + ", " +
                EMAIL_ID + ", " +
                ADDRESS_1 + ", " +
                CITY + ", " +
                STATE_PROVINCE + ", " +
                POSTAL_CODE + ", " +
                PHONE_JSON_ID + ", " +
                CELL_JSON_ID + ", " +
                EMAIL_JSON_ID + ", " +
                ADDRESS_JSON_ID + ", " +
                PARTNER_JSON_ID + ", " +
                PROFILE_PICTURE + ")";
    }

    public static final class TimelineTable {
        public static final String TABLE_NAME = "timeline";
        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority("org.lightsys.crmapp.provider").path(TABLE_NAME).build();

        public static final String CONTACT_ID = "contactId";
        public static final String PARTNER_ID = "partnerId";
        public static final String COLLABORATEE_ID = "collaborateeId";
        public static final String COLLABORATEE_NAME = "collaborateeName";
        public static final String CONTACT_HISTORY_ID = "contactHistoryId";
        public static final String CONTACT_HISTORY_TYPE = "contactHistoryType";
        public static final String SUBJECT = "subject";
        public static final String NOTES = "notes";
        public static final String DATE = "date";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("  +
                CONTACT_ID + "," +
                PARTNER_ID + "," +
                COLLABORATEE_ID + "," +
                COLLABORATEE_NAME + "," +
                CONTACT_HISTORY_ID + " PRIMARY KEY, " +
                CONTACT_HISTORY_TYPE + "," +
                SUBJECT + "," +
                NOTES + "," +
                DATE + ")";
    }

    public static final class EngagementTable {
        public static final String TABLE_NAME = "engagment";
        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority("org.lightsys.crmapp.provider").path(TABLE_NAME).build();

        public static final String PARTNER_ID = "partnerId";
        public static final String ENGAGEMENT_ID = "engagementId";
        public static final String DESCRIPTION = "description";
        public static final String ENGAGEMENT_TRACK = "track";
        public static final String ENGAGEMENT_STEP = "step";
        public static final String ENGAGEMENT_COMMENTS = "comments";
        public static final String COMPLETION_STATUS = "completionStatus";
        public static final String DATE = "date";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                PARTNER_ID + "," +
                ENGAGEMENT_ID + "," +
                DESCRIPTION + "," +
                ENGAGEMENT_TRACK + "," +
                ENGAGEMENT_STEP + "," +
                ENGAGEMENT_COMMENTS + "," +
                COMPLETION_STATUS+ "," +
                DATE + ")";

    }
}
