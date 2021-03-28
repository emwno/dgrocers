package com.dgrocers.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ListItem implements Parcelable {

	public static final Creator<ListItem> CREATOR = new Creator<ListItem>() {
		@Override
		public ListItem createFromParcel(Parcel in) {
			return new ListItem(in);
		}

		@Override
		public ListItem[] newArray(int size) {
			return new ListItem[size];
		}
	};

	private final int pos;
	private final String text;

	public ListItem(String text) {
		this(text, -1);
	}

	public ListItem(String text, int pos) {
		this.pos = pos;
		this.text = text;
	}

	protected ListItem(Parcel in) {
		pos = in.readInt();
		text = in.readString();
	}

	public int getPos() {
		return pos;
	}

	public String getText() {
		return text;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(pos);
		dest.writeString(text);
	}
}
