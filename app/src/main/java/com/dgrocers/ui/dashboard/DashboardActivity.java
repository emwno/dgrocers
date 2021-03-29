package com.dgrocers.ui.dashboard;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.dgrocers.databinding.ActivityDashboardBinding;

import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity implements DashboardContract.View {

	private DashboardPresenter mPresenter;
	private ActivityDashboardBinding mBinding;

	private int mTopNumber = 0;
	private List<Map.Entry<String, Integer>> mAddressOrderCountList;
	private List<Map.Entry<String, Integer>> mAreaOrderCountList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mBinding.topNumbers.setSelection(0, false);
		mBinding.topNumbers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mTopNumber = position;
				handleTopAreaNumberChange(getTopNumber(position));
				handleTopAddressNumberChange(getTopNumber(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		mBinding.topFilters.setSelection(0, false);
		mBinding.topFilters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mPresenter.loadTopOrdersWithFilter(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		mPresenter = new DashboardPresenter(this);
		mPresenter.loadOrders();
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == android.R.id.home) {
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public View getRoot() {
		return mBinding.getRoot();
	}

	@Override
	public void setOrderCounts(int dailyCount, int weeklyCount, int monthlyCount, int allTimeCount) {
		mBinding.dailyCount.setText(String.valueOf(dailyCount));
		mBinding.weeklyCount.setText(String.valueOf(weeklyCount));
		mBinding.monthlyCount.setText(String.valueOf(monthlyCount));
		mBinding.allTimeCount.setText(String.valueOf(allTimeCount));
	}

	@Override
	public void setAreaOrderCounts(List<Map.Entry<String, Integer>> areaOrderCountList) {
		mAreaOrderCountList = areaOrderCountList;
		handleTopAreaNumberChange(getTopNumber(mTopNumber));
	}

	@Override
	public void setAddressOrderCounts(List<Map.Entry<String, Integer>> addressOrderCountList) {
		mAddressOrderCountList = addressOrderCountList;
		handleTopAddressNumberChange(getTopNumber(mTopNumber));
	}

	private void handleTopAreaNumberChange(int num) {
		num = Math.min(num, mAreaOrderCountList.size());
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < num; i++) {
			Map.Entry<String, Integer> entry = mAreaOrderCountList.get(i);
			text.append(entry.getValue()).append(" | ").append(entry.getKey()).append("\n\n");
		}

		mBinding.topAreas.setText(text.toString().trim());
	}

	private void handleTopAddressNumberChange(int num) {
		num = Math.min(num, mAddressOrderCountList.size());
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < num; i++) {
			Map.Entry<String, Integer> entry = mAddressOrderCountList.get(i);
			text.append(entry.getValue()).append(" | ").append(entry.getKey()).append("\n\n");
		}

		mBinding.topAddresses.setText(text.toString().trim());
	}

	private int getTopNumber(int type) {
		if (type == 0) return 5;
		else return 10;
	}

}