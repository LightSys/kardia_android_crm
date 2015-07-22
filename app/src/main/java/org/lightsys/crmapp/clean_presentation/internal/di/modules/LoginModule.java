package org.lightsys.crmapp.clean_presentation.internal.di.modules;

import org.lightsys.crmapp.clean_presentation.internal.di.PerActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jake on 7/22/2015.
 */
@Module
public class LoginModule {

    public LoginModule() {}

    /**
     * @Provides @PerActivity @Named("userList") UserCase provideGetUserListUseCase(
     *     GetUserListUseCase getUserListUseCase) {
     *     return getUserListUseCase;
     * }
     */


}
