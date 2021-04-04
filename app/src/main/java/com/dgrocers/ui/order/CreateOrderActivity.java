package com.dgrocers.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dgrocers.R;
import com.dgrocers.databinding.ActivityCreateOrderBinding;
import com.dgrocers.firebase.AccountManager;
import com.dgrocers.firebase.FirebaseManager;
import com.dgrocers.model.Customer;
import com.dgrocers.model.CustomerProxy;
import com.dgrocers.model.Location;
import com.dgrocers.model.Order;
import com.dgrocers.services.OrderService;
import com.dgrocers.ui.base.BaseActivity;
import com.dgrocers.ui.bottomsheet.BaseBottomSheetDialog.OnBottomSheetItemSelectedCallback;
import com.dgrocers.ui.bottomsheet.CustomerBottomSheetDialog;
import com.dgrocers.ui.customer.CreateCustomerActivity;
import com.dgrocers.ui.view.ElasticTapAnimator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;

import java.util.List;

import static com.dgrocers.firebase.FirebaseConstants.ORDER_PAYMENT_STATUS_PENDING;
import static com.dgrocers.util.Constants.REQUEST_CREATE_CUSTOMER;
import static com.dgrocers.util.Constants.RESULT_SUCCESS;
import static com.dgrocers.util.Constants.getStatusText;

public class CreateOrderActivity extends BaseActivity implements OnBottomSheetItemSelectedCallback<Customer> {

	private final int[] mStatusIDs = {R.id.co_status_group_new, R.id.co_status_group_processing, R.id.co_status_group_out, R.id.co_status_group_delivered};

	private ActivityCreateOrderBinding mBinding;
	private CustomerBottomSheetDialog mCustomerBottomSheet;

	private int mSelectedStatus;
	private Customer mSelectedCustomer;
	private List<Location> mLocationList;
	private List<Customer> mCustomerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = ActivityCreateOrderBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());

		mBinding.coSelectCustomerBtn.setOnClickListener(v -> mCustomerBottomSheet.show(getSupportFragmentManager(), "coa"));
		mBinding.coCreateCustomerBtn.setOnClickListener(v -> {
			Intent intent = new Intent(CreateOrderActivity.this, CreateCustomerActivity.class);
			startActivityForResult(intent, REQUEST_CREATE_CUSTOMER);
		});

		mBinding.coCreateOrderBtn.setOnClickListener(v -> createOrder());
		ElasticTapAnimator.animate(mBinding.coSelectCustomerBtn);
		ElasticTapAnimator.animate(mBinding.coCreateOrderBtn);

		setStatusSelected(mStatusIDs[0]);
		mBinding.coStatusGroupNew.setOnClickListener(v -> setStatusSelected(v.getId()));
		mBinding.coStatusGroupProcessing.setOnClickListener(v -> setStatusSelected(v.getId()));
		mBinding.coStatusGroupOut.setOnClickListener(v -> setStatusSelected(v.getId()));
		mBinding.coStatusGroupDelivered.setOnClickListener(v -> setStatusSelected(v.getId()));

		FirebaseManager.getInstance().fetchLocations(locationList -> {
			mLocationList = locationList;
			FirebaseManager.getInstance().fetchCustomers(customerList -> {
				mCustomerList = customerList;
				mCustomerBottomSheet = CustomerBottomSheetDialog.newInstance(locationList, customerList);
				mBinding.coSelectCustomerBtn.setEnabled(true);
			});
		});
	}

	@Override
	public void onItemSelected(Customer customer) {
		mSelectedCustomer = customer;
		mBinding.coCustomer.setText(createCustomerText());
		mCustomerBottomSheet.dismiss();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CREATE_CUSTOMER && resultCode == RESULT_SUCCESS) {
			mSelectedCustomer = data.getParcelableExtra("customer");
			mCustomerList.add(mSelectedCustomer);
			String id = mSelectedCustomer.getAddress() + "\n" + mSelectedCustomer.getArea();
			mBinding.coCustomer.setText(id);
			Snackbar.make(mBinding.getRoot(), "Customer successfully created", BaseTransientBottomBar.LENGTH_SHORT).show();
			mCustomerBottomSheet = CustomerBottomSheetDialog.newInstance(mLocationList, mCustomerList);
		}
	}

	private void createOrder() {
		if (checkErrors()) {
			mBinding.getRoot().scrollTo(0, 0);
			return;
		}

		hideKeyboard();
		mBinding.coCreateOrderBtn.startAnimation();

		Order newOrder = new Order();
		newOrder.setCreatedAt(Timestamp.now());
		newOrder.setCustomer(new CustomerProxy(mSelectedCustomer));
		newOrder.setNotes(mBinding.coNotes.getText().toString().trim());
		newOrder.setItems(mBinding.coItems.getText().toString().trim());
		newOrder.setCurrentStatus(mSelectedStatus, AccountManager.getInstance().getAdminName(),
				getStatusText(getResources(), mSelectedStatus));
		newOrder.setPaymentStatus(ORDER_PAYMENT_STATUS_PENDING);

		OrderService.getInstance().createOrder(newOrder,
				newOrderId -> {
					// Update customer order list to reflect new order
					mSelectedCustomer.addOrder(newOrderId, newOrder.getCreatedAt());
					FirebaseManager.getInstance().updateCustomer(mSelectedCustomer, updated -> {
						setResult(RESULT_SUCCESS, getIntent());
						finish();
					});
				},
				error -> {
					Snackbar.make(mBinding.getRoot(), "Failed to create order.\n" + error, BaseTransientBottomBar.LENGTH_SHORT).show();
					mBinding.coCreateOrderBtn.revertAnimation();
				});
	}

	private String createCustomerText() {
		return mSelectedCustomer.getAddress() + "\n" + mSelectedCustomer.getArea() + ", " + mSelectedCustomer.getLocation();
	}

	private boolean checkErrors() {
		boolean error = false;

		if (mBinding.coCustomer.getText().toString().trim().length() == 0) {
			mBinding.coCustomer.setError("Select a customer");
			error = true;
		}

		if (mBinding.coItems.getText().toString().trim().length() == 0) {
			mBinding.coItems.setError("Enter order items");
			error = true;
		}

		return error;
	}

	private void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}

	private void setStatusSelected(int selectedId) {
		for (int i = 0; i < mStatusIDs.length; i++) {
			int id = mStatusIDs[i];
			if (selectedId == id) {
				mSelectedStatus = i; // Matches FirebaseConstants
				mBinding.getRoot().findViewById(id).setBackgroundColor(getColor(R.color.teal_700));
				((TextView) mBinding.getRoot().findViewById(id)).setTextColor(getColor(R.color.white));
			} else {
				mBinding.getRoot().findViewById(id).setBackgroundColor(getColor(R.color.white));
				((TextView) mBinding.getRoot().findViewById(id)).setTextColor(getColor(R.color.darker_gray));
			}
		}
	}

}
