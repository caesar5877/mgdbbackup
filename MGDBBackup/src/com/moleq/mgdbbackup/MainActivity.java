package com.moleq.mgdbbackup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends ListActivity implements OnTimeSetListener
{
	public static Context[] arrayContexts = null;
	public static String[] arrayDB = null;
	public static String[] arrayPackage = null;
	
	Calendar calendar = Calendar.getInstance();
	
	///--------------begin-----------SDCARD--------------------------
	public static String DATABASE_NAME = "mpos.db";
	public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	
	public static File PATH_FILE = new File(SDCARD + "/MoleQ/dbbackup/");
	public static File SDCARD_DB_FILENAME = new File(PATH_FILE + "/"+ DATABASE_NAME);
	
	
	
	public static String PROP_NAME = "config.dat";
	public static File PROP_PATH = new File(SDCARD + "/MoleQ/Properties/");
	public static File PROP_FILE = new File(PROP_PATH+ "/"+PROP_NAME);
	public static String PROP_FULL_NAME = PROP_PATH+ "/"+PROP_NAME;
	
	public static String DB_CONFIG_NAME = "dbconfig.dat";
	public static File DB_CONFIG_FILE = new File(PROP_PATH+ "/"+DB_CONFIG_NAME);
	public static String DB_CONFIG_FULL_NAME = PROP_PATH+ "/"+DB_CONFIG_NAME;
	
	public static String LOG_NAME = "Log.txt";
	public static String LOG_FULL_NAME = PROP_PATH+"/" + LOG_NAME;
	///---------------end  ----------SDCARD--------------------------
	
	///--------------begin-----------Backup Properties--------------------------
	public static String i5 = "";
	public static String i7 = "";
	public static String i3 = "";
	public static String i2 = "";
	public static String i1 = "";
	public static int hourOfDay = 0;
	public static int minute = 0;
	///--------------end-------------Backup Properties--------------------------
	
	
	private BackupSetting bSetting;
	private ArrayList<HashMap<String, Object>> listItem;
	private MyAdapter adapter;
	private Properties pro;
	
	private Dialog logDialog;
	private ListView lv_dialog_list;
	private ArrayList<HashMap<String, Object>> data;
	
	private AlertDialog.Builder dialog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		check();
		loadConfig();
		setDB();
		display();
		new startService().execute();
	}
	
	
	
	class startService extends AsyncTask<String, Integer, String>
	{
		@Override
		protected String doInBackground(String... params)
		{
			if (isWorked())
			{
				System.out.println("isWorked()-->true");
			}
			else 
			{
				System.out.println("isWorked()-->false");
				startService(new Intent("com.android.runservice"));
			}
			
			return null;
		}
		
		
		public boolean isWorked()
		{
			ActivityManager myManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE); 
			ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
			for(int i = 0 ; i<runningService.size();i++) 
			{
				 if(runningService.get(i).service.getClassName().toString().equals("com.moleq.mgdbbackup.RunService")) 
				 {
					 return true;
				 }
			}
			return false;
		}
	}
	

	
	//---begin---check---// 
		public void check()
		{
			/*if(!android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState()))
			{
				Toast("No Valid SDCard!");
				return;
			}*/
				
			if (!PROP_PATH.exists())
			{
				
				PROP_PATH.mkdirs();
				Toast("Create a Properties Folder");
			}
			if (!PROP_FILE.exists())
			{
				try
				{
					initProperties();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				Toast("Create a Property File");
			}
			if (!DB_CONFIG_FILE.exists())
			{
				try
				{
					initDBConfig();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				Toast("Create a configuration File");
			}
			
			if (!PATH_FILE.exists())
			{
				PATH_FILE.mkdirs();
				Toast("Create a dbbackup Folder");
			}
		}
		//---end---check---// 
	
	
	//--begin--init---//
		public void initProperties()
		{
			bSetting = new BackupSetting();
			pro = new Properties();
			pro.put("t1", "Backup Folder");
			pro.put("t2", "Enable auto-backup");
			pro.put("t3", "Auto-backup time");
			pro.put("t4", "Backup Status");
			pro.put("t5", "How Many Copys Backup");
			pro.put("t6", "Backup DB Now");
			pro.put("t7", "Backup Multiple DBs");
			pro.put("i1", "/mnt/sdcard/MoleQ/dbbackup/");
			pro.put("i2", "ON");
			pro.put("i3", "0:0");
			pro.put("i4", "/mnt/sdcard/MoleQ/Properties/Log.txt");
			pro.put("i5", "5");
			pro.put("i6", "");
			pro.put("i7", "mpos.db--com.moleq.posdb|");
			bSetting.saveConfig(this, PROP_FULL_NAME, pro);
		}
		
		public void initDBConfig()
		{
			BackupSetting backupSetting = new BackupSetting();
			Properties properties = new Properties();
			properties.put("p0", "com.moleq.posdb");
			properties.put("p1", "com.example.demo");
			properties.put("db0", "mpos.db");
			properties.put("db1", "person.db");
			backupSetting.saveConfig(this, DB_CONFIG_FULL_NAME, properties);
		}
		//--end--init---//
	
	public void loadConfig()
	{
		bSetting = new BackupSetting();
		pro = bSetting.loadConfig(this, PROP_FULL_NAME);
		i7 = (String) pro.get("i7");
		i5 = (String) pro.get("i5");
		i1 = (String) pro.get("i1");
		i2 = (String) pro.get("i2");
		i3 = (String) pro.get("i3");
		//System.out.println("i3-->"+i3);
		String[] arr = i3.split(":");
		hourOfDay = Integer.parseInt(arr[0]);
		minute = Integer.parseInt(arr[1]);
		
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
	}
	
	public void setDB()
	{
		try
		{
			String[] arr = i7.split("\\|");
			int length = arr.length;
			//System.out.println(arr.length);
			arrayContexts = new Context[length];
			arrayDB = new String[length];
			arrayPackage = new String[length];
			for (int i = 0; i < arr.length; i++)
			{
				String[] arrSub = arr[i].split("--");
				arrayContexts[i] = this.createPackageContext(arrSub[1], CONTEXT_IGNORE_SECURITY);
				arrayDB[i] = arrSub[0];
				arrayPackage[i] = arrSub[1];
			}
		} catch (Exception e)
		{
			writeLog(LOG_FULL_NAME, e.getMessage());
		}
	}
	
	
	
	//----begin---display---------//
	public void display()
	{
		String hourOfDayStr = "";
		String minuteStr = "";
		if (listItem == null)
		{
			listItem = new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			bSetting = new BackupSetting();
			pro = bSetting.loadConfig(this, PROP_FULL_NAME);
			
			map.put("title", pro.get("t1"));
			map.put("info", pro.get("i1"));
			map.put("img", R.drawable.folder1);
			listItem.add(map);

			map = new HashMap<String, Object>();
			map.put("title", pro.get("t2"));
			map.put("info", pro.get("i2"));
			System.out.println((String)pro.get("i2"));
			if ("OFF".equals(pro.get("i2")))
				map.put("img", R.drawable.off);
			else
				map.put("img", R.drawable.on);
			listItem.add(map);

			
			map = new HashMap<String, Object>();
			map.put("title", pro.get("t3"));
			if ((hourOfDay+"").length()==1)
				hourOfDayStr = "0"+hourOfDay;
			else
				hourOfDayStr = ""+hourOfDay;
			if((minute+"").length()==1)
				minuteStr = "0"+minute;
			else
				minuteStr = "" + minute; 
			
			map.put("info", hourOfDayStr+":"+minuteStr);
			map.put("img", R.drawable.clock);
			listItem.add(map);
			
			map = new HashMap<String, Object>();
			map.put("title", pro.get("t4"));
			map.put("info", pro.get("i4"));
			map.put("img", R.drawable.sync);
			listItem.add(map);
			
			map = new HashMap<String, Object>();
			map.put("title", pro.get("t5"));
			map.put("info", pro.get("i5"));
			map.put("img", R.drawable.timer);
			listItem.add(map);
			
			map = new HashMap<String, Object>();
			map.put("title", pro.get("t6"));
			map.put("info", pro.get("i6"));
			map.put("img", R.drawable.syncname2);
			listItem.add(map);
			
			map = new HashMap<String, Object>();
			map.put("title", pro.get("t7"));
			map.put("info", pro.get("i7"));
			map.put("img", R.drawable.database);
			listItem.add(map);
			
			map = new HashMap<String, Object>();
			map.put("title", "");
			map.put("info", "");
			map.put("img", R.drawable.exit);
			listItem.add(map);
			adapter = new MyAdapter(this,listItem);
			setListAdapter(adapter);
			
			i1 = pro.get("i1").toString().trim();
		}
		else 
		{
			listItem.clear();
			HashMap<String, Object> map = new HashMap<String, Object>();
			bSetting = new BackupSetting();
			pro = bSetting.loadConfig(this, PROP_FULL_NAME);
			map.put("title", pro.get("t1"));
			map.put("info", pro.get("i1"));
			map.put("img", R.drawable.folder1);
			listItem.add(map);

			map = new HashMap<String, Object>();
			map.put("title", pro.get("t2"));
			map.put("info", pro.get("i2"));
			System.out.println((String)pro.get("i2"));
			if ("OFF".equals(pro.get("i2")))
				map.put("img", R.drawable.off);
			else
				map.put("img", R.drawable.on);
			listItem.add(map);

			map = new HashMap<String, Object>();
			map.put("title", pro.get("t3"));
			
			if ((hourOfDay+"").length()==1)
				hourOfDayStr = "0"+hourOfDay;
			else
				hourOfDayStr = ""+hourOfDay;
			if((minute+"").length()==1)
				minuteStr = "0"+minute;
			else
				minuteStr = "" + minute; 
			
			map.put("info", hourOfDayStr+":"+minuteStr);
			map.put("img", R.drawable.clock);
			listItem.add(map);
			
			map = new HashMap<String, Object>();
			map.put("title", pro.get("t4"));
			map.put("info", pro.get("i4"));
			map.put("img", R.drawable.sync);
			listItem.add(map);
			
			map = new HashMap<String, Object>();
			map.put("title", pro.get("t5"));
			map.put("info", pro.get("i5"));
			map.put("img", R.drawable.timer);
			listItem.add(map);
			
			map = new HashMap<String, Object>();
			map.put("title", pro.get("t6"));
			map.put("info", pro.get("i6"));
			map.put("img", R.drawable.syncname2);
			listItem.add(map);
			
			map = new HashMap<String, Object>();
			map.put("title", pro.get("t7"));
			map.put("info", pro.get("i7"));
			map.put("img", R.drawable.database);
			listItem.add(map);
			
			map = new HashMap<String, Object>();
			map.put("title", "");
			map.put("info", "");
			map.put("img",  R.drawable.exit);
			listItem.add(map);
			adapter.setSource(listItem);
			adapter.notifyDataSetChanged();
			
			i1 = pro.get("i1").toString().trim();
		}
		
	}
	
	//----end---display---------//

	public final class ViewHolder
	{
		public ImageView img;
		public TextView title;
		public TextView info;
		public Button viewBtn;
	}

	
	//--begin--define adapter--//
	public class MyAdapter extends BaseAdapter
	{
		private ArrayList<HashMap<String, Object>> listItem;
		private LayoutInflater mInflater;
		
		public MyAdapter(Context context,
				ArrayList<HashMap<String, Object>> listItem)
		{
			this.mInflater = LayoutInflater.from(context);
			this.listItem = listItem;
		}

		public MyAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
		}

		
		public void setSource(ArrayList<HashMap<String, Object>> listItem)
		{
			this.listItem = listItem;
		}
		
		
		public int getCount()
		{
			return listItem.size();
		}

		public Object getItem(int arg0)
		{
			return null;
		}

		public long getItemId(int arg0)
		{
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent)
		{

			ViewHolder holder = null;
			if (convertView == null)
			{

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.vlist2, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.info = (TextView) convertView.findViewById(R.id.info);
				holder.viewBtn = (Button) convertView
						.findViewById(R.id.view_btn);
				convertView.setTag(holder);

			} else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			holder.img.setBackgroundResource((Integer) listItem.get(position).get(
					"img"));
			holder.title.setText((String) listItem.get(position).get("title"));
			holder.info.setText((String) listItem.get(position).get("info"));
			if (position==0)
				holder.viewBtn.setText("Browse");
			if (position==1)
				holder.viewBtn.setText("Click");
			if (position==2)
				holder.viewBtn.setText("Pick Time");
			if (position==3)
				holder.viewBtn.setText("History");
			if (position==4)
				holder.viewBtn.setText("Click");
			if (position==5)
				holder.viewBtn.setText("Click");
			if (position==6)
				holder.viewBtn.setText("Click");
			if (position==7)
				holder.viewBtn.setText("Exit");
			
			

			holder.viewBtn.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					if (position==0)
					{
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, FilePickerActivity.class);
						MainActivity.this.startActivityForResult(intent,CONTEXT_RESTRICTED);
					}
					if (position==1)
					{
						enable();
					}
					if (position==2)
					{
						calendar.setTimeInMillis(System.currentTimeMillis());
						new TimePickerDialog(MainActivity.this,MainActivity.this,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
					}
					if (position==3)
					{
						logDialogWindow();
					}
					if (position==4)
					{
						//setDays();
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, winActivity.class);
						MainActivity.this.startActivityForResult(intent, CONTEXT_IGNORE_SECURITY);
					}
					if (position==5)
					{
						backupNow("Backup", "Do You Backup Database Immediately?");
					}
					if (position==6)
					{
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, ListViewCheckBoxActivity.class);
						MainActivity.this.startActivityForResult(intent,5);
					}
					
					if (position==7)
					{
						exit("Exit", "Do You Want To Exit?");
					}
				}
			});
			return convertView;
		}
	}
	//--end--define adapter--//
	
	public void backupNow(String title, String message)
	{
		dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setTitle(title).setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(message)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						Intent intent = new Intent(MainActivity.this, BackupBroadCast.class);
						Bundle bl = new Bundle();
						bl.putString("type", "Now");
						intent.putExtras(bl);
						sendBroadcast(intent);
						Toast("Backup Finished!");
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				}).create().show();
	}
	
	
	public void exit(String title, String message)
	{
		dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setTitle(title).setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(message)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				}).create().show();
	}
	
	public void setDays()
	{
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, winActivity.class);
		MainActivity.this.startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CONTEXT_RESTRICTED)
		{
			String Path = "";
			try {
				Bundle bundle = data.getExtras();
				Path = bundle.getString("path");
			} catch (Exception e) {
				Path = "";
			}
			if (Path != "") {
				try {
					bSetting = new BackupSetting();
					pro = bSetting.loadConfig(this, PROP_FULL_NAME);
					pro.setProperty("i1", Path);
					bSetting.saveConfig(this, PROP_FULL_NAME, pro);
					i1 = Path;
					display();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Path = "";
		}
		if (requestCode == CONTEXT_IGNORE_SECURITY)
		{
			try
			{
				Bundle bundle = data.getExtras();
				i5 = bundle.getString("days");
				//System.out.println("days-->"+i5);
				if (i5 != null)
				{
					bSetting = new BackupSetting();
					pro = bSetting.loadConfig(this, PROP_FULL_NAME);
					pro.setProperty("i5", i5);
					bSetting.saveConfig(this, PROP_FULL_NAME, pro);
					display();
					//deleteDB(Integer.parseInt(DAYS));
				}
			} catch (Exception e)
			{
				i5 = (String) pro.get("i5");
			}
		}
		if (requestCode == 5)
		{
			try
			{
				Bundle bundle = data.getExtras();
				i7 = bundle.getString("dbNameString");
				//System.out.println("dbNameString-->"+i7);
				if (i7 != null)
				{
					String[] arr = i7.split("\\|");
					int length = arr.length;
					//System.out.println(arr.length);
					arrayContexts = new Context[length];
					arrayDB = new String[length];
					arrayPackage = new String[length];
					for (int i = 0; i < arr.length; i++)
					{
						String[] arrSub = arr[i].split("--");
						arrayContexts[i] = this.createPackageContext(arrSub[1], CONTEXT_IGNORE_SECURITY);
						arrayDB[i] = arrSub[0];
						arrayPackage[i] = arrSub[1];
					}
					BackupSetting bSetting = new BackupSetting();
					Properties pro = bSetting.loadConfig(this, PROP_FULL_NAME);
					pro.setProperty("i7", i7);
					bSetting.saveConfig(MainActivity.this, PROP_FULL_NAME, pro);
					display();
				}
			} catch (Exception e)
			{
				i7 = (String)pro.get("i7");
			}
		}
	}
	
	
	
	
	public void logDialogWindow()
	{
		// bindStateData();
		if (logDialog == null)
		{
			logDialog = new Dialog(this, R.style.Transparent_dialog);
			logDialog.setContentView(R.layout.perview);
		}
		lv_dialog_list = (ListView) logDialog.findViewById(R.id.lv_dialog_list);
		data = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hm = null;
		
		try
		{
			FileReader fr = new FileReader(LOG_FULL_NAME);
			BufferedReader in = new BufferedReader(fr);
			String s = null;
			while ((s=in.readLine())!=null)
			{
				hm = new HashMap<String, Object>();
				hm.put("data", s);
				if (s.contains("Success"))
					hm.put("image", R.drawable.checkg);
				else if (s.contains("Deleted"))
					hm.put("image", R.drawable.warning1);
				else
					hm.put("image", R.drawable.error);
				data.add(hm);
			}
				
			in.close();
			fr.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		SimpleAdapter sa = new SimpleAdapter(this, data, R.layout.vlist3,
				new String[]
				{ "data", "image" }, new int[]
				{ R.id.list_text1, R.id.list_image });
		lv_dialog_list.setAdapter(sa);
		lv_dialog_list
				.setOnItemClickListener(new AdapterView.OnItemClickListener()
				{
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id)
					{
						

					}
				});

		logDialog.show();
		logDialog.setCanceledOnTouchOutside(true);
	}

	public void enable()
	{
		bSetting = new BackupSetting();
		pro = bSetting.loadConfig(this, PROP_FULL_NAME);
		if ("OFF".equals(pro.get("i2")))
		{
			System.out.println("ON");
			pro.setProperty("i2", "ON");
			getTime();
		}
		else 
		{
			System.out.println("OFF");
			pro.setProperty("i2", "OFF");
			cancelIntent();
		}
			
		bSetting.saveConfig(this, PROP_FULL_NAME, pro);
		display();
	}
	
	public void getTime()
	{
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, 0);
		long nowLong = now.getTimeInMillis();
		long alarmLong = calendar.getTimeInMillis();
		if (alarmLong>=nowLong)
		{
			System.out.println("alarmLong >= nowLong");
			startIntent(alarmLong);
		} 
		else
		{
			System.out.println("alarmLong < nowLong");
			long nextLong = alarmLong + 24*3600*1000;
			startIntent(nextLong);
		}
	}
	
	
	
	public void startIntent(long time)
	{
		try
		{
			Intent intent = new Intent(MainActivity.this, BackupBroadCast.class);
			Bundle bl = new Bundle();
			bl.putString("type", "Activity");
			intent.putExtras(bl);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,time, 24*3600*1000, pendingIntent);
			//deleteDB(Integer.parseInt(i5));
		} catch (Exception e)
		{
			writeLog(LOG_FULL_NAME, e.getMessage()+".........");
		}
	}
	
	public void cancelIntent()
	{
		Intent intent = new Intent(MainActivity.this, BackupBroadCast.class);
		PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pi);
	}

	//--begin--set alarm and broadcast--//
	public void onTimeSet(TimePicker view, int hod, int min)
	{
		System.out.println("onTimeSet"+"  "+ hod+ "  "+min);
		bSetting = new BackupSetting();
		pro.setProperty("i3", hod+":"+min);
		bSetting.saveConfig(this, PROP_FULL_NAME, pro);
		hourOfDay = hod;
		minute = min;
		
		calendar.set(Calendar.HOUR_OF_DAY, hod);
		calendar.set(Calendar.MINUTE, min);
		
		if ("ON".equals(pro.getProperty("i2")))
			getTime();
		else
			Toast("Warning:--Enable auto-backup is OFF--");
		display();
	}
	//--end--set alarm and broadcast--//
	
	
	
	
	
	//--begin--Toast--//
	public void Toast(String str)
	{
		Toast.makeText(MainActivity.this, str,Toast.LENGTH_LONG).show();
	}
	//--end--Toast--//
	
	//----------begin----------log------------------------------//
	public void readLog(String file)
	{
		try
		{
			FileReader fr = new FileReader(file);
			BufferedReader in = new BufferedReader(fr);
			String s = null;
			while ((s=in.readLine())!=null)
			{
				System.out.println(s);
			}
				
			in.close();
			fr.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	//----------end------------log------------------------------//
	//----begin----writeLog----//
	public void writeLog(String file, String content)
	{
		BufferedWriter out = null;
		try
		{
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file,true)));
			out.write(content+"\r\n");
			out.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	//----end------writeLog----//
	
	//-----begin----deletedb--------------//
	public void deleteDB(int day) throws Exception
	{
		PATH_FILE = new File(i1);
		System.out.println("deleteDB-->" + PATH_FILE);
		Date date;
		Date dateNow;
		Calendar cal = Calendar.getInstance();
		Calendar calNow = Calendar.getInstance();
		File[] delFile;
		if (PATH_FILE.exists() && PATH_FILE.isDirectory())
		{
			delFile = PATH_FILE.listFiles(new FileFilterUtil());
			int i = delFile.length;
			System.out.println(delFile.length);
			for (int j = 0; j < i; j++)
			{
				if (delFile[j].isDirectory())
				{
					date = new Date(delFile[j].lastModified());
					dateNow = new Date();
					cal.setTime(date);
					calNow.setTime(dateNow);
					int dec = calNow.get(Calendar.DAY_OF_YEAR)
							- cal.get(Calendar.DAY_OF_YEAR);

					if (dec >= day)
					{
						//System.out.println(delFile[j].getName());
						del(delFile[j].getAbsolutePath());
						//System.out.println(delFile[j].getAbsolutePath());
						//delFile[j].delete();
					}
				}
			}

		}

	}
	//-----end------deletedb--------------//
	// delete filepath 下面全部内容
	public void del(String filepath) throws Exception
	{
		File f = new File(filepath);
		if (f.exists() && f.isDirectory())
		{
			if(f.listFiles().length !=0)
			{
				File delFile[] = f.listFiles();
				System.out.println(f.listFiles().length);
				int i = f.listFiles().length;
				for (int j = 0; j < i; j++)
				{
					if (delFile[j].isDirectory())
					{
						del(delFile[j].getAbsolutePath());
					}
					delFile[j].delete();
				}
			}
			if (f.listFiles().length == 0)
			{
				f.delete();
				writeLog(LOG_FULL_NAME, "Deleted!-->"+ f.getAbsolutePath());
			} 
		}
	}
}
