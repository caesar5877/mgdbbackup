package com.moleq.mgdbbackup;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.moleq.mgdbbackup.FilePickerAdapter;
import com.moleq.mgdbbackup.FileData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class FilePickerActivity extends Activity
{

	private ListView listViewFilePicker;
	private File mCurrentDirectory;
	FilePickerAdapter mFileAdapter;
	private ImageView iv_cancel;
	private ImageView iv_ok;
	private TextView tv_path;
	
	String fileEndings[] ={ "jpgee" };
	String imagePath = "";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filelist);
		listViewFilePicker = (ListView) findViewById(R.id.filelist);
		listViewFilePicker.setCacheColorHint(0x00000000);
		mFileAdapter = new FilePickerAdapter(this);
		listViewFilePicker.setAdapter(mFileAdapter);
		iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
		iv_cancel.setOnClickListener(onClickListener);

		iv_ok = (ImageView) findViewById(R.id.iv_ok);
		iv_ok.setOnClickListener(onClickListener);
		
		tv_path = (TextView) findViewById(R.id.tv_path);
		mCurrentDirectory = new File(Environment
				.getExternalStorageDirectory().getAbsolutePath());

		ListView.OnItemClickListener lv2click = new ListView.OnItemClickListener()
		{

			public void onItemClick(AdapterView parent, View view,
					int position, long id)
			{
				int fid = mFileAdapter.getItemType((int) id);
				String mPath = "";
				try
				{
					if (fid == 1)
					{
						String s1 = mFileAdapter.getItem((int) id).name;
						if (s1.equals("Back..."))
						{
							mPath = mCurrentDirectory.getParent();
						} else
						{
							mPath = mCurrentDirectory.getPath() + "/" + s1
									+ "/";
						}
						tv_path.setText(mPath);
						mCurrentDirectory = new File(mPath);
						ListFile(mCurrentDirectory);
					} else
					{
						Bundle bundle = new Bundle();
						bundle.putString("path", mCurrentDirectory.getPath()
								+ "/" + mFileAdapter.getItem((int) id).name);
						bundle.putString("imagePath",
								mCurrentDirectory.getPath());
						Intent mIntent = new Intent();
						mIntent.putExtras(bundle);
						setResult(RESULT_OK, mIntent);
						FilePickerActivity.this.finish();

					}
				} catch (Exception e)
				{
					Toast.makeText(FilePickerActivity.this, "Can not operate.",
							Toast.LENGTH_LONG).show();
					String error = e.getMessage();
					return;
				}
			}
		};
		ListFile(mCurrentDirectory);
		listViewFilePicker.setOnItemClickListener(lv2click);
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
				FilePickerActivity.this.finish();
				break;
			case R.id.iv_ok:
				getFolderPath();
				break;
			}
		}

	};
	
	public void getFolderPath()
	{
		Set set = mFileAdapter.getIsSelected().entrySet();
		Iterator iterator = set.iterator();
		int cnt = 0;
		String name = "";
		while (iterator.hasNext())
		{
			Map.Entry mapentry = (Map.Entry) iterator.next();
			if ((Boolean)mapentry.getValue())
			{
				cnt = cnt + 1;
				name =(String) mapentry.getKey();
			}
		}
		if (cnt >1)
		{
			Toast("Please Pick ONLY One Folder!");
			return;
		}else if (cnt == 0)
		{
			Toast("Please Pick One Folder!");
			return;
		}else 
		{
			//System.out.println("name-->"+name);
			if("Back...".equals(name))
			{
				Toast("It is NOT a Folder!");
				return;
			}
			else 
			{
				Bundle bundle = new Bundle();
				bundle.putString("path", mCurrentDirectory.getPath()
						+ "/" + name + "/");
				
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				FilePickerActivity.this.finish();
			}
		}
	}

	private void ListFile(File aDirectory)
	{
		mFileAdapter.clearItems();
		mFileAdapter.notifyDataSetChanged();
		listViewFilePicker.postInvalidate();
		if (!aDirectory.getPath().equals("/sdcard"))
		{
			FileData fd = new FileData();
			fd.name = "Back...";
			fd.type = 1;
			mFileAdapter.addItem(fd);
		}
		for (File f : aDirectory.listFiles())
		{
			if (f.isDirectory())
			{
				FileData fd = new FileData();
				fd.name = f.getName();
				fd.type = 1;
				mFileAdapter.addItem(fd);
			} else
			{
				if (checkEnds(f.getName().toLowerCase()))
				{
					FileData fd = new FileData();
					fd.name = f.getName();
					fd.type = 0;
					mFileAdapter.addItem(fd);
				}
			}
		}
		mFileAdapter.notifyDataSetChanged();
		listViewFilePicker.postInvalidate();
	}

	private boolean checkEnds(String checkItsEnd)
	{
		for (String aEnd : fileEndings)
		{
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	// --begin--Toast--//
	public void Toast(String str)
	{
		Toast.makeText(FilePickerActivity.this, str, Toast.LENGTH_LONG).show();
	}
	// --end--Toast--//
}
