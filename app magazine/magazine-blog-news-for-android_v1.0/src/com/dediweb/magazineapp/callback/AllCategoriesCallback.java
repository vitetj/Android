package com.dediweb.magazineapp.callback;

import java.util.List;

import com.dediweb.magazineapp.MainActivity;
import com.dediweb.magazineapp.data.MagazineAppDataHolder;
import com.dediweb.wordpress.fetcher.cmn.Category;
import com.dediweb.wordpress.fetcher.cmn.Post;
import com.dediweb.wordpress.fetcher.worker.WordpressFetcherable;

/**
 * The Class AllCategoriesCallback.
 */
public class AllCategoriesCallback implements WordpressFetcherable {

	/** The Activity. */
	private MainActivity mActivity;
	
	/** The Show with transition. */
	private boolean mShowWithTransition;
	
	/** The Is load more. */
	private boolean mIsLoadMore;

	/**
	 * Instantiates a new all categories callback.
	 *
	 * @param activity the activity
	 * @param showWithTransition the show with transition
	 */
	public AllCategoriesCallback(MainActivity activity,
			boolean showWithTransition) {
		this.mActivity = activity;
		this.mShowWithTransition = showWithTransition;
	}

	/* (non-Javadoc)
	 * @see com.dediweb.wordpress.fetcher.worker.WordpressFetcherable#resultAllCategories(java.util.List)
	 */
	@Override
	public void resultAllCategories(List<Category> categories) {
		if (categories != null && categories.size() > 0) {
			mActivity.setUpCategoryListView(categories);
		}
	}

	/* (non-Javadoc)
	 * @see com.dediweb.wordpress.fetcher.worker.WordpressFetcherable#resultAllPostsForCategory(java.util.List)
	 */
	@Override
	public void resultAllPostsForCategory(List<Post> posts) {

		if (!mIsLoadMore) {
			MagazineAppDataHolder.getInstance().getAllPosts().clear();
			mIsLoadMore = false;
			
			mActivity.liftAdapterLoadMoreFlag();
		}

		if(posts.size() > 0){
			MagazineAppDataHolder.getInstance().addToCurrentPosts(posts);
			
			mActivity.showOrRefreshHomeFragment(mShowWithTransition);
		}else{
			mActivity.hideHomeFragmentLoadingMore();
		}
		
		mActivity.hideLoadingDialog();
	}

	/**
	 * Sets the checks if is load more.
	 *
	 * @param b the new checks if is load more
	 */
	public void setIsLoadMore(boolean b) {
		this.mIsLoadMore = b;
	}

}
