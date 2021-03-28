package com.dgrocers.firebase;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.Timestamp;

import java.util.Date;


public class LocalStorageManager {

	private static LocalStorageManager sInstance;

	private final SharedPreferences mPreferences;

	public LocalStorageManager(Context context) {
		mPreferences = context.getSharedPreferences("admin-storage", Context.MODE_PRIVATE);
	}

	public static void init(Context context) {
		sInstance = new LocalStorageManager(context);
	}

	public static LocalStorageManager getInstance() {
		return sInstance;
	}

	public void setAdminLogin(String username) {
		mPreferences.edit()
				.putString("admin-username", username)
				.putLong("admin-login-timestamp", Timestamp.now().toDate().getTime())
				.apply();
	}

	public String getAdminUserName() {
		return mPreferences.getString("admin-username", null);
	}

	public Date getAdminLoginTimestamp() {
		return new Date(mPreferences.getLong("admin-login-timestamp", 0));
	}

	public void clearAdmin() {
		mPreferences.edit()
				.remove("admin-username")
				.remove("admin-login-timestamp")
				.apply();
	}

}
