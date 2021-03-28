package com.dgrocers.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dgrocers.BuildConfig;
import com.dgrocers.R;
import com.dgrocers.databinding.ActivityHomeBinding;
import com.dgrocers.firebase.FirebaseManager;
import com.dgrocers.ui.dashboard.DashboardActivity;
import com.dgrocers.ui.home.tabs.OrderTabFragment;
import com.dgrocers.ui.order.CreateOrderActivity;
import com.dgrocers.ui.order.ViewOrderActivity;
import com.dgrocers.ui.settings.SettingsActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import static com.dgrocers.ui.home.tabs.OrderTabFragment.OrderActionListener.OrderAction.MARK_DELIVERED;
import static com.dgrocers.ui.home.tabs.OrderTabFragment.OrderActionListener.OrderAction.MARK_NOOP;
import static com.dgrocers.ui.home.tabs.OrderTabFragment.OrderActionListener.OrderAction.MARK_OUT_FOR_DELIVERY;
import static com.dgrocers.ui.home.tabs.OrderTabFragment.OrderActionListener.OrderAction.MARK_PROCESSING;
import static com.dgrocers.util.Constants.NOTIFY_ORDER_CANCELLED;
import static com.dgrocers.util.Constants.REQUEST_CREATE_ORDER;
import static com.dgrocers.util.Constants.RESULT_SUCCESS;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {

	private HomePresenter mPresenter;
	private ActivityHomeBinding mBinding;
	private SectionsPagerAdapter mPagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = ActivityHomeBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());

		mPresenter = new HomePresenter(this);
		mPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

		OrderTabFragment newOrderFragment = new OrderTabFragment();
		OrderTabFragment processingOrderFragment = new OrderTabFragment();
		OrderTabFragment outForDeliveryOrderFragment = new OrderTabFragment();
		OrderTabFragment deliveredOrderFragment = new OrderTabFragment();

		newOrderFragment.setArgs(mPresenter, MARK_PROCESSING);
		processingOrderFragment.setArgs(mPresenter, MARK_OUT_FOR_DELIVERY);
		outForDeliveryOrderFragment.setArgs(mPresenter, MARK_DELIVERED);
		deliveredOrderFragment.setArgs(mPresenter, MARK_NOOP);

		mPagerAdapter.addFragment(newOrderFragment);
		mPagerAdapter.addFragment(processingOrderFragment);
		mPagerAdapter.addFragment(outForDeliveryOrderFragment);
		mPagerAdapter.addFragment(deliveredOrderFragment);

		mPresenter.setTabCallbacks(newOrderFragment, processingOrderFragment, outForDeliveryOrderFragment, deliveredOrderFragment);

		mBinding.viewPager.setOffscreenPageLimit(3);
		mBinding.viewPager.setAdapter(mPagerAdapter);
		mBinding.tabs.setupWithViewPager(mBinding.viewPager);

		mBinding.fab.setOnClickListener(v -> {
			Intent intent = new Intent(this, CreateOrderActivity.class);
			startActivityForResult(intent, REQUEST_CREATE_ORDER);
		});

		mPresenter.loadOrders();

		if (getIntent().getStringExtra("notification_order_id") != null) {
			handleNewOrderNotification(getIntent().getStringExtra("notification_order_id"));
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getStringExtra("notification_order_id") != null) {
			handleNewOrderNotification(intent.getStringExtra("notification_order_id"));
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// New successfully order created
		if (requestCode == REQUEST_CREATE_ORDER && resultCode == RESULT_SUCCESS) {
			mPresenter.loadOrders();
			Snackbar.make(mBinding.getRoot(), "Order successfully created", BaseTransientBottomBar.LENGTH_SHORT).show();
		}

		// (view) Order cancelled
		if (resultCode == NOTIFY_ORDER_CANCELLED) {
			mPresenter.loadOrders();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == R.id.menu_home_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		} else if (itemId == R.id.menu_home_dashboard) {
			startActivity(new Intent(this, DashboardActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public View getRoot() {
		return mBinding.getRoot();
	}

	@Override
	public void setBadgeNumbers(int newNum, int processingNum, int outNum, int deliveredNum) {
		if (newNum > 0) {
			mBinding.tabs.getTabAt(0).getOrCreateBadge().setNumber(newNum);
		} else {
			mBinding.tabs.getTabAt(0).removeBadge();
		}

		if (processingNum > 0) {
			mBinding.tabs.getTabAt(1).getOrCreateBadge().setNumber(processingNum);
		} else {
			mBinding.tabs.getTabAt(1).removeBadge();
		}

		if (outNum > 0) {
			mBinding.tabs.getTabAt(2).getOrCreateBadge().setNumber(outNum);
		} else {
			mBinding.tabs.getTabAt(2).removeBadge();
		}

		if (deliveredNum > 0) {
			mBinding.tabs.getTabAt(3).getOrCreateBadge().setNumber(deliveredNum);
		} else {
			mBinding.tabs.getTabAt(3).removeBadge();
		}
	}

	@Override
	public void showUpdateDialog() {
		if (!isFinishing())
			new AlertDialog.Builder(this)
					.setTitle("New version available")
					.setMessage("Please update app to new version.")
					.setCancelable(false)
					.setPositiveButton("Update", (dialog, which) -> {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					})
					.show();
	}

	private void handleNewOrderNotification(String objectId) {
		FirebaseManager.getInstance().fetchOrder(objectId,
				order -> {
					Intent intent = new Intent(HomeActivity.this, ViewOrderActivity.class);
					intent.putExtra("order", order);
					startActivity(intent);
				},
				e -> Snackbar.make(mBinding.getRoot(), "Failed to load order", BaseTransientBottomBar.LENGTH_SHORT).show());
	}

}