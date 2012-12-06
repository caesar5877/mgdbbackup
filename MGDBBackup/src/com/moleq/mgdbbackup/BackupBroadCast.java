package com.moleq.mgdbbackup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BackupBroadCast extends BroadcastReceiver
{
	private static String DATABASE_NAME = "mpos.db";
	private static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	private static String LOG_FULL_NAME = SDCARD + "/MoleQ/Properties/Log.txt";
	private File backupSubFolderPath = null;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			System.out.println("intent-->"+intent.getExtras().getString("type"));
			if ("Service".equals(intent.getExtras().getString("type")))
			{
				System.out.println("got it-->Service");
				String backupRootFolderPath = RunService.i1;
				
				//-----time stamp-----// 
				Date curDate = new Date(System.currentTimeMillis());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				String timestamp = formatter.format(curDate);
				//-----time stamp-----//
				
				backupSubFolderPath = new File(backupRootFolderPath + "/" +timestamp);
				backupSubFolderPath.mkdir();
				
				for (int i = 0; i < RunService.arrayPackage.length; i++)
				{
					BackupDatabase(RunService.arrayPackage[i], RunService.arrayDB[i]);
				}
				deleteDB(Integer.parseInt(MainActivity.i5));
			} 
			else if("Activity".equals(intent.getExtras().getString("type")))
			{
				System.out.println("got it-->Activity");
				String backupRootFolderPath = MainActivity.i1;
				
				//-----time stamp-----// 
				Date curDate = new Date(System.currentTimeMillis());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				String timestamp = formatter.format(curDate);
				//-----time stamp-----//
				
				backupSubFolderPath = new File(backupRootFolderPath + "/" +timestamp);
				backupSubFolderPath.mkdir();
				
				for (int i = 0; i < MainActivity.arrayPackage.length; i++)
				{
					BackupDatabase(MainActivity.arrayPackage[i], MainActivity.arrayDB[i]);
				}
				deleteDB(Integer.parseInt(MainActivity.i5));
			}
			else if ("Now".equals(intent.getExtras().getString("type")))
			{
				System.out.println("got it-->Now");
				String backupRootFolderPath = MainActivity.i1;
				
				//-----time stamp-----// 
				Date curDate = new Date(System.currentTimeMillis());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				String timestamp = formatter.format(curDate);
				//-----time stamp-----//
				
				backupSubFolderPath = new File(backupRootFolderPath + "/" +timestamp);
				backupSubFolderPath.mkdir();
				
				for (int i = 0; i < MainActivity.arrayPackage.length; i++)
				{
					BackupDatabase(MainActivity.arrayPackage[i], MainActivity.arrayDB[i]);
				}
				deleteDB(Integer.parseInt(MainActivity.i5));
			}
			
		} catch (Exception e)
		{
			System.out.println(e.getMessage()+".........................");
		}
		
		
		//BackupDatabase();
	}
	

	public void BackupDatabase(String packagePath, String dbName)
	{
		
		
		//String databaseFileName = "//data//data//com.moleq.posdb//databases//mpos.db";
		String databaseFileName = "//data//data//"+packagePath+"//databases//"+dbName;
		try
		{
			
			InputStream is = new FileInputStream(new File(databaseFileName));
			FileOutputStream fos = new FileOutputStream(backupSubFolderPath+"/"+dbName);

			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count = is.read(buffer)) > 0)
			{
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
			writeLog(LOG_FULL_NAME,"Success!-->"+backupSubFolderPath+"/"+dbName);
			
		} catch (Exception e)
		{
			writeLog(LOG_FULL_NAME, "Error!-->"+e.getMessage());
		}
	}
	
	
	//-----begin----deletedb--------------//
		public void deleteDB(int day) throws Exception
		{
			File PATH_FILE = new File(MainActivity.i1);
			System.out.println("deleteDB-->" + PATH_FILE);
			Date date;
			Date dateNow;
			Calendar cal = Calendar.getInstance();
			Calendar calNow = Calendar.getInstance();
			File[] delFile;
			if (PATH_FILE.exists() && PATH_FILE.isDirectory())
			{
				delFile = PATH_FILE.listFiles(new FileFilterUtil());
				//int i = delFile.length;
				//System.out.println(delFile.length);
				
				if (delFile !=null && delFile.length>0)
				{
					FileWrapper [] fileWrappers = new FileWrapper[delFile.length];
					
					for (int i = 0; i < delFile.length; i++)
					{
						fileWrappers[i] = new FileWrapper(delFile[i]);
					}
					Arrays.sort(fileWrappers);
					File[] sortedFiles = new File[delFile.length];
					for (int i = 0; i < delFile.length; i++)
					{
						sortedFiles[i] = fileWrappers[i].getFile();
						System.out.println("sortedFiles["+i+"]-->"+sortedFiles[i].getName());
					}
					
					for (int i = 0; i < sortedFiles.length; i++)
					{
						if (i>=day)
						{
							System.out.println("del--sortedFiles["+i+"]-->"+sortedFiles[i].getName());
							del(sortedFiles[i].getAbsolutePath());
						}
					}
				}
				
				
				
				
				
				/*for (int j = 0; j < i; j++)
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
				}*/

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
