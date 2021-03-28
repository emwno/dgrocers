package com.dgrocers.ui.bottomsheet;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dgrocers.R;
import com.dgrocers.databinding.FragmentBottomSheetDialogBinding;
import com.dgrocers.model.Area;
import com.dgrocers.model.ListItem;
import com.dgrocers.model.Location;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static androidx.core.content.res.ResourcesCompat.getDrawable;

public class BaseBottomSheetDialog<T> extends BottomSheetDialogFragment {

	private final Stack<Integer> mBackStack = new Stack<>();

	protected FragmentBottomSheetDialogBinding mBinding;
	protected OnBottomSheetItemSelectedCallback<T> mListener;

	protected List<Location> mLocationList;
	protected Location mCurrentLocation;
	protected Area mCurrentArea;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mBinding = FragmentBottomSheetDialogBinding.inflate(inflater, container, false);
		mListener = (OnBottomSheetItemSelectedCallback<T>) getContext(); // Calling activity implements method
		mBinding.bottomSheetPager.setOffscreenPageLimit(3);
		mBinding.backButton.setOnClickListener(v -> {
			handleBackPress();
		});
		setCurrentPage(0);
		return mBinding.getRoot();
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		return new BottomSheetDialog(getContext(), getTheme()) {
			@Override
			public void onBackPressed() {
				handleBackPress();
			}
		};
	}

	@Override
	public void onDismiss(@NonNull DialogInterface dialog) {
		super.onDismiss(dialog);
		mBinding.bottomSheetPager.setCurrentItem(0);
		mBackStack.clear();
	}

	private void handleBackPress() {
		if (mBackStack.size() > 1) {
			setCurrentPage(mBackStack.pop(), false);
		} else {
			dismiss();
		}
	}

	protected void setCurrentPage(int page) {
		setCurrentPage(page, true);
	}

	protected void setCurrentPage(int page, boolean addToBackStack) {
		if (addToBackStack) mBackStack.push(mBinding.bottomSheetPager.getCurrentItem());
		mBinding.bottomSheetPager.setCurrentItem(page);

		if (page == 0) {
			mBinding.backButton.setImageDrawable(getDrawable(getResources(), R.drawable.ic_close_black, null));
		} else {
			mBinding.backButton.setImageDrawable(getDrawable(getResources(), R.drawable.ic_arrow_left, null));
		}

		switch (page) {
			case 0:
				mBinding.title.setText("Locations");
				break;
			case 1:
				mBinding.title.setText("Areas");
				break;
			case 2:
				mBinding.title.setText("Sub Areas");
				break;
			case 3:
				mBinding.title.setText("Customers");
				break;
		}
	}

	protected List<ListItem> createLocationList(List<Location> locationList) {
		List<ListItem> list = new ArrayList<>();
		for (int i = 0; i < locationList.size(); i++) {
			Location location = locationList.get(i);
			list.add(new ListItem(location.getName(), i));
		}
		return list;
	}

	protected List<ListItem> createAreaList(List<Area> areaList) {
		List<ListItem> list = new ArrayList<>();
		for (int i = 0; i < areaList.size(); i++) {
			Area area = areaList.get(i);
			list.add(new ListItem(area.getName(), i));
		}
		return list;
	}

	protected List<ListItem> createSubAreaList(List<String> subAreaList) {
		List<ListItem> list = new ArrayList<>();
		for (int i = 0; i < subAreaList.size(); i++) {
			list.add(new ListItem(subAreaList.get(i), i));
		}
		return list;
	}

	protected String createAreaText() {
		return mCurrentArea.getName() + ", " + mCurrentLocation.getName();
	}

	protected String createSubAreaText(int position) {
		return mCurrentArea.getSubAreas().get(position) + ", " + createAreaText();
	}

	public interface OnBottomSheetItemSelectedCallback<T> {
		void onItemSelected(T item);
	}

}
