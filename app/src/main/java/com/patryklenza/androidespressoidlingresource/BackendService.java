package com.patryklenza.androidespressoidlingresource;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.Executors;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;

public class BackendService implements BackendContract {

    static {
        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("https://api.github.com")
                                                                 .setExecutors(Executors.newCachedThreadPool(), null)
                                                                 .setConverter(new GsonConverter(new Gson()))
                                                                 .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel
                                                                         .NONE)
                                                                 .build();
        service = restAdapter.create(BackendContract.class);
    }

    private static BackendContract service;

    @Override
    public Observable<List<Repository>> getJakesRepos() {
        return service.getJakesRepos();
    }
}