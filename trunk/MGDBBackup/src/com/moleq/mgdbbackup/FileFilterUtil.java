package com.moleq.mgdbbackup;
import java.io.*;

public class FileFilterUtil implements FileFilter
{

	public boolean accept(File pathname)
	{
		if(pathname.isDirectory()&&pathname.getName().length()==14)
		{
			return true;
		}else 
		{
			return false;
		}
	}
}
