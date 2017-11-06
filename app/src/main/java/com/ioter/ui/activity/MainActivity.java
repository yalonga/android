package com.ioter.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clouiotech.port.Adapt;
import com.clouiotech.port.PropertiesManager;
import com.ioter.R;
import com.ioter.common.font.Cniao5Font;
import com.ioter.common.util.DataUtil;
import com.ioter.common.util.DeviceUtil;
import com.ioter.common.util.ToastUtil;
import com.ioter.di.component.AppComponent;
import com.ioter.di.component.DaggerMainComponent;
import com.ioter.di.module.MainModule;
import com.ioter.hopeland.SupoinEpcCheckActivity;
import com.ioter.hopeland.SupoinEpcInOutActivity;
import com.ioter.presenter.MainPresenter;
import com.ioter.presenter.contract.MainContract;
import com.ioter.ui.widget.BannerLayout;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.MainView
{
    private static final int TIME_AUTO_SCROLL = 3000;
    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;
    /**
     * 选择系统图片Request Code
     */
    public static final int REQUEST_IMAGE = 112;
    /**
     * 请求CAMERA权限码
     */
    public static final int REQUEST_CAMERA_PERM = 101;


    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;


    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.banner)
    BannerLayout mBannerLayout;


    private View headerView;
    private ImageView mUserHeadView;
    private TextView mTextUserName;


    private void initBanner()
    {
        //mToolBar.setTitle("Ioter(" + DeviceUtil.getDeviceName(DeviceUtil.getDeviceId()) + ")");
        List<String> views = new ArrayList<String>();
        String bannerUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1508847478814&di=a96ab510762a29e2e4f3ffe1a47e756e&imgtype=0&src=http%3A%2F%2Fg.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2Fe61190ef76c6a7efd0bef8e7f4faaf51f2de6652.jpg";
        views.add(bannerUrl);
        views.add(bannerUrl);
        views.add(bannerUrl);
        mBannerLayout.setViewUrls(views);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_main;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
        DaggerMainComponent.builder().appComponent(appComponent).mainModule(new MainModule(this))
                .build().inject(this);
    }

    @Override
    public void init()
    {
        //initToolbar();
        initBanner();
        initDrawerLayout();
        initListen();
    }

    private void initListen()
    {
        findViewById(R.id.out_rlyt).setOnClickListener(this);
        findViewById(R.id.in_rlyt).setOnClickListener(this);
        findViewById(R.id.store_in_rlyt).setOnClickListener(this);
        findViewById(R.id.check_rlyt).setOnClickListener(this);
        findViewById(R.id.scan_rlyt).setOnClickListener(this);
        findViewById(R.id.more_rlyt).setOnClickListener(this);
    }


    private void initToolbar()
    {

        mToolBar.inflateMenu(R.menu.toolbar_menu);

        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {

                if (item.getItemId() == R.id.action_search)
                {

                }

                return true;
            }
        });


        MenuItem downloadMenuItem = mToolBar.getMenu().findItem(R.id.action_download);

    }

    private void initDrawerLayout()
    {


        headerView = mNavigationView.getHeaderView(0);

        mUserHeadView = (ImageView) headerView.findViewById(R.id.img_avatar);
        mUserHeadView.setImageDrawable(new IconicsDrawable(this, Cniao5Font.Icon.cniao_head).colorRes(R.color.white));

        mTextUserName = (TextView) headerView.findViewById(R.id.txt_username);


        mNavigationView.getMenu().findItem(R.id.menu_app_update).setIcon(new IconicsDrawable(this, Ionicons.Icon.ion_ios_loop));
/*        mNavigationView.getMenu().findItem(R.id.menu_download_manager).setIcon(new IconicsDrawable(this, Cniao5Font.Icon.cniao_download));
        mNavigationView.getMenu().findItem(R.id.menu_app_uninstall).setIcon(new IconicsDrawable(this, Ionicons.Icon.ion_ios_trash_outline));*/
/*        mNavigationView.getMenu().findItem(R.id.menu_supoin).setIcon(new IconicsDrawable(this, Ionicons.Icon.ion_ios_loop));
        mNavigationView.getMenu().findItem(R.id.menu_hopeland).setIcon(new IconicsDrawable(this, Ionicons.Icon.ion_ios_loop));*/
        mNavigationView.getMenu().findItem(R.id.menu_setting).setIcon(new IconicsDrawable(this, Ionicons.Icon.ion_ios_gear_outline));

        mNavigationView.getMenu().findItem(R.id.menu_logout).setIcon(new IconicsDrawable(this, Cniao5Font.Icon.cniao_shutdown));

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {


                switch (item.getItemId())
                {

                    case R.id.menu_logout:


                        break;
                    case R.id.menu_app_update:
                        if (DeviceUtil.getDeviceId() == DeviceUtil.SUPOIN)
                        {
                            DeviceUtil.setDeviceId(DeviceUtil.HOPELAND);
                        } else
                        {
                            DeviceUtil.setDeviceId(DeviceUtil.SUPOIN);
                        }
                        //mToolBar.setTitle("Ioter(" + DeviceUtil.getDeviceName(DeviceUtil.getDeviceId()) + ")");
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.menu_setting:

                        startActivity(new Intent(MainActivity.this, SettingActivity.class));

                        break;

                }


                return false;
            }
        });


        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.open, R.string.close);

        drawerToggle.syncState();

        mDrawerLayout.addDrawerListener(drawerToggle);


    }


    @Override
    public void onClick(View v)
    {
        PropertiesManager propertiesInstance;
        switch (v.getId())
        {
            case R.id.out_rlyt:
                if (DeviceUtil.getDeviceId() == DeviceUtil.SUPOIN)
                {
                    startActivity(new Intent(this, SupoinEpcInOutActivity.class));
                } else if (DeviceUtil.getDeviceId() == DeviceUtil.HOPELAND)
                {
                    propertiesInstance = Adapt.getPropertiesInstance();
                    if (!propertiesInstance.support("UHF"))
                    {
                        ToastUtil.toast("该设备不支持高频读写");
                        return;
                    }
                    startActivity(new Intent(this, EpcInOutActivity.class));
                }
                break;
            case R.id.in_rlyt:
                if (DeviceUtil.getDeviceId() == DeviceUtil.SUPOIN)
                {
                    Intent intent = new Intent(this, SupoinEpcInOutActivity.class);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                } else if (DeviceUtil.getDeviceId() == DeviceUtil.HOPELAND)
                {
                    propertiesInstance = Adapt.getPropertiesInstance();
                    if (!propertiesInstance.support("UHF"))
                    {
                        ToastUtil.toast("该设备不支持高频读写");
                        return;
                    }
                    if (!propertiesInstance.support("UHF"))
                    {
                        ToastUtil.toast("该设备不支持高频读写");
                        return;
                    }
                    Intent intent = new Intent(this, EpcInOutActivity.class);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                }
                break;
            case R.id.store_in_rlyt:
                if (DeviceUtil.getDeviceId() == DeviceUtil.SUPOIN)
                {
                    Intent intent = new Intent(this, SupoinEpcInOutActivity.class);
                    intent.putExtra("type", 2);
                    startActivity(intent);
                } else if (DeviceUtil.getDeviceId() == DeviceUtil.HOPELAND)
                {
                    propertiesInstance = Adapt.getPropertiesInstance();
                    if (!propertiesInstance.support("UHF"))
                    {
                        ToastUtil.toast("该设备不支持高频读写");
                        return;
                    }
                    if (!propertiesInstance.support("UHF"))
                    {
                        ToastUtil.toast("该设备不支持高频读写");
                        return;
                    }
                    Intent intent = new Intent(this, EpcInOutActivity.class);
                    intent.putExtra("type", 2);
                    startActivity(intent);
                }
                break;
            case R.id.check_rlyt:
                if (DeviceUtil.getDeviceId() == DeviceUtil.SUPOIN)
                {
                    startActivity(new Intent(this, SupoinEpcCheckActivity.class));
                } else if (DeviceUtil.getDeviceId() == DeviceUtil.HOPELAND)
                {
                    startActivity(new Intent(this, EpcCheckActivity.class));
                }
                break;
            case R.id.more_rlyt:
                startActivity(new Intent(this, MoreActivity.class));
                break;
            case R.id.scan_rlyt:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                            1);
                } else
                {
                    //有权限，直接拍照
                    Intent intent2 = new Intent(getApplication(), CaptureActivity.class);
                    startActivityForResult(intent2, REQUEST_CODE);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults)
    {
        if (requestCode == 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(getApplication(), CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);

            } else
            {
                // Permission Denied
                //  displayFrameworkBugMessageAndExit();
                Toast.makeText(this, "请在应用管理中打开“相机”访问权限！", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE)
        {
            //处理扫描结果（在界面上显示）
            if (null != data)
            {
                Bundle bundle = data.getExtras();
                if (bundle == null)
                {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS)
                {
                    String epcResult = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + epcResult, Toast.LENGTH_LONG).show();
                    if (epcResult != null)
                    {
                        mPresenter.getProductInfo(DataUtil.convertStringToHex(epcResult.replace("\n", "")).trim());
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED)
                {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == REQUEST_CAMERA_PERM)
        {
            Toast.makeText(this, "从设置页面返回...", Toast.LENGTH_SHORT)
                    .show();
        }
    }


}
