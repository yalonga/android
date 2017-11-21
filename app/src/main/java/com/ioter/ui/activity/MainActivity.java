package com.ioter.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioter.R;
import com.ioter.common.font.Cniao5Font;
import com.ioter.common.util.DeviceUtil;
import com.ioter.di.component.AppComponent;
import com.ioter.ui.fragment.MainFragment;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import butterknife.BindView;

public class MainActivity extends BaseActivity
{

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_ADDRESS = 4;
    public static final int MESSAGE_LOST = 5;
    public static final int MESSAGE_TOAST = 6;
    public static final int MESSAGE_START = 7;
    public static final int MESSAGE_STOP = 8;
    public static final int MESSAGE_BATTERY = 9;
    public static final int MESSAGE_VOLUME = 10;
    public static final int MESSAGE_CONTINUOUS = 11;
    public static final int MESSAGE_REPORT = 12;
    public static final int MESSAGE_RFPOWER = 13;
    public static final int MESSAGE_FOUND = 14;
    public static final int MESSAGE_THRESHOLD = 15;
    public static final int MESSAGE_UNIT = 16;
    public static final int MESSAGE_TAG = 17;
    public static final int MESSAGE_CLEAR = 18;
    public static final int MESSAGE_FIND = 19;
    public static final int MESSAGE_SEARCH_FINISH = 20;
    public static final int MESSAGE_ERROR = 21;


    public static final int MESSAGE_BARCODE = 22;
    public static final int MESSAGE_TAG_COUNT = 23;
    public static final int MESSAGE_MENU_ENABLE = 24;

    public static final int MESSAGE_TEMPERATURE = 25;
    public static final int MESSAGE_EPC_SIZE = 26;
    public static final int MESSAGE_LANGUAGE = 27;
    public static final int MESSAGE_MODEL = 28;
    public static final int MESSAGE_SESSION = 29;

    public static final String SCANNER_ADDR = "11:22:33:44:55:66";
    public static final String TOAST = "toast";


    private MainFragment mainFragment;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;


    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;


    private View headerView;
    private ImageView mUserHeadView;
    private TextView mTextUserName;

    @Override
    public int setLayout()
    {
        return R.layout.activity_main;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
    }

    @Override
    public void init()
    {
        initDrawerLayout();
        showFragment();
    }


    private void initDrawerLayout()
    {
        headerView = mNavigationView.getHeaderView(0);
        mUserHeadView = (ImageView) headerView.findViewById(R.id.img_avatar);
        mUserHeadView.setImageDrawable(new IconicsDrawable(this, Cniao5Font.Icon.cniao_head).colorRes(R.color.white));
        mTextUserName = (TextView) headerView.findViewById(R.id.txt_username);
        mNavigationView.getMenu().findItem(R.id.menu_app_supion).setIcon(new IconicsDrawable(this, Ionicons.Icon.ion_ios_loop));
        mNavigationView.getMenu().findItem(R.id.menu_app_hopeland).setIcon(new IconicsDrawable(this, Ionicons.Icon.ion_ios_loop));
        mNavigationView.getMenu().findItem(R.id.menu_app_swingu).setIcon(new IconicsDrawable(this, Ionicons.Icon.ion_ios_loop));
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
                    case R.id.menu_app_supion:
                        DeviceUtil.setDeviceId(DeviceUtil.SUPOIN);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.menu_app_hopeland:
                        DeviceUtil.setDeviceId(DeviceUtil.HOPELAND);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.menu_app_swingu:
                        DeviceUtil.setDeviceId(DeviceUtil.SWINGU);

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


    private void showFragment()
    {
        mainFragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt("device", DeviceUtil.getDeviceId());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, mainFragment);
        transaction.commitAllowingStateLoss();
    }




    @Override
    public void onClick(View view)
    {

    }
}
