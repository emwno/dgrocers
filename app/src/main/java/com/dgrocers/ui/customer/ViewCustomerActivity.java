package com.dgrocers.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.dgrocers.R;
import com.dgrocers.databinding.ActivityViewCustomerBinding;
import com.dgrocers.model.Customer;
import com.dgrocers.services.CustomerService;
import com.dgrocers.ui.base.BaseActivity;
import com.dgrocers.util.Utils;

import java.util.List;

import static com.dgrocers.util.Constants.REQUEST_EDIT_CUSTOMER;
import static com.dgrocers.util.Constants.RESULT_SUCCESS;

public class ViewCustomerActivity extends BaseActivity {

	private ActivityViewCustomerBinding mBinding;

	private Customer mCustomer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = ActivityViewCustomerBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());

		String customerId = getIntent().getStringExtra("customerId");

		CustomerService.getInstance().getCustomer(customerId,
				customer -> {
					mCustomer = customer;
					updateUI();
				}, error -> {
					Utils.showError(mBinding.getRoot(), "Failed to get customers.");
				}
		);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_customer_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_view_customer_edit && mCustomer != null) {
			Intent intent = new Intent(this, CreateCustomerActivity.class);
			intent.putExtra("customer", mCustomer);
			startActivityForResult(intent, REQUEST_EDIT_CUSTOMER);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Customer edited
		if (requestCode == REQUEST_EDIT_CUSTOMER && resultCode == RESULT_SUCCESS) {
			Utils.showError(mBinding.getRoot(), "Customer Updated");
			mCustomer = data.getParcelableExtra("customer");
			updateUI();
		}
	}

	private void updateUI() {
		mBinding.address.setText(mCustomer.getAddress());
		mBinding.location.setText(String.format("%s, %s", mCustomer.getArea(), mCustomer.getLocation()));
		mBinding.phoneNumberContainer.removeAllViews();
		addPhoneNumberUI(mCustomer.getPhoneNumbers());
	}

	private void addPhoneNumberUI(List<String> phoneNumbers) {
		for (String phone : phoneNumbers) {
			View phoneView = getLayoutInflater().inflate(R.layout.row_customer_view_phone, null);
			Button phoneButton = phoneView.findViewById(R.id.row_phone);
			phoneButton.setText(phone);
			phoneButton.setOnClickListener(v -> Utils.dialPhone(this, phone));
			mBinding.phoneNumberContainer.addView(phoneView);
		}
	}

}