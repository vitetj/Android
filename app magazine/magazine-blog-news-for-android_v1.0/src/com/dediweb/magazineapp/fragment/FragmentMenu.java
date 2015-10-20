package com.dediweb.magazineapp.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dediweb.magazineapp.MainActivity;
import com.dediweb.magazineapp.R;

/**
 * The Class FragmentMenu.
 */
public class FragmentMenu extends Fragment {

	/** The Constant TAG. */
	public static final String TAG = FragmentMenu.class.getSimpleName();

	/**
	 * New instance.
	 *
	 * @return the fragment menu
	 */
	public static FragmentMenu newInstance() {

		FragmentMenu fraMenu = new FragmentMenu();

		return fraMenu;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fra_menu, container, false);

		View about = v.findViewById(R.id.fra_menu_about);

		about.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).openAboutScreen();
			}
		});
		
		View feedback = v.findViewById(R.id.fra_menu_feedback);

		feedback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openMarket();
			}
		});
		
		

		return v;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	/**
	 * Open market.
	 */
	private void openMarket() {
		Uri uri = Uri.parse("market://details?id="
				+ getActivity().getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			getActivity().startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getActivity(), R.string.cannot_launch_market,
					Toast.LENGTH_LONG).show();
		}
	}

}
