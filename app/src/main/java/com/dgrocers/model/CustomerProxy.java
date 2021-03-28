package com.dgrocers.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CustomerProxy implements Parcelable {

	public static final Creator<CustomerProxy> CREATOR = new Creator<CustomerProxy>() {
		@Override
		public CustomerProxy createFromParcel(Parcel in) {
			return new CustomerProxy(in);
		}

		@Override
		public CustomerProxy[] newArray(int size) {
			return new CustomerProxy[size];
		}
	};

	private List<String> phoneNumbers = new ArrayList<>();

	private String objectId;
	private String address;
	private String area;
	private String location;

	public CustomerProxy() {
	}

	public CustomerProxy(Customer customer) {
		this.objectId = customer.getObjectId();
		this.address = customer.getAddress();
		this.area = customer.getArea();
		this.location = customer.getLocation();
		this.phoneNumbers = customer.getPhoneNumbers();
	}

	protected CustomerProxy(Parcel in) {
		objectId = in.readString();
		address = in.readString();
		area = in.readString();
		location = in.readString();
		in.readStringList(phoneNumbers);
	}

	public String getObjectId() {
		return objectId;
	}

	public String getAddress() {
		return address;
	}

	public String getArea() {
		return area;
	}

	public String getLocation() {
		return location;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
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
	}

}
