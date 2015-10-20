package com.dediweb.magazineapp.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.dediweb.magazineapp.R;

/**
 * The Class DialogFragmentLoading.
 */
public class DialogFragmentLoading extends DialogFragment {

	/** The Constant TAG. */
	public static final String TAG = DialogFragmentLoading.class
			.getSimpleName();

	/**
	 * New instance.
	 *
	 * @return the dialog fragment loading
	 */
	public static DialogFragmentLoading newInstance() {
		DialogFragmentLoading fragment = new DialogFragmentLoading();

		return fragment;
	}

	/** The parent view. */
	private View parentView;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);

		parentView = inflater.inflate(R.layout.dialog_fragment_loading,
				container, false);

		return parentView;
	}

}
