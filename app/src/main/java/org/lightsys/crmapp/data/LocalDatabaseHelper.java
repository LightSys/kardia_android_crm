package org.lightsys.crmapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Jake on 6/18/2015.
 */
public class LocalDatabaseHelper extends SQLiteOpenHelper {

    //private static LocalDatabaseHelper sInstance;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CRMApp.db";
    public LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* **************** Database Functions ************************** */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LocalDatabaseContract.SQL_CREATE_ACCOUNT_TABLE);
        db.execSQL(LocalDatabaseContract.SQL_CREATE_TIMESTAMP_TABLE);
        db.execSQL(LocalDatabaseContract.SQL_CREATE_MY_PEOPLE_TABLE);
        db.execSQL(LocalDatabaseContract.SQL_CREATE_ADDRESS_TABLE);
        db.execSQL(LocalDatabaseContract.SQL_CREATE_CONTACT_INFO_TABLE);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LocalDatabaseContract.SQL_DELETE_ACCOUNT_TABLE);
        onCreate(db);
    }



    /* ********************************* Add Queries ************************ */

    /**
     * Adds an account to the row of the database.
     * @param account, uses an Account object to retrieve needed data
     */
    public void addAccount(Account account){
        ContentValues values = new ContentValues();
        values.put(LocalDatabaseContract.AccountTable.COLUMN_ACCOUNT_NAME, account.getAccountName());
        values.put(LocalDatabaseContract.AccountTable.COLUMN_ACCOUNT_PASSWORD, account.getAccountPassword());
        values.put(LocalDatabaseContract.AccountTable.COLUMN_SERVER_ADDRESS, account.getServerName());
        values.put(LocalDatabaseContract.AccountTable.COLUMN_PARTNER_ID, account.getPartnerId());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(LocalDatabaseContract.AccountTable.TABLE_NAME, null, values);
        db.close();
    }

    public void addAddresses(ArrayList<GsonAddress> addresses) {
        String dupeQuery = "SELECT * FROM " + LocalDatabaseContract.AddressTable.TABLE_NAME + " WHERE kardiaId = ";
        SQLiteDatabase db = this.getWritableDatabase();

        for (GsonAddress address : addresses) {
            Cursor c = db.rawQuery(dupeQuery + address.id + ";", null);
            c.moveToFirst();

            if (c.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put(LocalDatabaseContract.AddressTable.COLUMN_PARTNER_ID, address.partnerId);
                values.put(LocalDatabaseContract.ContactInfoTable.COLUMN_REF_ID, address.id);
                values.put(LocalDatabaseContract.AddressTable.COLUMN_TYPE, address.locationType);
                values.put(LocalDatabaseContract.AddressTable.COLUMN_ADDRESS, address.address);
                values.put(LocalDatabaseContract.AddressTable.COLUMN_STREET_ADDRESS, address.addressOne);
                values.put(LocalDatabaseContract.AddressTable.COLUMN_CITY, address.city);
                values.put(LocalDatabaseContract.AddressTable.COLUMN_STATE, address.stateProvince);
                values.put(LocalDatabaseContract.AddressTable.COLUMN_ZIP_CODE, address.postalCode);
                values.put(LocalDatabaseContract.AddressTable.COLUMN_COUNTRY, address.countryCode);
                db.insert(LocalDatabaseContract.AddressTable.TABLE_NAME, null, values);
            }
        }
    }

    public void addCollaboratee(GsonCollaboratee collaboratee) {
        ContentValues values = new ContentValues();
        /**
         * This section should be updated to use GsonCollaboratee
         *
        values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_KARDIA_ID_REF, collaboratee.getKardiaIdRef());
        values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_COLLABORATOR_ID, collaboratee.getCollaboratorId());
        values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_COLLABORATOR_NAME, collaboratee.getCollaboratorName());
        values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_COLLABORATOR_TYPE_ID, collaboratee.getCollaboratorTypeId());
        values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_COLLABORATOR_TYPE, collaboratee.getCollaboratorType());
        values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_PARTNER_ID, collaboratee.getPartnerId());
        values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_PARTNER_NAME, collaboratee.getPartnerName());
        values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_PARTNER_REF, collaboratee.getPartnerRef());
        */
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(LocalDatabaseContract.AccountTable.TABLE_NAME, null, values);
        db.close();
    }

    public void addCollaboratees(ArrayList<GsonCollaboratee> collaboratees) {
        String dupeQuery = "SELECT * FROM myPeopleTable WHERE partnerId = ";

        SQLiteDatabase db = this.getWritableDatabase();


        for (GsonCollaboratee collaboratee : collaboratees) {
            Cursor c = db.rawQuery(dupeQuery + collaboratee.partnerId + ";", null);
            c.moveToFirst();

            if (c.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_KARDIA_ID_REF, collaboratee.id);
                values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_COLLABORATOR_ID, collaboratee.collaboratorId);
                values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_COLLABORATOR_NAME, collaboratee.collaboratorName);
                values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_COLLABORATOR_TYPE_ID, collaboratee.collaboratorTypeId);
                values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_COLLABORATOR_TYPE, collaboratee.collaboratorType);
                values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_PARTNER_ID, collaboratee.partnerId);
                values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_PARTNER_NAME, collaboratee.partnerName);
                values.put(LocalDatabaseContract.MyPeopleTable.COLUMN_PARTNER_REF, collaboratee.partnerRef);
                db.insert(LocalDatabaseContract.MyPeopleTable.TABLE_NAME, null, values);
            }
        }
        db.close();
    }

    public void addContactInfos(ArrayList<GsonContact> contactInfos) {
        String dupeQuery = "SELECT * FROM " + LocalDatabaseContract.ContactInfoTable.TABLE_NAME + " WHERE kardiaId = ";
        SQLiteDatabase db = this.getWritableDatabase();

        for (GsonContact contact : contactInfos) {
            Cursor c = db.rawQuery(dupeQuery + contact.id + ";", null);
            c.moveToFirst();

            if (c.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put(LocalDatabaseContract.ContactInfoTable.COLUMN_REF_ID, contact.id);
                values.put(LocalDatabaseContract.ContactInfoTable.COLUMN_PARTNER_ID, contact.partnerId);
                values.put(LocalDatabaseContract.ContactInfoTable.COLUMN_CONTACT_ID, contact.contactId);
                values.put(LocalDatabaseContract.ContactInfoTable.COLUMN_CONTACT, contact.contact);
                values.put(LocalDatabaseContract.ContactInfoTable.COLUMN_CONTACT_TYPE, contact.contactType);
                values.put(LocalDatabaseContract.ContactInfoTable.COLUMN_ADDRESS_ID, contact.addressId);
                values.put(LocalDatabaseContract.ContactInfoTable.COLUMN_PHONE_COUNTRY, contact.phoneCountry);
                values.put(LocalDatabaseContract.ContactInfoTable.COLUMN_PHONE_AREA, contact.phoneAreaCode);
                values.put(LocalDatabaseContract.ContactInfoTable.COLUMN_CONTACT_DATA, contact.contactData);

                db.insert(LocalDatabaseContract.ContactInfoTable.TABLE_NAME, null, values);
            }
        }
    }

    /**
     * Adds a timestamp to the database
     * @param date, date in standard millisecond form to be added
     */
    public void addTimeStamp(String date){
        ContentValues values = new ContentValues();
        values.put(LocalDatabaseContract.TimestampTable.COLUMN_DATE, date);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(LocalDatabaseContract.TimestampTable.TABLE_NAME, null, values);
        db.close();
    }

    /* ***************************** Get Queries ************************************** */

    /**
     * Returns all accounts from the AccountTable in the database. This function should be updated to use an async task.
     * @return A cursor to all accounts in the accountTable.
     */
    public ArrayList<Account> getAccounts() {
        ArrayList<Account> accounts = new ArrayList<Account>();
        String queryString = "SELECT * FROM " + LocalDatabaseContract.AccountTable.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(queryString, null);

        // Note that this is storing and returning the password as plain text which is literally the worst thing.
        while(c.moveToNext()) {
            Account account = new Account();
            account.setId(c.getInt(0));
            account.setAccountName(c.getString(1));
            account.setAccountPassword(c.getString(2));
            account.setServerName(c.getString(3));
            account.setPartnerId(c.getString(4));

            accounts.add(account);
        }

        c.close();
        db.close();

        return accounts;
    }

    public Cursor getCollaboratees() {
        String queryString = "SELECT * FROM myPeopleTable;";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(queryString, null);

        return c;
    }

    public String getPartnerId(long id) {
        String queryString = "SELECT partnerId FROM myPeopleTable WHERE " + LocalDatabaseContract.MyPeopleTable._ID + " == " + id;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(queryString, null);
        c.moveToFirst();
        String result = c.getString(0);

        c.close();
        db.close();
        return result;
    }

    /**
     * Pulls the timestamp from the database
     * @return A timestamp of the last update in millisecond form
     */
    public long getTimeStamp(){
        String queryString = "SELECT " + LocalDatabaseContract.TimestampTable.COLUMN_DATE +
                " FROM " + LocalDatabaseContract.TimestampTable.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(queryString, null);

        long date = -1;

        if(c.moveToFirst()){
            date = Long.parseLong(c.getString(0));
        }
        c.close();
        db.close();
        return date;
    }

    /* ***************************** Update Queries ********************************* */

    /**
     * Updates the timestamp from the originalDate (in milli) to currentDate (in milli)
     * @param originalDate, date (in milliseconds) of database timestamp before update
     * @param currentDate, date (in milliseconds) to update the timestamp to
     */
    public void updateTimeStamp(String originalDate, String currentDate){
        ContentValues values = new ContentValues();
        values.put(LocalDatabaseContract.TimestampTable.COLUMN_DATE, currentDate);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(LocalDatabaseContract.TimestampTable.TABLE_NAME, values,
                LocalDatabaseContract.TimestampTable.COLUMN_DATE + " = " + originalDate, null);
        db.close();
    }

    /* ***************************Contracts **************************************** */

    public static final class LocalDatabaseContract {
        public static final String TEXT_TYPE = " TEXT";
        public static final String INT_TYPE = " INTEGER";
        public static final String COMMA_SEP = ", ";
        // Note that this is storing the password in plaintext, which is pretty much the worst idea ever.
        // However, at this point, getting the app functional with the dev-server is more important.
        // Also, I'm not sure that I can actually authenticate with Centralix/Kardia without having the password
        // available.
        public static final String SQL_CREATE_ACCOUNT_TABLE =
                "CREATE TABLE " + AccountTable.TABLE_NAME + " (" +
                AccountTable._ID + " INTEGER PRIMARY KEY, " +
                AccountTable.COLUMN_ACCOUNT_NAME + TEXT_TYPE + COMMA_SEP +
                AccountTable.COLUMN_ACCOUNT_PASSWORD + TEXT_TYPE + COMMA_SEP +
                AccountTable.COLUMN_SERVER_ADDRESS + TEXT_TYPE + COMMA_SEP +
                AccountTable.COLUMN_PARTNER_ID + TEXT_TYPE +
                ")";

        public static final String SQL_CREATE_TIMESTAMP_TABLE =
                "CREATE TABLE " + TimestampTable.TABLE_NAME + " (" +
                TimestampTable._ID + " INTEGER PRIMARY KEY, " +
                TimestampTable.COLUMN_DATE + TEXT_TYPE + ")";

        public static final String SQL_CREATE_MY_PEOPLE_TABLE =
                "CREATE TABLE " + MyPeopleTable.TABLE_NAME + " (" +
                        MyPeopleTable._ID + " INTEGER PRIMARY KEY, " +
                        MyPeopleTable.COLUMN_KARDIA_ID_REF + TEXT_TYPE + COMMA_SEP +
                        MyPeopleTable.COLUMN_COLLABORATOR_ID + TEXT_TYPE + COMMA_SEP +
                        MyPeopleTable.COLUMN_COLLABORATOR_NAME + TEXT_TYPE + COMMA_SEP +
                        MyPeopleTable.COLUMN_COLLABORATOR_TYPE_ID + TEXT_TYPE + COMMA_SEP +
                        MyPeopleTable.COLUMN_COLLABORATOR_TYPE + TEXT_TYPE + COMMA_SEP +
                        MyPeopleTable.COLUMN_PARTNER_ID + TEXT_TYPE + COMMA_SEP +
                        MyPeopleTable.COLUMN_PARTNER_NAME + TEXT_TYPE + COMMA_SEP +
                        MyPeopleTable.COLUMN_PARTNER_REF + TEXT_TYPE +
                        ")";

        public static final String SQL_CREATE_ADDRESS_TABLE =
                "CREATE TABLE " + AddressTable.TABLE_NAME + " (" +
                        AddressTable._ID + " INTEGER PRIMARY KEY, " +
                        ContactInfoTable.COLUMN_REF_ID + TEXT_TYPE + COMMA_SEP +
                        AddressTable.COLUMN_PARTNER_ID + TEXT_TYPE + COMMA_SEP +
                        AddressTable.COLUMN_TYPE + TEXT_TYPE + COMMA_SEP +
                        AddressTable.COLUMN_ADDRESS + TEXT_TYPE + COMMA_SEP +
                        AddressTable.COLUMN_STREET_ADDRESS + TEXT_TYPE + COMMA_SEP +
                        AddressTable.COLUMN_CITY + TEXT_TYPE + COMMA_SEP +
                        AddressTable.COLUMN_STATE + TEXT_TYPE + COMMA_SEP +
                        AddressTable.COLUMN_ZIP_CODE + TEXT_TYPE + COMMA_SEP +
                        AddressTable.COLUMN_COUNTRY + TEXT_TYPE +
                        ")";

        public static final String SQL_CREATE_CONTACT_INFO_TABLE =
                "CREATE TABLE " + ContactInfoTable.TABLE_NAME + " (" +
                        ContactInfoTable._ID + " INTEGER PRIMARY KEY, " +
                        ContactInfoTable.COLUMN_REF_ID + TEXT_TYPE + COMMA_SEP +
                        ContactInfoTable.COLUMN_PARTNER_ID + TEXT_TYPE + COMMA_SEP +
                        ContactInfoTable.COLUMN_CONTACT_ID + TEXT_TYPE + COMMA_SEP +
                        ContactInfoTable.COLUMN_CONTACT + TEXT_TYPE + COMMA_SEP +
                        ContactInfoTable.COLUMN_CONTACT_TYPE + TEXT_TYPE + COMMA_SEP +
                        ContactInfoTable.COLUMN_ADDRESS_ID + TEXT_TYPE + COMMA_SEP +
                        ContactInfoTable.COLUMN_PHONE_COUNTRY + TEXT_TYPE + COMMA_SEP +
                        ContactInfoTable.COLUMN_PHONE_AREA + TEXT_TYPE + COMMA_SEP +
                        ContactInfoTable.COLUMN_CONTACT_DATA + TEXT_TYPE +
                        ")";


        public static final String SQL_DELETE_ACCOUNT_TABLE = " DROP TABLE IF EXISTS " + AccountTable.TABLE_NAME;

        public static abstract class AccountTable implements BaseColumns {
            public static final String TABLE_NAME = "accountTable";
            //public static final String COLUMN_TIMESTAMP = "timestamp";
            public static final String COLUMN_ACCOUNT_NAME = "accountName";
            public static final String COLUMN_ACCOUNT_PASSWORD = "accountPassword";
            public static final String COLUMN_SERVER_ADDRESS = "serverAddress";
            public static final String COLUMN_PARTNER_ID = "partnerId";
        }

        public static abstract class TimestampTable implements BaseColumns {
            public static final String TABLE_NAME = "timestampTable";
            public static final String COLUMN_DATE = "date";
        }

        /**
         * Note that columns in this table are named based on the JSON response from the API
         * Also note that I'm storing all data returned for each "Collaboratee", including
         * the collaborator name, id, and type. This might be useful, however it's currently
         * superfluous as the app currently only supports a single user to be signed in
         * (with some vestigal multi-account support from where I was referencing the donor app.
         * If the account is store store large data sets (such as 10,000+ unique people) it would
         * likely be a significant space savings to remove the extra data.
         *
         * Another thought would be to replace this table with a smaller table that only references
         * the main Kardia table, or even to simply implement the "My People" functionality
         * with logic that references the potential "all people" table. However, selecting from a
         * large table could be slow so maybe it's best to keep a secondary table after all.
         */
        public static abstract class MyPeopleTable implements BaseColumns {
            public static final String TABLE_NAME = "myPeopleTable";
            public static final String COLUMN_KARDIA_ID_REF = "kardiaIdRef";
            public static final String COLUMN_COLLABORATOR_ID = "collaboratorId";
            public static final String COLUMN_COLLABORATOR_NAME = "collaboratorName";
            public static final String COLUMN_COLLABORATOR_TYPE_ID = "collaboratorTypeId";
            public static final String COLUMN_COLLABORATOR_TYPE = "collaboratorType";
            public static final String COLUMN_PARTNER_ID = "partnerId";
            public static final String COLUMN_PARTNER_NAME = "partnerName";
            public static final String COLUMN_PARTNER_REF = "partnerRef";
        }

        public static abstract class AddressTable implements BaseColumns {
            public static final String TABLE_NAME = "addressTable";
            public static final String COLUMN_REF_ID = "kardiaId";
            public static final String COLUMN_PARTNER_ID = "partnerId";
            public static final String COLUMN_TYPE = "type";
            public static final String COLUMN_ADDRESS = "address";
            public static final String COLUMN_STREET_ADDRESS = "streetAddress";
            public static final String COLUMN_CITY = "city";
            public static final String COLUMN_STATE = "state";
            public static final String COLUMN_ZIP_CODE = "zipCode";
            public static final String COLUMN_COUNTRY = "country";

        }

        public static abstract class ContactInfoTable implements BaseColumns {
            public static final String TABLE_NAME = "contactInfoTable";
            public static final String COLUMN_REF_ID = "kardiaId";
            public static final String COLUMN_PARTNER_ID = "partnerId";
            public static final String COLUMN_CONTACT_ID = "contactId";
            public static final String COLUMN_CONTACT = "contact";
            public static final String COLUMN_CONTACT_TYPE = "contactType";
            public static final String COLUMN_ADDRESS_ID = "addressId";
            public static final String COLUMN_PHONE_COUNTRY = "phoneCountry";
            public static final String COLUMN_PHONE_AREA = "areaCode";
            public static final String COLUMN_CONTACT_DATA = "contactData";
        }

    }
}
