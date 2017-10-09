package org.lightsys.crmapp.data;

import android.accounts.AccountManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by nathan on 3/10/16.
 *
 * Commented by Judah on 7/26/16.
 *
 * this class holds methods for query, insert, delete, and update
 */
public class LocalDBProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private SQLiteDatabase mDatabase;

    private Context mContext;
    private AccountManager mAccountManager;

    static {
        //things used to identify what tables we are dealing with
        sUriMatcher.addURI(LocalDBTables.providerAuthority, LocalDBTables.StaffTable.TABLE_NAME, 1);
        sUriMatcher.addURI(LocalDBTables.providerAuthority, LocalDBTables.CollaborateeTable.TABLE_NAME, 2);
        sUriMatcher.addURI(LocalDBTables.providerAuthority, LocalDBTables.TimelineTable.TABLE_NAME, 3);
        sUriMatcher.addURI(LocalDBTables.providerAuthority, LocalDBTables.EngagementTable.TABLE_NAME, 4);
        sUriMatcher.addURI(LocalDBTables.providerAuthority, LocalDBTables.EngagementStepTable.TABLE_NAME, 5);
        sUriMatcher.addURI(LocalDBTables.providerAuthority, LocalDBTables.EngagementTrackTable.TABLE_NAME, 6);
        sUriMatcher.addURI(LocalDBTables.providerAuthority, LocalDBTables.NotificationsTable.TABLE_NAME, 7);
        sUriMatcher.addURI(LocalDBTables.providerAuthority, LocalDBTables.ConnectionTable.TABLE_NAME, 8);
        sUriMatcher.addURI(LocalDBTables.providerAuthority, LocalDBTables.FormTable.TABLE_NAME, 9);
    }


    @Override
    public boolean onCreate() {
        LocalDBCreator dbCreator = new LocalDBCreator(getContext());
        mDatabase = dbCreator.getWritableDatabase();

        return true;
    }

    //function that queries the database
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        //determine what table we are dealing with
        switch (sUriMatcher.match(uri)) {
            case 1:
                builder.setTables(LocalDBTables.StaffTable.TABLE_NAME);
                break;
            case 2:
                builder.setTables(LocalDBTables.CollaborateeTable.TABLE_NAME);
                break;
            case 3:
                builder.setTables(LocalDBTables.TimelineTable.TABLE_NAME);
                break;
            case 4:
                builder.setTables(LocalDBTables.EngagementTable.TABLE_NAME);
                break;
            case 5:
                builder.setTables(LocalDBTables.EngagementStepTable.TABLE_NAME);
                break;
            case 6:
                builder.setTables(LocalDBTables.EngagementTrackTable.TABLE_NAME);
                break;
            case 7:
                builder.setTables(LocalDBTables.NotificationsTable.TABLE_NAME);
                break;
            case 8:
                builder.setTables(LocalDBTables.ConnectionTable.TABLE_NAME);
                break;
            case 9:
                builder.setTables(LocalDBTables.FormTable.TABLE_NAME);
                break;
            default:
                break;
        }
        //Log.d("LocalDBProvider", "query: " + mDatabase.isDatabaseIntegrityOk() + ": " + projection.length + ": " + selection + ": " + selectionArgs.length + ": " + null + ": " + null + ": " + sortOrder);
        return builder.query(mDatabase, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    //function that inserts stuffs into the database
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;
        int id;

        //determine what table we are dealing with
        switch (sUriMatcher.match(uri)) {
            case 1:
                table = LocalDBTables.StaffTable.TABLE_NAME;
                id = values.getAsInteger(LocalDBTables.StaffTable.PARTNER_ID);
                break;
            case 2:
                table = LocalDBTables.CollaborateeTable.TABLE_NAME;
                id = values.getAsInteger(LocalDBTables.CollaborateeTable.COLLABORATER_ID);
                break;
            case 3:
                table = LocalDBTables.TimelineTable.TABLE_NAME;
                id = values.getAsInteger(LocalDBTables.TimelineTable.CONTACT_HISTORY_ID);
                break;
            case 4:
                table = LocalDBTables.EngagementTable.TABLE_NAME;
                id = values.getAsInteger(LocalDBTables.EngagementTable.ENGAGEMENT_ID);
                break;
            case 5:
                table = LocalDBTables.EngagementStepTable.TABLE_NAME;
                id = values.getAsInteger(LocalDBTables.EngagementStepTable.STEP_ID);
                break;
            case 6:
                table = LocalDBTables.EngagementTrackTable.TABLE_NAME;
                id = values.getAsInteger(LocalDBTables.EngagementTrackTable.TRACK_ID);
                break;
            case 7:
                table = LocalDBTables.NotificationsTable.TABLE_NAME;
                id = values.getAsInteger(LocalDBTables.NotificationsTable.NOTIFICATION_ID);
                break;
            case 8:
                table = LocalDBTables.ConnectionTable.TABLE_NAME;
                id = values.getAsInteger(LocalDBTables.ConnectionTable.CONNECTION_ID);
                break;
            case 9:
                table = LocalDBTables.FormTable.TABLE_NAME;
                id = values.getAsInteger(LocalDBTables.FormTable.FORM_ID);
                break;
            default:
                table = "";
                id = 0;
                break;
        }

        mDatabase.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        return ContentUris.withAppendedId(uri, id);
    }

    //function that deletes stuffs from the database
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table;

        //determine what table we are dealing with
        switch (sUriMatcher.match(uri)) {
            case 1:
                table = LocalDBTables.StaffTable.TABLE_NAME;
                break;
            case 2:
                table = LocalDBTables.CollaborateeTable.TABLE_NAME;
                break;
            case 3:
                table = LocalDBTables.TimelineTable.TABLE_NAME;
                break;
            case 4:
                table = LocalDBTables.EngagementTable.TABLE_NAME;
                break;
            case 5:
                table = LocalDBTables.EngagementStepTable.TABLE_NAME;
                break;
            case 6:
                table = LocalDBTables.EngagementTrackTable.TABLE_NAME;
                break;
            case 7:
                table = LocalDBTables.NotificationsTable.TABLE_NAME;
                break;
            case 8:
                table = LocalDBTables.ConnectionTable.TABLE_NAME;
                break;
            case 9:
                table = LocalDBTables.FormTable.TABLE_NAME;
                break;
            default:
                table = "";
                break;
        }

        return mDatabase.delete(table, selection, selectionArgs);
    }

    //function that updates stuffs in the database
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table;

        //determine what table we are dealing with
        switch (sUriMatcher.match(uri)) {
            case 1:
                table = LocalDBTables.StaffTable.TABLE_NAME;
                break;
            case 2:
                table = LocalDBTables.CollaborateeTable.TABLE_NAME;
                break;
            case 3:
                table = LocalDBTables.TimelineTable.TABLE_NAME;
                break;
            case 4:
                table = LocalDBTables.EngagementTable.TABLE_NAME;
                break;
            case 5:
                table = LocalDBTables.EngagementStepTable.TABLE_NAME;
                break;
            case 6:
                table = LocalDBTables.EngagementTrackTable.TABLE_NAME;
                break;
            case 7:
                table = LocalDBTables.NotificationsTable.TABLE_NAME;
                break;
            case 8:
                table = LocalDBTables.ConnectionTable.TABLE_NAME;
                break;
            case 9:
                table = LocalDBTables.FormTable.TABLE_NAME;
                break;
            default:
                table = "";
                break;
        }

        return mDatabase.update(table, values, selection, selectionArgs);
    }
}