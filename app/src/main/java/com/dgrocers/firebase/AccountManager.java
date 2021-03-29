package com.dgrocers.firebase;

import com.dgrocers.model.Admin;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

public class AccountManager {

	private static AccountManager sInstance;

	private static Admin mCurrentLoggedInAdmin;

	public static AccountManager getInstance() {
		if (sInstance == null) {
			sInstance = new AccountManager();
		}
		return sInstance;
	}

	public String getAdminName() {
		return LocalStorageManager.getInstance().getAdminUserName();
	}

	public void isCurrentLoginValid(OnRequestSuccessListener<Admin> successListener, OnRequestFailureListener failureListener) {
		// No login yet
		if (LocalStorageManager.getInstance().getAdminUserName() == null) {
			failureListener.onFailure("No user logged in");
			return;
		}

		FirebaseManager.getInstance().getAdminCollectionRef()
				.document(LocalStorageManager.getInstance().getAdminUserName()).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (!documentSnapshot.exists()) {
						failureListener.onFailure("User does not exist");
					} else {
						// Ensure admin account is active and login is required if password is changed (valid login timestamp)
						mCurrentLoggedInAdmin = documentSnapshot.toObject(Admin.class);
						if (mCurrentLoggedInAdmin.isActive() && LocalStorageManager.getInstance().getAdminLoginTimestamp().after(mCurrentLoggedInAdmin.getValidLoginTimestamp().toDate())) {
							successListener.onSuccess(mCurrentLoggedInAdmin);
						} else {
							failureListener.onFailure("Re-login required");
						}
					}
				})
				.addOnFailureListener(e -> {
					failureListener.onFailure(e.getMessage());
				});
	}

	public void loginAdmin(String username, String password, OnRequestSuccessListener<Admin> successListener, OnRequestFailureListener failureListener) {
		FirebaseManager.getInstance().getAdminCollectionRef().document(username).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (!documentSnapshot.exists()) {
						failureListener.onFailure("Username or password mismatch.");
					} else {
						mCurrentLoggedInAdmin = documentSnapshot.toObject(Admin.class);
						if (mCurrentLoggedInAdmin.getPassword().equals(password)) {
							LocalStorageManager.getInstance().setAdminLogin(username);
							subscribeToChannels();
							successListener.onSuccess(mCurrentLoggedInAdmin);
						} else {
							failureListener.onFailure("Username or password mismatch.");
						}
					}
				})
				.addOnFailureListener(error -> failureListener.onFailure(error.getMessage()));
	}

	public void logoutAdmin() {
		LocalStorageManager.getInstance().clearAdmin();
		FirebaseInstallations.getInstance().delete(); // Unsubscribe from all notification topics 1
		FirebaseMessaging.getInstance().deleteToken(); // Unsubscribe from all notification topics 2
	}

	private void subscribeToChannels() {
		FirebaseMessaging.getInstance().subscribeToTopic("new_order");
	}

}
