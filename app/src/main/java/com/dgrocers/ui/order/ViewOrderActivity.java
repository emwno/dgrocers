package com.dgrocers.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.dgrocers.R;
import com.dgrocers.databinding.ActivityViewOrderBinding;
import com.dgrocers.firebase.AccountManager;
import com.dgrocers.model.Order;
import com.dgrocers.model.OrderTrackItem;
import com.dgrocers.services.OrderService;
import com.dgrocers.ui.base.BaseActivity;
import com.dgrocers.util.Utils;
import com.google.android.material.snackbar.Snackbar;

import static com.dgrocers.firebase.FirebaseConstants.ORDER_PAYMENT_STATUS_PENDING;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_CANCELLED;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_DELIVERED;
import static com.dgrocers.util.Constants.NOTIFY_ORDER_CANCELLED;
import static com.dgrocers.util.Constants.getPaymentStatusText;
import static com.dgrocers.util.Constants.getStatusText;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

public class ViewOrderActivity extends BaseActivity {

	private ActivityViewOrderBinding mBinding;
	private MenuItem mEditMenuItem;
	private MenuItem mCancelEditMenuItem;
	private MenuItem mSaveMenuItem;
	private MenuItem mShareMenuItem;

	private Order mOrder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = ActivityViewOrderBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());

		mOrder = getIntent().getParcelableExtra("order");

		mBinding.voPaymentStatus.setText(getPaymentStatusText(getResources(), mOrder.getPaymentStatus()));
		mBinding.voPaymentStatus.setTextColor(getResources().getColor(
				mOrder.getPaymentStatus() == ORDER_PAYMENT_STATUS_PENDING ? R.color.yellow : R.color.green, null));
		mBinding.voAddress.setText(String.format("%s\n%s\n%s", mOrder.getCustomer().getAddress(),
				mOrder.getCustomer().getArea(), mOrder.getCustomer().getLocation()));
		mBinding.voNotes.setText(mOrder.getNotes());
		mBinding.voItems.setText(mOrder.getItems());

		for (String phone : mOrder.getCustomer().getPhoneNumbers()) {
			View phoneView = getLayoutInflater().inflate(R.layout.row_customer_view_phone, null);
			Button phoneButton = phoneView.findViewById(R.id.row_phone);

			phoneButton.setText(phone);
			phoneButton.setOnClickListener(v -> Utils.dialPhone(this, phone));

			mBinding.voPhoneNumberContainer.addView(phoneView);
		}

		for (int i = mOrder.getTrackingHistory().size() - 1; i >= 0; i--) {
			addTrackingView(mOrder.getTrackingHistory().get(i), false);
		}

		// Cannot cancel an order that has CANCELLED status or has been DELIVERED
		if (mOrder.getCurrentStatus().getStatus() == ORDER_STATUS_CANCELLED
				|| mOrder.getCurrentStatus().getStatus() == ORDER_STATUS_DELIVERED) {
			mBinding.voCancelBtn.setVisibility(View.GONE);
		} else {
			mBinding.voCancelBtn.setOnClickListener(v -> showCancelDialog());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_order_menu, menu);
		mEditMenuItem = menu.findItem(R.id.menu_view_order_edit);
		mCancelEditMenuItem = menu.findItem(R.id.menu_view_order_cancel_edit);
		mSaveMenuItem = menu.findItem(R.id.menu_view_order_save);
		mShareMenuItem = menu.findItem(R.id.menu_view_order_share);

		if (mOrder.getCurrentStatus().getStatus() == ORDER_STATUS_CANCELLED
				|| mOrder.getCurrentStatus().getStatus() == ORDER_STATUS_DELIVERED) {
			mEditMenuItem.setVisible(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == R.id.menu_view_order_edit) {
			setEditMode();
			return true;
		} else if (itemId == R.id.menu_view_order_cancel_edit) {
			cancelEditMode();
			return true;
		} else if (itemId == R.id.menu_view_order_save) {
			setSaveMode();
			return true;
		} else if (itemId == R.id.menu_view_order_share) {
			shareToWhatsApp();
		}

		return super.onOptionsItemSelected(item);
	}

	private void setEditMode() {
		getSupportActionBar().setTitle(R.string.title_activity_edit_order);
		mEditMenuItem.setVisible(false);
		mShareMenuItem.setVisible(false);
		mCancelEditMenuItem.setVisible(true);
		mSaveMenuItem.setVisible(true);

		mBinding.voNotes.setEnabled(true);
		mBinding.voItems.setEnabled(true);
		mBinding.voNotes.requestFocus();
	}

	private void cancelEditMode() {
		getSupportActionBar().setTitle(R.string.title_activity_view_order);
		mEditMenuItem.setVisible(true);
		mShareMenuItem.setVisible(true);
		mCancelEditMenuItem.setVisible(false);
		mSaveMenuItem.setVisible(false);
		mBinding.voNotes.setEnabled(false);
		mBinding.voItems.setEnabled(false);
	}

	private void setSaveMode() {
		cancelEditMode();

		mOrder.setNotes(mBinding.voNotes.getText().toString().trim());
		mOrder.setItems(mBinding.voItems.getText().toString().trim());
		mOrder.addToTracking("Order Modified", AccountManager.getInstance().getAdminName());
		OrderService.getInstance().updateOrder(mOrder, updated -> {
			if (updated) {
				addTrackingView(mOrder.getTrackingHistory().get(mOrder.getTrackingHistory().size() - 1), true);
				Snackbar.make(mBinding.getRoot(), "Order successfully updated", LENGTH_SHORT).show();
			}
		});
	}

	private void addTrackingView(OrderTrackItem trackItem, boolean addToTop) {
		View statusView = getLayoutInflater().inflate(R.layout.row_order_tracking_history, null);
		((TextView) statusView.findViewById(R.id.row_status_name)).setText(trackItem.getText());
		((TextView) statusView.findViewById(R.id.row_status_timestamp)).setText(trackItem.getTimestamp().toDate().toString());
		if (addToTop) {
			mBinding.voTrackingContainer.addView(statusView, 1);
		} else {
			mBinding.voTrackingContainer.addView(statusView);
		}
	}

	private void showCancelDialog() {
		new AlertDialog.Builder(this)
				.setMessage("Are you sure you want to cancel this order?")
				.setPositiveButton("Yes", (dialog, whichButton) -> {
					mOrder.setCurrentStatus(ORDER_STATUS_CANCELLED,
							AccountManager.getInstance().getAdminName(),
							getStatusText(getResources(), ORDER_STATUS_CANCELLED));

					OrderService.getInstance().updateOrder(mOrder, updated -> {
						if (updated) {
							addTrackingView(mOrder.getTrackingHistory().get(mOrder.getTrackingHistory().size() - 1), true);
							Snackbar.make(mBinding.getRoot(), "Order successfully cancelled", LENGTH_SHORT).show();
							setResult(NOTIFY_ORDER_CANCELLED);
						}
					});
				})
				.setNegativeButton("No", null).show();
	}

	private void shareToWhatsApp() {
		String order = mOrder.getCustomer().getAddress() + "\n" +
				mOrder.getCustomer().getArea() + ", " + mOrder.getCustomer().getLocation() +
				"\n\nOrder:\n" + mOrder.getItems();

		Log.e("king", order);
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.setPackage("com.whatsapp");
		intent.putExtra(Intent.EXTRA_TEXT, order);
		try {
			startActivity(intent);
		} catch (android.content.ActivityNotFoundException ex) {
			Snackbar.make(mBinding.getRoot(), "WhatsApp not installed.", LENGTH_SHORT).show();
		}
	}

}