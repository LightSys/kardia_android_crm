package org.lightsys.crmapp.CleanRepository;

import org.lightsys.crmapp.CleanModels.Repository.LoggedInAccountRepository;
import org.lightsys.crmapp.CleanModels.UserIdentifier;
import org.lightsys.crmapp.CleanRepository.LoggedInAccount.LoggedInAccountSource;
import org.lightsys.crmapp.CleanRepository.LoggedInAccount.LoggedInAccountSourceFactory;

import java.util.List;

/**
 * Created by Jake on 7/16/2015.
 */
public class LoggedInAccountRepositoryImplementation implements LoggedInAccountRepository {

    public LoggedInAccountRepositoryImplementation(LoggedInAccountSourceFactory loggedInAccountSourceFactory) {
        this.loggedInAccountSourceFactory = loggedInAccountSourceFactory;
    }

    private final LoggedInAccountSourceFactory loggedInAccountSourceFactory;


    @Override
    public void authenticate(String username, String password, String serverAddress) {

    }

    @Override
    public String getPartnerId() {
        return null;
    }

    @Override
    public List<UserIdentifier> getCollaboratees() {
        return null;
    }



}
