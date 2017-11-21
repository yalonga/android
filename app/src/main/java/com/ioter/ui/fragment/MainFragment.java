package com.ioter.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.clouiotech.port.Adapt;
import com.clouiotech.port.PropertiesManager;
import com.ioter.R;
import com.ioter.common.util.DataUtil;
import com.ioter.common.util.DeviceUtil;
import com.ioter.common.util.ToastUtil;
import com.ioter.di.component.AppComponent;
import com.ioter.di.component.DaggerMainComponent;
import com.ioter.di.module.MainModule;
import com.ioter.presenter.MainPresenter;
import com.ioter.presenter.contract.MainContract;
import com.ioter.supoin.SupoinEpcCheckActivity;
import com.ioter.supoin.SupoinEpcInOutActivity;
import com.ioter.swingu.SwinguEpcInOutActivity;
import com.ioter.ui.activity.EpcCheckActivity;
import com.ioter.ui.activity.EpcInOutActivity;
import com.ioter.ui.activity.MoreActivity;
import com.ioter.ui.widget.BannerLayout;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ioter.AppApplication.getApplication;

/**
 * Created by Administrator on 2017/11/20.
 */

public class MainFragment extends BaseFragment<MainPresenter> implements MainContract.MainView
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


    @BindView(R.id.banner)
    BannerLayout mBannerLayout;


    private int mDevice;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
            mDevice = bundle.getInt("device");
    }

    @Override
    public int setLayout()
    {
        return R.layout.fragment_main;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
        DaggerMainComponent.builder().appComponent(appComponent).mainModule(new MainModule(this))
                .build().inject(this);
    }

    @Override
    public void init(View view)
    {
        initBanner();
    }

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


    @OnClick({R.id.out_rlyt, R.id.in_rlyt, R.id.store_in_rlyt, R.id.check_rlyt, R.id.more_rlyt, R.id.scan_rlyt})
    public void onClick(View v)
    {
        PropertiesManager propertiesInstance;
        switch (v.getId())
        {
            case R.id.out_rlyt:
                if (DeviceUtil.getDeviceId() == DeviceUtil.SUPOIN)
                {
                    startActivity(new Intent(this.getActivity(), SupoinEpcInOutActivity.class));
                } else if (DeviceUtil.getDeviceId() == DeviceUtil.HOPELAND)
                {
                    propertiesInstance = Adapt.getPropertiesInstance();
                    if (!propertiesInstance.support("UHF"))
                    {
                        ToastUtil.toast("该设备不支持高频读写");
                        return;
                    }
                    startActivity(new Intent(this.getActivity(), EpcInOutActivity.class));
                } else
                {
                    Intent intent = new Intent(this.getActivity(), SwinguEpcInOutActivity.class);
                    intent.putExtra("type", 0);
                    startActivity(intent);
                }
                break;
            case R.id.in_rlyt:
                if (DeviceUtil.getDeviceId() == DeviceUtil.SUPOIN)
                {
                    Intent intent = new Intent(this.getActivity(), SupoinEpcInOutActivity.class);
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
                    Intent intent = new Intent(this.getActivity(), EpcInOutActivity.class);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                } else
                {
                    Intent intent = new Intent(this.getActivity(), SwinguEpcInOutActivity.class);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                }
                break;
            case R.id.store_in_rlyt:
                if (DeviceUtil.getDeviceId() == DeviceUtil.SUPOIN)
                {
                    Intent intent = new Intent(this.getActivity(), SupoinEpcInOutActivity.class);
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
                    Intent intent = new Intent(this.getActivity(), EpcInOutActivity.class);
                    intent.putExtra("type", 2);
                    startActivity(intent);
                } else
                {
                    Intent intent = new Intent(this.getActivity(), SwinguEpcInOutActivity.class);
                    intent.putExtra("type", 2);
                    startActivity(intent);
                }
                break;
            case R.id.check_rlyt:
                if (DeviceUtil.getDeviceId() == DeviceUtil.SUPOIN)
                {
                    startActivity(new Intent(this.getActivity(), SupoinEpcCheckActivity.class));
                } else if (DeviceUtil.getDeviceId() == DeviceUtil.HOPELAND)
                {
                    startActivity(new Intent(this.getActivity(), EpcCheckActivity.class));
                }
                break;
            case R.id.more_rlyt:
                startActivity(new Intent(this.getActivity(), MoreActivity.class));
                break;
            case R.id.scan_rlyt:
                if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.CAMERA},
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
                Toast.makeText(this.getActivity(), "请在应用管理中打开“相机”访问权限！", Toast.LENGTH_LONG).show();
                this.getActivity().finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
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
                    if (epcResult != null)
                    {
                        mPresenter.getProductInfo(DataUtil.convertStringToHex(epcResult.replace("\n", "")).trim());
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED)
                {
                    Toast.makeText(this.getActivity(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == REQUEST_CAMERA_PERM)
        {
            Toast.makeText(this.getActivity(), "从设置页面返回...", Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
