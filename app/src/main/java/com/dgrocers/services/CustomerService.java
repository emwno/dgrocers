package com.dgrocers.services;

import com.dgrocers.firebase.OnCompleteListener;
import com.dgrocers.firebase.OnRequestFailureListener;
import com.dgrocers.firebase.OnRequestSuccessListener;
import com.dgrocers.model.Customer;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static com.dgrocers.firebase.FirebaseConstants.CUSTOMERS;
import static com.google.firebase.firestore.Query.Direction.ASCENDING;

public class CustomerService {

	private static CustomerService sInstance;

	private final CollectionReference mCustomerCollectionRef;

	private CustomerService() {
		mCustomerCollectionRef = FirebaseFirestore.getInstance().collection(CUSTOMERS);
	}

	public static CustomerService getInstance() {
		if (sInstance == null) {
			sInstance = new CustomerService();
		}
		return sInstance;
	}

	public void getCustomer(String objectId, OnRequestSuccessListener<Customer> successListener, OnRequestFailureListener failureListener) {
		mCustomerCollectionRef.document(objectId).get()
				.addOnSuccessListener(documentReference -> successListener.onSuccess(documentReference.toObject(Customer.class)))
				.addOnFailureListener(e -> failureListener.onFailure(e.getMessage()));
	}

	public void createCustomer(Customer customer, OnRequestSuccessListener<String> successListener, OnRequestFailureListener failureListener) {
		mCustomerCollectionRef.add(customer)
				.addOnSuccessListener(documentReference -> successListener.onSuccess(documentReference.getId()))
				.addOnFailureListener(e -> failureListener.onFailure(e.getMessage()));
	}

	public void updateCustomer(Customer customer, OnCompleteListener<Boolean> listener) {
		mCustomerCollectionRef.document(customer.getObjectId()).set(customer)
				.addOnCompleteListener(task -> listener.onComplete(task.isSuccessful()));
	}

	public void getAllCustomers(OnRequestSuccessListener<List<Customer>> successListener, OnRequestFailureListener failureListener) {
		mCustomerCollectionRef.orderBy("address", ASCENDING).get()
				.addOnSuccessListener(queryDocumentSnapshots -> {
					List<Customer> customerList = queryDocumentSnapshots.toObjects(Customer.class);
					successListener.onSuccess(customerList);
				})
				.addOnFailureListener(e -> failureListener.onFailure(e.getMessage()));
	}

}
