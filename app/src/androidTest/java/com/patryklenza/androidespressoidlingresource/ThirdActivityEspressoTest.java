package com.patryklenza.androidespressoidlingresource;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

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
    public final ActivityRule<ThirdActivity> third = new ActivityRule<>(ThirdActivity.class);

    @Test
    public void thirdActivityTest() throws InterruptedException {
        GlobalApplication.ApplicationComponent component = Dagger_GlobalApplication_ApplicationComponent.builder()
                .restServicesModule(new MockRestServiceModule())
                .build();
        ((GlobalApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).setComponent(component);

        third.launchActivity();
//        Thread.sleep(2000);
        onView(withId(R.id.list)).check(matches(withText("1234123")));
    }
}
