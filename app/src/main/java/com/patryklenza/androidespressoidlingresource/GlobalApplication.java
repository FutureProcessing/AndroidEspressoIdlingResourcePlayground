package com.patryklenza.androidespressoidlingresource;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.concurrent.ArrayBlockingQueue;

import javax.inject.Singleton;

import dagger.Component;
import rx.Observable;
import rx.Subscriber;

public class GlobalApplication extends Application {
    private static Observable<ActivityEvent> _activityEventStream;
    private ApplicationComponent applicationComponent;

    public static Observable<ActivityEvent> activityEventStream() {
        return _activityEventStream;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityEventProducer activityEventProducer = new ActivityEventProducer();
        _activityEventStream = Observable.create(activityEventProducer);
        registerActivityLifecycleCallbacks(activityEventProducer);

        applicationComponent = Dagger_GlobalApplication_ApplicationComponent.builder()
                                                                            .restServicesModule(new RestServicesModule())
                                                                            .build();
        component().inject(this);
    }

    public ApplicationComponent component() {
        return applicationComponent;
    }

    public void setApplicationComponent(ApplicationComponent component) {
        this.applicationComponent = component;
    }

    @Singleton
    @Component(modules = RestServicesModule.class)
    public interface ApplicationComponent {
        void inject(GlobalApplication application);

        void inject(ThirdActivity thirdActivity);
    }

    private static class ActivityEventProducer implements ActivityLifecycleCallbacks, Observable.OnSubscribe<ActivityEvent> {

        private ArrayBlockingQueue<ActivityEvent> activityEvents = new ArrayBlockingQueue<>(256, false);
        private boolean anyOneSubscribed;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if(!anyOneSubscribed) {
                return;
            }
            ActivityEvent activityEvent = new ActivityEvent();
            activityEvent.setActivityClass(activity.getClass());
            activityEvent.setEventKind(ActivityEventKind.CREATED);
            activityEvents.add(activityEvent);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            if(!anyOneSubscribed) {
                return;
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if(!anyOneSubscribed) {
                return;
            }
            ActivityEvent activityEvent = new ActivityEvent();
            activityEvent.setActivityClass(activity.getClass());
            activityEvent.setEventKind(ActivityEventKind.DESTROYED);
            activityEvents.add(activityEvent);
        }

        @Override
        public void call(Subscriber<? super ActivityEvent> subscriber) {
            anyOneSubscribed = true;
            try {
                while(!subscriber.isUnsubscribed()) {
                    ActivityEvent activityEvent = activityEvents.take();
                    subscriber.onNext(activityEvent);
                }
            } catch(Exception e) {
                subscriber.onError(e);
            } finally {
                anyOneSubscribed = false;
                activityEvents.clear();
            }
        }
    }
}
