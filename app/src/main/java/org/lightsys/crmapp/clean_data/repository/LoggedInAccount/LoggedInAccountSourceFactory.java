package org.lightsys.crmapp.clean_data.repository.LoggedInAccount;

import android.content.Context;

/**
 * Created by Jake on 7/16/2015.
 * Factory that creates implementations of {@link LoggedInAccountSource}
 * It should decide whether the account or relevant information
 * is already in the DataBase or if it needs to go to the REST API
 * to get the data it needs.
 *
 * The LoggedInAccountSource Implementations should handle persisting the data
 * between each other.
 */
public class LoggedInAccountSourceFactory {

    private final Context context;
    // private final UserCache userCache
    // The example I'm following is using userCache to pass to it's
    // offline persistence layer, DiskUserDataStore. Not sure yet how that
    // works for me.

    public LoggedInAccountSourceFactory(Context context) {

        if (context == null) {
            throw new IllegalArgumentException("Constructor parameters cannot be null");
        }

        this.context = context;
    }

    /**
     * This function returns either a SQLite data source or a REST datasource.
     * @return
     */

    public LoggedInAccountSource create() {
        LoggedInAccountSource loggedInAccountSource;

        // This shouldn't be if true, it should have logic for which datasource to create.
        if (true) {
            loggedInAccountSource = new SQLiteDataSource(context);
        } else {
            loggedInAccountSource = createRESTApiDataSource();
        }

        return loggedInAccountSource;
    }

    /**
     * This function is used to force an online data source.
     * @return
     */

    private LoggedInAccountSource createRESTApiDataSource() {
        return null;
    }
}
