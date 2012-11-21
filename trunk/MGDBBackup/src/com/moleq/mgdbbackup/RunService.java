package com.moleq.mgdbbackup;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class RunService extends Service
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
	private Properties pro;
	
	@Override
	public void onCreate()
	{
		System.out.println("RunService-->onCreate-->service");
		super.onCreate();
		
		loadConfig();
		setDB();
		launchService();
		
	}
	
	public void launchService()
	{
		if ("ON".equals(i2))
		{
			getTime();
		}
		else //service call activity and notice turning on enable auto-backup 
		{
			System.out.println("Enable auto-backup");
			Intent intent = new Intent(RunService.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
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
			Intent intent = new Intent(RunService.this, BackupBroadCast.class);
			Bundle bl = new Bundle();
			bl.putString("type", "Service");
			intent.putExtras(bl);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,time, 24*3600*1000, pendingIntent);
			//deleteDB(Integer.parseInt(i5));
		} catch (Exception e)
		{
			System.out.println(e.getMessage()+".........");
		}
	}
	
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
			} 
		}
	}
	
	
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
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public void onStart(Intent intent, int startId)
	{
		System.out.println("RunService-->onStart-->service");
		super.onStart(intent, startId);
		
	}
	
	
	@Override
	public void onDestroy()
	{
		System.out.println("RunService-->onDestroy");
		super.onDestroy();
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
		System.out.println("RunService-->onUnbind");
		return super.onUnbind(intent);
	}
	
	@Override
	public void onRebind(Intent intent)
	{
		System.out.println("RunService-->onRebind");
		super.onRebind(intent);
	}
	@Override
	public IBinder onBind(Intent arg0)
	{
		System.out.println("RunService-->onBind");
		return null;
	}
}
