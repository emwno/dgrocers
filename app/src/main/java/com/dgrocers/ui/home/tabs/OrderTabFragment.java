package com.dgrocers.ui.home.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dgrocers.databinding.FragmentHomeOrderTabBinding;
import com.dgrocers.model.Order;
import com.dgrocers.ui.home.HomeContract;
import com.dgrocers.ui.home.tabs.OrderTabFragment.OrderActionListener.OrderAction;
import com.dgrocers.ui.order.ViewOrderActivity;

import java.util.List;

public class OrderTabFragment extends Fragment implements HomeContract.Tab, OrderListAdapter.OnOrderSelectedListener {

	private OrderAction mNextOrderActionType;
	private OrderActionListener mListener;
	private OrderListAdapter mAdapter;
	private FragmentHomeOrderTabBinding mBinding;

	public void setArgs(OrderActionListener mListener, OrderAction nextOrderActionType) {
		this.mListener = mListener;
		this.mNextOrderActionType = nextOrderActionType;
		this.mAdapter = new OrderListAdapter(this);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mBinding = FragmentHomeOrderTabBinding.inflate(inflater, container, false);
		mBinding.list.setLayoutManager(new LinearLayoutManager(getContext()));
		mBinding.list.setAdapter(mAdapter);
		return mBinding.getRoot();
	}

	@Override
	public void onOrdersLoaded(List<Order> orderList) {
		if (!isAdded()) return;

		int oldSize = mAdapter.getItemCount();
		int newSize = orderList.size();
		mAdapter.setItems(orderList);
		mAdapter.notifyItemRangeChanged(0, Math.max(oldSize, newSize));
		handleEmptyView(newSize == 0);
	}

	@Override
	public void onOrderAdded(int position) {
		if (!isAdded()) return;

		mAdapter.notifyItemInserted(position);
		handleEmptyView(false);
	}

	@Override
	public void onOrderRemoved(int position) {
		if (!isAdded()) return;

		mAdapter.notifyItemRemoved(position);
		handleEmptyView(mAdapter.getItemCount() == 0);
	}

	@Override
	public void onOrderSelected(Order order) {
		Intent intent = new Intent(getContext(), ViewOrderActivity.class);
		intent.putExtra("order", order);
		startActivity(intent);
	}

	@Override
	public void onOrderLongPressed(Order order, int position) {
		mListener.onOrderAction(order, position, mNextOrderActionType);
	}

	private void handleEmptyView(boolean showEmpty) {
		if (showEmpty) {
			mBinding.emptyText.setVisibility(View.VISIBLE);
		} else {
			mBinding.emptyText.setVisibility(View.GONE);
		}
	}

	public interface OrderActionListener {

		void onOrderAction(Order order, int position, OrderAction action);

		enum OrderAction {
			MARK_PROCESSING,
			MARK_OUT_FOR_DELIVERY,
			MARK_DELIVERED,
			MARK_NOOP
		}
	}

}