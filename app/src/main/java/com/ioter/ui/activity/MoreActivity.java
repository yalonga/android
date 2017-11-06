package com.ioter.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.clouiotech.port.Adapt;
import com.clouiotech.port.PropertiesManager;
import com.ioter.R;
import com.ioter.common.util.DeviceUtil;
import com.ioter.common.util.ToastUtil;
import com.ioter.di.component.AppComponent;
import com.ioter.hopeland.SupoinEpcReadActivity;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import butterknife.BindView;

/**
 * 更多
 *
 * @author Administrator
 */
public class MoreActivity extends BaseActivity
{

    @BindView(R.id.tool_bar)
    Toolbar mToolBar;

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
        findViewById(R.id.setting_llyt).setOnClickListener(this);
        findViewById(R.id.epc_read_llyt).setOnClickListener(this);
        findViewById(R.id.versions_llyt).setOnClickListener(this);
        findViewById(R.id.bgaqring_llyt).setOnClickListener(this);
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
                if (DeviceUtil.getDeviceId() == DeviceUtil.SUPOIN)
                {
                    ToastUtil.toast("暂无设置");
                } else if (DeviceUtil.getDeviceId() == DeviceUtil.HOPELAND)
                {
                    startActivity(new Intent(this, SettingActivity.class));
                }
                break;
            case R.id.epc_read_llyt:
                if (DeviceUtil.getDeviceId() == DeviceUtil.SUPOIN)
                {
                    startActivity(new Intent(this, SupoinEpcReadActivity.class));
                } else if (DeviceUtil.getDeviceId() == DeviceUtil.HOPELAND)
                {
                    PropertiesManager propertiesInstance = Adapt.getPropertiesInstance();
                    if (!propertiesInstance.support("UHF"))
                    {
                        ToastUtil.toast("该设备不支持高频读写");
                        return;
                    }
                    startActivity(new Intent(this, EpcReadActivity.class));
                }
                break;
            case R.id.versions_llyt:
                ToastUtil.toast("当前版本:" + getAppVersionName(this));
                break;
            case R.id.bgaqring_llyt:
                startActivity(new Intent(this, QrcodeTransferActivity.class));
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
        } catch (Exception e)
        {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

}
