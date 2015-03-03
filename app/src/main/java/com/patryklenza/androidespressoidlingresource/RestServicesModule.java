package com.patryklenza.androidespressoidlingresource;

import dagger.Module;
import dagger.Provides;

@Module
public class RestServicesModule {

    @Provides
    BackendContract provideBackendService() {
        return new BackendService();
    }
}