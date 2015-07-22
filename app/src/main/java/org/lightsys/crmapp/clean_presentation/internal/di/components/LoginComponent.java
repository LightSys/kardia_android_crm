package org.lightsys.crmapp.clean_presentation.internal.di.components;

import org.lightsys.crmapp.LoginActivity;
import org.lightsys.crmapp.clean_presentation.internal.di.PerActivity;
import org.lightsys.crmapp.clean_presentation.internal.di.modules.ActivityModule;
import org.lightsys.crmapp.clean_presentation.internal.di.modules.LoginModule;

import dagger.Component;

/**
 * Created by Jake on 7/22/2015.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, LoginModule.class})
public interface LoginComponent {

    // This will likely need to be changed. If this does what I think, it needs to inject each activity that
    // needs access to it, not just the loginActivity
    void inject(LoginActivity loginActivity);
}
