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
       final LoggedInAccountSource source = this.loggedInAccountSourceFactory.create();
        source.authenticate(username, password, serverAddress);
    }

    @Override
    public String getPartnerId(String username) {
        final LoggedInAccountSource source = this.loggedInAccountSourceFactory.create();
        return source.getPartnerId(username);
    }

    @Override
    public List<UserIdentifier> getCollaboratees(String partnerId) {
        final LoggedInAccountSource source = this.loggedInAccountSourceFactory.create();
        return source.getCollaboratees(partnerId);
    }



}
