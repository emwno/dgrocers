package com.dgrocers.ui.dashboard;

import com.dgrocers.ui.base.BaseContract;

import java.util.List;
import java.util.Map;

public interface DashboardContract {

	interface View extends BaseContract.View {
		android.view.View getRoot();

		void setOrderCounts(int dailyCount, int weeklyCount, int monthlyCount, int allTimeCount);

		void setAreaOrderCounts(List<Map.Entry<String, Integer>> areaOrderCountList);

		void setAddressOrderCounts(List<Map.Entry<String, Integer>> addressOrderCountList);
	}

	interface Presenter extends BaseContract.Presenter {
		void loadOrders();

		void loadTopOrdersWithFilter(int filterType);
	}
}
