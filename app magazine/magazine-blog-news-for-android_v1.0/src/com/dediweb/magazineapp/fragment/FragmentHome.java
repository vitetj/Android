package com.dediweb.magazineapp.fragment;

import java.util.List;

import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.dediweb.magazineapp.MainActivity;
import com.dediweb.magazineapp.R;
import com.dediweb.magazineapp.adapter.AdapterHome;
import com.dediweb.magazineapp.data.MagazineAppDataHolder;
import com.dediweb.magazineapp.settings.AppConstants;
import com.dediweb.magazineapp.util.ThemesManager;
import com.dediweb.wordpress.fetcher.cmn.Post;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.manuelpeinado.fadingactionbar.FadingActionBarHelperBase.HeaderImageLoaderable;
import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * The Class FragmentHome.
 */
public class FragmentHome extends Fragment {

	/** The Constant TAG. */
	public static final String TAG = FragmentHome.class.getSimpleName();

	/** The List view. */
	private ListView mListView;

	/** The Items. */
	private List<Post> mItems;

	/** The Fading helper. */
	private FadingActionBarHelper mFadingHelper;

	/** The Image options. */
	private DisplayImageOptions mImageOptions;

	/** The adapter. */
	private AdapterHome adapter;

	/** The Image loader call back. */
	private ImageLoaderUtil mImageLoaderCallBack;

	/** The view. */
	private View view;

	/**
	 * New instance.
	 *
	 * @param items the items
	 * @return the fragment home
	 */
	public static FragmentHome newInstance(List<Post> items) {

		FragmentHome fragment = new FragmentHome();
		fragment.mItems = items;

		Log.i("Magazine", "Start checking 7 size =" + items.size());

		return fragment;
	}

	/**
	 * Instantiates a new fragment home.
	 */
	public FragmentHome() {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.bitmapConfig(Config.RGB_565);
		builder.cacheInMemory();
		builder.showImageOnFail(R.drawable.home_default_post_picture);
		builder.cacheOnDisc();
		builder.imageScaleType(ImageScaleType.EXACTLY_STRETCHED);

		mImageOptions = builder.build();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		int actionBarBg = ThemesManager.getIdForSpecificAttribute(
				getActivity(), R.attr.magazine_ab_background_light);

		mFadingHelper = new FadingActionBarHelper()
				.actionBarBackground(actionBarBg)
				.headerLayout(R.layout.header_ab_home)
				.contentLayout(R.layout.fragment_home).lightActionBar(true);

		setUpHomeImage();

		mFadingHelper.setHeaderLoader(mImageLoaderCallBack);

		mFadingHelper.initActionBar(getActivity());

		view = mFadingHelper.createView(inflater);

		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		((MainActivity) getActivity()).enableActionBarSlider();

		((MainActivity) getActivity()).hideActionBarIcons();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter = new AdapterHome(getActivity(), mItems);

		mListView = (ListView) getView().findViewById(android.R.id.list);
		mListView.setAdapter(adapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				((MainActivity) getActivity()).showSinglePostFragment(
						mItems.get(position - 1), true);
			}
		});

		if (mFadingHelper.getMarginView() != null) {
			mFadingHelper.getMarginView().setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							((MainActivity) getActivity())
									.showSinglePostFragment(mItems.get(0), true);
						}
					});
		}

		setupAdmob();

		// TODO !!!DO NOT DELETE THIS FOR NOW!!!
		// mRecyclerView.setOnScrollListener(new AbsListView.OnScrollListener()
		// {
		// @Override
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		//
		// }
		//
		// @Override
		// public void onScroll(AbsListView view, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		// int topRowVerticalPosition = (mRecyclerView == null || mRecyclerView
		// .getChildCount() == 0) ? 0 : mRecyclerView
		// .getChildAt(0).getTop();
		// ((MainActivity) getActivity()).getSwipeRefreshLayout()
		// .setEnabled(topRowVerticalPosition >= 0);
		// }
		// });
	}

	/**
	 * Load more data.
	 */
	public void loadMoreData() {

		adapter.setLoadingMore(false);

		mItems = MagazineAppDataHolder.getInstance().getAllPosts();

		adapter.notifyDataSetChanged();
	}

	/**
	 * Hide loading more.
	 */
	public void hideLoadingMore() {
		adapter.cancelLoadingMore();

		adapter.notifyDataSetChanged();
	}

	/**
	 * Enable loading more.
	 */
	public void enableLoadingMore() {
		adapter.enableLoadingMore();
	}

	/**
	 * Checks if is loaded data.
	 *
	 * @return true, if is loaded data
	 */
	public boolean isLoadedData() {

		if (mItems.size() > 0) {
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}

			return true;
		}

		return false;
	}

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

	/**
	 * Sets the up home image.
	 */
	private void setUpHomeImage(){
		String uriToDisplay = "";

		if (mItems == null || mItems.size() == 0) {

			if (!((MainActivity) getActivity()).isNetworkConnected()) {
				((MainActivity) getActivity())
						.showToastWithMessage(getString(R.string.no_internet_connection));
			}

		} else if (mItems.get(0).getAttachments() == null
				|| mItems.get(0).getAttachments().size() == 0) {
			uriToDisplay = "drawable://" + R.drawable.home_default_post_picture;
		} else {
			uriToDisplay = mItems.get(0).getAttachments().get(0).getFullSize()
					.getUrl();
		}
		
		mImageLoaderCallBack = new ImageLoaderUtil(mImageOptions, uriToDisplay);

	}
	
	/**
	 * Setup admob.
	 */
	private void setupAdmob() {
		AdView admobView = (AdView) getView().findViewById(R.id.home_admob);

		if (AppConstants.isAdmobEnabled) {
			admobView.setVisibility(View.VISIBLE);
			AdRequest adRequest = new AdRequest.Builder().build();

			// Start loading the ad in the background.
			admobView.loadAd(adRequest);

		} else {
			admobView.setVisibility(View.GONE);
		}
	}
}
