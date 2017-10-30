package com.ioter.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.clouiotech.pda.rfid.EPCModel;
import com.clouiotech.pda.rfid.IAsynchronousMessage;
import com.clouiotech.pda.rfid.uhf.UHF;
import com.clouiotech.pda.rfid.uhf.UHFReader;
import com.clouiotech.util.Helper.Helper_ThreadPool;
import com.ioter.R;
import com.ioter.bean.PublicData;
import com.ioter.common.util.DeviceUtil;
import com.ioter.common.util.UIConstant;
import com.ioter.presenter.CheckPresenter;

import java.util.HashMap;

/**
 * @author RFID_lx Activity 基类
 */
public abstract class UHFBaseActivity extends BaseActivity<CheckPresenter> implements IAsynchronousMessage
{

    static Boolean _UHFSTATE = false; // 模块是否已经打开
    public static UHF CLReader = UHFReader.getUHFInstance();

    protected boolean isStartPingPong = false;
    private boolean isPowerLowShow = false;
    private boolean usingBackBattery = false;

    private boolean isKeyDown = false; // 板机是否按下
    private boolean isLongKeyDown = false; // 板机是否是长按状态
    private int keyDownCount = 0; // 板机按下次数
    private Boolean IsFlushList = true; // 是否刷列表
    private Object beep_Lock = new Object();
    ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);

    protected void msgProcess(Message msg)
    {
        switch (msg.what)
        {
            case UIConstant.MSG_RESULT_READ:
                ShowList(); // 刷新列表
                break;
            case UIConstant.MSG_UHF_POWERLOW:
                ShowPowerLow();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.d("CL7202K3", "onKeyDown keyCode = " + keyCode);
        if (keyCode == 131 || keyCode == 135)
        { // 按下扳机
            keyDown();
            if (!isKeyDown)
            {
                isKeyDown = true; //
                if (!isStartPingPong)
                {
                    Clear();
                    Pingpong_Stop(); // 停止间歇性读
                    isStartPingPong = true;
                    CLReader.Read_EPC(UIConstant._NowReadParam);
                    if (PublicData._IsCommand6Cor6B.equals("6C"))
                    {// 读6C标签
                        CLReader.Read_EPC(UIConstant._NowReadParam);
                    } else
                    {// 读6B标签
                        CLReader.Get6B(UIConstant._NowAntennaNo + "|1" + "|1" + "|" + "1,000F");
                    }
                }
            } else
            {
                if (keyDownCount < 10000)
                    keyDownCount++;
            }
            if (keyDownCount > 100)
            {
                isLongKeyDown = true;
            }
            if (isLongKeyDown)
            { // 长按事件

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        Log.d("CL7202K3", "onKeyUp keyCode = " + keyCode);
        if (keyCode == 131 || keyCode == 135)
        { // 放开扳机
            CLReader.Stop();
            isStartPingPong = false;
            keyDownCount = 0;
            isKeyDown = false;
            isLongKeyDown = false;
            keyUp();
        }
        return super.onKeyUp(keyCode, event);
    }

    protected void keyDown()
    {
    }

    protected void keyUp()
    {
    }

    protected void Clear()
    {
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Init();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Dispose();
    }

    private void Dispose()
    {
        isStartPingPong = false;
        IsFlushList = false;
        synchronized (beep_Lock)
        {
            beep_Lock.notifyAll();
        }
        UHF_Dispose();
    }

    private void Init()
    {
        usingBackBattery = DeviceUtil.canUsingBackBattery();
        if (!UHF_Init(usingBackBattery, this))
        { // 打开模块电源失败
            ShowMsg(getString(R.string.uhf_low_power_info), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface arg0, int arg1)
                {
                    UHFBaseActivity.this.finish();
                }
            });
        } else
        {
            try
            {
                UHF_GetReaderProperty(); // 获得读写器的能力
                UIConstant._NowReadParam = UIConstant._NowAntennaNo + "|1";
                Thread.sleep(20);
                CLReader.Stop(); // 停止指令
                Thread.sleep(20);
                UHF_SetTagUpdateParam(); // 设置标签重复上传时间为20ms
            } catch (Exception ee)
            {
            }
            IsFlushList = true;
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
                            sendMessage(UIConstant.MSG_RESULT_READ, null);
                            Thread.sleep(1000); // 一秒钟刷新一次
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Helper_ThreadPool.ThreadPool_StartSingle(new Runnable()
            { // 蜂鸣器发声
                @Override
                public void run()
                {
                    while (IsFlushList)
                    {
                        synchronized (beep_Lock)
                        {
                            try
                            {
                                beep_Lock.wait();
                            } catch (InterruptedException e)
                            {
                            }
                        }
                        if (IsFlushList)
                        {
                            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
                        }

                    }
                }
            });
        }
    }

    /**
     * 间歇性读
     */
    protected void Pingpong_Read()
    {
        if (isStartPingPong)
            return;
        isStartPingPong = true;
        Clear();
        Helper_ThreadPool.ThreadPool_StartSingle(new Runnable()
        {

            @Override
            public void run()
            {
                while (isStartPingPong)
                {
                    try
                    {
                        if (!isPowerLowShow)
                        {
                            if (usingBackBattery && !DeviceUtil.canUsingBackBattery())
                            {
                                sendMessage(UIConstant.MSG_UHF_POWERLOW, null);
                            }
                            if (PublicData._IsCommand6Cor6B.equals("6C"))
                            {// 读6C标签
                                CLReader.Read_EPC(UIConstant._NowReadParam);
                            } else
                            {// 读6B标签
                                CLReader.Get6B(UIConstant._NowAntennaNo + "|1" + "|1" + "|" + "1,000F");
                            }

                            Thread.sleep(PublicData._PingPong_ReadTime);

                            if (PublicData._PingPong_StopTime > 0)
                            {
                                CLReader.Stop();
                                Thread.sleep(PublicData._PingPong_StopTime);
                            }
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 停止间歇性读
     */
    protected void Pingpong_Stop()
    {
        isStartPingPong = false;
        CLReader.Stop();
    }

    /**
     * 刷新列表
     */
    protected void ShowList()
    {
    }

    ;

    private void ShowPowerLow()
    {
        new AlertDialog.Builder(this).setTitle("Confim")
                // 设置对话框标题
                .setMessage(getString(R.string.uhf_low_power_consumption))
                // 设置显示的内容
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
                {// 添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {// 确定按钮的响应事件
                        UHF_Init(false, UHFBaseActivity.this);
                        try
                        {
                            Thread.sleep(1000);
                        } catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        isPowerLowShow = false;
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
        {// 添加返回按钮
            @Override
            public void onClick(DialogInterface dialog, int which)
            {// 响应事件
                Dispose();
                isPowerLowShow = false;
                UHFBaseActivity.this.finish();
            }
        }).show();// 在按键响应事件中显示此对话框

    }

    /**
     * 超高频模块初始化
     *
     * @param log 接口回调方法
     * @return 是否初始化成功
     */
    public Boolean UHF_Init(boolean usingBackupPower, IAsynchronousMessage log)
    {
        Boolean rt = false;
        try
        {
            if (!_UHFSTATE)
            {
                rt = CLReader.OpenConnect(usingBackupPower, log);
                if (rt)
                {
                    _UHFSTATE = true;
                }
            } else
            {
                rt = true;
            }
        } catch (Exception ex)
        {
            Log.d("debug", "UHF上电出现异常：" + ex.getMessage());
        }
        return rt;
    }

    /**
     * 超高频模块释放
     */
    public void UHF_Dispose()
    {
        if (_UHFSTATE == true)
        {
            CLReader.CloseConnect();
            _UHFSTATE = false;
        }
    }

    /**
     * 获得读写器的读写能力
     */
    @SuppressLint("UseSparseArrays")
    @SuppressWarnings("serial")
    protected void UHF_GetReaderProperty()
    {
        String propertyStr = CLReader.GetReaderProperty();
        Log.d("Debug", "获得读写器能力：" + propertyStr);
        String[] propertyArr = propertyStr.split("\\|");
        HashMap<Integer, Integer> hm_Power = new HashMap<Integer, Integer>()
        {
            {
                put(1, 1);
                put(2, 3);
                put(3, 7);
                put(4, 15);
            }
        };
        if (propertyArr.length > 3)
        {
            try
            {
                UIConstant._Max_Power = Integer.parseInt(propertyArr[0]);
                UIConstant._Min_Power = Integer.parseInt(propertyArr[1]);
                int powerIndex = Integer.parseInt(propertyArr[2]);
                UIConstant._NowAntennaNo = hm_Power.get(powerIndex);
            } catch (Exception ex)
            {
                Log.d("Debug", "获得读写器能力失败,转换失败！");
            }
        } else
        {
            Log.d("Debug", "获得读写器能力失败！");
        }
    }

    /**
     * 设置标签上传参数
     */
    protected void UHF_SetTagUpdateParam()
    {
        // 先查询当前的设置是否一致，如果不一致才设置
        String searchRT = CLReader.GetTagUpdateParam();
        String[] arrRT = searchRT.split("\\|");
        if (arrRT.length >= 2)
        {
            int nowUpDataTime = Integer.parseInt(arrRT[0]);
            Log.d("Debug", "查标签上传时间：" + nowUpDataTime);
            if (UIConstant._UpDataTime != nowUpDataTime)
            {
                CLReader.SetTagUpdateParam("1," + UIConstant._UpDataTime);
                Log.d("Debug", "设置标签上传时间...");
            } else
            {

            }
        } else
        {
            Log.d("Debug", "查询标签上传时间失败...");
        }
    }

    @Override
    public void OutPutEPC(EPCModel model)
    {
        if (!isStartPingPong)
            return;
        synchronized (beep_Lock)
        {
            beep_Lock.notify();
        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            msgProcess(msg);
        }
    };


    /**
     * 气泡提示
     *
     * @param msg
     */
    protected void ShowToast(String msg)
    {
        sendMessage(UIConstant.MSG_SHOW_Toast, msg);
    }

    protected void sendMessage(int what, Object obj)
    {
        handler.sendMessage(handler.obtainMessage(what, obj));
    }

    protected void sendMessageDelay(Message message, long delayMillis)
    {
        handler.sendMessageDelayed(message, delayMillis);
    }
}
