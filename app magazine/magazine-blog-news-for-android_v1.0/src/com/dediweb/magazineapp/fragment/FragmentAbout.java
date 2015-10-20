package com.dediweb.magazineapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dediweb.magazineapp.MainActivity;
import com.dediweb.magazineapp.R;
import com.dediweb.magazineapp.settings.AppConstants;
import com.dediweb.magazineapp.util.ThemesManager;
import com.manuelpeinado.fadingactionbar.FadingActionBarHelperBase;
import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;

/**
 * The Class FragmentAbout.
 */
public class FragmentAbout extends Fragment {

	/** The tag. */
	public static String TAG = FragmentAbout.class.getSimpleName();

	/**
	 * New instance.
	 *
	 * @return the fragment about
	 */
	public static FragmentAbout newInstance() {
		FragmentAbout fragment = new FragmentAbout();

		return fragment;
	}

	/** The Fading helper. */
	private FadingActionBarHelperBase mFadingHelper;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		int actionBarBg = ThemesManager.getIdForSpecificAttribute(
				getActivity(), R.attr.magazine_ab_background_light);

		mFadingHelper = new FadingActionBarHelper()
				.actionBarBackground(actionBarBg)
				.headerLayout(R.layout.header_ab_about)
				.contentLayout(R.layout.fragment_about).lightActionBar(true);

		mFadingHelper.initActionBar(activity);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = mFadingHelper.createView(inflater);

		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		TextView blogTitle = (TextView) getView().findViewById(
				R.id.about_fragment_blog_title);
		blogTitle.setText(((MainActivity) getActivity()).getUserBlogTitle());

		TextView blogUrl = (TextView) getView().findViewById(
				R.id.about_fragment_blog_url);
		blogUrl.setText(((MainActivity) getActivity()).getUserWebUrl());
		blogUrl.setTag(blogUrl.getText());
		blogUrl.setOnClickListener(new AboutPageActions(1));

		TextView blogMail = (TextView) getView().findViewById(
				R.id.about_fragment_blog_mail);
		blogMail.setText(((MainActivity) getActivity()).getUserMail());
		blogMail.setTag(blogMail.getText());
		blogMail.setOnClickListener(new AboutPageActions(2));

		TextView blogPhone = (TextView) getView().findViewById(
				R.id.about_fragment_blog_phone);
		blogPhone.setText(((MainActivity) getActivity()).getUserBlogPhone());
		blogPhone.setTag(blogPhone.getText());
		blogPhone.setOnClickListener(new AboutPageActions(3));
		

		ImageView facebookUrl = (ImageView) getView().findViewById(
				R.id.about_fragment_facebook);
		facebookUrl.setTag(AppConstants.FACEBOOK_LINK);
		facebookUrl.setOnClickListener(new AboutPageActions(1));
		
		ImageView twitterUrl = (ImageView) getView().findViewById(
				R.id.about_fragment_twitter);
		twitterUrl.setTag(AppConstants.TWITTER_LINK);
		twitterUrl.setOnClickListener(new AboutPageActions(1));
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		((MainActivity) getActivity()).disableActionBarSlider();
		((MainActivity) getActivity()).hideActionBarIcons();
	}

	/**
	 * The Class AboutPageActions.
	 */
	private class AboutPageActions implements View.OnClickListener {

		/** The Specific case. */
		int mSpecificCase;

		/**
		 * Instantiates a new about page actions.
		 *
		 * @param specCase the spec case
		 */
		public AboutPageActions(int specCase) {
			this.mSpecificCase = specCase;
		}

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			
			String value = (String) v.getTag();
			
			switch (mSpecificCase) {
			case 1:
				openWebSiteWithIntent(value);
				break;
			case 2:
				openMailWithRecipient(value);
				break;
			case 3:
				openDialerWithPhone(value);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Open web site with intent.
	 *
	 * @param url the url
	 */
	private void openWebSiteWithIntent(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	/**
	 * Open mail with recipient.
	 *
	 * @param recipientMail the recipient mail
	 */
	private void openMailWithRecipient(String recipientMail) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		emailIntent.setType("vnd.android.cursor.item/email");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { recipientMail });
		startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
	}

	/**
	 * Open dialer with phone.
	 *
	 * @param phone the phone
	 */
	private void openDialerWithPhone(String phone) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + phone));
		startActivity(intent);
	}
}
