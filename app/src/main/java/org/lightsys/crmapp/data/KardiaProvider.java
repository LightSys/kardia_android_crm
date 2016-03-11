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
import android.util.Log;

/**
 * Created by nathan on 3/10/16.
 */
public class KardiaProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String providerAuthority = "org.lightsys.crmapp.provider";

    public static final String accountType = "org.lightsys.crmapp";

    //private static final String mimeType = "text/plain";

    private CRMOpenHelper mOpenHelper;

    private SQLiteDatabase db;

    static {
        sUriMatcher.addURI(providerAuthority, "staff", 1);
        sUriMatcher.addURI(providerAuthority, "collaboratees", 2);
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new CRMOpenHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d("ContentProvider", "yes");

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            /*case 1:
                builder.setTables(CRMContract.PartnerTable.NAME);
                break;
            case 2:
                builder.setTables(CRMContract.PartnerTable.NAME);
                builder.appendWhere(CRMContract.PartnerTable.Cols.PARNTER_ID + " = " + uri.getLastPathSegment());
                break;
            case 3:
                builder.setTables(CRMContract.CollaborateeTable.NAME);
                break;*/
            case 1:
                builder.setTables(CRMContract.StaffTable.NAME);
                break;
            case 2:
                builder.setTables(CRMContract.CollaborateeTable.NAME);
                break;
            default:
                break;
        }

        db = mOpenHelper.getWritableDatabase();

        Log.d("ContentProvider", builder.getTables());

        Log.d("Query", builder.buildQuery(projection, selection, selectionArgs, null, null, sortOrder, null));

        return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
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
            /*case 1:
                table = CRMContract.PartnerTable.NAME;
                id = values.getAsInteger(CRMContract.PartnerTable.Cols.PARNTER_ID);
                break;
            case 3:
                table = CRMContract.CollaborateeTable.NAME;
                id = values.getAsInteger(CRMContract.CollaborateeTable.Cols.COLLABORATER_ID);
                break;*/
            case 1:
                table = CRMContract.StaffTable.NAME;
                id = values.getAsInteger(CRMContract.StaffTable.PARTNER_ID);
                break;
            case 2:
                table = CRMContract.CollaborateeTable.NAME;
                id = values.getAsInteger(CRMContract.CollaborateeTable.COLLABORATER_ID);
                break;
            default:
                table = "";
                id = 0;
                break;
        }

        db = mOpenHelper.getWritableDatabase();

        db.insert(table, null, values);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table;
        int id;

        switch (sUriMatcher.match(uri)) {
            /*case 1:
                table = CRMContract.PartnerTable.NAME;
                id = values.getAsInteger(CRMContract.PartnerTable.Cols.PARNTER_ID);
                break;
            case 3:
                table = CRMContract.CollaborateeTable.NAME;
                id = values.getAsInteger(CRMContract.CollaborateeTable.Cols.COLLABORATER_ID);
                break;*/
            case 1:
                table = CRMContract.StaffTable.NAME;
                id = values.getAsInteger(CRMContract.StaffTable.PARTNER_ID);
                break;
            case 2:
                table = CRMContract.CollaborateeTable.NAME;
                id = values.getAsInteger(CRMContract.CollaborateeTable.COLLABORATER_ID);
                break;
            default:
                table = "";
                id = 0;
                break;
        }

        db = mOpenHelper.getWritableDatabase();



        return db.update(table, values, selection, selectionArgs);
    }
}