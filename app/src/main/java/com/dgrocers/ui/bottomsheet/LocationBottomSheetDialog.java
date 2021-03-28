package com.dgrocers.ui.bottomsheet;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dgrocers.model.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationBottomSheetDialog extends BaseBottomSheetDialog<String> {

	public static LocationBottomSheetDialog newInstance(List<Location> locationList) {
		LocationBottomSheetDialog mBottomSheet = new LocationBottomSheetDialog();
		Bundle arguments = new Bundle();
		arguments.putParcelableArrayList("locationList", (ArrayList<? extends Parcelable>) locationList);
		mBottomSheet.setArguments(arguments);
		return mBottomSheet;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLocationList = getArguments().getParcelableArrayList("locationList");

		BottomSheetPagerAdapter pagerAdapter = new BottomSheetPagerAdapter(getChildFragmentManager());

		BottomSheetPageFragment subAreaPage = BottomSheetPageFragment.newInstance("Sub Area");
		subAreaPage.setOnItemSelectedCallback(position -> {
			mListener.onItemSelected(createSubAreaText(position));
		});

		BottomSheetPageFragment areaPage = BottomSheetPageFragment.newInstance("Area");
		areaPage.setOnItemSelectedCallback(position -> {
			mCurrentArea = mCurrentLocation.getAreas().get(position);
			if (mCurrentArea.getSubAreas().size() > 0) {
				subAreaPage.setItems(createSubAreaList(mCurrentArea.getSubAreas()));
				setCurrentPage(2);
			} else {
				mListener.onItemSelected(createAreaText());
			}
		});

		BottomSheetPageFragment locationPage = BottomSheetPageFragment.newInstance("Location");
		locationPage.setItems(createLocationList(mLocationList));
		locationPage.setOnItemSelectedCallback(position -> {
			mCurrentLocation = mLocationList.get(position);
			areaPage.setItems(createAreaList(mCurrentLocation.getAreas()));
			setCurrentPage(1);
		});

		pagerAdapter.addFragment(locationPage);
		pagerAdapter.addFragment(areaPage);
		pagerAdapter.addFragment(subAreaPage);

		mBinding.bottomSheetPager.setAdapter(pagerAdapter);
	}

}