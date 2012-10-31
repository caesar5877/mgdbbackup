package com.moleq.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBHelper extends SQLiteOpenHelper
{
	private final static int DATABASE_VERSION = 1;
	private SQLiteDatabase db;

	public DBHelper(Context context)
	{
		super(context, null, null, DATABASE_VERSION);
	}

	public DBHelper(Context context, String name)
	{
		super(context, name, null, DATABASE_VERSION);
	}

	public DBHelper(Context context, String name, CursorFactory factory,
			int version)
	{
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}

	public String sqlExecute(String sql)
	{
		try
		{
			db = this.getWritableDatabase();
			db.execSQL(sql);
		} catch (Exception e)
		{
			String error = e.getMessage();
			return error;
		}
		return "";
	}

	public Cursor select(String sql)
	{
		try
		{
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);
			return cursor;
		} catch (Exception e)
		{
			DBError.error = e.getMessage();
			return null;
		}
	}

	public long ImageInsert(String upc, byte[] b, byte[] b2)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("F01", upc);
		values.put("ip_image", b);
		values.put("ip_image_sm", b2);
		long rowid = db.insert("t_item_pic", null, values);
		return rowid;
	}

	public int ImageUpdate(String upc, byte[] b, byte[] b2)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("ip_image", b);
		values.put("ip_image_sm", b2);
		String[] args =
		{ String.valueOf(upc) };
		return db.update("t_item_pic", values, "F01=?", args);
	}
}