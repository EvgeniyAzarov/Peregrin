package com.peregrin.background;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.peregrin.activities.ChatActivity;

public class ChatActivityStatusListener extends Application
        implements Application.ActivityLifecycleCallbacks {

    public boolean ChatActivityStatus = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    public boolean isAppForeground() {
        return ChatActivityStatus;
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
            ChatActivityStatus = true;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof ChatActivity) {
            ChatActivityStatus = false;
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }
}
