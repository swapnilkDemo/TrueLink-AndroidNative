package com.swapnilk.truelink.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class PopupService extends ForegroundService {
    public PopupService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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
