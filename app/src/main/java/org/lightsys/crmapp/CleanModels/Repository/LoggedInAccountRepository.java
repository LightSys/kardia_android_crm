package org.lightsys.crmapp.CleanModels.Repository;

import org.lightsys.crmapp.CleanModels.UserIdentifier;

import java.util.List;

/**
 * Created by Jake on 7/16/2015.
 */
public interface LoggedInAccountRepository {

    void authenticate(String username, String password, String serverAddress);

    String getPartnerId();

    List<UserIdentifier> getCollaboratees();
}
