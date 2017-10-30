package com.ioter.common.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ioter.common.util.ToastUtil;

import java.util.ArrayList;

public class MySqliteHelper
{
    private static MySqliteHelper mInstance;
    private DatabaseHelper mSqliteHelper;
    private SQLiteDatabase mDb;

    public static MySqliteHelper getInstance(Context context)
    {
        if(mInstance == null)
        {
            mInstance = new MySqliteHelper(context);
        }
        return mInstance;
    }

    private MySqliteHelper(Context context)
    {
        mSqliteHelper = DatabaseHelper.getInstance(context);
        initDatabase();
    }

    private void closeDatabase()
    {
        if(mDb != null || mDb.isOpen())
        {
            mDb.close();
        }
    }

    private void initDatabase()
    {
        synchronized (MySqliteHelper.this)
        {
            if(mDb == null || !mDb.isOpen())
            {
                mDb = mSqliteHelper.getReadableDatabase();
            }
        }
    }

    /**
     * 批量插入数据
     *
     * @param dataList
     */
    public void insert(ArrayList<ClothesData> dataList)
    {
        if (dataList == null || dataList.size() == 0)
        {
            return;
        }
        initDatabase();
        mDb.beginTransaction();
        for (int i = 0; i < dataList.size(); i++)
        {
            ClothesData data = dataList.get(i);
            Object[] bindArgs = new Object[]
            { data.mEpc, data.mStyleNum, data.mName, data.mModel, data.mColour, data.mSize, data.mPrice, data.mDesign };
            mDb.execSQL("insert into " + DatabaseHelper.TableName
                    + "(epc, styleNum, name, model, colour, size, price, design) values(?,?,?,?,?,?,?,?)", bindArgs);
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }

    /**
     * 根据Epc查找数据
     *
     * @param epc
     */
    public ClothesData queryWithEpc(String epc)
    {
        initDatabase();
        mDb.beginTransaction();
        Cursor cursor = mDb.rawQuery("select * from " + DatabaseHelper.TableName + " where epc like ?", new String[]
        { epc });
        ToastUtil.toast(cursor.getCount() + "");
        ClothesData data = new ClothesData();
        while (cursor.moveToNext())
        {
            data.mEpc = cursor.getString(1);
            data.mStyleNum = cursor.getString(2);
            data.mName = cursor.getString(3);
            data.mModel = cursor.getString(4);
            data.mColour = cursor.getString(5);
            data.mSize = cursor.getString(6);
            data.mPrice = cursor.getString(7);
            data.mDesign = cursor.getString(8);
        }
        cursor.close();
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        return data;
    }

}
