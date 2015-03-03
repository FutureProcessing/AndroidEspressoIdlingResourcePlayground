package com.patryklenza.androidespressoidlingresource;

import java.util.List;

import retrofit.http.GET;
import rx.Observable;

public interface BackendContract {
    @GET("/users/jakewharton/repos")
    Observable<List<Repository>> getJakesRepos();
}
