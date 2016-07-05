package org.lightsys.crmapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by nathan on 3/10/16.
 */
public class KardiaProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private CRMOpenHelper mOpenHelper;

    private SQLiteDatabase mDatabase;

    static {
        sUriMatcher.addURI(CRMContract.PROVIDER_AUTHORITY, "staff", 1);
        sUriMatcher.addURI(CRMContract.PROVIDER_AUTHORITY, "collaboratees", 2);
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new CRMOpenHelper(getContext());
        mDatabase = mOpenHelper.getWritableDatabase();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case 1:
                builder.setTables(CRMContract.StaffTable.TABLE_NAME);
                break;
            case 2:
                builder.setTables(CRMContract.CollaborateeTable.TABLE_NAME);
                break;
            default:
                break;
        }

        return builder.query(mDatabase, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;
        int id;

        switch (sUriMatcher.match(uri)) {
            case 1:
                table = CRMContract.StaffTable.TABLE_NAME;
                id = values.getAsInteger(CRMContract.StaffTable.PARTNER_ID);
                break;
            case 2:
                table = CRMContract.CollaborateeTable.TABLE_NAME;
                id = values.getAsInteger(CRMContract.CollaborateeTable.COLLABORATER_ID);
                break;
            default:
                table = "";
                id = 0;
                break;
        }

        mDatabase.insert(table, null, values);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table;

        switch (sUriMatcher.match(uri)) {
            case 1:
                table = CRMContract.StaffTable.TABLE_NAME;
                break;
            case 2:
                table = CRMContract.CollaborateeTable.TABLE_NAME;
                break;
            default:
                table = "";
                break;
        }

        return mDatabase.update(table, values, selection, selectionArgs);
    }
}