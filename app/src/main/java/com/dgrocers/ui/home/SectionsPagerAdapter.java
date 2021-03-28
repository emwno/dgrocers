package com.dgrocers.ui.home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dgrocers.R;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

	private final List<Fragment> mFragmentList = new ArrayList<>();

	@StringRes
	private static final int[] TAB_TITLES = new int[]{R.string.home_tab_new, R.string.home_tab_in_process, R.string.home_tab_out_for_delivery, R.string.home_tab_completed};
	private final Context mContext;

	public SectionsPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;
	}

	public void addFragment(Fragment fragment) {
		mFragmentList.add(fragment);
	}

	@NonNull
	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return mContext.getResources().getString(TAB_TITLES[position]);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

}