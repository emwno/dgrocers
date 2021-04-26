package com.dgrocers.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dgrocers.R;
import com.dgrocers.databinding.ActivityCreateCustomerBinding;
import com.dgrocers.firebase.FirebaseManager;
import com.dgrocers.model.Customer;
import com.dgrocers.services.CustomerService;
import com.dgrocers.ui.base.BaseActivity;
import com.dgrocers.ui.bottomsheet.BaseBottomSheetDialog.OnBottomSheetItemSelectedCallback;
import com.dgrocers.ui.bottomsheet.LocationBottomSheetDialog;
import com.dgrocers.ui.view.ElasticTapAnimator;
import com.dgrocers.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.dgrocers.util.Constants.RESULT_SUCCESS;

public class CreateCustomerActivity extends BaseActivity implements OnBottomSheetItemSelectedCallback<String> {

	List<EditText> mPhoneEditTextList = new ArrayList<>();

	private ActivityCreateCustomerBinding mBinding;
	private LocationBottomSheetDialog mLocationBottomSheet;

	private String mLocationText;
	private String mAreaText;

	private String mEditCustomerId; // Only used to in edit-mode

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = ActivityCreateCustomerBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());

		addPhoneNumberField(true);
		mBinding.ccSelectLocationBtn.setOnClickListener(v -> mLocationBottomSheet.show(getSupportFragmentManager(), "cca"));
		mBinding.ccAddPhoneNumberBtn.setOnClickListener(v -> addPhoneNumberField(false));
		mBinding.ccCreateCustomerBtn.setOnClickListener(v -> createCustomer());

		ElasticTapAnimator.animate(mBinding.ccSelectLocationBtn);
		ElasticTapAnimator.animate(mBinding.ccCreateCustomerBtn);

		FirebaseManager.getInstance().fetchLocations(locationList -> {
			mLocationBottomSheet = LocationBottomSheetDialog.newInstance(locationList);
			mBinding.ccSelectLocationBtn.setEnabled(true);
		});

		Customer editCustomer = getIntent().getParcelableExtra("customer");
		if (editCustomer != null) {
			mEditCustomerId = editCustomer.getObjectId();
			setupEditUI(editCustomer);
		}
	}

	@Override
	public void onItemSelected(String locationText) {
		mAreaText = locationText.substring(0, locationText.lastIndexOf(","));
		mLocationText = locationText.substring(locationText.lastIndexOf(",") + 2);
		mBinding.ccLocation.setText(locationText);
		mLocationBottomSheet.dismiss();
	}

	// Only used in edit-mode
	private void setupEditUI(Customer editCustomer) {
		getSupportActionBar().setTitle("Edit Customer");
		mBinding.ccCreateCustomerBtn.setText("Save Customer");
		mBinding.ccCreateCustomerBtn.setOnClickListener(v -> saveCustomer());

		mAreaText = editCustomer.getArea();
		mLocationText = editCustomer.getLocation();

		mBinding.ccAddress.setText(editCustomer.getAddress());
		mBinding.ccLocation.setText(String.format("%s, %s", mAreaText, mLocationText));
		mPhoneEditTextList.get(0).setText(editCustomer.getPhoneNumbers().get(0));

		for (int i = 1; i < editCustomer.getPhoneNumbers().size(); i++) {
			addPhoneNumberField(false);
			mPhoneEditTextList.get(i).setText(editCustomer.getPhoneNumbers().get(i));
		}
	}

	private void addPhoneNumberField(boolean first) {
		View phoneView = getLayoutInflater().inflate(R.layout.row_customer_add_phone, null);
		mBinding.ccPhoneNumberContainer.addView(phoneView);
		mPhoneEditTextList.add(phoneView.findViewById(R.id.row_phone));
		if (first)
			phoneView.findViewById(R.id.row_remove_phone).setVisibility(View.GONE);
		else
			phoneView.findViewById(R.id.row_remove_phone).setOnClickListener(v -> {
				mBinding.ccPhoneNumberContainer.removeView(phoneView);
				mPhoneEditTextList.remove(phoneView.findViewById(R.id.row_phone));
			});
	}

	private void createCustomer() {
		if (checkErrors()) {
			mBinding.getRoot().scrollTo(0, 0);
			return;
		}

		hideKeyboard();
		mBinding.ccCreateCustomerBtn.startAnimation();

		Customer newCustomer = new Customer();
		newCustomer.setAddress(mBinding.ccAddress.getText().toString().trim());
		newCustomer.setLocation(mLocationText);
		newCustomer.setArea(mAreaText);

		for (EditText editText : mPhoneEditTextList) {
			newCustomer.addPhoneNumber(editText.getText().toString().trim());
		}

		CustomerService.getInstance().createCustomer(newCustomer,
				newCustomerId -> {
					newCustomer.setObjectId(newCustomerId);
					Intent intent = getIntent();
					intent.putExtra("customer", newCustomer);
					setResult(RESULT_SUCCESS, intent);
					finish();
				},
				error -> {
					Utils.showError(mBinding.getRoot(), "Failed to create customer.");
					mBinding.ccCreateCustomerBtn.revertAnimation();
				});
	}

	private void saveCustomer() {
		if (checkErrors()) {
			mBinding.getRoot().scrollTo(0, 0);
			return;
		}

		hideKeyboard();
		mBinding.ccCreateCustomerBtn.startAnimation();

		Customer updatedCustomer = new Customer();
		updatedCustomer.setObjectId(mEditCustomerId);
		updatedCustomer.setAddress(mBinding.ccAddress.getText().toString().trim());
		updatedCustomer.setLocation(mLocationText);
		updatedCustomer.setArea(mAreaText);

		for (EditText editText : mPhoneEditTextList) {
			updatedCustomer.addPhoneNumber(editText.getText().toString().trim());
		}

		CustomerService.getInstance().updateCustomer(updatedCustomer, updated -> {
			if (updated) {
				Intent intent = new Intent(this, CreateCustomerActivity.class);
				intent.putExtra("customer", updatedCustomer);
				setResult(RESULT_SUCCESS, intent);
				finish();
			} else {
				Utils.showError(mBinding.getRoot(), "Failed to update customer.");
				mBinding.ccCreateCustomerBtn.revertAnimation();
			}
		});
	}

	private boolean checkErrors() {
		boolean error = false;

		if (mBinding.ccAddress.getText().toString().trim().length() == 0) {
			mBinding.ccAddress.setError("Enter an address");
			error = true;
		}

		if (mBinding.ccLocation.getText().toString().trim().length() == 0) {
			mBinding.ccLocation.setError("Select an area");
			error = true;
		}

		for (EditText editText : mPhoneEditTextList) {
			int length = editText.getText().toString().trim().length();
			if (length == 0) {
				editText.setError("Enter a phone number");
				error = true;
			} else if (length < 11) {
				editText.setError("Enter a valid phone number");
				error = true;
			}
		}

		return error;
	}

	private void hideKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

}