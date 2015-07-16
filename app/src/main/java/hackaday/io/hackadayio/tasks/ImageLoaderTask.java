package hackaday.io.hackadayio.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by paul on 2015/07/13.
 */

/**
 * Created by paul on 2015/06/10.
 */
public class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {

    private Exception exception;
    private Context context;

    public ImageLoaderTask(Context context) {
        this.context = context;
    }

    protected Bitmap doInBackground(String... urls) {
        try {
            String imageurl = urls[0];

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context.getApplicationContext())
                    .memoryCache(new WeakMemoryCache())
                    .denyCacheImageMultipleSizesInMemory()
                    //.discCache(new UnlimitedDiscCache(cacheDir))
                    //.imageDownloader(new URLConnectionImageDownloader(120 * 1000, 120 * 1000))
                    //.enableLogging()
                    .threadPoolSize(1)
                    .build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            if(!imageLoader.isInited()) {
                imageLoader.init(config);
            }

            ImageLoader imgLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheOnDisc()
                    .resetViewBeforeLoading()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .build();


            return imgLoader.loadImageSync(imageurl, options);

        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    protected void onPostExecute(Bitmap bmp) {
        // TODO: check this.exception
        // TODO: do something with the image
    }

}