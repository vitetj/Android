package com.dediweb.magazineapp;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dediweb.magazineapp.adapter.AdapterSlidingMenu;
import com.dediweb.magazineapp.callback.AllCategoriesCallback;
import com.dediweb.magazineapp.data.MagazineAppDataHolder;
import com.dediweb.magazineapp.fragment.DialogFragmentFont;
import com.dediweb.magazineapp.fragment.DialogFragmentLoading;
import com.dediweb.magazineapp.fragment.DialogFragmentShare;
import com.dediweb.magazineapp.fragment.FragmentAbout;
import com.dediweb.magazineapp.fragment.FragmentHome;
import com.dediweb.magazineapp.fragment.FragmentMenu;
import com.dediweb.magazineapp.fragment.FragmentSinglePost;
import com.dediweb.magazineapp.settings.AppConstants;
import com.dediweb.magazineapp.util.ScaleUtil;
import com.dediweb.magazineapp.util.ThemesManager;
import com.dediweb.wordpress.fetcher.cmn.Category;
import com.dediweb.wordpress.fetcher.cmn.Post;
import com.dediweb.wordpress.fetcher.worker.WordpressFetcher;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * The Class MainActivity.
 */
public class MainActivity extends ActionBarActivity {
	
	/** The Constant FONT_SIZE. */
	public static final String FONT_SIZE = "fontsize";

	/** The Constant RECEIVER_RECEIVE_NEW_DATA. */
	public static final String RECEIVER_RECEIVE_NEW_DATA = "receiver.receive.new.data";

	/** The Categoty list view. */
	private ListView mCategotyListView;
	
	/** The Drawer toggle. */
	private ActionBarDrawerToggle mDrawerToggle;
	
	/** The Left drawer. */
	private DrawerLayout mLeftDrawer;
	
	/** The Sliding about container. */
	private View mSlidingAboutContainer;
	
	/** The currently loaded category. */
	private Category currentlyLoadedCategory;

	/** The Wordpress fetcher call back. */
	private AllCategoriesCallback mWordpressFetcherCallBack;

	/** The Ab share icon. */
	private View mAbShareIcon;

	/** The Ab font icon. */
	private View mAbFontIcon;

	/** The is finished checking data. */
	private boolean isFinishedCheckingData;

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ThemesManager.setCorrectTheme(this);

		ImageLoader.getInstance().init(
				MagazineAppContext.createImageLoaderConfiguration(this));

		setContentView(R.layout.activity_main);

		setUpActionbarSlidingMenu();

		giveMeAllCategories();

		initCategoryViews();

		showOrRefreshHomeFragment(true);

		initDataCheckerTask();

		initTextSize();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		if (item.getItemId() == android.R.id.home) {
			getSupportFragmentManager().popBackStack();
		}

		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();

		hideMenu();
	}

	/**
	 * Perform sliding menu clicked.
	 *
	 * @param category the category
	 * @param showWithTransition the show with transition
	 * @param page the page
	 */
	public void performSlidingMenuClicked(Category category,
			boolean showWithTransition, int page) {

		hideMenu();

		currentlyLoadedCategory = category;

		if (mWordpressFetcherCallBack == null) {
			mWordpressFetcherCallBack = new AllCategoriesCallback(
					MainActivity.this, showWithTransition);
		}

		WordpressFetcher.getInstance(this).fetchPostsForCategoryWithCallback(
				mWordpressFetcherCallBack, category, page);

		closeLeftSlidingMenu();
	}

	/**
	 * Sets the up category list view.
	 *
	 * @param categories the new up category list view
	 */
	public void setUpCategoryListView(List<Category> categories) {
		AdapterSlidingMenu adapter = new AdapterSlidingMenu(this, categories);

		mCategotyListView.setAdapter(adapter);

		mCategotyListView.setClickable(true);

		mCategotyListView
				.setOnItemClickListener(new SlidingMenuAdapterItemClickListner());

		// performSlidingMenuClicked(null, true);
	}

	/**
	 * Show or refresh home fragment.
	 *
	 * @param showWithTransition the show with transition
	 */
	public void showOrRefreshHomeFragment(boolean showWithTransition) {

		Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(
				FragmentHome.TAG);

		if (homeFragment != null) {
			((FragmentHome) homeFragment).loadMoreData();
		} else {
			FragmentHome fragment = FragmentHome
					.newInstance(MagazineAppDataHolder.getInstance()
							.getAllPosts());
			showScreen(fragment, FragmentHome.TAG, false, showWithTransition);
		}

	}

	/**
	 * Show single post fragment.
	 *
	 * @param post the post
	 * @param showWithTransition the show with transition
	 */
	public void showSinglePostFragment(Post post, boolean showWithTransition) {

		hideMenu();

		FragmentSinglePost fragment = null;

		if (currentlyLoadedCategory == null) {
			fragment = FragmentSinglePost.newInstance(post,
					post.getRecentPostsCounter());
		} else {
			fragment = FragmentSinglePost.newInstance(post,
					currentlyLoadedCategory.getPostCount());
		}

		showScreen(fragment, FragmentSinglePost.TAG, true, showWithTransition);
	}

	/**
	 * Enable action bar slider.
	 */
	public void enableActionBarSlider() {
		mDrawerToggle.setDrawerIndicatorEnabled(true);
	}

	/**
	 * Disable action bar slider.
	 */
	public void disableActionBarSlider() {
		mDrawerToggle.setDrawerIndicatorEnabled(false);
	}

	/**
	 * Gets the user web url.
	 *
	 * @return the user web url
	 */
	public String getUserWebUrl() {
		return AppConstants.USER_WEBSITE_URL;
	}

	/**
	 * Gets the user mail.
	 *
	 * @return the user mail
	 */
	public String getUserMail() {
		return AppConstants.USER_MAIL;
	}

	/**
	 * Gets the user blog title.
	 *
	 * @return the user blog title
	 */
	public String getUserBlogTitle() {
		return AppConstants.USER_BLOG_TITLE;
	}

	/**
	 * Gets the user blog phone.
	 *
	 * @return the user blog phone
	 */
	public String getUserBlogPhone() {
		return AppConstants.USER_PHONE;
	}

	/**
	 * Disable left sliding menu.
	 */
	public void disableLeftSlidingMenu() {
		mLeftDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	/**
	 * Enable left sliding menu.
	 */
	public void enableLeftSlidingMenu() {
		mLeftDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}

	/**
	 * Load more.
	 *
	 * @param page the page
	 */
	public void loadMore(int page) {
		if (mWordpressFetcherCallBack == null) {
			mWordpressFetcherCallBack = new AllCategoriesCallback(
					MainActivity.this, false);

		}

		mWordpressFetcherCallBack.setIsLoadMore(true);

		performSlidingMenuClicked(currentlyLoadedCategory, false, page);
	}

	/**
	 * Hide home fragment loading more.
	 */
	public void hideHomeFragmentLoadingMore() {
		Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(
				FragmentHome.TAG);

		if (homeFragment != null) {
			((FragmentHome) homeFragment).hideLoadingMore();
		}
	}

	/**
	 * Lift adapter load more flag.
	 */
	public void liftAdapterLoadMoreFlag() {
		Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(
				FragmentHome.TAG);

		if (homeFragment != null) {
			((FragmentHome) homeFragment).enableLoadingMore();
			;
		}
	}

	/**
	 * Open menu.
	 */
	public void openMenu() {

		if (getSupportFragmentManager().findFragmentByTag(FragmentMenu.TAG) != null) {
			hideMenu();
		} else {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();

			FragmentMenu fraMenu = FragmentMenu.newInstance();

			ft.replace(R.id.main_container_menu, fraMenu, FragmentMenu.TAG);

			ft.addToBackStack(String.valueOf(System.identityHashCode(fraMenu)));

			ft.commitAllowingStateLoss();
			getSupportFragmentManager().executePendingTransactions();
		}
	}

	/**
	 * Open about screen.
	 */
	public void openAboutScreen() {

		if (getSupportFragmentManager().findFragmentByTag(FragmentAbout.TAG) == null) {
			FragmentAbout fragmentAbout = FragmentAbout.newInstance();

			showScreen(fragmentAbout, FragmentAbout.TAG, true, true);

		}

		closeLeftSlidingMenu();
		hideMenu();

	}

	/**
	 * Hide menu.
	 */
	public void hideMenu() {
		Fragment fraMenu = getSupportFragmentManager().findFragmentByTag(
				FragmentMenu.TAG);

		if (fraMenu != null) {
			getSupportFragmentManager().popBackStack();
		}
	}

	/**
	 * Show loading dialog.
	 */
	public void showLoadingDialog() {

		DialogFragmentLoading fragment = DialogFragmentLoading.newInstance();

		fragment.show(getSupportFragmentManager(), DialogFragmentLoading.TAG);
	}

	/**
	 * Hide loading dialog.
	 */
	public void hideLoadingDialog() {
		Fragment dialog = getSupportFragmentManager().findFragmentByTag(
				DialogFragmentLoading.TAG);

		if (dialog != null) {
			((DialogFragmentLoading) dialog).dismiss();
		}
	}

	/**
	 * Hide action bar icons.
	 */
	public void hideActionBarIcons() {
		this.mAbShareIcon.setVisibility(View.GONE);
		this.mAbFontIcon.setVisibility(View.GONE);
	}

	/**
	 * Show action bar icons.
	 */
	public void showActionBarIcons() {
		this.mAbShareIcon.setVisibility(View.VISIBLE);
		this.mAbFontIcon.setVisibility(View.VISIBLE);
	}

	/**
	 * Checks if is home adapter having data.
	 *
	 * @return true, if is home adapter having data
	 */
	public boolean isHomeAdapterHavingData() {
		Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(
				FragmentHome.TAG);

		if (homeFragment != null && homeFragment instanceof FragmentHome
				&& ((FragmentHome) homeFragment).isLoadedData()) {
			Log.i("Magazine", "Start checking 5");
			return true;
		}

		Log.i("Magazine", "Start checking 6");
		return false;
	}

	/**
	 * Load more data to home adapter.
	 */
	public void loadMoreDataToHomeAdapter() {

		Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(
				FragmentHome.TAG);

		if (homeFragment != null && homeFragment instanceof FragmentHome
				&& ((FragmentHome) homeFragment).isLoadedData()) {
			((FragmentHome) homeFragment).loadMoreData();
		}
	}

	/**
	 * Inits the data checker task.
	 */
	public void initDataCheckerTask() {

		Handler handler = new Handler();

		isFinishedCheckingData = false;

		for (int i = 1; i < 4; i++) {

			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (!isFinishedCheckingData) {

						Log.i("Magazine", "Start checking 1");

						if (MagazineAppDataHolder.getInstance().getAllPosts()
								.size() > 0) {
							Log.i("Magazine", "Start checking 2");

							if (isHomeAdapterHavingData()) {
								Log.i("Magazine", "Start checking 3");
								isFinishedCheckingData = true;
							} else {
								Log.i("Magazine", "Start checking 4");
								loadMoreDataToHomeAdapter();
							}
						} else {

						}
					}
				}
			}, i * 2000);
		}

	}

	/**
	 * Inits the text size.
	 */
	private void initTextSize() {
		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

		float size = prefs.getFloat(FONT_SIZE, 0);

		if (size == 0) {
			SharedPreferences.Editor ed = prefs.edit();
			ed.putFloat(FONT_SIZE,
					ScaleUtil.getScaledInitialTextSize(this));
			ed.commit();

		}
	}

	/**
	 * Sets the up actionbar sliding menu.
	 */
	private void setUpActionbarSlidingMenu() {
		
		int actionBarBg = ThemesManager.getIdForSpecificAttribute(
				this, R.attr.magazine_ic_drawer);
		
		mLeftDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mLeftDrawer, /* DrawerLayout object */
		actionBarBg, /* nav drawer icon to replace 'Up' caret */
		R.string.open_dr_desc, /* "open drawer" description */
		R.string.app_name /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(getString(R.string.app_name));
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(getString(R.string.open_dr_desc));
			}

		};

		// Set the drawer toggle as the DrawerListener
		mLeftDrawer.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
				.inflate(R.layout.ab_custom_view, null);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setCustomView(actionBarLayout);

		View overflowMenu = actionBarLayout
				.findViewById(R.id.ab_custom_view_overflow);
		overflowMenu.setOnClickListener(new OverflowMenuActionListener());

		mAbShareIcon = actionBarLayout.findViewById(R.id.ab_custom_view_share);
		mAbShareIcon.setOnClickListener(new ShareMenuActionListener());

		mAbFontIcon = actionBarLayout
				.findViewById(R.id.ab_custom_view_font_size);
		mAbFontIcon.setOnClickListener(new FontMenuActionListener());

	}

	/**
	 * Inits the category views.
	 */
	private void initCategoryViews() {
		mLeftDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		mCategotyListView = (ListView) findViewById(R.id.sliding_menu_categories_lv);

		mSlidingAboutContainer = findViewById(R.id.sliding_menu_about_container);

		mSlidingAboutContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openAboutScreen();
			}
		});

		TextView blogTitle = (TextView) findViewById(R.id.sliding_menu_blog_title);
		blogTitle.setText(getUserBlogTitle());

		TextView blogUrl = (TextView) findViewById(R.id.sliding_menu_blog_url);
		blogUrl.setText(getUserWebUrl());

		TextView blogMail = (TextView) findViewById(R.id.sliding_menu_blog_mail);
		blogMail.setText(getUserMail());
	}

	/**
	 * Give me all categories.
	 */
	private void giveMeAllCategories() {
		WordpressFetcher.getInstance(this).fetchAllCategoriesWithCallback(
				new AllCategoriesCallback(this, true));
	}

	/**
	 * Show screen.
	 *
	 * @param content the content
	 * @param contentTag the content tag
	 * @param addToBackStack the add to back stack
	 * @param showWithTransition the show with transition
	 */
	private void showScreen(Fragment content, String contentTag,
			boolean addToBackStack, boolean showWithTransition) {
		hideMenu();

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		if (showWithTransition) {
			ft.setCustomAnimations(R.anim.left_slide_in, R.anim.left_slide_out,
					R.anim.right_slide_in, R.anim.right_slide_out);
		}

		ft.replace(R.id.main_container, content, contentTag);

		if (addToBackStack) {
			ft.addToBackStack(String.valueOf(System.identityHashCode(content)));
		}

		ft.commitAllowingStateLoss();
		fm.executePendingTransactions();
	}

	/**
	 * Close left sliding menu.
	 */
	private void closeLeftSlidingMenu() {
		mLeftDrawer.closeDrawer(Gravity.LEFT);
	}

	/**
	 * The Class SlidingMenuAdapterItemClickListner.
	 */
	private class SlidingMenuAdapterItemClickListner implements
			OnItemClickListener {

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			performSlidingMenuClicked((Category) view.getTag(), false, 0);

			showLoadingDialog();

			mWordpressFetcherCallBack.setIsLoadMore(false);
		}
	}

	/**
	 * The listener interface for receiving overflowMenuAction events.
	 * The class that is interested in processing a overflowMenuAction
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addOverflowMenuActionListener<code> method. When
	 * the overflowMenuAction event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OverflowMenuActionEvent
	 */
	private class OverflowMenuActionListener implements View.OnClickListener {

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			openMenu();
		}
	}

	/**
	 * The listener interface for receiving fontMenuAction events.
	 * The class that is interested in processing a fontMenuAction
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addFontMenuActionListener<code> method. When
	 * the fontMenuAction event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see FontMenuActionEvent
	 */
	private class FontMenuActionListener implements View.OnClickListener {

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			DialogFragmentFont fraFont = DialogFragmentFont.newInstance();

			fraFont.show(getSupportFragmentManager(), DialogFragmentFont.TAG);
		}
	}

	/**
	 * The listener interface for receiving shareMenuAction events.
	 * The class that is interested in processing a shareMenuAction
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addShareMenuActionListener<code> method. When
	 * the shareMenuAction event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see ShareMenuActionEvent
	 */
	private class ShareMenuActionListener implements View.OnClickListener {

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			DialogFragmentShare fraShare = DialogFragmentShare.newInstance();

			fraShare.show(getSupportFragmentManager(), DialogFragmentShare.TAG);
		}
	}

	/**
	 * Update single font size.
	 */
	public void updateSingleFontSize() {
		Fragment fr = getSupportFragmentManager().findFragmentByTag(
				FragmentSinglePost.TAG);

		if (fr != null) {
			((FragmentSinglePost) fr).updateTextSize();
		}
	}

	/**
	 * Gets the current post.
	 *
	 * @return the current post
	 */
	public Post getCurrentPost() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentSinglePost fragment = (FragmentSinglePost) fm
				.findFragmentByTag(FragmentSinglePost.TAG);
		return fragment.getPost();
	}

	/**
	 * Checks if is network connected.
	 *
	 * @return true, if is network connected
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * Show toast with message.
	 *
	 * @param string the string
	 */
	public void showToastWithMessage(String string) {
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	}
}
