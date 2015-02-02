package com.patryklenza.androidespressoidlingresource;

import java.util.List;

import retrofit.http.GET;
import rx.Observable;

public interface BackendContract {
    @GET("/users/pwittchen/repos")
    Observable<List<Repository>> getRepositoriesForUser();
}
