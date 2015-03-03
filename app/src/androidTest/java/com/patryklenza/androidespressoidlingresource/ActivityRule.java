package com.patryklenza.androidespressoidlingresource;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

// Jake Wharton's ActivityRule -> https://gist.github.com/JakeWharton/1c2f2cadab2ddd97f9fb
public class ActivityRule<T extends Activity> implements TestRule {
    private final Class<T> activityClass;

    private T activity;
    private Instrumentation instrumentation;

    public ActivityRule(Class<T> activityClass) {
        this.activityClass = activityClass;
    }

    protected Intent getLaunchIntent(String targetPackage, Class<T> activityClass) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(targetPackage, activityClass.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * Get the running instance of the specified activity. This will launch it if it is not already
     * running.
     */
    public final T get() {
        launchActivity();
        return activity;
    }

    /**
     * Get the {@link Instrumentation} instance for this test.
     */
    public final Instrumentation instrumentation() {
        launchActivity();
        return instrumentation;
    }

    @Override
    public final Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                launchActivity();

                base.evaluate();

                if(!activity.isFinishing()) {
                    activity.finish();
                }
                activity = null; // Eager reference kill in case someone leaked our reference.
            }
        };
    }

    @SuppressWarnings("unchecked") // Guarded by generics at the constructor.
    public void launchActivity() {
        if(activity != null) {
            return;
        }

        Instrumentation instrumentation = fetchInstrumentation();

        String targetPackage = instrumentation.getTargetContext().getPackageName();
        Intent intent = getLaunchIntent(targetPackage, activityClass);

        activity = (T) instrumentation.startActivitySync(intent);
        instrumentation.waitForIdleSync();
    }

    private Instrumentation fetchInstrumentation() {
        Instrumentation result = instrumentation;
        return result != null ? result
                : (instrumentation = InstrumentationRegistry.getInstrumentation());
    }
}