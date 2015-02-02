package com.patryklenza.androidespressoidlingresource;

import android.provider.Settings;
import android.support.test.espresso.IdlingResource;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.functions.Action1;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class ThirdActivityEspressoTest {
    @Rule
    public final ActivityRule<ThirdActivity> third = new ActivityRule<>(ThirdActivity.class);

    @Test
    public void thirdActivityTest() throws InterruptedException {
        ((GlobalApplication)third.instrumentation().getTargetContext().getApplicationContext()).initWithModules(new MockRestServicesModule());
        ((GlobalApplication)third.instrumentation().getTargetContext().getApplicationContext()).inject(third.get());
//         third.get()
        Thread.sleep(2000);
        onView(withId(R.id.list)).check(matches(withText("123456789")));
    }

    private static class DecoratedLongRunningService extends RealLongRunningService implements IdlingResource {

        private ResourceCallback resourceCallback;
        private volatile boolean isRunning;

        @Override
        public void doLongRunningOpAndReturnResult(Action1<String> result) {
            isRunning = true;
            super.doLongRunningOpAndReturnResult(new Action1<String>() {
                @Override
                public void call(String realResult) {
                    result.call(realResult);
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
