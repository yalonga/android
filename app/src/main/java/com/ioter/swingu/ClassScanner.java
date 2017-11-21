package com.ioter.swingu;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.ioter.R;

public class ClassScanner
{
    private int device_icon;
    private String device_name;
    private String device_mac;
    private int device_type;
    private BluetoothDevice device;

    //»ý¼ºÀÚ¼±¾ð
    public ClassScanner(int device_icon, int device_type, BluetoothDevice device)
    {
        super();
        boolean debug = false;
        this.device_icon = device_icon;
        this.device_name = device.getName();
        this.device_mac = device.getAddress();
        this.device_type = device_type;
        this.device = device;
        //debug°ªÀÌ true·Î º¯°æµÉ°æ¿ì ±â±âÀÇ ¸ðµç °ªÀ» Ãâ·ÂÇÑ´Ù.
        if (debug)
        {
            Log.d("dsm362", Integer.toString(this.device_icon));
            Log.d("dsm362", this.device_name);
            Log.d("dsm362", this.device_mac);
            Log.d("dsm362", Integer.toString(this.device_type));
        }
    }

    //getter & setter¼±¾ð ÀÌ°÷ÀÇ getter°ú setterÀÇ °ªÀº ¹ÝÈ¯°ªµéÀÌ ÀÖ´Ù.
    public int getIcon()
    {
        return this.device_icon;
    }

    public String getName()
    {
        return this.device_name;
    }

    public String getMac()
    {
        return this.device_mac;
    }

    public int getType()
    {
        return this.device_type;
    }

    public BluetoothDevice getDevice()
    {
        return this.device;
    }

    public boolean getConnected()
    {
        //µð¹ÙÀÌ½º Å¸ÀÔÀÌ ºí·çÅõ½º°¡ ¿¬°áµÇÀÖ´Â»óÅÂÀÏ°æ¿ì true°ªÀ» ¸®ÅÏ ¾Æ´Ò°æ¿ì false¸¦ ¸®ÅÏÇÑ´Ù.
        if (this.device_type == R.mipmap.ic_action_bluetooth_connected) return true;
        else return false;
    }

    //device-type°ªÀ» ic_action_bluetooth_connected°ªÀ¸·Î º¯°æÇÏ¿© getConnected°¡ ½ÇÇàµÉ°æ¿ì True°ªÀ¸·Î ¹ÝÈ¯ÇÏ°Ô ÇÑ´Ù.
    public void setConnected()
    {
        this.device_type = R.mipmap.ic_action_bluetooth_connected;
    }

    //¸¶Áö¸·¾îµå·¹½º¸¦ ¸Þ°Ôº¯¼ö·Î¹Þ´Â setDisConnected
    public void setDisConnected(String last_addr)
    {
        //µð¹ÙÀÌ½º Å¸ÀÔÀÌ ic_action_bluetooth_connectedÀÏ°æ¿ì
        if (this.device_type == R.mipmap.ic_action_bluetooth_connected)
        {
            //¸¶Áö¸· ¾îµå·¹½º°¡ device_mac¿Í °°À»°æ¿ì
            if (last_addr.equals(device_mac))
                // µð¹ÙÀÌ½º Å¸ÀÔÀº ic_action_save·Î ÁöÁ¤µÈ´Ù.
                this.device_type = R.mipmap.ic_action_save;
            else
                //¾Æ´Ò°æ¿ì ºí·çÅõ½º·Î º¯°æ
                this.device_type = R.mipmap.ic_action_bluetooth;
        }
    }
}
