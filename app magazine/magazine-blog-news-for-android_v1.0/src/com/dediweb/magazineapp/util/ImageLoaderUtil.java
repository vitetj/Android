package com.dmbteam.magazineapp.util;

import android.widget.ImageView;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelperBase.HeaderImageLoaderable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * The Class ImageLoaderUtil.
 */
public class ImageLoaderUtil implements HeaderImageLoaderable {

	/** The Uri. */
	public String mUri;
	
	/** The Image options. */
	private DisplayImageOptions mImageOptions;

	/**
	 * Instantiates a new image loader util.
	 *
	 * @param imageOptions the image options
	 * @param uri the uri
	 */
	public ImageLoaderUtil(DisplayImageOptions imageOptions, String uri) {
		this.mUri = uri;
		this.mImageOptions = imageOptions;
	}

	/* (non-Javadoc)
	 * @see com.manuelpeinado.fadingactionbar.FadingActionBarHelperBase.HeaderImageLoaderable#loadHeaderImage(android.widget.ImageView)
	 */
	@Override
	public void loadHeaderImage(ImageView image) {
		ImageLoader.getInstance().displayImage(mUri, image, mImageOptions);
	}

}