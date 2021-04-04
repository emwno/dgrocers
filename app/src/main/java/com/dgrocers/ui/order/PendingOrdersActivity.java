package com.dgrocers.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.dgrocers.databinding.ActivityPendingOrdersBinding;
import com.dgrocers.firebase.OnRequestFailureListener;
import com.dgrocers.firebase.OnRequestSuccessListener;
import com.dgrocers.model.Order;
import com.dgrocers.services.OrderService;
import com.dgrocers.ui.base.BaseActivity;
import com.dgrocers.ui.home.tabs.OrderListAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static com.dgrocers.firebase.FirebaseConstants.ORDER_PAYMENT_STATUS_PAID;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

public class PendingOrdersActivity extends BaseActivity implements OnRequestSuccessListener<List<Order>>, OnRequestFailureListener, OrderListAdapter.OnOrderSelectedListener {

	private OrderListAdapter mAdapter;
	private ActivityPendingOrdersBinding mBinding;
	private List<Order> mPendingOrderList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = ActivityPendingOrdersBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());

		mAdapter = new OrderListAdapter(this);
		mBinding.list.setLayoutManager(new LinearLayoutManager(this));
		mBinding.list.setAdapter(mAdapter);

		OrderService.getInstance().getAllPendingOrders(this, this);
	}

	@Override
	public void onSuccess(List<Order> orderList) {
		mPendingOrderList = orderList;
		mBinding.loading.setVisibility(View.GONE);

		if (orderList.size() > 0) {
			mAdapter.setItems(orderList);
			mAdapter.notifyItemRangeChanged(0, orderList.size());
		} else {
			mBinding.statusText.setVisibility(View.VISIBLE);
			mBinding.statusText.setText("No Orders");
		}
	}

	@Override
	public void onFailure(String error) {
		mBinding.loading.setVisibility(View.GONE);
		mBinding.statusText.setVisibility(View.VISIBLE);
		mBinding.statusText.setText("Failed to get orders...\n" + error);
	}

	@Override
	public void onOrderSelected(Order order) {
		Intent intent = new Intent(this, ViewOrderActivity.class);
		intent.putExtra("order", order);
		startActivity(intent);
	}

	@Override
	public void onOrderLongPressed(Order order, int position) {
		order.setPaymentStatus(ORDER_PAYMENT_STATUS_PAID);
		OrderService.getInstance().updateOrder(order, result -> {
			mPendingOrderList.remove(position);
			mAdapter.notifyItemRemoved(position);
			Snackbar.make(mBinding.getRoot(), "Order marked as paid", LENGTH_SHORT).show();
		});
	}

}