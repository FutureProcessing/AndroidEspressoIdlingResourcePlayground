package com.patryklenza.androidespressoidlingresource;

public class MockRestServiceModule extends RestServicesModule {
    @Override
    BackendContract provideLogoutService() {
        return new MockBackendService();
    }
}
