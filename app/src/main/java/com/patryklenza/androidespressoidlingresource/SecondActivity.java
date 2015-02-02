package com.patryklenza.androidespressoidlingresource;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


public class SecondActivity extends Activity {

    private RealLongRunningService service = new RealLongRunningService();
    private Button button1OnSecondActivity;
    private TextView textViewOpResult;

    public void setService(RealLongRunningService service) {
        this.service = service;
    }

    public RealLongRunningService getService() {
        return service;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        button1OnSecondActivity = (Button) findViewById(R.id.button1OnSecondActivity);
        textViewOpResult = (TextView) findViewById(R.id.textViewOpResult);

        button1OnSecondActivity.setOnClickListener(v -> {
                    button1OnSecondActivity.setEnabled(false);
                    textViewOpResult.setText("Running...");
                    service.doLongRunningOpAndReturnResult(result -> textViewOpResult.post(() -> textViewOpResult.setText(result)));
                }
        );
    }
}
