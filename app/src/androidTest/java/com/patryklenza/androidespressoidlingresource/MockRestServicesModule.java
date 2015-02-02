package com.patryklenza.androidespressoidlingresource;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        overrides = true,
        injects = ThirdActivity.class
)
public class MockRestServicesModule {

    @Provides
    public BackendContract provideLogoutService() {
        return new MockBackendService();
    }
}