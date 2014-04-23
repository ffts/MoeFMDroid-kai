package ffts.android.moefmdroid.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.download.HttpClientImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;


/**
 * Created by kzh on 13-8-3.
 * Email:ffts133@gmail.com
 */
public class MoeApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initImageLoader();
    }

    public static Context getContext() {
        return context;
    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(this);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 1) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .discCache(new UnlimitedDiscCache(cacheDir)) // default
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .defaultDisplayImageOptions(options)
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }
}
