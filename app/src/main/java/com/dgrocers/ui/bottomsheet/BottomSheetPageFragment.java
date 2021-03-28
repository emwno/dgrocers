package com.dgrocers.ui.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dgrocers.R;
import com.dgrocers.databinding.FragmentBottomSheetPageBinding;
import com.dgrocers.model.ListItem;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetPageFragment extends Fragment {

	private final PageListAdapter mAdapter;
	private FragmentBottomSheetPageBinding mBinding;

	public BottomSheetPageFragment() {
		this.mAdapter = new PageListAdapter();
	}

	public static BottomSheetPageFragment newInstance(String header) {
		BottomSheetPageFragment mBottomSheet = new BottomSheetPageFragment();
		Bundle arguments = new Bundle();
		arguments.putString("header", header);
		mBottomSheet.setArguments(arguments);
		return mBottomSheet;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mBinding = FragmentBottomSheetPageBinding.inflate(inflater, container, false);

		mBinding.list.setAdapter(mAdapter);
		mBinding.list.setLayoutManager(new LinearLayoutManager(getContext()));
		mBinding.emptyText.setText(String.format("No %ss", getArguments().getString("header")));

		return mBinding.getRoot();
	}

	public void setOnItemSelectedCallback(OnBottomSheetPageItemSelectedListener listener) {
		mAdapter.setItemSelectedListener(listener);
	}

	public void setItems(List<ListItem> items) {
		mAdapter.setItems(items);
		if (mBinding != null && isAdded()) {
			mBinding.emptyText.setVisibility(items.size() > 0 ? View.GONE : View.VISIBLE);
		}
	}

	public interface OnBottomSheetPageItemSelectedListener {
		void onItemSelected(int position);
	}

	static class ItemViewHolder extends RecyclerView.ViewHolder {

		private final TextView text;

		ItemViewHolder(View itemView) {
			super(itemView);
			text = itemView.findViewById(R.id.list_item_text);
		}

		public void bind(String header) {
			text.setText(header);
		}
	}

	static class PageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

		private OnBottomSheetPageItemSelectedListener mListener;
		private List<ListItem> mItems = new ArrayList<>();

		@NonNull
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
			ItemViewHolder holder = new ItemViewHolder(view);
			view.setOnClickListener(view1 -> mListener.onItemSelected(mItems.get(holder.getAdapterPosition()).getPos()));
			return holder;
		}

		@Override
		public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
			((ItemViewHolder) holder).bind(mItems.get(position).getText());
		}

		@Override
		public int getItemCount() {
			return mItems.size();
		}

		public void setItemSelectedListener(OnBottomSheetPageItemSelectedListener listener) {
			this.mListener = listener;
		}

		public void setItems(List<ListItem> items) {
			mItems = items;
			notifyDataSetChanged();
		}
	}
}
