package org.lightsys.crmapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by nathan on 3/9/16.
 */
public class UserLab {
    private static UserLab sUserLab;

    private SQLiteDatabase mDatabase;

    public static UserLab get(Context context) {
        if(sUserLab == null) {
            sUserLab = new UserLab(context);
        }

        return sUserLab;
    }

    private UserLab(Context context) {
        mDatabase = new CRMBaseHelper(context.getApplicationContext()).getWritableDatabase();
    }

    public void addUser(User user) {
        ContentValues values = getContentValues(user);

        mDatabase.insert(CRMDbSchema.UserTable.NAME, null, values);
    }

    public User getUser() {
        User user = new User();

        UserCursorWrapper cursor = queryUsers(null, null);

        try {
            cursor.moveToFirst();
            user = cursor.getUser();
        } catch(CursorIndexOutOfBoundsException ciofbe) {

        } finally {
            cursor.close();
        }

        return user;
    }

    public void updateUser(User user) {
        String username = user.getUsername();
        ContentValues values = getContentValues(user);

        mDatabase.update(CRMDbSchema.UserTable.NAME, values, CRMDbSchema.UserTable.Cols.USERNAME + " = ?", new String[] { username });
    }

    public void deleteUser(User user) {
        String username = user.getUsername();

        mDatabase.delete(CRMDbSchema.UserTable.NAME, CRMDbSchema.UserTable.Cols.USERNAME + " = ?", new String[] { username });
    }

    private static ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(CRMDbSchema.UserTable.Cols.USERNAME, user.getUsername());
        values.put(CRMDbSchema.UserTable.Cols.PASSWORD, user.getPassword());
        values.put(CRMDbSchema.UserTable.Cols.SERVER, user.getServer());
        values.put(CRMDbSchema.UserTable.Cols.PARTNER_ID, user.getStaff().getPartnerId());
        return values;
    }

    private UserCursorWrapper queryUsers(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CRMDbSchema.UserTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new UserCursorWrapper(cursor);
    }
}
