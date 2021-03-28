package com.dgrocers.firebase;

public class FirebaseConstants {

	public static final String ADMINS = "admins/";
	public static final String CUSTOMERS = "customers/";
	public static final String LOCATIONS = "locations/";
	public static final String ORDERS = "orders/";

	public static final String CONFIG_APP = "configs/app";

	public static final int ORDER_STATUS_NEW = 0;
	public static final int ORDER_STATUS_PROCESSING = 1;
	public static final int ORDER_STATUS_OUT_FOR_DELIVERY = 2;
	public static final int ORDER_STATUS_DELIVERED = 3;
	public static final int ORDER_STATUS_CANCELLED = 4;
	public static final int ORDER_STATUS_REFUNDED = 5;

	public static final int ORDER_PAYMENT_STATUS_PENDING = 10;
	public static final int ORDER_PAYMENT_STATUS_PAID = 11;

}
