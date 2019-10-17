package com.example.dinhthai.sendingandreceivingdata.bluetooth.utils;

import android.app.Activity;

/**
 * Created by Steve on 1/29/18.
 */

public class ThreadHelper {

    public static void run(boolean runOnUi, Activity activity, Runnable runnable) {

        if(runOnUi)
            {
                activity.runOnUiThread(runnable);
            }
        else
            {
                runnable.run();
            }
        }
}
