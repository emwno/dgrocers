package com.dgrocers.ui.bottomsheet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetPagerAdapter extends FragmentPagerAdapter {

	private final List<Fragment> mFragmentList = new ArrayList<>();

	public BottomSheetPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public void addFragment(Fragment fragment) {
		mFragmentList.add(fragment);
	}

	@NonNull
	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

}