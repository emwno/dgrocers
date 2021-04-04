package com.dgrocers.firebase;

import android.util.Log;

import com.dgrocers.model.AppConfig;
import com.dgrocers.model.Customer;
import com.dgrocers.model.Location;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static com.dgrocers.firebase.FirebaseConstants.ADMINS;
import static com.dgrocers.firebase.FirebaseConstants.CONFIG_APP;
import static com.dgrocers.firebase.FirebaseConstants.CUSTOMERS;
import static com.dgrocers.firebase.FirebaseConstants.LOCATIONS;
import static com.google.firebase.firestore.Query.Direction.ASCENDING;

public class FirebaseManager {

	private static FirebaseManager sInstance;
	private final CollectionReference mLocationCollectionRef;
	private final List<OnServerUpdateListener<AppConfig>> mAppConfigUpdateListeners = new ArrayList<>();
	private final List<OnServerUpdateListener<List<Customer>>> mCustomerCollectionUpdateListeners = new ArrayList<>();
	private final FirebaseFirestore mFirestore;
	private final DocumentReference mAppConfigDocumentRef;
	private final CollectionReference mAdminCollectionRef;
	private final CollectionReference mCustomerCollectionRef;

	public FirebaseManager() {
		mFirestore = FirebaseFirestore.getInstance();
		mAppConfigDocumentRef = mFirestore.document(CONFIG_APP);
		mAdminCollectionRef = mFirestore.collection(ADMINS);
		mCustomerCollectionRef = mFirestore.collection(CUSTOMERS);
		mLocationCollectionRef = mFirestore.collection(LOCATIONS);

		// Add listener for changes in app config
		mAppConfigDocumentRef.addSnapshotListener((snapshot, error) -> {
			if (snapshot != null) {
				mAppConfigUpdateListeners.forEach(listener -> listener.onServerUpdate(snapshot.toObject(AppConfig.class)));
			}
		});

		// Customer data updated on server from other another client;
		mCustomerCollectionRef.addSnapshotListener((snapshot, error) -> {
			if (snapshot != null && !snapshot.getMetadata().hasPendingWrites()) {
				mCustomerCollectionUpdateListeners.forEach(listener -> listener.onServerUpdate(snapshot.toObjects(Customer.class)));
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

	public void registerCustomerCollectionUpdateListener(OnServerUpdateListener<List<Customer>> listener) {
		mCustomerCollectionUpdateListeners.add(listener);
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

	public void fetchCustomers(OnCompleteListener<List<Customer>> listener) {
		mCustomerCollectionRef.orderBy("address", ASCENDING).get()
				.addOnSuccessListener(queryDocumentSnapshots -> {
					List<Customer> customerList = queryDocumentSnapshots.toObjects(Customer.class);
					listener.onComplete(customerList);
				})
				.addOnFailureListener(e -> Log.e("king", e.toString()));
	}

	public void createNewCustomer(Customer customer, OnRequestSuccessListener<String> successListener, OnRequestFailureListener failureListener) {
		mCustomerCollectionRef.add(customer)
				.addOnSuccessListener(documentReference -> successListener.onSuccess(documentReference.getId()))
				.addOnFailureListener(e -> failureListener.onFailure(e.getMessage()));
	}

	public void updateCustomer(Customer customer, OnCompleteListener<Boolean> listener) {
		mCustomerCollectionRef.document(customer.getObjectId()).set(customer).addOnCompleteListener(task -> listener.onComplete(task.isSuccessful()));
	}

	/* Package private */
	CollectionReference getAdminCollectionRef() {
		return mAdminCollectionRef;
	}

	public void deleteAllCustomers() {
		mCustomerCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
			for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
				DocumentReference document = mCustomerCollectionRef.document(doc.getId());
				document.delete();
			}
		});
	}
}
