package org.lightsys.crmapp.clean_presentation.internal.di.modules;

import android.content.Context;

import org.lightsys.crmapp.clean_data.repository.LIA_RepositoryImpl;
import org.lightsys.crmapp.clean_domain.repository.LoggedInAccountRepository;
import org.lightsys.crmapp.clean_presentation.AndroidApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jake on 7/22/2015.
 */
@Module
public class ApplicationModule {
    private final AndroidApplication application;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }

    @Provides @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides @Singleton LoggedInAccountRepository provideLoggedInAccountRepository(LIA_RepositoryImpl liaRepositoryImpl) {
        return liaRepositoryImpl;
    }
}
