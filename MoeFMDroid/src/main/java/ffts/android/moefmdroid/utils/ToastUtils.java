package ffts.android.moefmdroid.utils;

import android.content.Context;
import android.widget.Toast;

import ffts.android.moefmdroid.app.MoeApplication;

/**
 * Created by ffts on 14-3-29.
 * Email:ffts133@gmail.com
 */
public class ToastUtils {
    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void toast(String msg) {
        Toast.makeText(MoeApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
