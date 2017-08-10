package org.lightsys.crmapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nathan on 3/9/16.
 *
 * This thing creates the database
 */
public class CRMOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crmBase.db";

    public CRMOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CRMContract.StaffTable.CREATE_TABLE);
        db.execSQL(CRMContract.CollaborateeTable.CREATE_TABLE);
        db.execSQL(CRMContract.TimelineTable.CREATE_TABLE);
        db.execSQL(CRMContract.EngagementTable.CREATE_TABLE);
        db.execSQL(CRMContract.EngagementStepTable.CREATE_TABLE);
        db.execSQL(CRMContract.EngagementTrackTable.CREATE_TABLE);
        db.execSQL(CRMContract.NotificationsTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
