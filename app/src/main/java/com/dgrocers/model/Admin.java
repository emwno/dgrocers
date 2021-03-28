package com.dgrocers.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

public class Admin implements Parcelable {

	public static final Creator<Admin> CREATOR = new Creator<Admin>() {
		@Override
		public Admin createFromParcel(Parcel in) {
			return new Admin(in);
		}

		@Override
		public Admin[] newArray(int size) {
			return new Admin[size];
		}
	};

	@DocumentId
	private String name;

	private String password;
	private boolean active;
	private Timestamp validLoginTimestamp;

	public Admin() {
	}

	protected Admin(Parcel in) {
		name = in.readString();
		password = in.readString();
		active = in.readByte() != 0;
		validLoginTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public boolean isActive() {
		return active;
	}

	public Timestamp getValidLoginTimestamp() {
		return validLoginTimestamp;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(password);
		dest.writeByte((byte) (active ? 1 : 0));
		dest.writeParcelable(validLoginTimestamp, flags);
	}

}
