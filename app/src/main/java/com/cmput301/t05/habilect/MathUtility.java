package com.cmput301.t05.habilect;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import static java.lang.Thread.sleep;

/**
 * Created by Ian on 2017-11-08.
 */

public final class MathUtility {
    public static void Animate(final int steps, final int totalDuration, final Handler handler) {
        HandlerThread handlerThread = new HandlerThread("AnimationThread");
        handlerThread.start();
        Handler requestHandler = new Handler(handlerThread.getLooper());

        final long startTime = System.currentTimeMillis();
        for (int i=0; i<steps; i++) {
            final int finalI = i;
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        sleep(totalDuration/steps);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Message msg = new Message();
                        msg.obj = new long[] {startTime, totalDuration, finalI};
                        handler.sendMessage(msg);
                        if (finalI == steps-1) {
                            Message finishedMsg = new Message();
                            msg.obj = null;
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            requestHandler.post(myRunnable);
        }
    }

    public static float EasingOut(double time, double duration, int power) {
        return (float) Math.min(1 - Math.pow(1 - (time / duration), power), 1);
    }
}
