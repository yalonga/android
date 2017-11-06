package com.ioter.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.clouiotech.pda.rfid.EPCModel;
import com.ioter.R;
import com.ioter.common.sqlite.ClothesData;
import com.ioter.common.util.ToastUtil;
import com.ioter.di.component.AppComponent;
import com.ioter.di.component.DaggerCheckComponent;
import com.ioter.di.module.CheckModule;
import com.ioter.presenter.contract.CheckContract;
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

/**
 * 盘点
 *
 * @author hzj
 */
public class EpcCheckActivity extends UHFBaseActivity implements CheckContract.CheckView, DefaultCheckAdapter.IEpcCheck
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
    @BindView(R.id.read_btn)
    Button mReadBtn;
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
        return R.layout.activity_epc_check;
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
        mCommitBtn.setOnClickListener(this);
        mReadBtn.setOnClickListener(this);

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

    @Override
    protected void ShowList()
    {
        if (!isStartPingPong)
            return;
        synchronized (hmList_Lock)
        {
/*            for (EPCModel item : mEpcList)
            {
                if(item._EPC.equals("45414153584C30434230"))
                {
                    ToastUtil.toast(item._EPC);
                }
            }*/
            if (mAdapter != null)
            {
                mAdapter.updateModel(mEpcList);
            }
        }
    }

    @Override
    protected void keyDown()
    {
        super.keyDown();
        mReadBtn.setText(getString(R.string.stop));
        mReadBtn.setClickable(false);
    }

    @Override
    protected void keyUp()
    {
        super.keyUp();
        mReadBtn.setText(getString(R.string.start));
        mReadBtn.setClickable(true);
        mAdapter.sortDataList();
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
            case R.id.read_btn:
                if (mWarehouseId == 0 || mShelfId == 0)
                {
                    ToastUtil.toast("请先设置仓库及货架");
                    return;
                }
                if (TextUtils.isEmpty(mStroeName) || mStroeName.length() == 0)
                {
                    ToastUtil.toast("请先设置门店");
                    return;
                }
                readEpc(v);
                break;
            default:
                break;
        }
    }


    /**
     * 读功能
     *
     * @param v
     */
    private void readEpc(View v)
    {
        Button btnRead = (Button) v;
        String controlText = btnRead.getText().toString();
        if (controlText.equals(getString(R.string.start)))
        {
            Pingpong_Read();
            btnRead.setText(getString(R.string.stop));
        } else
        {
            Pingpong_Stop();
            btnRead.setText(getString(R.string.start));
            mAdapter.sortDataList();
        }
    }

    @Override
    public void OutPutEPC(EPCModel model)
    {
        super.OutPutEPC(model);
        try
        {
            synchronized (hmList_Lock)
            {
                if (mCacheList.containsKey(model._EPC + model._TID))
                {
                    EPCModel tModel = mCacheList.get(model._EPC + model._TID);
                    tModel._TotalCount++;
                } else
                {
                    mCacheList.put(model._EPC + model._TID, model);
                    mEpcList.add(model);
                }
            }
        } catch (Exception ex)
        {
            Log.e("Debug", "标签输出异常：" + ex.getMessage());
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
