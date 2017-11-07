package com.ioter.hopeland;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ioter.R;
import com.ioter.hopeland.uhf.UHF1.UHF001;
import com.ioter.hopeland.uhf.UHF1Function.AndroidWakeLock;
import com.ioter.hopeland.uhf.UHF1Function.SPconfig;
import com.ioter.hopeland.uhf.UHF1Function.ScreenListener;
import com.ioter.presenter.CheckPresenter;
import com.ioter.ui.activity.BaseActivity;
import com.uhf.api.cls.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.ioter.hopeland.Comm.Awl;
import static com.ioter.hopeland.Comm.checkDevice;
import static com.ioter.hopeland.Comm.context;
import static com.ioter.hopeland.Comm.isQuick;
import static com.ioter.hopeland.Comm.isrun;
import static com.ioter.hopeland.Comm.lsTagList;
import static com.ioter.hopeland.Comm.moduleType;
import static com.ioter.hopeland.Comm.myapp;
import static com.ioter.hopeland.Comm.rfidOperate;
import static com.ioter.hopeland.Comm.soundPool;
import static com.ioter.hopeland.Comm.tagListSize;
import static com.ioter.hopeland.uhf.UHF1.UHF001.Devaddrs;
import static com.ioter.hopeland.uhf.UHF1.UHF001.UHF1handler;

/**
 * 盘点
 *
 * @author hzj
 */
public abstract class SupoinUHFBaseActivity extends BaseActivity<CheckPresenter>
{

    int scanCode = 0;
    private GoogleApiClient client;
    ScreenListener l;

    private HashMap<String, EpcBeen> mCacheList = new HashMap<String, EpcBeen>();
    private ArrayList<EpcBeen> mEpcList = new ArrayList<>();

    @Override
    public void init()
    {
        Comm.app = getApplication();
        Comm.spConfig = new SPconfig(this);
        context = SupoinUHFBaseActivity.this;
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(this, R.raw.beep51, 1);
        Log.d("test", "soundPool");
        Awl = new AndroidWakeLock((PowerManager) getSystemService(Context.POWER_SERVICE));
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("Activity", "onResume");
        if (moduleType == Comm.Module.UHF005)
            rfidOperate.onResume(this);
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        InitDevice();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction0());
        client.disconnect();
        Comm.stopScan();
        release();
        Awl.ReleaseWakeLock();
    }

    public Action getIndexApiAction0()
    {
        Thing object = new Thing.Builder()
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public void InitDevice()
    {
        checkDevice();
        if (!Comm.powerUp())
        {
            Comm.powerDown();
            Toast.makeText(this, "上电失败",
                    Toast.LENGTH_SHORT).show();
            Log.d("test", "powerUp false");
        } else
        {
            Log.d("test", "powerUp SEC");
        }
        Comm.connecthandler = connectH;
        Comm.Connect();

        Log.d("test", "connect SUC");
    }

    String[] Coname = new String[]{"NO", "EPC", "Count"};

    private void fliterDatas()
    {
        try
        {
            String epcstr = "";//epc
            if (moduleType == Comm.Module.UHF001)
            {
                if (Devaddrs.size() > 0)
                    //mTotalNumTv.setText(String.valueOf(Devaddrs.size()));
                    if (isQuick && !isrun)
                        //tv_state.setText(String.valueOf("正在处理数据，请稍后。。。"));
                        if (!isQuick || !isrun)
                        {
                            Iterator<Map.Entry<String, Reader.TAGINFO>> iesb;
                            synchronized (this)
                            {
                                Map<String, Reader.TAGINFO> Devaddrs2 = new LinkedHashMap<String, Reader.TAGINFO>();
                                Devaddrs2.putAll(Devaddrs);
                                iesb = Devaddrs2.entrySet().iterator();
                            }
                            while (iesb.hasNext())
                            {
                                // int ListIndex = 0;
                                Map<String, String> m = new HashMap<String, String>();
                                Reader.TAGINFO bd = iesb.next().getValue();
                                epcstr = Reader.bytes_Hexstr(bd.EpcId);
                                if (epcstr.length() > 4)
                                {
                                    if (mCacheList.containsKey(epcstr))
                                    {
                                        EpcBeen epcBeen = mCacheList.get(epcstr);
                                        epcBeen.count = bd.ReadCnt + "";
                                    } else
                                    {
                                        EpcBeen epcBeen = new EpcBeen();
                                        epcBeen.epcValue = String.format("%-24s", epcstr);
                                        epcBeen.count = bd.ReadCnt + "";
                                        mCacheList.put(epcstr, epcBeen);
                                        mEpcList.add(epcBeen);
                                    }
                                }
                            }
                        }
            } else if (moduleType == Comm.Module.UHF005)
            {
                try
                {
                    if (!isQuick || !isrun)
                    {

                        for (int i = 0; i < tagListSize; i++)
                        {
                            epcstr = lsTagList.get(i).strEPC.replace(" ", "");
                            int readCount = lsTagList.get(i).nReadCount;
                            if (epcstr.length() > 4)
                            {
                                if (mCacheList.containsKey(epcstr))
                                {
                                    EpcBeen epcBeen = mCacheList.get(epcstr);
                                    epcBeen.count = String.valueOf(readCount);
                                } else
                                {
                                    EpcBeen epcBeen = new EpcBeen();
                                    epcBeen.epcValue = epcstr;
                                    epcBeen.count = String.valueOf(readCount);
                                    mCacheList.put(epcstr, epcBeen);
                                    mEpcList.add(epcBeen);
                                }
                            }
                        }
                        showlist(mEpcList);
                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
    }


    protected abstract void showlist(ArrayList<EpcBeen> mEpcList);


    private Handler uhfhandler = new Handler()
    {
        @SuppressWarnings({"unchecked", "unused"})
        @Override
        public void handleMessage(Message msg)
        {
            try
            {
                switch (msg.what)
                {
                    case Comm.UHF5MESSAGE_TEXT:
                        tagListSize = lsTagList.size();
                        if (tagListSize > 0)
                        {
                            fliterDatas();
                        }
                        break;
                    case Comm.UHF1MESSAGE_TEXT:
                        Bundle bd = msg.getData();
                        String strR = bd.get("Result").toString();
                        int readCount = bd.getInt("readCount");
                        if (readCount > 0)
                            //tv_state.setText(String.valueOf(readCount));
                            if (strR == "SUCCEED")
                                fliterDatas();
                        break;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };


    public Handler connectH = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle bd = msg.getData();
            String strMsg = bd.get("Msg").toString();
            if (moduleType == Comm.Module.UHF001)
            {

                if (!strMsg.equals("") && strMsg != null)
                {
                    UHF001.mhandler = uhfhandler;
                    myapp.needreconnect = false;
                    l = new ScreenListener(SupoinUHFBaseActivity.this);
                    l.begin(new ScreenListener.ScreenStateListener()
                    {
                        @Override
                        public void onScreenOn()
                        {
                        }

                        @Override
                        public void onScreenOff()
                        {

                            if (myapp.Mreader != null)
                                myapp.Mreader.CloseReader();

                            if (myapp.Rpower != null)
                                myapp.needreconnect = true;
                        }
                    });
                }
            } else if (moduleType == Comm.Module.UHF005)
            {
                rfidOperate.mHandler = uhfhandler;

                setPower();
            }
        }
    };

    protected void setPower()
    {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            release();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void release()
    {
        if (UHF1handler != null)
            Comm.stopScan();
        if (myapp != null)
        {
            if (myapp.Mreader != null)
                myapp.Mreader.CloseReader();
            if (myapp.Rpower != null)
                myapp.Rpower.PowerDown();
        }
        Comm.powerDown();
    }

    protected void readEpc()
    {
    }

    /* 释放按键事件 */
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (scanCode == 261 && isrun || scanCode == 261 && !isrun)
        {
            readEpc();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e)
    {
        try
        {
            scanCode = e.getScanCode();
            return super.dispatchKeyEvent(e);
        } catch (Exception ex)
        {
            // TODO: handle exception
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onDestroy()
    {
        Comm.stopScan();
        release();
        Awl.ReleaseWakeLock();
        super.onDestroy();
    }
}
