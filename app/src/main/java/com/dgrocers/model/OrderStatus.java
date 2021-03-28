package com.dgrocers.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class OrderStatus implements Parcelable {

	public static final Creator<OrderStatus> CREATOR = new Creator<OrderStatus>() {
		@Override
		public OrderStatus createFromParcel(Parcel in) {
			return new OrderStatus(in);
		}

		@Override
		public OrderStatus[] newArray(int size) {
			return new OrderStatus[size];
		}
	};

	private int status;
	private Timestamp timestamp;

	public OrderStatus() {
	}

	public OrderStatus(int status, Timestamp timestamp) {
		this.status = status;
		this.timestamp = timestamp;
	}

	protected OrderStatus(Parcel in) {
		status = in.readInt();
		timestamp = in.readParcelable(Timestamp.class.getClassLoader());
	}

	public int getStatus() {
		return status;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(status);
		dest.writeParcelable(timestamp, flags);
	}
}
