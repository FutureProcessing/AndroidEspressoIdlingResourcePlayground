package com.patryklenza.androidespressoidlingresource;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
        //BackendService backendService = new BackendService();
        backendContract.getRepositoriesForUser().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Action1<List<Repository>>() {
                    @Override
                    public void call(List<Repository> repositories) {
                        //StringBuilder builder = new StringBuilder();
                        //for (Repository repository : repositories) {
                       //     builder.append(String.valueOf(repository.id)).append(", ");
                        //}
                        textView.setText(String.valueOf(repositories.get(0).id));
                    }
                });
    }

}
