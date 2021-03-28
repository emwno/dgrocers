package com.dgrocers.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dgrocers.databinding.ActivityLoginBinding;
import com.dgrocers.firebase.AccountManager;
import com.dgrocers.model.Admin;
import com.dgrocers.ui.home.HomeActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

	private ActivityLoginBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(mBinding.getRoot());
		mBinding.loginButton.setOnClickListener(v -> login());
	}

	private void login() {
		if (checkErrors()) {
			return;
		}

		AccountManager.getInstance().loginAdmin(
				mBinding.username.getText().toString().trim(),
				mBinding.password.getText().toString().trim(),
				this::goHome,
				error -> Snackbar.make(mBinding.getRoot(), error, Snackbar.LENGTH_LONG).show());
	}

	private boolean checkErrors() {
		boolean error = false;

		if (mBinding.username.getText().toString().trim().length() == 0) {
			mBinding.username.setError("Enter a username");
			error = true;
		}

		if (mBinding.password.getText().toString().trim().length() == 0) {
			mBinding.password.setError("Enter a password");
			error = true;
		}

		return error;
	}

	private void goHome(Admin admin) {
		subscribeToChannels();
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("admin", admin);
		startActivity(intent);
		finish();
	}

	private void subscribeToChannels() {
		FirebaseMessaging.getInstance().subscribeToTopic("new_order");
	}

}
