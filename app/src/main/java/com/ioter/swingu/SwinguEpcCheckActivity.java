package com.ioter.swingu;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.clouiotech.pda.rfid.EPCModel;
import com.ioter.R;
import com.ioter.common.sqlite.ClothesData;
import com.ioter.common.util.ToastUtil;
import com.ioter.di.component.AppComponent;
import com.ioter.di.component.DaggerCheckComponent;
import com.ioter.di.module.CheckModule;
import com.ioter.presenter.CheckPresenter;
import com.ioter.presenter.contract.CheckContract;
import com.ioter.ui.activity.BaseActivity;
import com.ioter.ui.adapter.DefaultCheckAdapter;
import com.ioter.ui.adapter.SelectPop;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

import static com.ioter.ui.activity.MainActivity.MESSAGE_DEVICE_ADDRESS;
import static com.ioter.ui.activity.MainActivity.MESSAGE_STATE_CHANGE;
import static com.ioter.ui.activity.MainActivity.MESSAGE_TAG;
import static com.ioter.ui.activity.MainActivity.MESSAGE_TOAST;
import static com.ioter.ui.activity.MainActivity.SCANNER_ADDR;
import static com.ioter.ui.activity.MainActivity.TOAST;

/**
 * 盘点
 *
 */
public class SwinguEpcCheckActivity extends BaseActivity<CheckPresenter> implements CheckContract.CheckView, DefaultCheckAdapter.IEpcCheck
{
    private int mWarehouseId; // 仓库id;
    private int mShelfId; // 货架id;
    private String mStroeName; // 门店
    private int mWarehousePosition;
    private int mShelfPosition;
    private JSONArray mWarehouseData;
    private JSONArray mStoreData;

    private TextView mWarehouseTv;
    private TextView mShelfTv;
    private TextView mStoreTv;
    private int mID;
    private Object hmList_Lock = new Object();
    private HashMap<String, EPCModel> mCacheList = new HashMap<String, EPCModel>();
    private ArrayList<EPCModel> mEpcList = new ArrayList<>();
    private DefaultCheckAdapter mAdapter;
    @BindView(R.id.commit_btn)
    Button mCommitBtn;
    @BindView(R.id.check_num_tv)
    TextView mCheckTv;
    @BindView(R.id.un_check_num_tv)
    TextView mUnCheckTv;
    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    private FragmentScannerList mFragment_scanner_list;
    private SwingAPI mSwing = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_swing_epc_check;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
        DaggerCheckComponent.builder().appComponent(appComponent).checkModule(new CheckModule(this)).build().inject(this);
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
        if (mSwing == null) mSwing = new SwingAPI(this, mHandler);
        mFragment_scanner_list = new FragmentScannerList();
        mFragment_scanner_list.setSwing(mSwing);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, mFragment_scanner_list);
        transaction.commit();

        mCommitBtn.setOnClickListener(this);
        //为RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        mWarehouseTv = (TextView) findViewById(R.id.warehouse_tv);
        mShelfTv = (TextView) findViewById(R.id.shelf_tv);
        mStoreTv = (TextView) findViewById(R.id.store_tv);
        findViewById(R.id.warehouse_llyt).setOnClickListener(this);
        findViewById(R.id.shelf_llyt).setOnClickListener(this);
        findViewById(R.id.store_llyt).setOnClickListener(this);

        takeWarehouseData();
        takeTaskList();
    }

    private void takeWarehouseData()
    {
        mPresenter.getWarehouseData(2);
    }


    private String mConnectedDeviceName;
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1)
                    {
                        case SwingAPI.STATE_NONE:
                            if (!TextUtils.isEmpty(mConnectedDeviceName))
                            {
                                Toast.makeText(getApplication(), String.format("%s is disconnected", mConnectedDeviceName), Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                case MESSAGE_TAG:
                    // TODO �±� �ν� ó��
                    byte[] readBuf = (byte[]) msg.obj;
                    if (readBuf != null)
                    {
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        mAdapter.updateEpcValue(readMessage);
                    }
                    break;
                case MESSAGE_DEVICE_ADDRESS:
                    String mLast_addr = msg.getData().getString(SCANNER_ADDR);
                    BluetoothDevice scanner = mFragment_scanner_list.mBTadapter.getRemoteDevice(mLast_addr);
                    mConnectedDeviceName = scanner.getName();
                    if (!TextUtils.isEmpty(mConnectedDeviceName))
                    {
                        Toast.makeText(getApplication(), String.format("%s is connected", mConnectedDeviceName), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplication(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    class EpcValue
    {
        String epcValue;
        int count = 1;
    }


    private void showWarehouseSelect(View v)
    {
        final SelectPop mWarehouseSelectPop = new SelectPop(this);
        try
        {
            ArrayList<String> wareList = new ArrayList<String>();
            for (int i = 0; i < mWarehouseData.length(); i++)
            {
                wareList.add(mWarehouseData.getJSONObject(i).getString("WhName"));
            }
            mWarehouseSelectPop.updateData(wareList);
            mWarehouseSelectPop.setonItemClickListener(new AdapterView.OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    mWarehousePosition = position;
                    try
                    {
                        JSONObject jsonObject = mWarehouseData.getJSONObject(position);
                        mWarehouseTv.setText(jsonObject.getString("WhName"));
                        mWarehouseId = jsonObject.getInt("ID");
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    mWarehouseSelectPop.dismiss();
                }
            });
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        mWarehouseSelectPop.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    private void showStoreSelect(View v)
    {
        final SelectPop mStoreSelectPop = new SelectPop(this);
        try
        {
            ArrayList<String> storeList = new ArrayList<String>();
            for (int i = 0; i < mStoreData.length(); i++)
            {
                storeList.add(mStoreData.getJSONObject(i).getString("StoreName"));
            }
            mStoreSelectPop.updateData(storeList);
            mStoreSelectPop.setonItemClickListener(new AdapterView.OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    try
                    {
                        JSONObject jsonObject = mStoreData.getJSONObject(position);
                        mStoreTv.setText(jsonObject.getString("StoreName"));
                        mStroeName = jsonObject.getString("StoreName");
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    mStoreSelectPop.dismiss();
                }
            });
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        mStoreSelectPop.showAtLocation(v, Gravity.CENTER, 0, 0);
    }


    private void showShelfSelect(View v)
    {
        final SelectPop mShelfSelectPop = new SelectPop(this);
        try
        {
            JSONObject jsonObject = mWarehouseData.getJSONObject(mWarehousePosition);
            JSONArray shelfArray = jsonObject.getJSONArray("ListWhSite");
            ArrayList<String> shelfList = new ArrayList<String>();
            for (int i = 0; i < shelfArray.length(); i++)
            {
                shelfList.add(shelfArray.getJSONObject(i).getString("WhSiteName"));
            }
            mShelfSelectPop.updateData(shelfList);
            mShelfSelectPop.setonItemClickListener(new AdapterView.OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    mShelfPosition = position;
                    try
                    {
                        JSONObject jsonObject = mWarehouseData.getJSONObject(mWarehousePosition);
                        JSONArray shelfArray = jsonObject.getJSONArray("ListWhSite");
                        JSONObject shelfObject = shelfArray.getJSONObject(mShelfPosition);
                        mShelfTv.setText(shelfObject.getString("WhSiteName"));
                        mShelfId = shelfObject.getInt("ID");
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    mShelfSelectPop.dismiss();
                }
            });
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        mShelfSelectPop.showAtLocation(v, Gravity.CENTER, 0, 0);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.commit_btn:
                submitEpcList();
                break;
            case R.id.warehouse_llyt:
                if (mWarehouseData == null || mWarehouseData.length() == 0)
                {
                    ToastUtil.toast("没有可用的仓库数据");
                    return;
                }
                showWarehouseSelect(v);
                break;
            case R.id.shelf_llyt:
                if (mWarehouseData == null || mWarehouseData.length() == 0 || mWarehouseData.length() <= mWarehousePosition)
                {
                    ToastUtil.toast("没有可用的仓库数据");
                    return;
                }
                if (mWarehouseId == 0)
                {
                    ToastUtil.toast("请先选择仓库");
                    return;
                }
                showShelfSelect(v);
                break;
            case R.id.store_llyt:
                if (mWarehouseId == 0 || mShelfId == 0)
                {
                    ToastUtil.toast("请先设置仓库及货架");
                    return;
                }
                showStoreSelect(v);
                break;
            default:
                break;
        }
    }


    /**
     * 获取要盘点的清单列表
     */
    private void takeTaskList()
    {
        mPresenter.getCheckList();
    }

    /**
     * 将已盘点的EPC列表提交到后台
     */
    private void submitEpcList()
    {
        ArrayList<String> checkedEpcList = mAdapter.getCheckedEpcList();
        if (checkedEpcList.size() == 0)
        {
            ToastUtil.toast("没有可提交数据");
            return;
        }

        mPresenter.submitEpcList(checkedEpcList, mID);
    }

    @Override
    public void updateList(ArrayList<ClothesData> list)
    {
        if (mAdapter == null)
        {
            mAdapter = new DefaultCheckAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setCheckedCountListener(this);
        }
        mAdapter.setNewData(list);
    }

    @Override
    public void setId(int id)
    {
        mID = id;
    }

    @Override
    public void setWareData(JSONObject object)
    {
        try
        {
            mWarehouseData = object.getJSONArray("ListWh");
            mStoreData = object.getJSONArray("ListStore");
        } catch (JSONException e)
        {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void checkedCount(int totalCount, int count)
    {
        mCheckTv.setText("已盘点：" + count);
        mUnCheckTv.setText("未盘点：" + (totalCount - count));
    }
}
