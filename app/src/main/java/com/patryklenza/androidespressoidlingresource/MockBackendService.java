package com.patryklenza.androidespressoidlingresource;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class MockBackendService implements BackendContract {
    @Override
    public Observable<List<Repository>> getRepositoriesForUser() {
        List<Repository> mockedData = new ArrayList<>();
        Repository mockRepository = new Repository();
        mockRepository.id = 123456789;
        mockedData.add(mockRepository);
        return Observable.just(mockedData);
    }
}
