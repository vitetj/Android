package com.dediweb.magazineapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dediweb.magazineapp.R;
import com.dediweb.wordpress.fetcher.cmn.Category;

/**
 * The Class AdapterSlidingMenu.
 */
public class AdapterSlidingMenu extends ArrayAdapter<Category> {

	/**
	 * Instantiates a new adapter sliding menu.
	 *
	 * @param context the context
	 * @param cagerories the cagerories
	 */
	public AdapterSlidingMenu(Context context, List<Category> cagerories) {
		super(context, 0, cagerories);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getInflater().inflate(R.layout.list_item_category,
					null);
		}

		if (position == 0) {
			convertView.findViewById(R.id.list_item_category_divider)
					.setVisibility(View.VISIBLE);
		} else {
			convertView.findViewById(R.id.list_item_category_divider)
					.setVisibility(View.GONE);
		}

		TextView categoryText = (TextView) convertView
				.findViewById(R.id.list_item_category_text);
		categoryText.setText(getItem(position).getTitle());

		convertView.setTag(getItem(position));

		return convertView;
	}

	/**
	 * Gets the inflater.
	 *
	 * @return the inflater
	 */
	private LayoutInflater getInflater() {
		return LayoutInflater.from(getContext());
	}
}
