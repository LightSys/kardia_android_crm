package org.lightsys.crmapp.CleanRepository.LoggedInAccount;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import org.lightsys.crmapp.CleanModels.Collaboratee;
import org.lightsys.crmapp.CleanModels.LoggedInUser;
import org.lightsys.crmapp.CleanModels.UserIdentifier;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Jake on 7/20/2015.
 *
 * I'm not sure if this is the best way to implement the database. You might be better off using an
 * ORM such as ActiveAndroid. It might also be better to create one SQLiteHelper class for the whole app,
 * or to use the singleton method.
 */
public class LIA_SQLiteHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CRMApp.db";
    public LIA_SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LoggedInAccountContract.SQL_CREATE_ACCOUNT_TABLE);
        db.execSQL(LoggedInAccountContract.SQL_CREATE_COLLABORATEES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /* ********************** Add methods *********************** */

    public void addLoggedInAccount(String username, String password, String serverAddress, String partnerId) {
        ContentValues values = new ContentValues();
        values.put(LoggedInAccountContract.LoggedInAccountTable.COLUMN_ACCOUNT_NAME, username);
        values.put(LoggedInAccountContract.LoggedInAccountTable.COLUMN_ACCOUNT_PASSWORD, password);
        values.put(LoggedInAccountContract.LoggedInAccountTable.COLUMN_SERVER_ADDRESS, serverAddress);
        values.put(LoggedInAccountContract.LoggedInAccountTable.COLUMN_PARTNER_ID, partnerId);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(LoggedInAccountContract.LoggedInAccountTable.TABLE_NAME, null, values);
        db.close();
    }

    public void addCollaboratees(ArrayList<UserIdentifier> collaboratees) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (UserIdentifier userIdentifier : collaboratees) {
            ContentValues values = new ContentValues();
            values.put(LoggedInAccountContract.CollaborateesTable.COLUMN_NAME, userIdentifier.getName());
            values.put(LoggedInAccountContract.CollaborateesTable.COLUMN_PARTNER_ID, userIdentifier.getPartnerId());

            db.insert(LoggedInAccountContract.CollaborateesTable.TABLE_NAME, null, values);
        }

        db.close();

    }
    /* ********************** Get methods *********************** */

    public LoggedInUser getLoggedInUser() {
        String queryString = "SELECT * FROM " + LoggedInAccountContract.LoggedInAccountTable.TABLE_NAME;

        //We could add a partnerId field to collaboratees table to allow us to support multiple logged in users, i.e. we could select collaboratees corresponding to the current user.

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(queryString, null);

        LoggedInUser loggedInUser = new LoggedInUser();
        // Note that this is storing and returning the password as plain text which is literally the worst thing.
        while(c.moveToNext()) {
            loggedInUser.setUsername(c.getString(1));
            loggedInUser.setPassword(c.getString(2));
            loggedInUser.setServerAddress(c.getString(3));
            loggedInUser.setPartnerId(c.getString(4));
        }

        c.close();
        db.close();

        loggedInUser.setCollaborateeList(this.getCollaboratees());

        return loggedInUser;
    }

    public ArrayList<UserIdentifier> getCollaboratees() {
        String queryCollaboratees = "SELECT * FROM " + LoggedInAccountContract.CollaborateesTable.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(queryCollaboratees, null);
        c.moveToFirst();

        /**
         * Create the array list for Collaboratees. We initialize its size to the number of collaboratees
         * we have. Since we're moving the cursor to the first, we need to go ahead and add the first
         * to the array list before we move into the while loop.
         */
        ArrayList<UserIdentifier> collaboratees = new ArrayList<>(c.getCount());
        collaboratees.add(new UserIdentifier(c.getString(1), c.getString(2)));

        while (c.moveToNext()) {
            collaboratees.add(new UserIdentifier(c.getString(1), c.getString(2)));
        }

        c.close();
        db.close();

        return collaboratees;
    }

    /* ********************** Update methods ******************** */

    }

    /* ********************** Contracts ************************* */

    class LoggedInAccountContract {
        public static final String TEXT_TYPE = " TEXT";
        public static final String INT_TYPE = " INTEGER";
        public static final String COMMA_SEP = ", ";
        // Note that this is storing the password in plaintext, which is pretty much the worst idea ever.
        // However, at this point, getting the app functional with the dev-server is more important.
        // Also, I'm not sure that I can actually authenticate with Centralix/Kardia without having the password
        // available.
        public static final String SQL_CREATE_ACCOUNT_TABLE =
                "CREATE TABLE " + LoggedInAccountTable.TABLE_NAME + " (" +
                        LoggedInAccountTable._ID + " INTEGER PRIMARY KEY, " +
                        LoggedInAccountTable.COLUMN_ACCOUNT_NAME + TEXT_TYPE + COMMA_SEP +
                        LoggedInAccountTable.COLUMN_ACCOUNT_PASSWORD + TEXT_TYPE + COMMA_SEP +
                        LoggedInAccountTable.COLUMN_SERVER_ADDRESS + TEXT_TYPE + COMMA_SEP +
                        LoggedInAccountTable.COLUMN_PARTNER_ID + TEXT_TYPE +
                        ")";

        public static final String SQL_CREATE_COLLABORATEES_TABLE =
                "CREATE TABLE " + CollaborateesTable.TABLE_NAME + " (" +
                        CollaborateesTable._ID + TEXT_TYPE + COMMA_SEP +
                        CollaborateesTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                        CollaborateesTable.COLUMN_PARTNER_ID + TEXT_TYPE +
                        ")";

        public static final String SQL_DELETE_ACCOUNT_TABLE = " DROP TABLE IF EXISTS " + LoggedInAccountTable.TABLE_NAME;
        public static final String SQL_DELETE_COLLABORATEES_TABLE = " DROP TABLE IF EXISTS " + CollaborateesTable.TABLE_NAME;

        public static abstract class LoggedInAccountTable implements BaseColumns {
            public static final String TABLE_NAME = "loggedInAccountTable";
            public static final String COLUMN_ACCOUNT_NAME = "accountName";
            public static final String COLUMN_ACCOUNT_PASSWORD = "accountPassword";
            public static final String COLUMN_SERVER_ADDRESS = "serverAddress";
            public static final String COLUMN_PARTNER_ID = "partnerId";
        }

        public static abstract class CollaborateesTable implements BaseColumns {
            public static final String TABLE_NAME = "collaborateesTable";
            public static final String COLUMN_NAME = "name";
            public static final String COLUMN_PARTNER_ID = "partnerId";
        }
}
