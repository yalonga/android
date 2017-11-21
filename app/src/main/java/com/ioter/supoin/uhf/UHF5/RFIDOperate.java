package com.ioter.supoin.uhf.UHF5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.clouiotech.util.Helper.Helper_ThreadPool;
import com.ioter.supoin.Comm;
import com.ioter.supoin.uhf.UHF5Base.CMD;
import com.ioter.supoin.uhf.UHF5Base.ReaderBase;
import com.ioter.supoin.uhf.UHF5Base.StringTool;
import com.ioter.supoin.uhf.UHF5helper.ISO180006BOperateTagBuffer;
import com.ioter.supoin.uhf.UHF5helper.InventoryBuffer;
import com.ioter.supoin.uhf.UHF5helper.OperateTagBuffer;
import com.ioter.supoin.uhf.UHF5helper.ReaderHelper;
import com.ioter.supoin.uhf.UHF5helper.ReaderSetting;
import com.ioter.supoin.uhf.serialport.SerialPort;
import com.ioter.supoin.uhf.serialport.SerialPortFinder;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static com.ioter.supoin.Comm.UHF5MESSAGE_TEXT;
import static com.ioter.supoin.Comm.btTarget;
import static com.ioter.supoin.Comm.comm_callback;
import static com.ioter.supoin.Comm.lsTagList;
import static com.ioter.supoin.Comm.operateType;
import static com.ioter.supoin.Comm.playSound;
import static com.ioter.supoin.Comm.session;
import static com.ioter.supoin.Comm.strPwd;
import static com.ioter.supoin.Comm.tagUHF1;
import static com.ioter.supoin.Comm.uhf5outpouwer;
import static com.ioter.supoin.uhf.UHF5helper.ReaderHelper.setContext;


public class RFIDOperate
{
    private String TAG = "RFIDOperate";


    private String scanCode;
    private Vibrator mvibrator;
    private boolean g_mvibrator = true;
    private boolean g_sound = true;
    private MediaPlayer mmediaplayer;
    public static final String CTRL_FILE = "/sys/devices/platform/psam/psam_state";

    private final String dc_power = "dc_power";
    private final String en = "en";
    private final String comstr = "com";
    private final String RootPath = "/sys/devices/platform/uhf/";
    private int[] uants = new int[]{1};
    UHF5Application myapp;
    static int readtime = 50, sleep = 0, operaCount = 0, operadatatype = 0;
    PowerManager pm;
    WakeLock wl;
    Context mContext;
    public static String opwStr;
    // connect
    public ReaderHelper mReaderHelper;
    public SerialPortFinder mSerialPortFinder;

    public SerialPort mSerialPort = null;
    public static ReaderBase mReader;
    public static ReaderSetting m_curReaderSetting;
    public static InventoryBuffer m_curInventoryBuffer;
    public static OperateTagBuffer m_curOperateTagBuffer;
    public static ISO180006BOperateTagBuffer m_curOperateTagISO18000Buffer;
    public LocalBroadcastManager lbm;

    public Handler mHandler;//扫描数据异步操作


    public String getScanCode()
    {
        return scanCode;
    }

    public void setScanCode(String scanCode)
    {
        this.scanCode = scanCode;
    }

    public void onCreate(Context context, String opwStr)
    {
        try
        {
            pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "com.supoin.hailan");
//            wl.acquire();
            this.mContext = context;
            this.opwStr = opwStr;

            // 连接RFID
            setContext(context);
            mSerialPortFinder = new SerialPortFinder();
            if (Connect(Comm.COM, Comm.Baudrate))
            {
                m_curReaderSetting = mReaderHelper.getCurReaderSetting();
                m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
                m_curOperateTagBuffer = mReaderHelper.getCurOperateTagBuffer();
                m_curOperateTagISO18000Buffer = mReaderHelper
                        .getCurOperateTagISO18000Buffer();
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void setOpw(String opwStr)
    {
        byte btOutputPower = 0x00;
        try
        {
            btOutputPower = (byte) Integer.parseInt(opwStr);
        } catch (Exception e)
        {
            e.printStackTrace();
            //Log.d(UHF005.tagUHF5,e.getMessage());
        }

        mReader.setOutputPower(m_curReaderSetting.btReadId, btOutputPower);
        m_curReaderSetting.btAryOutputPower = new byte[]{btOutputPower};
    }

    public boolean Connect(String posPort, int posBaud)
    {
        try
        {
            mSerialPort = new SerialPort(new File(posPort), posBaud, 0);
            Log.d("TAG", "Connect" + posPort);

            try
            {
                mReaderHelper = ReaderHelper.getDefaultHelper();
                mReaderHelper.setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream());
                mReader = mReaderHelper.getReader();
            } catch (Exception e)
            {
                e.printStackTrace();

                return false;
            }
            return true;
        } catch (SecurityException e)
        {
            e.printStackTrace();
            return false;
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        } catch (InvalidParameterException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void startScan(int Seeeion, int btTarget, int antNo)
    {
        try
        {
            mReaderHelper.setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream());
            mReader = mReaderHelper.getReader();
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        setOpw(this.opwStr);
        //m_curInventoryBuffer.clearInventoryPar();
        //设置天线
        m_curInventoryBuffer.lAntenna.add((byte) (antNo & 0xFF));

        m_curInventoryBuffer.bLoopInventoryReal = true;
        m_curInventoryBuffer.btRepeat = 0;
        // 每条命令的盘存次数
        String strRepeat = "1";
        m_curInventoryBuffer.btRepeat = (byte) Integer.parseInt(strRepeat);

        // if (mCbRealSession.isChecked()) {
        m_curInventoryBuffer.bLoopCustomizedSession = true;
        // session id:0:S0,1:S1,2:S2,3:S3
        m_curInventoryBuffer.btSession = (byte) (Seeeion & 0xFF);
        // Inventoried Flag 0:A,1:B
        m_curInventoryBuffer.btTarget = (byte) (btTarget & 0xFF);
        // } else {
        // m_curInventoryBuffer.bLoopCustomizedSession = false;
        // }
        m_curInventoryBuffer.clearInventoryRealResult();
        mReaderHelper.setInventoryFlag(true);
        mReaderHelper.clearInventoryTotal();
        byte btWorkAntenna = m_curInventoryBuffer.lAntenna
                .get(m_curInventoryBuffer.nIndexAntenna);
        if (btWorkAntenna < 0)
            btWorkAntenna = 0;
        mReader.setWorkAntenna(m_curReaderSetting.btReadId, btWorkAntenna);
        m_curReaderSetting.btWorkAntenna = btWorkAntenna;
        mLoopHandler.removeCallbacks(mLoopRunnable);
        mLoopHandler.postDelayed(mLoopRunnable, Comm.rfidSleep);
        IsFlushList = true;
    }

    public void stop()
    {
        mReaderHelper.setInventoryFlag(false);
        m_curInventoryBuffer.bLoopInventoryReal = false;
        mLoopHandler.removeCallbacks(mLoopRunnable);
        IsFlushList = false;
        refreshText();
    }

    public void onResume(Context context)
    {
        if (mReader != null)
        {
            if (!mReader.IsAlive())
                mReader.StartWait();
        }
    }

    private Handler mLoopHandler = new Handler();
    private Runnable mLoopRunnable = new Runnable()
    {
        public void run()
        {
            try
            {
                byte btWorkAntenna = m_curInventoryBuffer.lAntenna
                        .get(m_curInventoryBuffer.nIndexAntenna);
                if (btWorkAntenna < 0)
                    btWorkAntenna = 0;
                mReader.setWorkAntenna(m_curReaderSetting.btReadId, btWorkAntenna);
            } catch (Exception e)
            {

            }
            //playSound();
            mLoopHandler.postDelayed(this, Comm.rfidSleep);
        }
    };

    int TagsCountTest = 0,//之前不重复总数
            mTagsCount = 0,//现在不重复总数
            mtagstotal = 0,//现在总数
            TagsTotaltext = 0;//之前总数

    public void refreshText()
    {
        List<String> mAccessList = new ArrayList<String>();
        Message m = Message.obtain(mHandler, UHF5MESSAGE_TEXT);
        lsTagList = m_curInventoryBuffer.lsTagList;
        // playSound();
        if (mHandler == null)
        {
            return;
        }
        mHandler.sendMessage(m);

//        mTagsCount = m_curInventoryBuffer.lsTagList.size();
        mtagstotal = mReaderHelper.getInventoryTotal();
        // TagsTotaltext = Integer.parseInt(mTagsTotalText.getText().toString());
        if (mtagstotal != TagsTotaltext)
        {
            playSound();
            // soundPool.play(1, 1, 1, 0, 0, 1);
        }
//        if (mTagsCount != TagsCountTest) {
//            playSound();
//        }
//        TagsCountTest = mTagsCount;
        TagsTotaltext = mtagstotal;
    }

    private Boolean IsFlushList = true; // 是否刷列表

    public final BroadcastReceiver mRecv = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            byte btCmd = intent.getByteExtra("cmd", (byte) 0x00);
            if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_READER_SETTING) && btCmd == CMD.GET_FIRMWARE_VERSION)
            {
                String strVersion = String.valueOf(m_curReaderSetting.btMajor & 0xFF) + "." + String.valueOf(m_curReaderSetting.btMinor & 0xFF);
                comm_callback.onReceive(strVersion, 0);
            } else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL))
            {
                switch (btCmd)
                {
                    case CMD.REAL_TIME_INVENTORY:
                    case CMD.CUSTOMIZED_SESSION_TARGET_INVENTORY:
                        mLoopHandler.removeCallbacks(mLoopRunnable);
                        mLoopHandler.postDelayed(mLoopRunnable, Comm.rfidSleep);
                        //playSound();
                        // 刷新线程
                        Helper_ThreadPool.ThreadPool_StartSingle(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                while (IsFlushList)
                                {
                                    try
                                    {
                                        Thread.sleep(1000); // 一秒钟刷新一次
                                        refreshText();
                                        ;
                                    } catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        break;
                    case ReaderHelper.INVENTORY_ERR:
                    case ReaderHelper.INVENTORY_ERR_END:
                    case ReaderHelper.INVENTORY_END:
                        if (mReaderHelper.getInventoryFlag())
                        {
                            mLoopHandler.removeCallbacks(mLoopRunnable);
                            mLoopHandler.postDelayed(mLoopRunnable, Comm.rfidSleep);
                        } else
                        {
                            mLoopHandler.removeCallbacks(mLoopRunnable);
                        }
                        // 刷新线程
                        Helper_ThreadPool.ThreadPool_StartSingle(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                while (IsFlushList)
                                {
                                    try
                                    {
                                        Thread.sleep(1000); // 一秒钟刷新一次
                                        refreshText();
                                        ;
                                    } catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        break;
                }
            }

            if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_READER_SETTING) && Comm.opeT == operateType.getPower)
            {
                Message msg = new Message();
                Bundle b = new Bundle();
                if (m_curReaderSetting.btAryOutputPower != null)
                {
                    uhf5outpouwer = m_curReaderSetting.btAryOutputPower[0] & 0xFF;
                    int setSel = (uhf5outpouwer * 100 + 10 - 500) / 100;
                    opwStr = String.valueOf(uhf5outpouwer);
                    b.putInt("ant1Power", setSel);
//                    b.putInt("ant2Power", setSel);
//                    b.putInt("ant3Power", setSel);
//                    b.putInt("ant4Power", setSel);
                }
                msg.setData(b);
                Comm.mOtherHandler.sendMessage(msg);
            } else if ((intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_READER_SETTING) && Comm.opeT == operateType.setPower)
                    || (intent.getAction().equals(ReaderHelper.BROADCAST_WRITE_DATA) && Comm.opeT == operateType.setPower))
            {
                Message msg = new Message();
                Bundle b = new Bundle();
                boolean isSuc = false;
                if (m_curReaderSetting.btAryOutputPower != null)
                {
                    opwStr = String.valueOf(uhf5outpouwer);
                    isSuc = true;
                } else
                    isSuc = false;
                b.putBoolean("isSetPower", isSuc);
                msg.setData(b);
                Comm.mOtherHandler.sendMessage(msg);
            } else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_READER_SETTING) && Comm.opeT == operateType.getReg)
            {
                Message msg = new Message();
                Bundle b = new Bundle();
                switch (m_curReaderSetting.btRegion & 0xFF)
                {
                    case 0x01:
                        Comm.mPos1 = (m_curReaderSetting.btFrequencyStart & 0xFF) - 43;
                        Comm.mPos2 = (m_curReaderSetting.btFrequencyEnd & 0xFF) - 43;
                        // spinner_region.setSelection(1);
                        b.putInt("getReg", 1);
                        break;
                    case 0x02:
                        Comm.mPos1 = (m_curReaderSetting.btFrequencyStart & 0xFF) - 7;
                        Comm.mPos2 = (m_curReaderSetting.btFrequencyEnd & 0xFF) - 7;
                        b.putInt("getReg", 2);
                        break;
                    case 0x03:
                        Comm.mPos1 = m_curReaderSetting.btFrequencyStart & 0xFF;
                        Comm.mPos2 = m_curReaderSetting.btFrequencyEnd & 0xFF;
                        b.putInt("getReg", 0);
                        break;
                    case 0x04:
                        b.putInt("getReg", 3);
                        break;
                    default:
                        break;
                }
                msg.setData(b);
                Comm.mOtherHandler.sendMessage(msg);
            } else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_READER_SETTING) && Comm.opeT == operateType.setReg)
            {
                Message msg = new Message();
                Bundle b = new Bundle();
                Boolean isSetReg;
                int i = m_curReaderSetting.btRegion & 0xFF;
                if (i > 0 && i < 5)
                    isSetReg = true;
                else
                    isSetReg = false;
                b.putBoolean("isSetReg", isSetReg);
                msg.setData(b);
                Comm.mOtherHandler.sendMessage(msg);
            } else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_OPERATE_TAG) && Comm.opeT == operateType.readOpe)
            {
                Message msg = new Message();
                String strData = "";
                byte[] rdata = new byte[Integer.valueOf(operaCount) * 2];
                char[] out = null;
                if (m_curOperateTagBuffer.lsTagList.size() > 0)
                {
                    strData = m_curOperateTagBuffer.lsTagList.get(0).strData;
                    strData = strData.replace(" ", "");
//                    String s1=strData.substring(0,24);
//                    String s2=strData.substring(24,strData.length());
//                    byte[] rdata1 = new byte[6];
//                    byte[] rdata2 = new byte[(Integer.valueOf(operaCount) * 2)-6];
//                    Comm.Str2Hex(s1, 6, rdata1);
//                    Comm.Str2Hex(s2, (Integer.valueOf(operaCount) * 2)-6, rdata2);

                    Comm.Str2Hex(strData, operaCount, rdata);

                    if (operadatatype == 1)
                    {
                        out = new char[rdata.length];
                        for (int i = 0; i < rdata.length; i++)
                            out[i] = (char) rdata[i];
                        strData = String.valueOf(out);
                    } else if (operadatatype == 2)
                    {
                        try
                        {
                            strData = new String(rdata, "gbk");
                            // strData = new String(rdata2, "gbk");
                        } catch (UnsupportedEncodingException e)
                        {
                            Log.d(tagUHF1, "readOP err:" + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    Bundle b = new Bundle();
                    b.putString("readData", strData);
                    msg.setData(b);
                }
//                mHandler.sendMessage(m);
                Comm.mRWLHandler.sendMessage(msg);
            } else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_OPERATE_TAG) && Comm.opeT == operateType.writeOpe)
            {
                boolean isSucceed = false;
                if (m_curOperateTagBuffer.lsTagList.size() > 0)
                {
                    isSucceed = true;
                }
                Message msg = new Message();
                Bundle b = new Bundle();
                b.putBoolean("isWriteSucceed", isSucceed);
                msg.setData(b);
//                mHandler.sendMessage(m);
                Comm.mRWLHandler.sendMessage(msg);
            } else if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_OPERATE_TAG) && Comm.opeT == operateType.lockOpe)
            {
                boolean isSucceed = false;
                if (m_curOperateTagBuffer.lsTagList.size() > 0)
                {
                    isSucceed = true;
                }
                Message msg = new Message();
                Bundle b = new Bundle();
                b.putBoolean("isLockSucceed", isSucceed);
                msg.setData(b);
//                mHandler.sendMessage(m);
                Comm.mRWLHandler.sendMessage(msg);
            }
        }
//            else if(Comm.opeT == operateType.lockOpe||Comm.opeT == operateType.writeOpe||Comm.opeT == operateType.readOpe){
//                mHandler.sendMessage(m);
//            }

    };

    public void onDestroy(Context context)
    {
        if (lbm != null)
            lbm.unregisterReceiver(mRecv);
        mLoopHandler.removeCallbacks(mLoopRunnable);
        IsFlushList = false;
    }

    public static boolean setUHF5Parameters()
    {
        try
        {
            //设置功率
            byte btOutputPower = 0x00;
            btOutputPower = (byte) Integer.parseInt("30");
            mReader.setOutputPower(m_curReaderSetting.btReadId, btOutputPower);
            m_curReaderSetting.btAryOutputPower = new byte[]{btOutputPower};

            //设置频率 中国频段
//            byte btRegion = 0x00, btStartFreq = (byte) 43, btEndFreq = (byte) 52;
//            mReader.setFrequencyRegion(m_curReaderSetting.btReadId, btRegion, btStartFreq, btEndFreq);
//            m_curReaderSetting.btRegion = btRegion;
//            m_curReaderSetting.btFrequencyStart = btStartFreq;
//            m_curReaderSetting.btFrequencyEnd = btEndFreq;

            //session & btTarget
            session = 1;
            btTarget = 0;


        } catch (NumberFormatException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static String[] getuhf5Fre(int SelectedItemPosition)
    {
        float nStart = 0x0;
        int nloop = 0;
        String[] ssf = null;
        switch (SelectedItemPosition)
        {
            case 0:
                nStart = 920.00f;
                ssf = new String[11];
                for (nloop = 0; nloop < 11; nloop++)
                {
                    String strTemp = String.format("%.2f", nStart);
                    ssf[nloop] = strTemp;
                    nStart += 0.5f;
                }
                break;
            case 1:
                nStart = 902.00f;
                ssf = new String[53];
                for (nloop = 0; nloop < 53; nloop++)
                {
                    String strTemp = String.format("%.2f", nStart);
                    ssf[nloop] = strTemp;
                    nStart += 0.5f;
                }
                break;
            case 2:
                nStart = 865.00f;
                ssf = new String[7];
                for (nloop = 0; nloop < 7; nloop++)
                {
                    String strTemp = String.format("%.2f", nStart);
                    ssf[nloop] = strTemp;
                    nStart += 0.5f;
                }
                break;
            case 3:
                nStart = 920.00f;
                ssf[1] = String.valueOf(nStart);
                break;
        }
        return ssf;
    }

    public static String uhf5readOp(int ant, int tagBank, String opCount, String startAdd, int datatype)
    {
        String readRet = "";
        try
        {
            byte btReadId = -1;
            byte btMemBank = 0x00;
            byte btWordAdd = 0x00;
            byte btWordCnt = 0x00;
            byte[] btAryPassWord = new byte[4];
            operadatatype = datatype;
            operaCount = Integer.parseInt(opCount);
            if (tagBank == 0)
                btMemBank = 0x00;
            else if (tagBank == 1)
                btMemBank = 0x01;
            else if (tagBank == 2)
                btMemBank = 0x02;
            else if (tagBank == 3)
                btMemBank = 0x03;

            String[] reslut = StringTool.stringToStringArray(strPwd.toUpperCase(), 2);
            btAryPassWord = StringTool.stringArrayToByteArray(reslut, 4);
            btWordAdd = (byte) Integer.parseInt(startAdd);
            btWordCnt = (byte) operaCount;

            m_curOperateTagBuffer.clearBuffer();
            mReader.readTag(btReadId, btMemBank, btWordAdd, btWordCnt, btAryPassWord);
            Log.d("readTag", String.valueOf(btReadId) + " " + String.valueOf(btMemBank) + " " + String.valueOf(btWordAdd) + " " + String.valueOf(btWordCnt));
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
            Log.d("readTag", e.getMessage());
            readRet = e.getMessage();
        }
        return readRet;
    }

    public static String uhf5writeOp(int ant, int tagBank, String opCount, String startAdd, int datatype, String strWriteData)
    {
        String writeRet = "";
        boolean isSucceed = false;
        Message msg = new Message();
        Bundle b = new Bundle();
        try
        {
            byte btMemBank = 0x00;
            byte btWordAdd = 0x00;
            byte btWordCnt = 0x00;
            byte[] btAryData = null;
            String[] result = null;

            if (tagBank == 0)
                btMemBank = 0x00;
            else if (tagBank == 1)
                btMemBank = 0x01;
            else if (tagBank == 2)
                btMemBank = 0x02;
            else if (tagBank == 3)
                btMemBank = 0x03;

            byte[] btAryPassWord = new byte[4];
            if (strPwd != null && !strPwd.equals(""))
            {
                result = StringTool.stringToStringArray(strPwd.toUpperCase(), 2);
                btAryPassWord = StringTool.stringArrayToByteArray(result, result.length);
            }

            if (strWriteData != null && !strWriteData.equals(""))
            {
                if (datatype == 0)
                {
                    btAryData = new byte[strWriteData.length() / 2];
                    Comm.Str2Hex(strWriteData, strWriteData.length(), btAryData);
                } else if (datatype == 1)
                {
                    String ascstr = strWriteData;
                    if (ascstr.length() % 2 != 0)
                        ascstr += "0";
                    btAryData = ascstr.getBytes();
                } else if (datatype == 2)
                {
                    try
                    {
                        String ascstr = strWriteData;
                        btAryData = ascstr.getBytes("gbk");
                    } catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }
                }
            } else
            {
                writeRet = "写入数据为空";
                b.putBoolean("isLockSucceed", isSucceed);
                msg.setData(b);
                Comm.mRWLHandler.sendMessage(msg);
                return writeRet;
            }

            btWordAdd = (byte) Integer.parseInt(startAdd);
            result = StringTool.stringToStringArray(strWriteData.toUpperCase(), 2);
            //btAryData = StringTool.stringArrayToByteArray(result, result.length);
            btWordCnt = (byte) ((result.length / 2 + result.length % 2) & 0xFF);

            m_curOperateTagBuffer.clearBuffer();
            mReader.writeTag(m_curReaderSetting.btReadId, btAryPassWord, btMemBank, btWordAdd, btWordCnt, btAryData);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
            Log.d("writeTag", e.getMessage());
            writeRet = e.getMessage();
            b.putBoolean("isLockSucceed", isSucceed);
            msg.setData(b);
            Comm.mRWLHandler.sendMessage(msg);
        }
        return writeRet;
    }

    public static String uhf5lockOp(int ant, int tagBank, int LockType)
    {
        String lockRet = "";
        boolean isSucceed = false;
        Message msg = new Message();
        Bundle b = new Bundle();

        try
        {
            byte btMemBank = 0x00;
            byte btLockType = 0x00;
            byte[] btAryPassWord = null;
            if (tagBank == 0)
            {
                btMemBank = 0x04;
            } else if (tagBank == 1)
            {
                btMemBank = 0x05;
            } else if (tagBank == 2)
            {
                btMemBank = 0x03;
            } else if (tagBank == 3)
            {
                btMemBank = 0x02;
            } else if (tagBank == 4)
            {
                btMemBank = 0x01;
            }

            if (LockType == 0)
            {
                btLockType = 0x00;
            } else if (LockType == 1)
            {
                btLockType = 0x01;
            } else if (LockType == 2)
            {
                btLockType = 0x03;
            }

            if (strPwd != null && !strPwd.equals(""))
            {
                String[] reslut = StringTool.stringToStringArray(strPwd.toUpperCase(), 2);
                btAryPassWord = StringTool.stringArrayToByteArray(reslut, 4);
            } else
            {
                lockRet = "访问密码为空或错误";
                Log.d("lockTag", lockRet);
                b.putBoolean("isLockSucceed", isSucceed);
                msg.setData(b);
                Comm.mRWLHandler.sendMessage(msg);
                return lockRet;
            }

            m_curOperateTagBuffer.clearBuffer();
            mReader.lockTag(m_curReaderSetting.btReadId, btAryPassWord, btMemBank, btLockType);
        } catch (Exception e)
        {
            e.printStackTrace();
            lockRet = e.getMessage();
            Log.d("lockTag", lockRet);
            b.putBoolean("isLockSucceed", isSucceed);
            msg.setData(b);
            Comm.mRWLHandler.sendMessage(msg);
        }

        return lockRet;
    }
}

