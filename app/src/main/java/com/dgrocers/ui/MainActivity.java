package com.dgrocers.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dgrocers.BuildConfig;
import com.dgrocers.databinding.ActivityMainBinding;
import com.dgrocers.firebase.AccountManager;
import com.dgrocers.firebase.FirebaseManager;
import com.dgrocers.ui.home.HomeActivity;
import com.dgrocers.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		FirebaseManager.getInstance().fetchAppConfig(
				appConfig -> {
					if (BuildConfig.VERSION_CODE != appConfig.getVersion()) {
						showUpdateDialog();
					} else {
						validateLogin();
					}
				},
				error -> {
					// TODO: show dialog?
				});
	}

	private void validateLogin() {
		AccountManager.getInstance().isCurrentLoginValid(
				admin -> {
					Intent intent = new Intent(this, HomeActivity.class);
					intent.putExtra("admin", admin);
					if (getIntent().getStringExtra("notification_order_id") != null)
						intent.putExtra("notification_order_id", getIntent().getStringExtra("notification_order_id"));
					startActivity(intent);
					finish();
				},
				error -> {
					startActivity(new Intent(this, LoginActivity.class));
					finish();
				});
	}

	private void showUpdateDialog() {
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

}