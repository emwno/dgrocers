package com.dgrocers.ui.home.tabs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dgrocers.R;
import com.dgrocers.model.Order;
import com.perfomer.blitz.BlitzKt;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

	private List<Order> mItems = new ArrayList<>();
	private final OnOrderSelectedListener mListener;

	public OrderListAdapter(OnOrderSelectedListener mListener) {
		this.mListener = mListener;
	}

	@NonNull
	@Override
	public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_item, parent, false);
		OrderViewHolder holder = new OrderViewHolder(view);
		view.setOnClickListener(view1 -> {
			int position = holder.getAdapterPosition();
			if (position != -1)
				mListener.onOrderSelected(mItems.get(position));
		});
		view.setOnLongClickListener(view1 -> {
			int position = holder.getAdapterPosition();
			if (position != -1)
				mListener.onOrderLongPressed(mItems.get(position), position);
			return true;
		});
		return holder;
	}

	@Override
	public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
		holder.mArea.setText(mItems.get(position).getCustomer().getArea() + " | " + mItems.get(position).getCustomer().getLocation());
		holder.mAddress.setText(mItems.get(position).getCustomer().getAddress());
		BlitzKt.setTimeAgo(holder.mTimer, mItems.get(position).getCurrentStatus().getTimestamp().toDate(), false, true);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	public void setItems(List<Order> mItems) {
		this.mItems = mItems;
	}

	public interface OnOrderSelectedListener {
		void onOrderSelected(Order order);

		void onOrderLongPressed(Order order, int position);
	}

	public static class OrderViewHolder extends RecyclerView.ViewHolder {

		public TextView mArea;
		public TextView mAddress;
		public TextView mTimer;

		public OrderViewHolder(View view) {
			super(view);
			mArea = view.findViewById(R.id.row_order_area);
			mAddress = view.findViewById(R.id.row_order_address);
			mTimer = view.findViewById(R.id.row_order_timer);
		}
	}

}