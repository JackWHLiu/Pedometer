package com.lwh.pedometer.material;

import java.util.List;

import com.lwh.pedometer.R;
import com.lwh.pedometer.pojo.Step;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecordAdapter extends Adapter<RecordAdapter.RecordViewHolder> {
	
	Context mContext;
	List<Step> mList;
	LayoutInflater mInflater;

	public RecordAdapter(Context context,List<Step> list){
		this.mContext = context;
		this.mList = list;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	class RecordViewHolder extends RecyclerView.ViewHolder{

		TextView item1;
		TextView item2;
		
		public RecordViewHolder(View itemView) {
			super(itemView);
			item1 = (TextView) itemView.findViewById(R.id.tv_record_date);
			item2 = (TextView) itemView.findViewById(R.id.tv_record_step);
		}
		
	}

	@Override
	public int getItemCount() {
		return mList.size();
	}

	@Override
	public void onBindViewHolder(RecordViewHolder holder, int position) {
		Step data = mList.get(position);
		holder.item1.setText(data.getDate());
		holder.item2.setText(data.getStep());
	}

	@Override
	public RecordViewHolder onCreateViewHolder(ViewGroup vg, int position) {
		View view = mInflater.inflate(R.layout.item_step_record,vg,false);
		return new RecordViewHolder(view);
	}
	
}
