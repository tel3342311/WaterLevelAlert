package com.liteon.waterlevelalert.util;

import java.util.ArrayList;

import com.liteon.waterlevelalert.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DataAdapter extends BaseAdapter {

	private ArrayList<DataRecord> mDataList;
	private LayoutInflater mInflater;
	
	public DataAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setDataList(ArrayList<DataRecord> list) {
		mDataList = list;
	}
	
	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);			
		}
		
		TextView date = (TextView) convertView.findViewById(R.id.date_text);
		TextView pole = (TextView) convertView.findViewById(R.id.pole_text);
		TextView status = (TextView) convertView.findViewById(R.id.status_text);
		ImageView statusIcon = (ImageView) convertView.findViewById(R.id.status_icon);

		DataRecord data = mDataList.get(position);
		date.setText(data.getDate());
		pole.setText(data.getPoleId());
		status.setText(data.getStatus());
		setIconByStatus(statusIcon, data.getStatus());
		return convertView;
	}
	
	private void setIconByStatus(ImageView imageView, String status) {
		//TODO update status icon 
		if (status.equals("Warning")) {
			imageView.setBackgroundColor(0xFFFF0000);
		} else if (status.equals("Secondary Alert")) {
			imageView.setBackgroundColor(0xFF00FF00);
		} else {
			imageView.setBackgroundColor(0xFF0000FF);
		}
	}

}
