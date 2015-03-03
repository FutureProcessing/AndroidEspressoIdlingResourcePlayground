package com.patryklenza.androidespressoidlingresource;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ThirdActivityEspressoTest {
    public final ActivityRule<ThirdActivity> thirdActivity = new ActivityRule<>(ThirdActivity.class);

    @Test
    public void thirdActivityTest() throws InterruptedException {
        GlobalApplication.ApplicationComponent component = Dagger_GlobalApplication_ApplicationComponent.builder()
                                                                                                        .restServicesModule(new MockRestServiceModule())
                                                                                                        .build();
        ((GlobalApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).setApplicationComponent(
                component);

        thirdActivity.launchActivity();
        onView(withId(R.id.list)).check(matches(withText("abs.io")));
    }
}
