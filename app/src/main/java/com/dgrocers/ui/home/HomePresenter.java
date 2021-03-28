package com.dgrocers.ui.home;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.dgrocers.BuildConfig;
import com.dgrocers.firebase.AccountManager;
import com.dgrocers.firebase.FirebaseManager;
import com.dgrocers.model.AppConfig;
import com.dgrocers.model.Order;
import com.dgrocers.ui.home.HomeContract.Tab;
import com.dgrocers.ui.home.HomeContract.View;
import com.dgrocers.ui.home.tabs.OrderTabFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static com.dgrocers.firebase.FirebaseConstants.ORDER_PAYMENT_STATUS_PAID;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_PAYMENT_STATUS_PENDING;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_CANCELLED;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_DELIVERED;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_NEW;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_OUT_FOR_DELIVERY;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_PROCESSING;
import static com.dgrocers.ui.home.tabs.OrderTabFragment.OrderActionListener.OrderAction.MARK_DELIVERED;
import static com.dgrocers.ui.home.tabs.OrderTabFragment.OrderActionListener.OrderAction.MARK_NOOP;
import static com.dgrocers.util.Constants.getActionText;
import static com.dgrocers.util.Constants.getStatusText;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

public class HomePresenter implements HomeContract.Presenter, OrderTabFragment.OrderActionListener {

	private final Context mContext;
	private final View mView;

	private List<Order> mNewOrderList = new ArrayList<>();
	private List<Order> mProcessingOrderList = new ArrayList<>();
	private List<Order> mOutForDeliveryOrderList = new ArrayList<>();
	private List<Order> mDeliveredOrderList = new ArrayList<>();

	private Tab mNewOrderTab;
	private Tab mProcessingTab;
	private Tab mOutForDeliveryTab;
	private Tab mDeliveredTab;

	public HomePresenter(Context context) {
		this.mContext = context;
		this.mView = (View) context;
		FirebaseManager.getInstance().registerOrderCollectionUpdateListener(object -> loadOrders());
		FirebaseManager.getInstance().registerAppConfigUpdateListener(this::checkAppVersion);
	}

	@Override
	public void loadOrders() {
		FirebaseManager.getInstance().fetchOrders(this::handleOrders,
				error -> Snackbar.make(mView.getRoot(), "Failed to fetch orders", LENGTH_LONG).show());
	}

	@Override
	public void onOrderAction(Order order, int position, OrderAction action) {
		// Do not allow any (long-press) action on completed orders
		if (action == MARK_NOOP) {
			return;
		}

		// Special case: ask to confirm payment status of order first and then simply mark as delivered
		if (action == MARK_DELIVERED) {
			handleMarkDelivered(order, position, action);
			return;
		}

		Snackbar.make(mView.getRoot(), "Marking order as " + getActionText(mContext.getResources(), action), LENGTH_LONG)
				.setAction("CANCEL", v -> handleOrderActionUndo(position, action))
				.addCallback(new Snackbar.Callback() {

					@Override
					public void onShown(Snackbar snackbar) {
						handleOrderAction(order, position, action);
					}

					@Override
					public void onDismissed(Snackbar snackbar, int event) {
						if (event != DISMISS_EVENT_ACTION)
							updateMarking(order, action);
					}

				}).show();
	}

	@Override
	public void setTabCallbacks(Tab newOrderTab, Tab processingTab, Tab outForDeliveryTab, Tab deliveredTab) {
		this.mNewOrderTab = newOrderTab;
		this.mProcessingTab = processingTab;
		this.mOutForDeliveryTab = outForDeliveryTab;
		this.mDeliveredTab = deliveredTab;
	}

	private void handleOrders(List<Order> orderList) {
		categorizeOrders(orderList);
		mView.setBadgeNumbers(mNewOrderList.size(), mProcessingOrderList.size(), mOutForDeliveryOrderList.size(), mDeliveredOrderList.size());
		mNewOrderTab.onOrdersLoaded(mNewOrderList);
		mProcessingTab.onOrdersLoaded(mProcessingOrderList);
		mOutForDeliveryTab.onOrdersLoaded(mOutForDeliveryOrderList);
		mDeliveredTab.onOrdersLoaded(mDeliveredOrderList);
	}

	private void categorizeOrders(List<Order> orderList) {
		// Making new lists instead of clear existing ones since the same
		// instances are used in the fragments. When the server issues an
		// update, we need to retain the size of the old list to update
		// the lists properly (see OrderTabFragment#onOrdersLoaded).
		mNewOrderList = new ArrayList<>();
		mProcessingOrderList = new ArrayList<>();
		mOutForDeliveryOrderList = new ArrayList<>();
		mDeliveredOrderList = new ArrayList<>();

		for (Order order : orderList) {
			if (order.getCurrentStatus().getStatus() == ORDER_STATUS_NEW) {
				mNewOrderList.add(order);
			} else if (order.getCurrentStatus().getStatus() == ORDER_STATUS_PROCESSING) {
				mProcessingOrderList.add(order);
			} else if (order.getCurrentStatus().getStatus() == ORDER_STATUS_OUT_FOR_DELIVERY) {
				mOutForDeliveryOrderList.add(order);
			} else if (order.getCurrentStatus().getStatus() == ORDER_STATUS_DELIVERED ||
					order.getCurrentStatus().getStatus() == ORDER_STATUS_CANCELLED) {
				mDeliveredOrderList.add(order);
			}
		}

	}

	private void handleOrderAction(Order order, int position, OrderAction action) {
		switch (action) {
			case MARK_PROCESSING:
				setOrderStatus(order, ORDER_STATUS_PROCESSING);
				mNewOrderList.remove(position);
				mProcessingOrderList.add(order);
				mNewOrderTab.onOrderRemoved(position);
				mProcessingTab.onOrderAdded(mProcessingOrderList.size());
				break;
			case MARK_OUT_FOR_DELIVERY:
				setOrderStatus(order, ORDER_STATUS_OUT_FOR_DELIVERY);
				mProcessingOrderList.remove(position);
				mOutForDeliveryOrderList.add(order);
				mProcessingTab.onOrderRemoved(position);
				mOutForDeliveryTab.onOrderAdded(mProcessingOrderList.size());
				break;
			case MARK_DELIVERED:
				setOrderStatus(order, ORDER_STATUS_DELIVERED);
				mOutForDeliveryOrderList.remove(position);
				mDeliveredOrderList.add(order);
				mOutForDeliveryTab.onOrderRemoved(position);
				mDeliveredTab.onOrderAdded(mProcessingOrderList.size());
				break;
		}
	}

	/**
	 * This method assumes that when an order is marked it is added to the end of the queue
	 * of the next list.
	 */
	private void handleOrderActionUndo(int position, OrderAction action) {
		Order order;
		switch (action) {
			case MARK_PROCESSING:
				order = mProcessingOrderList.remove(mProcessingOrderList.size() - 1);
				order.undoLastStatusChange();
				mNewOrderList.add(position, order);
				mProcessingTab.onOrderRemoved(mProcessingOrderList.size());
				mNewOrderTab.onOrderAdded(position);
				break;
			case MARK_OUT_FOR_DELIVERY:
				order = mOutForDeliveryOrderList.remove(mOutForDeliveryOrderList.size() - 1);
				order.undoLastStatusChange();
				mProcessingOrderList.add(position, order);
				mOutForDeliveryTab.onOrderRemoved(mOutForDeliveryOrderList.size());
				mProcessingTab.onOrderAdded(position);
				break;
			case MARK_DELIVERED:
				order = mDeliveredOrderList.remove(mDeliveredOrderList.size() - 1);
				order.undoLastStatusChange();
				mOutForDeliveryOrderList.add(position, order);
				mDeliveredTab.onOrderRemoved(mProcessingOrderList.size());
				mOutForDeliveryTab.onOrderAdded(position);
				break;
		}
	}

	private void setOrderStatus(Order order, int status) {
		order.setCurrentStatus(status, AccountManager.getInstance().getCurrentLoggedInAdmin().getName(), getStatusText(mContext.getResources(), status));
	}

	private void checkAppVersion(AppConfig appConfig) {
		if (BuildConfig.VERSION_CODE != appConfig.getVersion()) {
			mView.showUpdateDialog();
		}
	}

	private void handleMarkDelivered(Order order, int position, OrderAction action) {
		new AlertDialog.Builder(mContext)
				.setTitle("Set payment status")
				.setItems(new String[]{"Pending", "Paid"}, (dialog, pos) -> {
					int paymentStatus = pos == 0 ? ORDER_PAYMENT_STATUS_PENDING : ORDER_PAYMENT_STATUS_PAID;
					order.setPaymentStatus(paymentStatus);
					handleOrderAction(order, position, action);
					updateMarking(order, action);
				}).show();
	}

	private void updateMarking(Order order, OrderAction action) {
		FirebaseManager.getInstance().updateOrder(order, result -> {
			mView.setBadgeNumbers(mNewOrderList.size(), mProcessingOrderList.size(), mOutForDeliveryOrderList.size(), mDeliveredOrderList.size());
			Snackbar.make(mView.getRoot(), "Order marked as " + getActionText(mContext.getResources(), action), LENGTH_SHORT).show();
		});
	}

}
