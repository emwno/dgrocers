package com.dgrocers.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class OrderTrackItem implements Parcelable {

	public static final Creator<OrderTrackItem> CREATOR = new Creator<OrderTrackItem>() {
		@Override
		public OrderTrackItem createFromParcel(Parcel in) {
			return new OrderTrackItem(in);
		}

		@Override
		public OrderTrackItem[] newArray(int size) {
			return new OrderTrackItem[size];
		}
	};

	private String text;
	private String actionBy;
	private Timestamp timestamp;

	public OrderTrackItem() {
	}

	public OrderTrackItem(String text, String actionBy, Timestamp timestamp) {
		this.text = text;
		this.actionBy = actionBy;
		this.timestamp = timestamp;
	}

	protected OrderTrackItem(Parcel in) {
		text = in.readString();
		actionBy = in.readString();
		timestamp = in.readParcelable(Timestamp.class.getClassLoader());
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getActionBy() {
		return actionBy;
	}

	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(text);
		dest.writeString(actionBy);
		dest.writeParcelable(timestamp, flags);
	}
}
