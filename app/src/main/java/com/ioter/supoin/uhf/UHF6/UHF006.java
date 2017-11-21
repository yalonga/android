package com.ioter.supoin.uhf.UHF6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ioter.supoin.Comm;
import com.ioter.supoin.uhf.UHF5helper.InventoryBuffer;
import com.ioter.supoin.uhf.serialport.SerialPort;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by WGC on 2017/8/30.
 */

public class UHF006 {
    public static SerialPort mSerialPort = null;
    //private FileDescriptor mFd;
    public static FileInputStream mFileInputStream;
    public static FileOutputStream mFileOutputStream;
    public static LocalBroadcastManager mLocalBroadcastManager ;

    public static  boolean Connect(String posPort, int posBaud) {
        try {
            mSerialPort = new SerialPort(new File(posPort), posBaud, 0);
            mFileInputStream= (FileInputStream)mSerialPort.getInputStream();
            mFileOutputStream = (FileOutputStream) mSerialPort.getOutputStream();
            mLocalBroadcastManager= LocalBroadcastManager.getInstance(Comm.context);
            return true;
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 发送数据，使用synchronized()防止并发操作。
     * @param btArySenderData	要发送的数据
     * @return	成功 :0, 失败:-1
     */
    private boolean sendMessage(byte[] btArySenderData) {

        try {
            synchronized (mFileOutputStream) {		//防止并发
                mFileOutputStream.write(btArySenderData);
                return true;
            }
        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 存盘标签(快速模式)，标签数据刷新。
     * @param btCmd					命令类型(用于指定类型的刷新)
     * @param curInventoryBuffer	当前标签数据
     */
    private void GetHardwareVer(byte btCmd, InventoryBuffer curInventoryBuffer) {
        Intent itent = new Intent("GetHardwareVer");
        itent.putExtra("cmd", btCmd);
        mLocalBroadcastManager.sendBroadcast(itent);
    };

    public final BroadcastReceiver mRecv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}
