package org.videolan.mudiAudioVideo;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class handlerClass {
    public void Adhandler() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.v("handlerRun","Handler running");
                Adhandler();
            }
        }, 2000);
    }
}
