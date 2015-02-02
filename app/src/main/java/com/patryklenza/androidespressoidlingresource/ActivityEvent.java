package com.patryklenza.androidespressoidlingresource;

import android.app.Activity;

public class ActivityEvent {
    private Class<? extends Activity> activityClass;
    private ActivityEventKind eventKind;

    @Override
    public String toString() {
        return "ActivityEvent{" +
                "activityClass=" + activityClass +
                ", eventKind=" + eventKind +
                '}';
    }

    public Class<? extends Activity> getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class<? extends Activity> activityClass) {
        this.activityClass = activityClass;
    }

    public ActivityEventKind getEventKind() {
        return eventKind;
    }

    public void setEventKind(ActivityEventKind eventKind) {
        this.eventKind = eventKind;
    }
}
