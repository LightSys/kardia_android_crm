package org.lightsys.crmapp.CleanRepository.LoggedInAccount;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import org.lightsys.crmapp.CleanModels.LoggedInUser;
import org.lightsys.crmapp.CleanModels.UserIdentifier;

import java.util.ArrayList;
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
    public Boolean authenticate(String username, String password, String serverAddress) {
        /**
         * I'm instantiating RESTApiDataSource here right now, however it should probably be implemented
         * using the Singleton pattern. If LoggedInUser is implemented using a singleton as well, then
         * that should eliminate the need for a LoggedInUser to be passed in via the constructor. It would
         * also likely eliminate or change the need for authenticate to return a LoggedInuser.
         */



        LIA_SQLiteHelper db = new LIA_SQLiteHelper(context);

        if ((loggedInUser = db.getLoggedInUser()) != null) {
            db.close();
            return VALID;
        } else {
            if (restApiDataSource.authenticate(username, password, serverAddress)) {
                db.addLoggedInAccount(username, password, serverAddress, restApiDataSource.getPartnerId(username));
                db.close();
                return VALID;
            } else {
                db.close();
                return INVALID;
            }
        }
    }

    /**
     * This is a little messy right now. It calls getLoggedInUser from the database then getPartnerId on that.
     * Basically the logic is fairly redundant since ParnerId is retrieved at the time the account is added to
     * the database.
     *
     * However, this function really isn't useful outside of initially querying the API to get the Partner Id
     * for the account that is logging in.
     *
     * @param username
     * @return
     */
    @Override
    public String getPartnerId(String username) {
        LIA_SQLiteHelper db = new LIA_SQLiteHelper(context);
        String partnerId;
        if ((partnerId = db.getLoggedInUser().getPartnerId()) != null) {
            db.close();
            return partnerId;
        } else {
            // We should store this result in the database, however if you get to this point then you can
            // be fairly certain the account doesn't exist in the database yet.
            return restApiDataSource.getPartnerId(username);
        }
    }

    @Override
    public ArrayList<UserIdentifier> getCollaboratees(String partnerId) {
        LIA_SQLiteHelper db = new LIA_SQLiteHelper(context);
        ArrayList<UserIdentifier> collaboratees;

        if ((collaboratees = db.getCollaboratees()) != null) {
            db.close();
            return collaboratees;
        } else {
            collaboratees = restApiDataSource.getCollaboratees(partnerId);
            db.addCollaboratees(collaboratees);
            db.close();
            return  collaboratees;
        }
    }
}
