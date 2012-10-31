package com.moleq.mgdbbackup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BackupBroadCast extends BroadcastReceiver
{
	private static String DATABASE_NAME = "mpos.db";
	private static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	private static String LOG_FULL_NAME = SDCARD + "/MoleQ/Properties/Log.txt";
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		System.out.println("got it");
		BackupDatabase();
	}
	

	public void BackupDatabase()
	{
		
		System.out.println(MainActivity.PATH);
		String backupRootFolderPath = MainActivity.PATH;
		
		//-----time stamp-----// 
		Date curDate = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = formatter.format(curDate);
		//-----time stamp-----//
		
		File backupSubFolderPath = new File(backupRootFolderPath + "/" +timestamp);
		String databaseFileName = "//data//data//com.moleq.posdb//databases//mpos.db";
		try
		{
			backupSubFolderPath.mkdir();
			InputStream is = new FileInputStream(new File(databaseFileName));
			FileOutputStream fos = new FileOutputStream(backupSubFolderPath+"/"+DATABASE_NAME);

			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count = is.read(buffer)) > 0)
			{
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
			writeLog(LOG_FULL_NAME,"Success!-->"+backupSubFolderPath+"/"+DATABASE_NAME);
			
		} catch (Exception e)
		{
			writeLog(LOG_FULL_NAME, "Error!-->"+e.getMessage());
		}
	}
	
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
}
