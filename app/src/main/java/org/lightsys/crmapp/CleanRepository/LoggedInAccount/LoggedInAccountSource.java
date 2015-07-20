package org.lightsys.crmapp.CleanRepository.LoggedInAccount;

import org.lightsys.crmapp.CleanModels.LoggedInUser;
import org.lightsys.crmapp.CleanModels.UserIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 7/16/2015.
 */
public interface LoggedInAccountSource {

    Boolean VALID = true;
    Boolean INVALID = false;

    Boolean authenticate(String username, String password, String serverAddress);

    String getPartnerId(String username);

    //Not sure if this should require partnerId, or if we should just find it in the LoggedInUser.
    ArrayList<UserIdentifier> getCollaboratees(String partnerId);
}
