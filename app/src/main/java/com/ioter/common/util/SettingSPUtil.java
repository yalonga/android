package com.ioter.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ioter.AppApplication;

public class SettingSPUtil
{
    public static final String Username = "username";
    public static final String Warehouse_Id = "warehouseId";
    public static final String Shelf_Id = "shelfId";
    
    public static SharedPreferences getSp()
    {
        return AppApplication.getApplication().getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    public static void putBoolean(String key, boolean value)
    {
        getSp().edit().putBoolean(key, value).commit();
    }

    public static void putInt(String key, int value)
    {
        getSp().edit().putInt(key, value).commit();
    }

    public static void putLong(String key, long value)
    {
        getSp().edit().putLong(key, value).commit();
    }

    public static void putString(String key, String value)
    {
        getSp().edit().putString(key, value).commit();
    }

    public static boolean getBoolean(String key)
    {
        return getSp().getBoolean(key, false);
    }

    public static int getInt(String key)
    {
        return getSp().getInt(key, 0);
    }

    public static long getLong(String key)
    {
        return getSp().getLong(key, 0);
    }

    public static String getString(String key)
    {
        return getSp().getString(key, "");
    }
}