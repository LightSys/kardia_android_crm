package org.lightsys.crmapp.clean_data.repository;

import org.lightsys.crmapp.clean_domain.repository.LoggedInAccountRepository;
import org.lightsys.crmapp.clean_domain.UserIdentifier;
import org.lightsys.crmapp.clean_data.repository.LoggedInAccount.LoggedInAccountSource;
import org.lightsys.crmapp.clean_data.repository.LoggedInAccount.LIA_SourceFactory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Jake on 7/16/2015.
 */
@Singleton
public class LIA_RepositoryImpl implements LoggedInAccountRepository {

    @Inject
    public LIA_RepositoryImpl(LIA_SourceFactory liaSourceFactory) {
        this.liaSourceFactory = liaSourceFactory;
    }

    private final LIA_SourceFactory liaSourceFactory;


    @Override
    public void authenticate(String username, String password, String serverAddress) {
       final LoggedInAccountSource source = this.liaSourceFactory.create();
        source.authenticate(username, password, serverAddress);
    }

    @Override
    public String getPartnerId(String username) {
        final LoggedInAccountSource source = this.liaSourceFactory.create();
        return source.getPartnerId(username);
    }

    @Override
    public List<UserIdentifier> getCollaboratees(String partnerId) {
        final LoggedInAccountSource source = this.liaSourceFactory.create();
        return source.getCollaboratees(partnerId);
    }



}
