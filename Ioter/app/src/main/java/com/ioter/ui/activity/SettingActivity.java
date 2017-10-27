package com.ioter.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.clouiotech.pda.rfid.EPCModel;
import com.clouiotech.pda.rfid.IAsynchronousMessage;
import com.clouiotech.pda.rfid.uhf.UHFReader;
import com.ioter.R;
import com.ioter.common.util.DeviceUtil;
import com.ioter.common.util.ToastUtil;
import com.ioter.di.component.AppComponent;

public class SettingActivity extends BaseActivity implements IAsynchronousMessage
{
    private boolean mIsInitData; // 是否已初始化过数据
    /**
     * 功率 0-30dbm
     **/
    private int mPower;
    /**
     * 频率 0-4 对应Array_Frequency频段列表
     **/
    private int mFrequency;
    /**
     * 基带参数 ，存放基带速率(0-255 Array_BaseSpeedType列表)
     * 、Q值(0-15)、session(0-3)、searchType(0-2)
     **/
    private int[] mBaseBandData;

    private Spinner mPowerSp;
    private Spinner mFrequencySp;
    private Spinner mBandSp;
    private Spinner mQSp;
    private Spinner mSessionSp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_setting;
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

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        UHFReader._Config.OpenConnect(DeviceUtil.canUsingBackBattery(), this);
        if (!mIsInitData)
        {
            initData();
            mIsInitData = true;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        UHFReader._Config.CloseConnect();
    }

    private void initTitle()
    {
        findViewById(R.id.app_common_bar_left_iv).setOnClickListener(this);
        ((TextView) findViewById(R.id.app_common_bar_title_tv)).setText("配置");
    }

    private void initView()
    {
        mPowerSp = (Spinner) findViewById(R.id.power_sp);
        mFrequencySp = (Spinner) findViewById(R.id.frequency_sp);
        mBandSp = (Spinner) findViewById(R.id.band_sp);
        mQSp = (Spinner) findViewById(R.id.q_sp);
        mSessionSp = (Spinner) findViewById(R.id.session_sp);

        findViewById(R.id.power_get_btn).setOnClickListener(this);
        findViewById(R.id.power_set_btn).setOnClickListener(this);
        findViewById(R.id.frequency_get_btn).setOnClickListener(this);
        findViewById(R.id.frequency_set_btn).setOnClickListener(this);
        findViewById(R.id.band_get_btn).setOnClickListener(this);
        findViewById(R.id.band_set_btn).setOnClickListener(this);
        findViewById(R.id.q_get_btn).setOnClickListener(this);
        findViewById(R.id.q_set_btn).setOnClickListener(this);
        findViewById(R.id.session_get_btn).setOnClickListener(this);
        findViewById(R.id.session_set_btn).setOnClickListener(this);
    }

    private void initData()
    {
        // 获取功率
        mPower = UHFReader._Config.GetANTPowerParam();
        mPowerSp.setSelection(mPower);
        // 获取频率
        mFrequency = UHFReader._Config.GetFrequency();
        mFrequencySp.setSelection(mFrequency);
        // 获取基带参数
        String baseBandParam = UHFReader._Config.GetEPCBaseBandParam();
        String[] split = baseBandParam.split("\\|");
        if (split != null && split.length == 4)
        {
            mBaseBandData = new int[split.length];
            for (int i = 0; i < split.length; i++)
            {
                mBaseBandData[i] = Integer.parseInt(split[i]);
            }
            if (mBaseBandData[0] == 255)
            {
                mBandSp.setSelection(4);
            } else if (mBaseBandData[0] < 4)
            {
                mBandSp.setSelection(mBaseBandData[0]);
            }
            mQSp.setSelection(mBaseBandData[1]);
            mSessionSp.setSelection(mBaseBandData[2]);
        }
    }

    @Override
    public void onClick(View v)
    {
        String baseBandParam;
        String[] split;
        switch (v.getId())
        {
            case R.id.app_common_bar_left_iv:
                finish();
                break;
            case R.id.power_get_btn:
                int power = UHFReader._Config.GetANTPowerParam();
                ToastUtil.toast("power:" + power);
                mPowerSp.setSelection(power);
                mPower = power;
                break;
            case R.id.power_set_btn:
                int powerValue = mPowerSp.getSelectedItemPosition();
                ToastUtil.toast(powerValue + "");
                if (UHFReader._Config.SetANTPowerParam(1, powerValue) == 0)
                {
                    ToastUtil.toast("设置成功");
                    mPower = powerValue;
                } else
                {
                    ToastUtil.toast("设置失败");
                }
                break;
            case R.id.frequency_get_btn:
                int frequency = UHFReader._Config.GetFrequency();
                ToastUtil.toast("frequency:" + frequency);
                mFrequencySp.setSelection(frequency);
                mFrequency = frequency;
                break;
            case R.id.frequency_set_btn:
                int frequencyIndex = mFrequencySp.getSelectedItemPosition();
                if (UHFReader._Config.SetFrequency(frequencyIndex) == 0)
                {
                    ToastUtil.toast("设置成功");
                    mFrequency = frequencyIndex;
                } else
                {
                    ToastUtil.toast("设置失败");
                }
                break;
            case R.id.band_get_btn:
                baseBandParam = UHFReader._Config.GetEPCBaseBandParam();
                ToastUtil.toast("ePCBaseBandParam:" + baseBandParam);
                split = baseBandParam.split("\\|");
                if (split != null && split.length == 4)
                {
                    mBaseBandData = new int[split.length];
                    for (int i = 0; i < split.length; i++)
                    {
                        mBaseBandData[i] = Integer.parseInt(split[i]);
                    }
                    if (mBaseBandData[0] == 255)
                    {
                        mBandSp.setSelection(4);
                    } else if (mBaseBandData[0] < 4)
                    {
                        mBandSp.setSelection(mBaseBandData[0]);
                    }
                }
                break;
            case R.id.band_set_btn:
                if (mBaseBandData == null)
                {
                    ToastUtil.toast("基带参数错误");
                    return;
                }
                int basebandMode = mBandSp.getSelectedItemPosition();
                if (basebandMode == 4)
                {
                    basebandMode = 255;
                }
                if (UHFReader._Config.SetEPCBaseBandParam(basebandMode, mBaseBandData[1], mBaseBandData[2], mBaseBandData[3]) == 0)
                {
                    ToastUtil.toast("设置成功");
                    mBaseBandData[0] = basebandMode;
                } else
                {
                    ToastUtil.toast("设置失败");
                }
                break;
            case R.id.q_get_btn:
                baseBandParam = UHFReader._Config.GetEPCBaseBandParam();
                ToastUtil.toast("ePCBaseBandParam:" + baseBandParam);
                split = baseBandParam.split("\\|");
                if (split != null && split.length == 4)
                {
                    mBaseBandData = new int[split.length];
                    for (int i = 0; i < split.length; i++)
                    {
                        mBaseBandData[i] = Integer.parseInt(split[i]);
                    }
                    mQSp.setSelection(mBaseBandData[1]);
                }
                break;
            case R.id.q_set_btn:
                if (mBaseBandData == null)
                {
                    ToastUtil.toast("基带参数错误");
                    return;
                }
                int qValue = mQSp.getSelectedItemPosition();
                if (UHFReader._Config.SetEPCBaseBandParam(mBaseBandData[0], qValue, mBaseBandData[2], mBaseBandData[3]) == 0)
                {
                    ToastUtil.toast("设置成功");
                    mBaseBandData[1] = qValue;
                } else
                {
                    ToastUtil.toast("设置失败");
                }
                break;
            case R.id.session_get_btn:
                baseBandParam = UHFReader._Config.GetEPCBaseBandParam();
                ToastUtil.toast("ePCBaseBandParam:" + baseBandParam);
                split = baseBandParam.split("\\|");
                if (split != null && split.length == 4)
                {
                    mBaseBandData = new int[split.length];
                    for (int i = 0; i < split.length; i++)
                    {
                        mBaseBandData[i] = Integer.parseInt(split[i]);
                    }
                    mSessionSp.setSelection(mBaseBandData[2]);
                }
                break;
            case R.id.session_set_btn:
                if (mBaseBandData == null)
                {
                    ToastUtil.toast("基带参数错误");
                    return;
                }
                int session = mSessionSp.getSelectedItemPosition();
                if (UHFReader._Config.SetEPCBaseBandParam(mBaseBandData[0], mBaseBandData[1], session, mBaseBandData[3]) == 0)
                {
                    ToastUtil.toast("设置成功");
                    mBaseBandData[2] = session;
                } else
                {
                    ToastUtil.toast("设置失败");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void OutPutEPC(EPCModel arg0)
    {
        // TODO Auto-generated method stub

    }

}
