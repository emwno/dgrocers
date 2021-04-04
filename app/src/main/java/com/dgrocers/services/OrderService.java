package com.dgrocers.services;

import android.util.Log;

import com.dgrocers.BuildConfig;
import com.dgrocers.firebase.OnCompleteListener;
import com.dgrocers.firebase.OnRequestFailureListener;
import com.dgrocers.firebase.OnRequestSuccessListener;
import com.dgrocers.firebase.OnServerUpdateListener;
import com.dgrocers.model.Order;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.dgrocers.firebase.FirebaseConstants.ORDERS;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_PAYMENT_STATUS_PENDING;
import static com.google.firebase.firestore.Query.Direction.ASCENDING;
import static com.google.firebase.firestore.Query.Direction.DESCENDING;

public class OrderService {

	private static OrderService sInstance;

	private final CollectionReference mOrderCollectionRef;
	private final List<OnServerUpdateListener<List<Order>>> mOrderCollectionUpdateListeners = new ArrayList<>();

	private OrderService() {
		mOrderCollectionRef = FirebaseFirestore.getInstance().collection(ORDERS);

		// Order data updated on server from other another client;
		mOrderCollectionRef.addSnapshotListener((snapshot, error) -> {
			if (snapshot != null && !snapshot.getMetadata().hasPendingWrites()) {
				// Null since this is just to notify that we should re-fetch orders (temp fix to firebase sending back unchanged order documents)
				mOrderCollectionUpdateListeners.forEach(listener -> listener.onServerUpdate(null));
			}
		});
	}

	public static OrderService getInstance() {
		if (sInstance == null) {
			sInstance = new OrderService();
		}
		return sInstance;
	}

	public void createOrder(Order order, OnRequestSuccessListener<String> successListener, OnRequestFailureListener failureListener) {
		mOrderCollectionRef.add(order)
				.addOnSuccessListener(documentReference -> successListener.onSuccess(documentReference.getId()))
				.addOnFailureListener(e -> failureListener.onFailure(e.getMessage()));
	}

	public void updateOrder(Order order, OnCompleteListener<Boolean> listener) {
		mOrderCollectionRef.document(order.getObjectId()).set(order).addOnCompleteListener(task -> listener.onComplete(task.isSuccessful()));
	}

	public void getOrder(String objectId, OnRequestSuccessListener<Order> successListener, OnRequestFailureListener failureListener) {
		mOrderCollectionRef.document(objectId).get()
				.addOnSuccessListener(documentReference -> successListener.onSuccess(documentReference.toObject(Order.class)))
				.addOnFailureListener(e -> failureListener.onFailure(e.getMessage()));
	}

	public void getDayOrders(OnRequestSuccessListener<List<Order>> successListener, OnRequestFailureListener failureListener) {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 2);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);

		Calendar tomorrow = Calendar.getInstance();
		tomorrow.set(Calendar.HOUR_OF_DAY, 23);
		tomorrow.set(Calendar.MINUTE, 59);
		tomorrow.set(Calendar.SECOND, 59);

		mOrderCollectionRef
				.orderBy("createdAt", ASCENDING)
				.whereGreaterThan("createdAt", today.getTime())
				.whereLessThan("createdAt", tomorrow.getTime())
				.get()
				.addOnSuccessListener(queryDocumentSnapshots -> {
					List<Order> orderList = queryDocumentSnapshots.toObjects(Order.class);
					successListener.onSuccess(orderList);
				})
				.addOnFailureListener(e -> {
					Log.e("king", e.getMessage());
					failureListener.onFailure(e.getMessage());
				});
	}

	public void getAllOrders(OnRequestSuccessListener<List<Order>> successListener, OnRequestFailureListener failureListener) {
		mOrderCollectionRef
				.orderBy("createdAt", DESCENDING)
				.get()
				.addOnSuccessListener(queryDocumentSnapshots -> {
					List<Order> orderList = queryDocumentSnapshots.toObjects(Order.class);
					successListener.onSuccess(orderList);
				})
				.addOnFailureListener(e -> {
					Log.e("king", e.getMessage());
					failureListener.onFailure(e.getMessage());
				});
	}


	public void getAllPendingOrders(OnRequestSuccessListener<List<Order>> successListener, OnRequestFailureListener failureListener) {
		mOrderCollectionRef
				.orderBy("createdAt", DESCENDING)
				.whereEqualTo("paymentStatus", ORDER_PAYMENT_STATUS_PENDING)
				.get()
				.addOnSuccessListener(queryDocumentSnapshots ->
						successListener.onSuccess(queryDocumentSnapshots.toObjects(Order.class))
				)
				.addOnFailureListener(e -> {
					Log.e("king", e.getMessage());
					failureListener.onFailure(e.getMessage());
				});
	}

	public void deleteAllOrders() {
		if (BuildConfig.DEBUG)
			mOrderCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
				for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
					mOrderCollectionRef.document(doc.getId()).delete();
				}
			});
		else throw new RuntimeException("Not allowed to delete orders in PROD mode.");
	}

	public void registerOrderCollectionUpdateListener(OnServerUpdateListener<List<Order>> listener) {
		mOrderCollectionUpdateListeners.add(listener);
	}

}
