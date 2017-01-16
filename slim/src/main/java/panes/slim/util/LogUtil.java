package panes.slim.util;

import android.util.Log;

import panes.slim.SlimConfig;

/**
 * Created by panes.
 */
public class LogUtil {
    public static void i (String log){
        if (SlimConfig.isDebug){
            Log.i(SlimConfig.TAG, log);
        }
    }

    public static void e (String log){
        if (SlimConfig.isDebug){
            Log.e(SlimConfig.TAG, log);
        }
    }
}
