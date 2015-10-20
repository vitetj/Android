package com.dediweb.magazineapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dediweb.magazineapp.MainActivity;
import com.dediweb.magazineapp.R;
import com.dediweb.magazineapp.settings.AppConstants;
import com.dediweb.magazineapp.util.ImageLoaderUtil;
import com.dediweb.magazineapp.util.ScaleUtil;
import com.dediweb.magazineapp.util.ThemesManager;
import com.dediweb.magazineapp.util.URLImageParser;
import com.dediweb.wordpress.fetcher.cmn.Post;
import com.dediweb.wordpress.fetcher.util.DateUtil;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.manuelpeinado.fadingactionbar.FadingActionBarHelperBase;
import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * The Class FragmentSinglePost.
 */
public class FragmentSinglePost extends Fragment {

	/** The tag. */
	public static String TAG = FragmentSinglePost.class.getSimpleName();

	/** The Post. */
	private Post mPost;

	/** The Categoty posts counter. */
	private int mCategotyPostsCounter;

	/** The Image options. */
	private DisplayImageOptions mImageOptions;

	/**
	 * Instantiates a new fragment single post.
	 */
	public FragmentSinglePost() {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.bitmapConfig(Config.RGB_565);
		builder.cacheInMemory();
		builder.showImageOnFail(R.drawable.home_default_post_picture);
		builder.cacheOnDisc();
		builder.imageScaleType(ImageScaleType.EXACTLY_STRETCHED);

		mImageOptions = builder.build();
	}

	/**
	 * New instance.
	 *
	 * @param post the post
	 * @param categoryCount the category count
	 * @return the fragment single post
	 */
	public static FragmentSinglePost newInstance(Post post, int categoryCount) {
		FragmentSinglePost fragment = new FragmentSinglePost();

		fragment.mPost = post;
		fragment.mCategotyPostsCounter = categoryCount;

		return fragment;
	}

	/** The Fading helper. */
	private FadingActionBarHelperBase mFadingHelper;

	/** The Category title. */
	private TextView mCategoryTitle;

	/** The Post title. */
	private TextView mPostTitle;

	/** The Post date. */
	private TextView mPostDate;

	/** The Post content. */
	private TextView mPostContent;

	/** The prefs. */
	private SharedPreferences prefs;

	/** The More from category tv. */
	private TextView mMoreFromCategoryTv;

	/** The Category count. */
	private TextView mCategoryCount;

	/** The More from container. */
	private View mMoreFromContainer;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		((MainActivity) getActivity()).disableLeftSlidingMenu();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		((MainActivity) getActivity()).enableLeftSlidingMenu();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		prefs = getActivity().getPreferences(Context.MODE_PRIVATE);

		int actionBarBg = ThemesManager.getIdForSpecificAttribute(
				getActivity(), R.attr.magazine_ab_background_light);

		mFadingHelper = new FadingActionBarHelper()
				.actionBarBackground(actionBarBg)
				.headerLayout(R.layout.header_ab_home)
				.contentLayout(R.layout.fragment_single_post)
				.lightActionBar(true);

		String uriToDisplay = "";
		if (mPost.getAttachments() == null
				|| mPost.getAttachments().size() == 0) {
			uriToDisplay = "drawable://" + R.drawable.home_default_post_picture;
		} else {
			uriToDisplay = mPost.giveMeBiggestAttachment();
		}

		mFadingHelper.setHeaderLoader(new ImageLoaderUtil(mImageOptions,
				uriToDisplay));

		mFadingHelper.initActionBar(getActivity());

		View view = mFadingHelper.createView(inflater);

		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Init views
		mCategoryTitle = (TextView) getView().findViewById(
				R.id.fragment_single_post_category);

		mPostTitle = (TextView) getView().findViewById(
				R.id.fragment_single_post_title);

		mPostDate = (TextView) getView().findViewById(
				R.id.fragment_single_post_date);

		mPostContent = (TextView) getView().findViewById(
				R.id.fragment_single_post_content);

		updateTextSize();

		// Set data to views
		mCategoryTitle.setText(mPost.getPostCategoryAsString());
		mPostTitle.setText(mPost.getTitle());
		mPostDate.setText(DateUtil.postsDatePublishedFormatter1.format(mPost
				.getPublishedDate()));

		URLImageParser p = new URLImageParser(mPostContent, getActivity());
		mPostContent.setText(Html.fromHtml(
				mPost.getContentString().replace((char) 160, (char) 32)
						.replace((char) 65532, (char) 32).trim(), p, null));
		mPostContent.setMovementMethod(LinkMovementMethod.getInstance());

		mMoreFromCategoryTv = (TextView) getView().findViewById(
				R.id.fragment_single_more_from_tv);

		if (mPost.getCategories().size() > 1) {
			mMoreFromCategoryTv
					.setText(getString(R.string.single_category_recent_posts));
		} else {
			mMoreFromCategoryTv.setText(String.format(
					getString(R.string.single_category_more),
					mPost.getPostCategoryAsString()));
		}

		mCategoryCount = (TextView) getView().findViewById(
				R.id.fragment_single_category_count);
		mCategoryCount.setText("" + mCategotyPostsCounter);

		mMoreFromContainer = getView().findViewById(
				R.id.fragment_single_more_from_container);

		setupAdmob();

		setupMoreFromContainer();
	}

	/**
	 * Setup more from container.
	 */
	private void setupMoreFromContainer() {

		mMoreFromContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});

	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		((MainActivity) getActivity()).disableActionBarSlider();
		((MainActivity) getActivity()).showActionBarIcons();
	}

	/**
	 * Update text size.
	 */
	public void updateTextSize() {

		float size = prefs.getFloat(
				MainActivity.FONT_SIZE, 0);

		Log.i("Magazine_Text", "FragmentSinglePost size = " + size);

		mPostContent.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				size + ScaleUtil.dipsToPixels(getActivity(), 10));
	}

	/**
	 * Setup admob.
	 */
	private void setupAdmob() {
		AdView admobView = (AdView) getView().findViewById(
				R.id.single_post_admob);

		if (AppConstants.isAdmobEnabled) {
			admobView.setVisibility(View.VISIBLE);
			AdRequest adRequest = new AdRequest.Builder().build();

			// Start loading the ad in the background.
			admobView.loadAd(adRequest);

		} else {
			admobView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Gets the post.
	 *
	 * @return the post
	 */
	public Post getPost() {
		return mPost;
	}
}
