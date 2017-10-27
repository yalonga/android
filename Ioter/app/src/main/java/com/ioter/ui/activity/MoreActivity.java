package com.ioter.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.clouiotech.port.Adapt;
import com.clouiotech.port.PropertiesManager;
import com.ioter.R;
import com.ioter.common.util.ToastUtil;
import com.ioter.di.component.AppComponent;

/**
 * 更多
 * 
 * @author Administrator
 * 
 */
public class MoreActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_more;
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
        ((TextView) findViewById(R.id.app_common_bar_title_tv)).setText("更多");
        findViewById(R.id.app_common_bar_left_iv).setOnClickListener(this);
    }

    private void initView()
    {
        findViewById(R.id.setting_llyt).setOnClickListener(this);
        findViewById(R.id.epc_read_llyt).setOnClickListener(this);
        findViewById(R.id.versions_llyt).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.app_common_bar_left_iv:
            finish();
            break;
        case R.id.setting_llyt:
            startActivity(new Intent(this, SettingActivity.class));
            break;
        case R.id.epc_read_llyt:
            PropertiesManager propertiesInstance = Adapt.getPropertiesInstance();
            if (!propertiesInstance.support("UHF"))
            {
                ToastUtil.toast("该设备不支持高频读写");
                return;
            }
            startActivity(new Intent(this, EpcReadActivity.class));
            break;
        case R.id.versions_llyt:
            ToastUtil.toast("当前版本:" + getAppVersionName(this));
            break;

        default:
            break;
        }
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context)
    {
        String versionName = "";
        try
        {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0)
            {
                return "";
            }
        }
        catch (Exception e)
        {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

}
