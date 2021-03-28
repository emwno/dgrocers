package com.dgrocers.util;

import android.content.res.Resources;

import com.dgrocers.R;
import com.dgrocers.ui.home.tabs.OrderTabFragment.OrderActionListener.OrderAction;

import static com.dgrocers.firebase.FirebaseConstants.ORDER_PAYMENT_STATUS_PAID;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_PAYMENT_STATUS_PENDING;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_CANCELLED;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_DELIVERED;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_NEW;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_OUT_FOR_DELIVERY;
import static com.dgrocers.firebase.FirebaseConstants.ORDER_STATUS_PROCESSING;

public class Constants {

	public static final int RESULT_SUCCESS = 10;
	public static final int RESULT_FAILED = 11;

	public static final int REQUEST_CREATE_CUSTOMER = 21;
	public static final int REQUEST_CREATE_ORDER = 22;
	public static final int REQUEST_PERM_PHONE_CALL = 23;

	public static final int NOTIFY_ORDER_CANCELLED = 30;

	public static String getStatusText(Resources resources, int status) {
		switch (status) {
			case ORDER_STATUS_NEW:
				return resources.getString(R.string.order_status_received);
			case ORDER_STATUS_PROCESSING:
				return resources.getString(R.string.order_status_processing);
			case ORDER_STATUS_OUT_FOR_DELIVERY:
				return resources.getString(R.string.order_status_out_for_delivery);
			case ORDER_STATUS_DELIVERED:
				return resources.getString(R.string.order_status_delivered);
			case ORDER_STATUS_CANCELLED:
				return resources.getString(R.string.order_status_cancelled);
			default:
				return "-";
		}
	}

	public static String getActionText(Resources resources, OrderAction action) {
		switch (action) {
			case MARK_PROCESSING:
				return resources.getString(R.string.order_status_processing);
			case MARK_OUT_FOR_DELIVERY:
				return resources.getString(R.string.order_status_out_for_delivery);
			case MARK_DELIVERED:
				return resources.getString(R.string.order_status_delivered);
			default:
				return "-";
		}
	}

	public static String getPaymentStatusText(Resources resources, int status) {
		switch (status) {
			case ORDER_PAYMENT_STATUS_PENDING:
				return resources.getString(R.string.order_payment_status_pending);
			case ORDER_PAYMENT_STATUS_PAID:
				return resources.getString(R.string.order_payment_status_paid);
			default:
				return "-";
		}
	}

}
