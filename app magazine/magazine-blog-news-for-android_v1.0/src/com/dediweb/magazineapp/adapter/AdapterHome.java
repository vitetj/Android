package com.dediweb.magazineapp.adapter;

import java.util.List;

import org.jsoup.Jsoup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dediweb.magazineapp.MainActivity;
import com.dediweb.magazineapp.R;
import com.dediweb.wordpress.fetcher.cmn.Post;
import com.dediweb.wordpress.fetcher.util.DateUtil;

/**
 * The Class AdapterHome.
 */
public class AdapterHome extends ArrayAdapter<Post> {

	/** The is loading more. */
	private boolean isLoadingMore;
	
	/** The Cancel loading more. */
	private boolean mCancelLoadingMore;

	/**
	 * Instantiates a new adapter home.
	 *
	 * @param context the context
	 * @param objects the objects
	 */
	public AdapterHome(Context context, List<Post> objects) {
		super(context, 0, objects);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolderHome holder = null;

		if (convertView == null) {

			convertView = getInflater().inflate(R.layout.list_item_home,
					parent, false);

			holder = new ViewHolderHome();

			holder.title = (TextView) convertView
					.findViewById(R.id.list_item_home_title);
			holder.text = (TextView) convertView
					.findViewById(R.id.list_item_home_text);
			holder.date = (TextView) convertView
					.findViewById(R.id.list_item_home_date);
			holder.photoIcon = (ImageView) convertView
					.findViewById(R.id.list_item_home_photo_icon);
			holder.videoIcon = (ImageView) convertView
					.findViewById(R.id.list_item_home_video);
			holder.delimier = convertView
					.findViewById(R.id.list_item_home_delimiter);
			holder.loadingMore = (TextView) convertView
					.findViewById(R.id.list_item_home_loading_more);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolderHome) convertView.getTag();
		}

		Post post = getItem(position);

		if (getCount() >= 10 && getCount() % 10 == 0 && (position == (getCount() - 1)) && !isLoadingMore) {
			isLoadingMore = true;

			((MainActivity) getContext()).loadMore(getCount() / 10 + 1);
			
			Log.i("Magazine_Load_More", "" + (getCount() / 10 + 1));
		}

		if (getCount() >= 10 && getCount() % 10 == 0 && (position == (getCount() - 1)) && !mCancelLoadingMore) {
			holder.delimier.setVisibility(View.GONE);
			holder.loadingMore.setVisibility(View.VISIBLE);
		} else {
			holder.delimier.setVisibility(View.VISIBLE);
			holder.loadingMore.setVisibility(View.GONE);
		}

		if (post.getAttachments() != null && post.getAttachments().size() > 0) {
			holder.photoIcon.setVisibility(View.VISIBLE);
		} else {
			holder.photoIcon.setVisibility(View.GONE);
		}
		
		if (post.getVIdeoCounter() > 0) {
			holder.videoIcon.setVisibility(View.VISIBLE);
		} else {
			holder.videoIcon.setVisibility(View.GONE);
		}

		holder.title.setText(post.getPostCategoryAsString());
		holder.text.setText(Jsoup.parse(post.getTitle()).text());
		holder.date.setText(DateUtil.postsDatePublishedFormatter1.format(post
				.getPublishedDate()));

		return convertView;

	}

	/**
	 * Sets the loading more.
	 *
	 * @param isLoadingMore the new loading more
	 */
	public void setLoadingMore(boolean isLoadingMore) {
		this.isLoadingMore = isLoadingMore;
	}

	/**
	 * Gets the inflater.
	 *
	 * @return the inflater
	 */
	private LayoutInflater getInflater() {
		return LayoutInflater.from(getContext());
	}

	/**
	 * The Class ViewHolderHome.
	 */
	public class ViewHolderHome {

		/** The title. */
		TextView title;
		
		/** The text. */
		TextView text;
		
		/** The date. */
		TextView date;
		
		/** The photo icon. */
		ImageView photoIcon;
		
		/** The video icon. */
		ImageView videoIcon;
		
		/** The delimier. */
		View delimier;
		
		/** The loading more. */
		TextView loadingMore;
	}

	/**
	 * Cancel loading more.
	 */
	public void cancelLoadingMore() {
		mCancelLoadingMore = true;
	}

	/**
	 * Enable loading more.
	 */
	public void enableLoadingMore() {
		mCancelLoadingMore = false;
	}
}
