package com.dgrocers.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Area implements Parcelable {

	public static final Creator<Area> CREATOR = new Creator<Area>() {
		@Override
		public Area createFromParcel(Parcel in) {
			return new Area(in);
		}

		@Override
		public Area[] newArray(int size) {
			return new Area[size];
		}
	};

	private final List<String> subAreas = new ArrayList<>();

	private String name;

	public Area() {
	}

	protected Area(Parcel in) {
		name = in.readString();
		in.readStringList(subAreas);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSubAreas() {
		return subAreas;
	}

	public void addSubArea(String subArea) {
		subAreas.add(subArea);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeStringList(subAreas);
	}
}
