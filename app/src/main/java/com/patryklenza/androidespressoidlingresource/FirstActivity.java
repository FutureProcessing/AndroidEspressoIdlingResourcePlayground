package com.patryklenza.androidespressoidlingresource;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class FirstActivity extends Activity {

    private Button button1OnFirstActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        button1OnFirstActivity = (Button) findViewById(R.id.button1OnFirstActivity);

        button1OnFirstActivity.setOnClickListener(v -> {
            button1OnFirstActivity.setEnabled(false);
            Observable.empty()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .delay(10, TimeUnit.SECONDS)
                    .subscribe(launchSecondActivitySubscriber());
        });
    }

    private Subscriber<Object> launchSecondActivitySubscriber() {
        return new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                Intent secondActivity = new Intent(FirstActivity.this, SecondActivity.class);
                startActivity(secondActivity);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object o) {
            }
        };
    }
}
