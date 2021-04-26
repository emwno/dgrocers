package com.dgrocers.firebase;

import android.util.Log;

import com.dgrocers.model.AppConfig;
import com.dgrocers.model.Location;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static com.dgrocers.firebase.FirebaseConstants.ADMINS;
import static com.dgrocers.firebase.FirebaseConstants.CONFIG_APP;
import static com.dgrocers.firebase.FirebaseConstants.LOCATIONS;

public class FirebaseManager {

	private static FirebaseManager sInstance;
	private final CollectionReference mLocationCollectionRef;
	private final List<OnServerUpdateListener<AppConfig>> mAppConfigUpdateListeners = new ArrayList<>();
	private final FirebaseFirestore mFirestore;
	private final DocumentReference mAppConfigDocumentRef;
	private final CollectionReference mAdminCollectionRef;

	public FirebaseManager() {
		mFirestore = FirebaseFirestore.getInstance();
		mAppConfigDocumentRef = mFirestore.document(CONFIG_APP);
		mAdminCollectionRef = mFirestore.collection(ADMINS);
		mLocationCollectionRef = mFirestore.collection(LOCATIONS);

		// Add listener for changes in app config
		mAppConfigDocumentRef.addSnapshotListener((snapshot, error) -> {
			if (snapshot != null) {
				mAppConfigUpdateListeners.forEach(listener -> listener.onServerUpdate(snapshot.toObject(AppConfig.class)));
			}
		});
	}

	public static FirebaseManager getInstance() {
		if (sInstance == null) {
			sInstance = new FirebaseManager();
		}
		return sInstance;
	}

	public void registerAppConfigUpdateListener(OnServerUpdateListener<AppConfig> listener) {
		mAppConfigUpdateListeners.add(listener);
	}

	public void fetchAppConfig(OnRequestSuccessListener<AppConfig> successListener, OnRequestFailureListener failureListener) {
		mAppConfigDocumentRef.get()
				.addOnSuccessListener(snapshot -> successListener.onSuccess(snapshot.toObject(AppConfig.class)))
				.addOnFailureListener(e -> failureListener.onFailure(e.getMessage()));
	}

	public void fetchLocations(OnCompleteListener<List<Location>> listener) {
		mLocationCollectionRef.get()
				.addOnSuccessListener(queryDocumentSnapshots -> {
					List<Location> orderList = queryDocumentSnapshots.toObjects(Location.class);
					listener.onComplete(orderList);
				})
				.addOnFailureListener(e -> Log.e("king", e.toString()));
	}

	/* Package private */
	CollectionReference getAdminCollectionRef() {
		return mAdminCollectionRef;
	}
}
