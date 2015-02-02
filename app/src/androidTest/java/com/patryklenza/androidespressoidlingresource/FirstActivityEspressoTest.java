package com.patryklenza.androidespressoidlingresource;

import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class FirstActivityEspressoTest {
    @Rule
    public final ActivityRule<FirstActivity> main = new ActivityRule<>(FirstActivity.class);

    @Test
    public void firstActivityTest() throws InterruptedException {
        SecondActivityCreatedIdlingResource idlingResouce = new SecondActivityCreatedIdlingResource();

        GlobalApplication.activityEventStream().
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(idlingResouce);

        onView(withId(R.id.button1OnFirstActivity)).perform(click());

        registerIdlingResources(idlingResouce);

        onView(withText("Second Activity")).check(ViewAssertions.matches(isDisplayed()));
    }

    private static class SecondActivityCreatedIdlingResource implements IdlingResource, Action1<ActivityEvent> {
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
        public void call(ActivityEvent activityEvent) {
            if (activityEvent.getActivityClass().equals(SecondActivity.class) && activityEvent.getEventKind() == ActivityEventKind.CREATED) {
                secondActivityCreated = true;
                resourceCallback.onTransitionToIdle();
            }
        }
    }
}
