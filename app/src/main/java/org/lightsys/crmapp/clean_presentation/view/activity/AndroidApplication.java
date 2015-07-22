package org.lightsys.crmapp.clean_presentation.view.activity;

import android.app.Application;

import org.lightsys.crmapp.clean_presentation.internal.di.components.ApplicationComponent;
import org.lightsys.crmapp.clean_presentation.internal.di.modules.ApplicationModule;

/**
 * Created by Jake on 7/22/2015.
 */
public class AndroidApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        this.initializeInjector();
    }

    private void initializeInjector() {
        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }
}
