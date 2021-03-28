package com.dgrocers.ui.bottomsheet;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dgrocers.model.Customer;
import com.dgrocers.model.ListItem;
import com.dgrocers.model.Location;

import java.util.ArrayList;
import java.util.List;

public class CustomerBottomSheetDialog extends BaseBottomSheetDialog<Customer> {

	private List<Customer> mCustomerList;

	public static CustomerBottomSheetDialog newInstance(List<Location> locationList, List<Customer> customerList) {
		CustomerBottomSheetDialog mBottomSheet = new CustomerBottomSheetDialog();
		Bundle arguments = new Bundle();
		arguments.putParcelableArrayList("locationList", (ArrayList<? extends Parcelable>) locationList);
		arguments.putParcelableArrayList("customerList", (ArrayList<? extends Parcelable>) customerList);
		mBottomSheet.setArguments(arguments);
		return mBottomSheet;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLocationList = getArguments().getParcelableArrayList("locationList");
		mCustomerList = getArguments().getParcelableArrayList("customerList");

		BottomSheetPagerAdapter pagerAdapter = new BottomSheetPagerAdapter(getChildFragmentManager());

		BottomSheetPageFragment customerPage = BottomSheetPageFragment.newInstance("Customer");
		customerPage.setOnItemSelectedCallback(position -> mListener.onItemSelected(mCustomerList.get(position)));

		BottomSheetPageFragment subAreaPage = BottomSheetPageFragment.newInstance("Sub Area");
		subAreaPage.setOnItemSelectedCallback(position -> {
			customerPage.setItems(
					createCustomerList(
							mCurrentLocation.getName(),
							mCurrentArea.getSubAreas().get(position) + ", " + mCurrentArea.getName()));
			setCurrentPage(3);
		});

		BottomSheetPageFragment areaPage = BottomSheetPageFragment.newInstance("Area");
		areaPage.setOnItemSelectedCallback(position -> {
			mCurrentArea = mCurrentLocation.getAreas().get(position);
			if (mCurrentArea.getSubAreas().size() > 0) {
				subAreaPage.setItems(createSubAreaList(mCurrentArea.getSubAreas()));
				setCurrentPage(2);
			} else {
				customerPage.setItems(
						createCustomerList(
								mCurrentLocation.getName(),
								mCurrentArea.getName()));
				setCurrentPage(3);
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
		pagerAdapter.addFragment(customerPage);

		mBinding.bottomSheetPager.setAdapter(pagerAdapter);
	}

	private List<ListItem> createCustomerList(String location, String area) {
		List<ListItem> list = new ArrayList<>();
		for (int i = 0; i < mCustomerList.size(); i++) {
			Customer customer = mCustomerList.get(i);
			if (customer.getLocation().equals(location) && customer.getArea().equals(area))
				list.add(new ListItem(customer.getAddress(), i));
		}
		return list;
	}

}