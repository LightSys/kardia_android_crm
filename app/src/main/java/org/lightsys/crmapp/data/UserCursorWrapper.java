package org.lightsys.crmapp.data;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by nathan on 3/9/16.
 */
public class UserCursorWrapper extends CursorWrapper {
    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        String username = getString(getColumnIndex(CRMDbSchema.UserTable.Cols.USERNAME));
        String password = getString(getColumnIndex(CRMDbSchema.UserTable.Cols.PASSWORD));
        String server = getString(getColumnIndex(CRMDbSchema.UserTable.Cols.SERVER));
        String partnerId = getString(getColumnIndex(CRMDbSchema.UserTable.Cols.PARTNER_ID));

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setServer(server);
        user.setStaff(new Staff(partnerId, username));

        return user;
    }
}
