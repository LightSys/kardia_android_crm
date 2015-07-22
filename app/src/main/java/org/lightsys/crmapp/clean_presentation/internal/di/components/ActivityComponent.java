package org.lightsys.crmapp.clean_presentation.internal.di.components;

import android.app.Activity;

import org.lightsys.crmapp.clean_presentation.internal.di.PerActivity;
import org.lightsys.crmapp.clean_presentation.internal.di.modules.ActivityModule;

import dagger.Component;

/**
 * Created by Jake on 7/22/2015.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    Activity activity();
}
