package com.dgrocers.ui.home;

import com.dgrocers.model.Order;
import com.dgrocers.ui.base.BaseContract;

import java.util.List;

public interface HomeContract {

	interface View extends BaseContract.View {
		android.view.View getRoot();

		void setBadgeNumbers(int newNum, int processingNum, int outNum, int deliveredNum);

		void showUpdateDialog();
	}

	interface Presenter extends BaseContract.Presenter {
		void loadOrders();

		void setTabCallbacks(Tab newOrderTab, Tab processingTab, Tab outForDeliveryTab, Tab deliveredTab);
	}

	interface Tab {
		void onOrdersLoaded(List<Order> orderList);

		void onOrderAdded(int position);

		void onOrderRemoved(int position);
	}
}
