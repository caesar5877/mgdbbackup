package com.moleq.mgdbbackup;

import java.io.File;

public class FileWrapper implements Comparable
{
	
	private File file;
	
	
	public FileWrapper(File file)
	{
		this.file = file;
	}

	public int compareTo(Object obj)
	{
		FileWrapper castObj = (FileWrapper) obj;

		if (this.file.getName().compareTo(castObj.getFile().getName()) > 0)
		{
			return -1;
		}
		else if (this.file.getName().compareTo(castObj.getFile().getName()) < 0)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	
	public File getFile()
	{
		return this.file;
	}
}
