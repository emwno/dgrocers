package com.dgrocers.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Location implements Parcelable {

	public static final Creator<Location> CREATOR = new Creator<Location>() {
		@Override
		public Location createFromParcel(Parcel in) {
			return new Location(in);
		}

		@Override
		public Location[] newArray(int size) {
			return new Location[size];
		}
	};

	private final List<Area> areas = new ArrayList<>();

	private String name;

	public Location() {
	}

	protected Location(Parcel in) {
		name = in.readString();
		in.readList(areas, Area.class.getClassLoader());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Area> getAreas() {
		return areas;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeList(areas);
	}

	public void addArea(Area area) {
		areas.add(area);
	}

}
