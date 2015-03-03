package com.patryklenza.androidespressoidlingresource;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ThirdActivity extends Activity {

    TextView textView;

    @Inject
    BackendContract backendContract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        textView = (TextView) findViewById(R.id.list);
        ((GlobalApplication) getApplication()).component().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        performBackendCall();
    }

    public void performBackendCall() {
        backendContract.getJakesRepos().
                subscribeOn(Schedulers.io()).
                               observeOn(AndroidSchedulers.mainThread()).
                               subscribe(repositories -> textView.setText(repositories.get(0).name));
    }
}
