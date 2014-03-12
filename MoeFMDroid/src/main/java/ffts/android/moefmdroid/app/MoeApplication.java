package ffts.android.moefmdroid.app;

import android.app.Application;


/**
 * Created by kzh on 13-8-3.
 * Email:ffts133@gmail.com
 */
public class MoeApplication extends Application {

    private static MoeApplication instance;

    public static MoeApplication getApplication() {
        if (instance == null) {
            instance = new MoeApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
