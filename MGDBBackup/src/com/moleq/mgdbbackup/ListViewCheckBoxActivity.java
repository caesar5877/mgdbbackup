package com.moleq.mgdbbackup;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class ListViewCheckBoxActivity extends Activity
{

	private ListView listView;
	DraftDailyAdapter mAdapter;
	private ImageView iv_cancel;
	private ImageView iv_ok;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		listView = (ListView) findViewById(R.id.list);
		listView.setCacheColorHint(0x00000000);
		mAdapter = new DraftDailyAdapter(this);
		listView.setAdapter(mAdapter);
		iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
		iv_cancel.setOnClickListener(onClickListener);

		iv_ok = (ImageView) findViewById(R.id.iv_ok);
		iv_ok.setOnClickListener(onClickListener);
		
		listDB();
	}

	private OnClickListener onClickListener = new OnClickListener()
	{

		public void onClick(View v)
		{

			switch (v.getId())
			{
			case R.id.iv_cancel:
				Intent mIntent = new Intent();
				setResult(RESULT_OK, mIntent);
				ListViewCheckBoxActivity.this.finish();
				break;
			case R.id.iv_ok:
				getDB();
				break;
			}
		}

	};
	
	public void getDB()
	{
		Set set = mAdapter.getIsSelected().entrySet();
		Iterator iterator = set.iterator();
		int cnt = 0;
		String name = "";
		while (iterator.hasNext())
		{
			Map.Entry mapentry = (Map.Entry) iterator.next();
			if ((Boolean)mapentry.getValue())
			{
				cnt = cnt + 1;
				name = name + (String) mapentry.getKey() + "|";
				
				System.out.println(name);
			}
		}
		if (cnt == 0)
		{
			Toast("Please Pick One DB!");
			return;
		}else 
		{
			Bundle bundle = new Bundle();
			bundle.putString("dbNameString", name);
			Intent intent = new Intent();
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			ListViewCheckBoxActivity.this.finish();
		}
	}

	private void listDB()
	{
		mAdapter.clearItems();
		mAdapter.notifyDataSetChanged();
		listView.postInvalidate();
		BackupSetting backupSetting = new BackupSetting();
		Properties properties = backupSetting.loadConfig(this, MainActivity.DB_CONFIG_FULL_NAME);
		int length = properties.size()/2;
		FileData fd;
		for (int i = 0; i < length; i++)
		{
			fd = new FileData();
			fd.name = (String)properties.get("db"+i)+"--"+(String)properties.get("p"+i);
			fd.type = 0;
			//fd.name2 = (String)properties.get("p"+i);
			mAdapter.addItem(fd);
		}
		
		mAdapter.notifyDataSetChanged();
		listView.postInvalidate();
	}
	
	private void parseString(String str)
	{
	 	String[] arr1 = str.split("--");
	 	
	}


	// --begin--Toast--//
	public void Toast(String str)
	{
		Toast.makeText(ListViewCheckBoxActivity.this, str, Toast.LENGTH_LONG).show();
	}
	// --end--Toast--//
}
