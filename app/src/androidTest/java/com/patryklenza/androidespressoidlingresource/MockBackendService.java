package com.patryklenza.androidespressoidlingresource;

import java.util.Arrays;
import java.util.List;

import rx.Observable;

public class MockBackendService implements BackendContract {
    @Override
    public Observable<List<Repository>> getJakesRepos() {
        Repository repo = new Repository();
        repo.id = 1234123;
        repo.name = "abs.io";
        return Observable.just(Arrays.asList(repo));
    }
}
