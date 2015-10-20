package com.dediweb.magazineapp;

import android.app.Application;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.os.Build;
import android.util.Log;

import com.dediweb.magazineapp.settings.AppConstants;
import com.dediweb.wordpress.fetcher.worker.WordpressFetcher;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * The Class MagazineAppContext.
 */
public class MagazineAppContext extends Application {

	/** The Constant aspectRationHeader. */
	public static final double aspectRationHeader = 1.50;

	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		// Init fetcher with your server URL
		WordpressFetcher.getInstance(this).setServerUrl(
				AppConstants.WORDPRESS_SERVER_URL);
	}

	// Disc cache 50mb maximum
	/** The Constant MAX_DISC_CACHE_SIZE. */
	private static final int MAX_DISC_CACHE_SIZE = 50 * 1024 * 1024;

	// Set memory storage
	/** The Constant MEMORY_CACHE_SIZE. */
	private static final int MEMORY_CACHE_SIZE = (int) (Runtime.getRuntime()
			.maxMemory() * 0.25);

	/**
	 * Creates the image loader configuration.
	 *
	 * @param appContext the app context
	 * @return the image loader configuration
	 */
	@SuppressWarnings("deprecation")
	public static ImageLoaderConfiguration createImageLoaderConfiguration(
			MainActivity appContext) {

		Log.v("Utils",
				"Start building ImageLoader configuration for API level "
						+ Build.VERSION.SDK_INT);
		ImageLoaderConfiguration.Builder b = new ImageLoaderConfiguration.Builder(
				appContext);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			b.threadPoolSize(1);
		} else {
			b.threadPoolSize(3);
		}

		int mScreenWidth = appContext.getWindowManager().getDefaultDisplay()
				.getWidth();
		int mScreenHeight = (int) (mScreenWidth / aspectRationHeader);

		b.memoryCacheExtraOptions(mScreenWidth, mScreenHeight);
		b.discCacheExtraOptions(mScreenWidth, mScreenHeight,
				CompressFormat.PNG, 90, null);
		b.threadPriority(Thread.NORM_PRIORITY - 1);
		b.memoryCacheSize(MEMORY_CACHE_SIZE);
		b.discCacheSize(MAX_DISC_CACHE_SIZE);
		b.discCacheFileNameGenerator(new HashCodeFileNameGenerator());
		b.imageDownloader(new BaseImageDownloader(appContext));
		b.tasksProcessingOrder(QueueProcessingType.FIFO);
		b.defaultDisplayImageOptions(DisplayImageOptions.createSimple());
		// b.enableLogging();

		return b.build();
	}

	/**
	 * Builds the image options.
	 *
	 * @param scaleType the scale type
	 * @param inMemoryCache the in memory cache
	 * @param resetViewBeforeLoading the reset view before loading
	 * @return the display image options
	 */
	public static DisplayImageOptions buildImageOptions(
			ImageScaleType scaleType, boolean inMemoryCache,
			boolean resetViewBeforeLoading) {

		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.bitmapConfig(Config.RGB_565);
		if (inMemoryCache)
			builder.cacheInMemory();
		if (resetViewBeforeLoading)
			builder.resetViewBeforeLoading();
		builder.cacheOnDisc();
		builder.imageScaleType(scaleType);

		return builder.build();
	}
}
