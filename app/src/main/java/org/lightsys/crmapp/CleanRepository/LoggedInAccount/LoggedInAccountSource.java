package org.lightsys.crmapp.CleanRepository.LoggedInAccount;

import org.lightsys.crmapp.CleanModels.LoggedInUser;
import org.lightsys.crmapp.CleanModels.UserIdentifier;

import java.util.List;

/**
 * Created by Jake on 7/16/2015.
 */
public interface LoggedInAccountSource {

    LoggedInUser authenticate(String username, String password, String serverAddress);

    String getPartnerId(String username);

    List<UserIdentifier> getCollaboratees(String partnerId);
}
