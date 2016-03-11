package org.lightsys.crmapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nathan on 3/9/16.
 */
public class CRMOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crmBase.db";

    public CRMOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CRMContract.StaffTable.NAME + "(" +
                        CRMContract.StaffTable.PARTNER_ID + ", " +
                        CRMContract.StaffTable.KARDIA_LOGIN +
                        //"FOREIGN KEY(" + CRMContract.StaffTable.PARTNER_ID + ") REFERENCES " + CRMContract.PartnerTable.NAME + "(" + CRMContract.CollaborateeTable.PARTNER_ID + ")," +
                        ")"
        );

        db.execSQL("create table " + CRMContract.PartnerTable.NAME + "(" +
                        CRMContract.PartnerTable.PARNTER_ID + " PRIMARY KEY, " +
                        CRMContract.PartnerTable.PARTNER_NAME +
                        ")"
        );

        db.execSQL("create table " + CRMContract.CollaborateeTable.NAME + "(" +
                        CRMContract.CollaborateeTable.COLLABORATER_ID + ", " +
                        CRMContract.CollaborateeTable.PARTNER_ID + ", " +
                        CRMContract.CollaborateeTable.PARTNER_NAME + ", " +
                        CRMContract.CollaborateeTable.SURNAME + ", " +
                        CRMContract.CollaborateeTable.GIVEN_NAMES + ", " +
                        CRMContract.CollaborateeTable.PHONE + ", " +
                        CRMContract.CollaborateeTable.CELL + ", " +
                        CRMContract.CollaborateeTable.EMAIL + ", " +
                        CRMContract.CollaborateeTable.ADDRESS_1 + ", " +
                        CRMContract.CollaborateeTable.CITY + ", " +
                        CRMContract.CollaborateeTable.STATE_PROVINCE + ", " +
                        CRMContract.CollaborateeTable.POSTAL_CODE +
                        //"FOREIGN KEY(" + CRMContract.CollaborateeTable.COLLABORATER_ID + ") REFERENCES " + CRMContract.PartnerTable.NAME + "(" + CRMContract.CollaborateeTable.PARTNER_ID + ")," +
                        //"FOREIGN KEY(" + CRMContract.CollaborateeTable.PARTNER_ID + ") REFERENCES " + CRMContract.PartnerTable.NAME + "(" + CRMContract.CollaborateeTable.PARTNER_ID + ")," +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
