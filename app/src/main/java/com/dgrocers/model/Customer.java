package com.dgrocers.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.List;

public class Customer implements Parcelable {

	public static final Creator<Customer> CREATOR = new Creator<Customer>() {
		@Override
		public Customer createFromParcel(Parcel in) {
			return new Customer(in);
		}

		@Override
		public Customer[] newArray(int size) {
			return new Customer[size];
		}
	};

	private final List<String> phoneNumbers = new ArrayList<>();
	private final List<OrderProxy> orders = new ArrayList<>();

	@DocumentId
	private String objectId;
	private String address;
	private String area;
	private String location;

	public Customer() {
	}

	protected Customer(Parcel in) {
		objectId = in.readString();
		address = in.readString();
		area = in.readString();
		location = in.readString();
		in.readStringList(phoneNumbers);
		in.readList(orders, OrderProxy.class.getClassLoader());
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void addPhoneNumber(String phone) {
		this.phoneNumbers.add(phone);
	}

	public List<OrderProxy> getOrders() {
		return orders;
	}

	public void addOrder(String newOrderId, Timestamp createdAt) {
		orders.add(new OrderProxy(newOrderId, createdAt));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(objectId);
		dest.writeString(address);
		dest.writeString(area);
		dest.writeString(location);
		dest.writeStringList(phoneNumbers);
		dest.writeList(orders);
	}
}
