package org.lightsys.crmapp.CleanRepository.LoggedInAccount;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import org.lightsys.crmapp.CleanModels.LoggedInUser;
import org.lightsys.crmapp.CleanModels.UserIdentifier;

import java.util.List;

/**
 * Created by Jake on 7/16/2015.
 *
 * Note that the class currently relies on running authenticate first.
 */
public class SQLiteDataSource implements LoggedInAccountSource {

    Context context;
    LoggedInUser loggedInUser;
    RESTApiDataSource restApiDataSource;

    public SQLiteDataSource(Context context) {
        this.context = context;
    }

    @Override
    public LoggedInUser authenticate(String username, String password, String serverAddress) {
        /**
         * I'm instantiating RESTApiDataSource here right now, however it should probably be implemented
         * using the Singleton pattern. If LoggedInUser is implemented using a singleton as well, then
         * that should eliminate the need for a LoggedInUser to be passed in via the constructor. It would
         * also likely eliminate or change the need for authenticate to return a LoggedInuser.
         */



        LIA_SQLiteHelper db = new LIA_SQLiteHelper(context);

        if ((loggedInUser = db.getLoggedInUser()) != null) {

        } else {
            restApiDataSource.authenticate(username, password, serverAddress);
        }
        return loggedInUser;
    }

    @Override
    public String getPartnerId() {
        return loggedInUser.getPartnerId();
    }

    @Override
    public List<UserIdentifier> getCollaboratees() {
        return loggedInUser.getCollaborateeList();
    }
}
