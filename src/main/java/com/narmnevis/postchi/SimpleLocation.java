package com.narmnevis.postchi;

public class SimpleLocation implements Location {

	public static final String[] PROPERTIES = { "city", "postCode", "street" };

	private String region;
	private String city;
	private String street;
	private String postCode;

	@Override
	public String getRegion() {
		return region;
	}

	@Override
	public String getCity() {
		return city;
	}

	@Override
	public String getStreet() {
		return street;
	}

	@Override
	public String getPostCode() {
		return postCode;
	}

	@Override
	public int hashCode() {
		return postCode.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof Location) {
			return false;
		}
		Location l = (Location) obj;
		return hashCode() == l.hashCode();
	}

	@Override
	public String toString() {
		return "Location [" + postCode + "," + region + "," + city + "," + street + "]";
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

}
