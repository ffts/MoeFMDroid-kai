package ffts.android.moefmdroid.utils;

/**
 * Created by ffts on 13-9-15.
 * Email:ffts133@gmail.com
 */
public class StringUtils {
    public static boolean isNotNull(String s) {
        if (s != null && !s.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotNull(String... s) {
        for (String value : s) {
            if (!isNotNull(value)) {
                return false;
            }
        }
        return true;
    }
}
