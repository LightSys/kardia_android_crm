package org.lightsys.crmapp.clean_presentation.internal.di.modules;

import android.app.Activity;

import org.lightsys.crmapp.clean_presentation.internal.di.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jake on 7/22/2015.
 */
@Module
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides @PerActivity Activity activity() {
        return this.activity;
    }
}
