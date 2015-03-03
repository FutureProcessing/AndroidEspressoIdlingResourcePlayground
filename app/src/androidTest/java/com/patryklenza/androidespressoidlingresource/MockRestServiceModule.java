package com.patryklenza.androidespressoidlingresource;

public class MockRestServiceModule extends RestServicesModule {
    @Override
    BackendContract provideBackendService() {
        return new MockBackendService();
    }
}
