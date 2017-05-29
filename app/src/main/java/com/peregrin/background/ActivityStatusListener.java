package com.peregrin.background;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.peregrin.activities.ChatActivity;

public class ActivityStatusListener extends Application
        implements Application.ActivityLifecycleCallbacks {

    private boolean chatActivityStatus = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    public boolean isChatActivityForeground() {
        return chatActivityStatus;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof ChatActivity) {
            chatActivityStatus = true;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof ChatActivity) {
            chatActivityStatus = false;
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }
}
