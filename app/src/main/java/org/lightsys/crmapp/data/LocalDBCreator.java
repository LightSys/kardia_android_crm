package org.lightsys.crmapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nathan on 3/9/16.
 *
 * creates the localDatabase
 */
public class LocalDBCreator extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crmBase.db";

    public LocalDBCreator(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LocalDBTables.StaffTable.CREATE_TABLE);
        db.execSQL(LocalDBTables.CollaborateeTable.CREATE_TABLE);
        db.execSQL(LocalDBTables.TimelineTable.CREATE_TABLE);
        db.execSQL(LocalDBTables.EngagementTable.CREATE_TABLE);
        db.execSQL(LocalDBTables.EngagementStepTable.CREATE_TABLE);
        db.execSQL(LocalDBTables.EngagementTrackTable.CREATE_TABLE);
        db.execSQL(LocalDBTables.NotificationsTable.CREATE_TABLE);
        db.execSQL(LocalDBTables.ConnectionTable.CREATE_TABLE);
        db.execSQL(LocalDBTables.FormTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
