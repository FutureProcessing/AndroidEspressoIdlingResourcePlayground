package com.patryklenza.androidespressoidlingresource;

import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FirstActivityEspressoTest {
    @Rule
    public final ActivityRule<FirstActivity> firstActivity = new ActivityRule<>(FirstActivity.class);
    private SecondActivityCreatedIdlingResource secondActivityCreatedIdlingResource;

    @After
    public void tearDown() {
        unregisterIdlingResources(secondActivityCreatedIdlingResource);
        secondActivityCreatedIdlingResource.unsubscribe();
    }

    @Test
    public void firstActivityTest() throws InterruptedException {
        secondActivityCreatedIdlingResource = new SecondActivityCreatedIdlingResource();

        GlobalApplication.activityEventStream().
                subscribeOn(Schedulers.newThread())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(secondActivityCreatedIdlingResource);

        // Click on button that simulates long network call and after success goes to SecondActivity
        onView(withId(R.id.button1OnFirstActivity)).perform(click());

        // Wait until SecondActivity is created
        registerIdlingResources(secondActivityCreatedIdlingResource);

        // Validate label on SecondActivity
        onView(withText("Second Activity")).check(ViewAssertions.matches(isDisplayed()));
    }

    private static class SecondActivityCreatedIdlingResource extends Subscriber<ActivityEvent> implements IdlingResource {
        private volatile ResourceCallback resourceCallback;
        private volatile boolean secondActivityCreated;

        @Override
        public String getName() {
            return "SecondActivity Created";
        }

        @Override
        public boolean isIdleNow() {
            return secondActivityCreated;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
            this.resourceCallback = resourceCallback;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(ActivityEvent activityEvent) {
            if(secondActivityCreated(activityEvent)) {
                secondActivityCreated = true;
                resourceCallback.onTransitionToIdle();
            }
        }

        private boolean secondActivityCreated(ActivityEvent activityEvent) {
            return activityEvent.getActivityClass().equals(SecondActivity.class) && activityEvent.getEventKind() == ActivityEventKind.CREATED;
        }
    }
}
