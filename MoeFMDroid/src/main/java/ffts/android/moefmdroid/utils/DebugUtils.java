package ffts.android.moefmdroid.utils;

import android.util.Log;

/**
 * Created by ffts on 13-8-4.
 * Email:ffts133@gmail.com
 */
public class DebugUtils {
    private static final boolean isDebug = true;
    private static final String TAG = "MoeFMDroid";

    public static void debug(String msg) {
        debug(TAG, msg);
    }

    public static void debug(String tag, String msg) {
        if(isDebug){
            Log.d(tag, msg);
        }
    }
}
