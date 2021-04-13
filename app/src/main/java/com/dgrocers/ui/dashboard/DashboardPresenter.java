package com.dgrocers.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;

import com.dgrocers.model.Order;
import com.dgrocers.services.OrderService;
import com.dgrocers.ui.dashboard.DashboardContract.View;
import com.google.android.material.snackbar.Snackbar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

public class DashboardPresenter implements DashboardContract.Presenter {

	private final List<Order> mDailyOrderList = new ArrayList<>();
	private final List<Order> mWeeklyOrderList = new ArrayList<>();
	private final List<Order> mMonthlyOrderList = new ArrayList<>();

	private final Context mContext;
	private final View mView;

	private List<Order> mAllOrderList = new ArrayList<>();

	public DashboardPresenter(Context context) {
		this.mContext = context;
		this.mView = (View) context;
	}

	@Override
	public void loadOrders() {
		OrderService.getInstance().getAllOrders(this::handleOrders,
				error -> Snackbar.make(mView.getRoot(), "Failed to fetch orders", LENGTH_LONG).show());
	}

	@Override
	public void loadTopOrdersWithFilter(int filterType) {
		Map<String, Integer> addressOrderCountMap = new HashMap<>();
		Map<String, Integer> areaOrderCountMap = new HashMap<>();

		// Decide which list to use for filters
		List<Order> topList;
		if (filterType == 0) {
			topList = mWeeklyOrderList;
		} else if (filterType == 1) {
			topList = mMonthlyOrderList;
		} else {
			topList = mAllOrderList;
		}

		// Top counts
		for (Order order : topList) {
			// Counts by address
			String address = order.getCustomer().getAddress() + ", " + order.getCustomer().getArea();
			Integer addressOrderCount = addressOrderCountMap.getOrDefault(address, 0);
			addressOrderCountMap.put(address, addressOrderCount + 1);

			// Count by area
			String area = order.getCustomer().getArea();
			Integer areaOrderCount = areaOrderCountMap.getOrDefault(area, 0);
			areaOrderCountMap.put(area, areaOrderCount + 1);
		}

		List<Map.Entry<String, Integer>> addressOrderList = new ArrayList<>(addressOrderCountMap.entrySet());
		addressOrderList.sort((e1, e2) -> e2.getValue() - e1.getValue());
		mView.setAddressOrderCounts(addressOrderList);

		List<Map.Entry<String, Integer>> areaOrderList = new ArrayList<>(areaOrderCountMap.entrySet());
		areaOrderList.sort((e1, e2) -> e2.getValue() - e1.getValue());
		mView.setAreaOrderCounts(areaOrderList);
	}

	private void handleOrders(List<Order> orderList) {
		mAllOrderList = orderList;

		LocalDate today = LocalDate.now();
		LocalDate lastSunday = today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
		LocalDate lastMonth = today.with(TemporalAdjusters.firstDayOfMonth()).minusDays(1);

		// Counts by day/week/month
		for (Order order : orderList) {
			LocalDate orderDate = order.getCreatedAt().toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if (orderDate.isEqual(today)) {
				mDailyOrderList.add(order);
			}
			if (orderDate.isAfter(lastSunday)) {
				mWeeklyOrderList.add(order);
			}
			if (orderDate.isAfter(lastMonth)) {
				mMonthlyOrderList.add(order);
			}
		}

		mView.setOrderCounts(mDailyOrderList.size(), mWeeklyOrderList.size(), mMonthlyOrderList.size(), orderList.size());

		// Top counts
		loadTopOrdersWithFilter(0);
	}


}
