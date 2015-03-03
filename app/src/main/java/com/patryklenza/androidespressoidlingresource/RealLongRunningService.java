package com.patryklenza.androidespressoidlingresource;

import rx.functions.Action1;

public class RealLongRunningService {
    public void doLongRunningOpAndReturnResult(Action1<String> result) {
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                result.call("SUCCESS");
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
