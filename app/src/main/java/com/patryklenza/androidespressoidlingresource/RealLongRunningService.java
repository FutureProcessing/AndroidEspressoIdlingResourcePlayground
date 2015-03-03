package com.patryklenza.androidespressoidlingresource;

import rx.functions.Action1;

public class RealLongRunningService {
    public void doLongRunningOpAndReturnResult(Action1<String> action) {
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                action.call("SUCCESS");
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
