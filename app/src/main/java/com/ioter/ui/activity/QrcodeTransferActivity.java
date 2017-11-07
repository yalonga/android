package com.ioter.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ioter.R;
import com.ioter.common.util.ACache;
import com.ioter.common.util.DataUtil;
import com.ioter.common.util.DateUtil;
import com.ioter.common.util.ToastUtil;
import com.ioter.common.util.UIConstant;
import com.ioter.di.component.AppComponent;
import com.ioter.hopeland.Comm;
import com.ioter.hopeland.EpcBeen;
import com.ioter.hopeland.SupoinUHFBaseActivity;
import com.ioter.hopeland.uhf.UHF5Base.StringTool;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

import static com.ioter.R.id.read_bt;
import static com.ioter.hopeland.Comm.Awl;
import static com.ioter.hopeland.Comm.opeT;
import static com.ioter.hopeland.Comm.operateType.nullOperate;
import static com.ioter.hopeland.Comm.operateType.setPower;
import static com.ioter.hopeland.Comm.setAntPower;
import static com.ioter.ui.activity.MainActivity.REQUEST_CAMERA_PERM;

/**
 * 更多
 *
 * @author Administrator
 */
public class QrcodeTransferActivity extends SupoinUHFBaseActivity
{
    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;


    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.qrcode_tv)
    TextView mQrCodeTv;
    @BindView(R.id.epc_tv)
    TextView mEpcTv;
    @BindView(R.id.qrcode_bt)
    Button mQrCodeBt;
    @BindView(read_bt)
    Button mEpcReadBt;
    @BindView(R.id.write_bt)
    Button mEpcWriteBt;
    @BindView(R.id.write_et)
    EditText mWriteEpcEt;
    @BindView(R.id.set_power_et)
    EditText mPowerSetEt;
    @BindView(R.id.set_power_bt)
    Button mPowerSetBt;
    private String waterCode;


    @Override
    public int setLayout()
    {
        return R.layout.activity_transfer;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {

    }

    @Override
    public void init()
    {
        super.init();
        initTitle();
        initView();
        Comm.mRWLHandler = tagOpehandler;
        Comm.mOtherHandler = opeHandler;
    }

    @Override
    protected void showlist(ArrayList<EpcBeen> mEpcList)
    {
        if (mEpcList != null && mEpcList.size() > 0)
        {
            mEpcTv.setText(mEpcList.get(0).epcValue);
        }
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
        findViewById(R.id.qrcode_bt).setOnClickListener(this);
        findViewById(read_bt).setOnClickListener(this);
        findViewById(R.id.write_bt).setOnClickListener(this);
        findViewById(R.id.set_power_bt).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.qrcode_bt:
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
            case read_bt:
                readEpc();
/*                try
                {
                    mEpcTv.setText("");
                    opeT = Comm.operateType.readOpe;
                    int datatype = 0;//16进制
                    int ant = 0;//天线
                    int tagBank = 1;//epc块
                    String opCount = 6 + "";
                    String startAdd = 2 + "";//opCount：操作的块数；startAdd：起始地址；
                    Comm.readTag(ant, tagBank, opCount, startAdd, datatype);
                } catch (Exception e)
                {
                    Toast.makeText(this, "Exception:" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }*/
                break;
            case R.id.write_bt:
                String qrcode = mQrCodeTv.getText().toString();
                if (DataUtil.isEmpty(qrcode) || qrcode.length() != 13)
                {
                    ToastUtil.toast("请先设置条形码");
                    return;
                }
                String epc = mEpcTv.getText().toString();
                if (DataUtil.isEmpty(epc))
                {
                    ToastUtil.toast("请先读取EPC");
                    return;
                }
                try
                {
                    boolean isWrite = true;
                    opeT = Comm.operateType.writeOpe;
                    int datatype = 0;
                    int ant = 0;
                    int tagBank = 1;
                    String opCount = 6 + "";
                    String startAdd = 2 + "";

                    String strWriteData = createWriteData();
                    mWriteEpcEt.setText(strWriteData);


                    if (tagBank == 1 && (startAdd.equals("0") || startAdd.equals("1")))
                    {
                        Toast.makeText(this, "写操作不能操作EPC区第0块和第1块",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Comm.writeTag(ant, tagBank, opCount, startAdd, datatype, strWriteData);

                    int len = 0;
                    if (datatype == 0 && strWriteData.length() % 4 == 0)
                    {
                        String[] result = StringTool.stringToStringArray(strWriteData.toUpperCase(), 2);
                        len = (byte) ((result.length / 2 + result.length % 2) & 0xFF);
                    } else if (datatype == 1 && strWriteData.length() % 2 == 0)
                    {
                        len = strWriteData.length() / 2;
                    } else if (datatype == 2)
                    {
                        len = strWriteData.length();
                    } else
                    {
                        Toast.makeText(this, "输入的数据长度不对",
                                Toast.LENGTH_SHORT).show();
                        isWrite = false;
                    }
                    if (isWrite)
                    {
                        //etCountW.setText(String.valueOf(len));
                        Comm.writeTag(ant, tagBank, opCount, startAdd, datatype, strWriteData);
                    } else
                        Toast.makeText(this, "write fall",
                                Toast.LENGTH_SHORT).show();
                } catch (Exception e)
                {
                    Toast.makeText(this, "Exception:" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.set_power_bt:
                try
                {
                    Comm.opeT = setPower;
                    int ant1pow = Integer.parseInt(mPowerSetEt.getText().toString());
                    int ant2pow = 500;
                    int ant3pow = 500;
                    int ant4pow = 500;
                    setAntPower(ant1pow, ant2pow, ant3pow, ant4pow);
                } catch (Exception e)
                {
                    Toast.makeText(this,
                            "Set Exception:" + e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setPower();
    }

    @Override
    protected void setPower()
    {
        if (!isPowerSet)
        {
            isPowerSet = true;

            new Timer().schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Comm.opeT = setPower;
                        int ant1pow = 500;
                        int ant2pow = 500;
                        int ant3pow = 500;
                        int ant4pow = 500;
                        setAntPower(ant1pow, ant2pow, ant3pow, ant4pow);
                    } catch (Exception e)
                    {
                        return;
                    }
                }
            }, 500);
        }
    }

    private String createWriteData()
    {
        String qrCode = mQrCodeTv.getText().toString();
        int checkCode = new Random().nextInt(10);
        int year = DateUtil.getYear(new Date());
        int mon = DateUtil.getMon(new Date());
        String day = String.format("%02d", DateUtil.getDay(new Date()));
        String month = Integer.toHexString(mon);
        String date = (year + "").substring(2) + month + day;
        waterCode = ACache.get(this).getAsString(UIConstant.WATER_CODE);
        if (DataUtil.isEmpty(waterCode))
        {
            waterCode = "00000";
        } else
        {
            long parseValue = Long.parseLong(waterCode, 16);
            waterCode = padLeft(Long.toHexString(0x00001L + parseValue), 5);
        }
        return qrCode + checkCode + date + waterCode;
    }

    public static String padLeft(String s, int length)
    {
        byte[] bs = new byte[length];
        byte[] ss = s.getBytes();
        Arrays.fill(bs, (byte) (48 & 0xff));
        System.arraycopy(ss, 0, bs, length - ss.length, ss.length);
        return new String(bs);
    }

    private boolean isPowerSet;

    @Override
    protected void readEpc()
    {
        String controlText = mEpcReadBt.getText().toString();

        if (controlText.equals("读取EPC"))
        {
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
            mEpcReadBt.setText("停止读取");
        } else
        {
            Awl.ReleaseWakeLock();
            Comm.stopScan();
            mEpcReadBt.setText("读取EPC");
        }
    }


    private android.os.Handler tagOpehandler = new android.os.Handler()
    {
        @SuppressWarnings({"unchecked", "unused"})
        @Override
        public void handleMessage(Message msg)
        {
            try
            {
                switch (Comm.opeT)
                {
                    case readOpe:
                        Bundle rb = msg.getData();
                        String strErr = rb.getString("Err");
                        String strEPC = rb.getString("readData");
                        if (strEPC != "")
                        {
                            mEpcTv.setText(strEPC);
                        } else
                            Toast.makeText(QrcodeTransferActivity.this, "Read Fail" + strErr, Toast.LENGTH_SHORT).show();
                        break;
                    case writeOpe:
                        Bundle wb = msg.getData();
                        boolean isWriteSucceed = wb.getBoolean("isWriteSucceed");
                        if (isWriteSucceed)
                        {
                            ACache.get(QrcodeTransferActivity.this).put(UIConstant.WATER_CODE, waterCode);
                            Toast.makeText(QrcodeTransferActivity.this, "Write Succeed", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(QrcodeTransferActivity.this, "Write Fail", Toast.LENGTH_SHORT).show();
                        break;
                    case writeepcOpe:
                        Bundle web = msg.getData();
                        boolean isWriteEPCSucceed = web.getBoolean("isWriteSucceed");
                        if (isWriteEPCSucceed)
                            Toast.makeText(QrcodeTransferActivity.this, "Write Succeed", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(QrcodeTransferActivity.this, "Write Fail", Toast.LENGTH_SHORT).show();
                        break;
                    case lockOpe:
                        Bundle lb = msg.getData();
                        boolean isLockSucceed = lb.getBoolean("isLockSucceed");
                        if (isLockSucceed)
                            Toast.makeText(QrcodeTransferActivity.this, "Lock Succeed", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(QrcodeTransferActivity.this, "Lock Fail", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(QrcodeTransferActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
            Comm.opeT = nullOperate;
        }
    };

    private android.os.Handler opeHandler = new android.os.Handler()
    {
        @SuppressWarnings({"unchecked", "unused"})
        @Override
        public void handleMessage(Message msg)
        {
            try
            {
                switch (Comm.opeT)
                {
                    case setPower:
                        Bundle setPb = msg.getData();
                        boolean isSetPower = setPb.getBoolean("isSetPower");
                        if (isSetPower)
                            Toast.makeText(QrcodeTransferActivity.this,
                                    "Set Succeed", Toast.LENGTH_SHORT)
                                    .show();
                        else
                            Toast.makeText(QrcodeTransferActivity.this,
                                    "Set Fail", Toast.LENGTH_SHORT)
                                    .show();
                        break;
                    default:
                        break;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            Comm.opeT = nullOperate;
        }
    };

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
                        mQrCodeTv.setText(epcResult.replace("\n", "").trim());
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
