package com.patryklenza.androidespressoidlingresource;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import dagger.Module;
import dagger.ObjectGraph;
import rx.Observable;
import rx.Subscriber;

public class GlobalApplication extends Application {
    private static Observable<ActivityEvent> _activityEventStream;
    private static ObjectGraph objectGraph;

    public static Observable<ActivityEvent> activityEventStream() {
        return _activityEventStream;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityEventProducer activityEventProducer = new ActivityEventProducer();
        _activityEventStream = Observable.create(activityEventProducer);
        registerActivityLifecycleCallbacks(activityEventProducer);

        List<Object> modules = getProductionModules();
        objectGraph = ObjectGraph.create(modules.toArray());
    }

    private static List<Object> getProductionModules() {
        List<Object> modules = new ArrayList<>();
        modules.add(new RestServicesModule());
        return modules;
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }

    public static void initWithModules(Object... modules){
        List<Object> modulesList =  new ArrayList<>();
//        modulesList.addAll(getProductionModules());
        modulesList.addAll(Arrays.asList(modules));
        objectGraph = ObjectGraph.create(modules);
    }

    private static class ActivityEventProducer implements ActivityLifecycleCallbacks, Observable.OnSubscribe<ActivityEvent> {

        private ArrayBlockingQueue<ActivityEvent> activityEvents = new ArrayBlockingQueue<>(16, false);

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            ActivityEvent activityEvent = new ActivityEvent();
            activityEvent.setActivityClass(activity.getClass());
            activityEvent.setEventKind(ActivityEventKind.CREATED);
            activityEvents.add(activityEvent);
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            ActivityEvent activityEvent = new ActivityEvent();
            activityEvent.setActivityClass(activity.getClass());
            activityEvent.setEventKind(ActivityEventKind.DESTROYED);
            activityEvents.add(activityEvent);
        }

        @Override
        public void call(Subscriber<? super ActivityEvent> subscriber) {
            try {
                while (!subscriber.isUnsubscribed()) {
                    ActivityEvent activityEvent = activityEvents.take();
                    subscriber.onNext(activityEvent);
                }
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }
    }
}
