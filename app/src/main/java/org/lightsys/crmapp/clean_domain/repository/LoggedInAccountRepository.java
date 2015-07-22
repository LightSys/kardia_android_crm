package org.lightsys.crmapp.clean_domain.repository;

import org.lightsys.crmapp.clean_domain.UserIdentifier;

import java.util.List;

/**
 * Created by Jake on 7/16/2015.
 */
public interface LoggedInAccountRepository {

    void authenticate(String username, String password, String serverAddress);

    String getPartnerId(String username);

    List<UserIdentifier> getCollaboratees(String partnerId);
}
