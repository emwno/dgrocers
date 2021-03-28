package com.dgrocers.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.List;

public class Order implements Parcelable {

	public static final Creator<Order> CREATOR = new Creator<Order>() {
		@Override
		public Order createFromParcel(Parcel in) {
			return new Order(in);
		}

		@Override
		public Order[] newArray(int size) {
			return new Order[size];
		}
	};

	private final List<OrderTrackItem> trackingHistory = new ArrayList<>();

	@DocumentId
	private String objectId;

	private int orderNumber;
	private int paymentStatus;
	private OrderStatus currentStatus;
	private CustomerProxy customer;
	private Timestamp createdAt;
	private String items;
	private String notes;

	public Order() {
	}

	protected Order(Parcel in) {
		objectId = in.readString();
		orderNumber = in.readInt();
		paymentStatus = in.readInt();
		currentStatus = in.readParcelable(OrderStatus.class.getClassLoader());
		customer = in.readParcelable(CustomerProxy.class.getClassLoader());
		createdAt = in.readParcelable(Timestamp.class.getClassLoader());
		items = in.readString();
		notes = in.readString();
		in.readList(trackingHistory, OrderTrackItem.class.getClassLoader());
	}

	public String getObjectId() {
		return objectId;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public CustomerProxy getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerProxy customer) {
		this.customer = customer;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public OrderStatus getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(int newStatus, String actionBy, String newStatusText) {
		setCurrentStatus(newStatus, newStatusText, actionBy, Timestamp.now());
	}

	public void setCurrentStatus(int newStatus, String newStatusText, String actionBy, Timestamp timestamp) {
		this.currentStatus = new OrderStatus(newStatus, timestamp);
		this.trackingHistory.add(new OrderTrackItem(newStatusText, actionBy, timestamp));
	}

	public void addToTracking(String text, String actionBy) {
		trackingHistory.add(new OrderTrackItem(text, actionBy, Timestamp.now()));
	}

	public void undoLastStatusChange() {
		this.trackingHistory.remove(trackingHistory.size() - 1);
	}

	public List<OrderTrackItem> getTrackingHistory() {
		return trackingHistory;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(objectId);
		dest.writeInt(orderNumber);
		dest.writeInt(paymentStatus);
		dest.writeParcelable(currentStatus, flags);
		dest.writeParcelable(customer, flags);
		dest.writeParcelable(createdAt, flags);
		dest.writeString(items);
		dest.writeString(notes);
		dest.writeList(trackingHistory);
	}

}
