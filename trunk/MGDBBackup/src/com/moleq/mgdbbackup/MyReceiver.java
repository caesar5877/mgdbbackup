package com.moleq.mgdbbackup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver
{

	public static String DATABASE_NAME = "mpos.db";
	public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	public static String PATH = "";
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
	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
			{
				if (check())
				{
					Thread.sleep(2000);
					//Toast.makeText(context, "check() is true", Toast.LENGTH_SHORT).show();
					System.out.println("true");
					Intent i = new Intent(RunService.ACCOUNT_SERVICE);
					i.setClass(context, RunService.class);
					context.startService(i);
				}
				else 
				{
					Thread.sleep(2000);
					//Toast.makeText(context, "check() is false", Toast.LENGTH_SHORT).show();
					System.out.println("false");
					intent.setClass(context, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			}
		} catch (Exception e)
		{
			writeLog(LOG_FULL_NAME, e.getMessage());
		}
		
	}
	
	public boolean check()
	{
		try
		{
			Thread.sleep(5000);
			//if(!android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) return false;
			if (!PROP_PATH.exists()) return false;
			if (!PROP_FILE.exists()) return false;
			if (!DB_CONFIG_FILE.exists()) return false;
			if (!PATH_FILE.exists()) return false;
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
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
}