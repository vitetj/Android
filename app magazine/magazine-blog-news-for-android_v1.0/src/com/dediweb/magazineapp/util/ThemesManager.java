package com.dmbteam.magazineapp.util;

import com.dmbteam.magazineapp.R;
import com.dmbteam.magazineapp.settings.AppConstants;
import com.dmbteam.magazineapp.settings.ThemeEnum;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;

/**
 * The Class ThemesManager.
 */
public class ThemesManager {
	
	/**
	 * Sets the correct theme.
	 *
	 * @param context the new correct theme
	 */
	public static void setCorrectTheme(Context context) {

		if (AppConstants.APP_THEME == ThemeEnum.BLUE) {
			context.setTheme(R.style.blue);
		}else if(AppConstants.APP_THEME == ThemeEnum.GREEN){
			context.setTheme(R.style.green);
		}else if(AppConstants.APP_THEME == ThemeEnum.PINK){
			context.setTheme(R.style.pink);
		}else if(AppConstants.APP_THEME == ThemeEnum.DARK){
			context.setTheme(R.style.dark);
		}else if(AppConstants.APP_THEME == ThemeEnum.RED){
			context.setTheme(R.style.red);
		}else if(AppConstants.APP_THEME == ThemeEnum.ORANGE){
			context.setTheme(R.style.orange);
		}
	}
	
	/**
	 * Gets the id for specific attribute.
	 *
	 * @param context the context
	 * @param attrId the attr id
	 * @return the id for specific attribute
	 */
	public static int getIdForSpecificAttribute(Context context, int attrId) {
		TypedArray themesArray = context.getTheme().obtainStyledAttributes(
				getThemeName(context), new int[] { attrId });
		return themesArray.getResourceId(0, 0);
	}

	/**
	 * Gets the theme name.
	 *
	 * @param context the context
	 * @return the theme name
	 */
	public static int getThemeName(Context context) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			int themeResId = packageInfo.applicationInfo.theme;

			return themeResId;
			// return context.getResources().getResourceEntryName(themeResId);
		} catch (NameNotFoundException e) {
			return -1;
		}
	}
}
