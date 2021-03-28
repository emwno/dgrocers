package com.dgrocers.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class OrderProxy implements Parcelable {

	public static final Creator<OrderProxy> CREATOR = new Creator<OrderProxy>() {
		@Override
		public OrderProxy createFromParcel(Parcel in) {
			return new OrderProxy(in);
		}

		@Override
		public OrderProxy[] newArray(int size) {
			return new OrderProxy[size];
		}
	};

	private String objectId;
	private Timestamp createdAt;

	public OrderProxy() {
	}

	public OrderProxy(String newOrderId, Timestamp createdAt) {
		this.objectId = newOrderId;
		this.createdAt = createdAt;
	}

	protected OrderProxy(Parcel in) {
		objectId = in.readString();
		createdAt = in.readParcelable(Timestamp.class.getClassLoader());
	}

	public String getObjectId() {
		return objectId;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(objectId);
		dest.writeParcelable(createdAt, flags);
	}
}
