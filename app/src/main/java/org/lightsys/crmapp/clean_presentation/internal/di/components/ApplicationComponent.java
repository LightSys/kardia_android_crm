package org.lightsys.crmapp.clean_presentation.internal.di.components;

import android.content.Context;

import org.lightsys.crmapp.clean_domain.repository.LoggedInAccountRepository;
import org.lightsys.crmapp.clean_presentation.internal.di.modules.ApplicationModule;
import org.lightsys.crmapp.clean_presentation.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Jake on 7/22/2015.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(BaseActivity baseActivity);

    //Exposed to sub-graphs
    Context context();

    LoggedInAccountRepository loggedInAccountRepository();
}
