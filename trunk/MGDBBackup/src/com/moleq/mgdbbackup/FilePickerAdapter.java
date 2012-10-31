package com.moleq.mgdbbackup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.moleq.mgdbbackup.FileData;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FilePickerAdapter extends BaseAdapter {
	private Context mContext;
	private Vector mItems = new Vector();
	private LinearLayout layout;
	private LayoutInflater mInflater;
	private Bitmap file;
	private Bitmap documents;
	private Bitmap back;
	
	private static HashMap<String,Boolean> isSelected;

	public FilePickerAdapter(Context context)
	{
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
		file = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.file);
		documents = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.folder);
		back = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.back2);
		isSelected = new HashMap<String, Boolean>();
		//initDate();
	}

	/*private void initDate()
	{
		for (int i = 0; i < mItems.size(); i++)
		{
			getIsSelected().put(i, false);
		}
	}*/
	    

	public void addItem(FileData item) {
		mItems.add(item);
	}

	public FileData getItem(int it) {
		return (FileData) mItems.elementAt(it);
	}

	public int getCount() {
		return mItems.size();
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public int getItemType(int arg0) {
		return getItem(arg0).type;
	}

	public void clearItems() {
		mItems.clear();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
//		if(null == view){
//			view = (RelativeLayout) inflate.inflate(R.layout.file_row, null);
//		}
//		
//		textViewItem = (TextView) view.findViewById(R.id.filetext);
//		textViewItem.setTextSize(20);
//		textViewItem.setText(getItem(position).name);
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.file_row,null);
			holder.text = (TextView) convertView.findViewById(R.id.filetext);
			holder.icon = (ImageView) convertView.findViewById(R.id.fileicon);
			holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.text.setText(getItem(position).name);
		if(getItem(position).type == 0){
			holder.icon.setImageBitmap(file);
		}else if(getItem(position).type == 1 && getItem(position).name.equals("Back...")){
			holder.icon.setImageBitmap(back);
		}else if(getItem(position).type == 1){
			holder.icon.setImageBitmap(documents);
		}
		
		final int p = position;
		isSelected.clear();
		isSelected = new HashMap<String, Boolean>();
		holder.cb.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				
				CheckBox box = (CheckBox) v;
				isSelected.put(getItem(p).name, box.isChecked());
				System.out.println("p-->" + p);
				System.out.println(box.isChecked()?"true":"false");
				System.out.println(getItem(p).name);
			}
		});
		return convertView;
	}
	
	public static HashMap<String, Boolean> getIsSelected()
	{
		
		return isSelected;
	}

	public static void setIsSelected(HashMap<String, Boolean> isSelected)
	{
		FilePickerAdapter.isSelected = isSelected;
	}
	
	public final class ViewHolder
	{
		public TextView text;
		public ImageView icon;
		public CheckBox cb;
	}
}
