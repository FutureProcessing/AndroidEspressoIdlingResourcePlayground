package com.patryklenza.androidespressoidlingresource;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class RestServicesModule {

    @Provides
    BackendContract provideLogoutService() {
        return new BackendService();
    }
}