package com.patryklenza.androidespressoidlingresource;

import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.functions.Action1;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SecondActivityEspressoTest {
    @Rule
    public final ActivityRule<SecondActivity> secondActivity = new ActivityRule<>(SecondActivity.class);

    @Test
    public void secondActivityTest() throws InterruptedException {
        DecoratedLongRunningService decoratedLongRunningService = new DecoratedLongRunningService();
        registerIdlingResources(decoratedLongRunningService);
        secondActivity.get().setService(decoratedLongRunningService);

        onView(withId(R.id.button1OnSecondActivity)).perform(click());


        onView(withText("SUCCESS")).check(ViewAssertions.matches(isDisplayed()));
    }

    private static class DecoratedLongRunningService extends RealLongRunningService implements IdlingResource {

        private ResourceCallback resourceCallback;
        private volatile boolean isRunning;

        @Override
        public void doLongRunningOpAndReturnResult(Action1<String> action) {
            isRunning = true;
            super.doLongRunningOpAndReturnResult(new Action1<String>() {
                @Override
                public void call(String realResult) {
                    action.call(realResult);
                    isRunning = false;
                    resourceCallback.onTransitionToIdle();
                }
            });
        }

        @Override
        public String getName() {
            return "Long Running Service";
        }

        @Override
        public boolean isIdleNow() {
            return !isRunning;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
            this.resourceCallback = resourceCallback;
        }
    }
}
