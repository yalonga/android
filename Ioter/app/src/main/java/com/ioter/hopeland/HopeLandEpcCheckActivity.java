package com.ioter.hopeland;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ioter.R;
import com.ioter.di.component.AppComponent;
import com.ioter.hopeland.uhf.UHF1.UHF001;
import com.ioter.hopeland.uhf.UHF1Function.AndroidWakeLock;
import com.ioter.hopeland.uhf.UHF1Function.SPconfig;
import com.ioter.hopeland.uhf.UHF1Function.ScreenListener;
import com.ioter.ui.activity.BaseActivity;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;
import com.uhf.api.cls.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;

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
public class HopeLandEpcCheckActivity extends BaseActivity
{
    private HopeLandCheckAdapter mAdapter;
    @BindView(R.id.close_btn)
    Button mCloseBtn;
    @BindView(R.id.read_btn)
    Button mReadBtn;

    @BindView(R.id.total_num_tv)
    TextView mTotalNumTv;
    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.tool_bar)
    Toolbar mToolBar;

    int scanCode = 0;
    private GoogleApiClient client;
    ScreenListener l;


    private HashMap<String, EpcBeen> mCacheList = new HashMap<String, EpcBeen>();
    private ArrayList<EpcBeen> mEpcList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_epc_check;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
    }

    @Override
    public void init()
    {
        initTitle();
        initView();
    }

    private void initTitle()
    {
        mToolBar.setNavigationIcon(
                new IconicsDrawable(this)
                        .icon(Ionicons.Icon.ion_ios_arrow_back)
                        .sizeDp(16)
                        .color(getResources().getColor(R.color.md_white_1000)
                        )
        );
        mToolBar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initView()
    {
        mCloseBtn.setOnClickListener(this);
        mReadBtn.setOnClickListener(this);

        //为RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        Comm.app = getApplication();
        Comm.spConfig = new SPconfig(this);
        context = HopeLandEpcCheckActivity.this;

        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(this, R.raw.beep51, 1);
        Log.d("test", "soundPool");
        Awl = new AndroidWakeLock((PowerManager) getSystemService(Context.POWER_SERVICE));
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //InitDevice();
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
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
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction0());
        client.disconnect();
        release();
    }

    public Action getIndexApiAction0()
    {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
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
            // Toast.makeText(MainActivity.this, "powerUp false", Toast.LENGTH_SHORT).show();
        } else
        {
            Log.d("test", "powerUp SEC");
            //Toast.makeText(MainActivity.this, "powerUp SEC", Toast.LENGTH_SHORT).show();
        }
        Comm.connecthandler = connectH;
        Comm.Connect();

        Log.d("test", "connect SUC");
    }

    String[] Coname = new String[]{"NO", "                      EPC  ", "Count"};

    private void showlist()
    {
        try
        {
            String epcstr = "";//epc
            if (moduleType == Comm.Module.UHF001)
            {
                if (Devaddrs.size() > 0)
                    mTotalNumTv.setText(String.valueOf(Devaddrs.size()));
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
                        if (mAdapter == null)
                        {
                            mAdapter = new HopeLandCheckAdapter();
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        mAdapter.setNewData(mEpcList);
                        int totalCount = 0;
                        for (EpcBeen item : mEpcList)
                        {
                            totalCount += Integer.valueOf(item.count);
                        }
                        mTotalNumTv.setText("总读取次数 " + totalCount);
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
                            showlist();
                        }
                        break;
                    case Comm.UHF1MESSAGE_TEXT:
                        Bundle bd = msg.getData();
                        String strR = bd.get("Result").toString();
                        int readCount = bd.getInt("readCount");
                        if (readCount > 0)
                            //tv_state.setText(String.valueOf(readCount));
                            if (strR == "SUCCEED")
                                showlist();
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
                    l = new ScreenListener(HopeLandEpcCheckActivity.this);
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
            }
        }
    };


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.read_btn:
                try
                {
                    Awl.WakeLock();
                    Comm.startScan();
                } catch (Exception ex)
                {
                    Toast.makeText(this,
                            "ERR：" + ex.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case R.id.close_btn:
                Awl.ReleaseWakeLock();
                Comm.stopScan();
                break;
            default:
                break;
        }
    }

    private void back()
    {
        if (mReadBtn.getText().toString().equals(getString(R.string.stop)))
        {
            return;
        }
        finish();
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

    /* 释放按键事件 */
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (scanCode == 261 && isrun)
            mCloseBtn.performClick();
        else if (scanCode == 261 && !isrun)
            mReadBtn.performClick();
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


}
