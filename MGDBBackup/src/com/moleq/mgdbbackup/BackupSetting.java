package com.moleq.mgdbbackup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.content.Context;

public class BackupSetting
{
	
	public Properties loadConfig(Context context, String file)
	{
		Properties properties = new Properties();
		try
		{
			FileInputStream s = new FileInputStream(file);
			properties.load(s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return properties;
	}
	
	public void saveConfig(Context context, String file, Properties properties)
	{
		try
		{
			FileOutputStream s = new FileOutputStream(file, false);
			properties.store(s, "");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
