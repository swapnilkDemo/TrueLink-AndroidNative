package com.swapnilk.truelink.service;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class PopupService extends HeadlessJsTaskService {

    @Override
    protected @Nullable
    HeadlessJsTaskConfig getTaskConfig(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            return new HeadlessJsTaskConfig(
                    "PopupTask",
                    Arguments.fromBundle(extras),
                    5000,
                    true);
        }
        return null;
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        // create the custom or default notification
//
//        // create an instance of Window class
//        // and display the content on screen
//        Window window = new Window(this);
//        window.open(
//                "https://google.com",
//                "google.com",
//                1,
//                "MALWARE",
//                1,
//                2,
//                4,
//                3,
//                4,
//                0,
//                "1.1.1.1",
//                "Social Media",
//                "HTTPS",
//                "Timbuktoo",
//                "Google"
//        );
//    }
}
