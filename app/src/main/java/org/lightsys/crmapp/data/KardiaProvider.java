package org.lightsys.crmapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by nathan on 3/10/16.
 */
public class KardiaProvider extends ContentProvider {
    private static final int USERS = 1;
    private static final int USERS_ID = 2;
    private static final int USERS_USERNAME = 3;
    private static final int USERS_PASSWORD = 4;
    private static final int USERS_SERVER = 5;
    private static final int USERS_PARTNERID = 6;

    private static final int PARTNERS = 7;
    private static final int PARTNERS_PARTNERID = 8;
    private static final int PARTNERS_PARTNERNAME = 9;
    private static final int PARTNERS_ID = 11;

    private static final int PARTNERS_COLLABORATEES = 13;
    private static final int PARTNERS_COLLABORATEES_ID = 10;
    private static final int PARTNERS_COLLABORATEES_COLLABORATEEID = 12;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "users", USERS);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "users/#", USERS_ID);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "users/#/username", USERS_USERNAME);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "users/#/password", USERS_PASSWORD);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "users/#/server", USERS_SERVER);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "users/#/partnerId", USERS_PARTNERID);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "partners", PARTNERS);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "partners/#", PARTNERS_ID);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "partners/#/partnerId", PARTNERS_PARTNERID);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "partners/#/partnerName", PARTNERS_PARTNERNAME);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "partners/#/collaboratees", PARTNERS_COLLABORATEES);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "partners/#/collaboratees/#", PARTNERS_COLLABORATEES_ID);
        sUriMatcher.addURI("org.lightsys.crmapp.provider", "partners/#/collaboratees/#/collbaorateeId", PARTNERS_COLLABORATEES_COLLABORATEEID);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}