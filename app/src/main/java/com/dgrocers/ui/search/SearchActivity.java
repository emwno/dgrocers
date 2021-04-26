package com.dgrocers.ui.search;

import android.content.Intent;
import android.os.Bundle;

import com.dgrocers.databinding.ActivitySearchBinding;
import com.dgrocers.firebase.FirebaseManager;
import com.dgrocers.model.Customer;
import com.dgrocers.services.CustomerService;
import com.dgrocers.ui.base.BaseActivity;
import com.dgrocers.ui.bottomsheet.BaseBottomSheetDialog;
import com.dgrocers.ui.bottomsheet.CustomerBottomSheetDialog;
import com.dgrocers.ui.customer.ViewCustomerActivity;
import com.dgrocers.util.Utils;

public class SearchActivity extends BaseActivity implements BaseBottomSheetDialog.OnBottomSheetItemSelectedCallback<Customer> {

	private ActivitySearchBinding mBinding;
	private CustomerBottomSheetDialog mAddressBottomSheet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = ActivitySearchBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());

		mBinding.searchByAddress.setOnClickListener(v -> mAddressBottomSheet.show(getSupportFragmentManager(), "sba"));

		FirebaseManager.getInstance().fetchLocations(locationList -> {
			CustomerService.getInstance().getAllCustomers(customerList -> {
				mAddressBottomSheet = CustomerBottomSheetDialog.newInstance(locationList, customerList);
				mBinding.searchByAddress.setEnabled(true);
			}, error -> {
				Utils.showError(mBinding.getRoot(), "Failed to load customers.");
			});
		});
	}

	@Override
	public void onItemSelected(Customer customer) {
		mAddressBottomSheet.dismiss();
		Intent intent = new Intent(this, ViewCustomerActivity.class);
		intent.putExtra("customerId", customer.getObjectId());
		startActivity(intent);
	}

}