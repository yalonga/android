package com.ioter.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.clouiotech.pda.rfid.EPCModel;
import com.ioter.AppApplication;
import com.ioter.R;
import com.ioter.bean.EpcInOutData;
import com.ioter.common.sqlite.ClothesData;
import com.ioter.common.util.SettingSPUtil;
import com.ioter.common.util.ToastUtil;
import com.ioter.di.component.AppComponent;
import com.ioter.di.component.DaggerCheckComponent;
import com.ioter.di.module.CheckModule;
import com.ioter.presenter.contract.CheckContract;
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
 * 出入库
 *
 */
public class EpcInOutActivity extends UHFBaseActivity implements CheckContract.CheckView
{
    /**
     * 0 出库 1 入库
     **/
    private int mType;
    private int mWarehouseId; // 仓库id;
    private int mShelfId; // 货架id;
    private String mStroeName; // 门店
    private int mReceiveStoreId; // 门店id
    private int mWarehousePosition;
    private int mShelfPosition;
    private int mStorePosition;
    private JSONArray mWarehouseData;
    private JSONArray mStoreData;

    private TextView mWarehouseTv;
    private TextView mShelfTv;
    private TextView mStoreTv;
    private MyAdapter mAdapter;
    private Object hmList_Lock = new Object();
    private HashMap<String, EPCModel> mCacheList = new HashMap<String, EPCModel>();
    private ArrayList<EPCModel> mEpcList = new ArrayList<>();
    @BindView(R.id.read_btn)
    Button mReadBtn;
    @BindView(R.id.commit_btn)
    Button mCommitBtn;
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;

    @Override
    public int setLayout()
    {
        return R.layout.activity_epc_inout;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
        DaggerCheckComponent.builder().appComponent(appComponent).checkModule(new CheckModule(this)).build().inject(this);
    }

    @Override
    public void init()
    {
        mType = getIntent().getIntExtra("type", 0);
        initTitle();
        initView();
        takeWarehouseData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        mToolBar.setTitle(getTitleName(mType));
        mToolBar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private String getTitleName(int type)
    {
        switch (type)
        {
            case 0:
                return "出库";
            case 1:
                return "入库";
            case 2:
                return "入店";
        }
        return "";
    }

    private void initView()
    {
        mWarehouseTv = (TextView) findViewById(R.id.warehouse_tv);
        mShelfTv = (TextView) findViewById(R.id.shelf_tv);
        mStoreTv = (TextView) findViewById(R.id.store_tv);
        findViewById(R.id.warehouse_llyt).setOnClickListener(this);
        findViewById(R.id.shelf_llyt).setOnClickListener(this);
        findViewById(R.id.store_llyt).setOnClickListener(this);
        mReadBtn.setOnClickListener(this);
        mCommitBtn.setText(getTitleName(mType));
        mCommitBtn.setOnClickListener(this);
        ListView epcLv = (ListView) findViewById(R.id.epc_lv);
        mAdapter = new MyAdapter();
        epcLv.setAdapter(mAdapter);
    }

    @Override
    protected void ShowList()
    {
        if (!isStartPingPong)
            return;
        synchronized (hmList_Lock)
        {
            mAdapter.update(mEpcList);
        }
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
            mWarehouseSelectPop.setonItemClickListener(new OnItemClickListener()
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
            mStoreSelectPop.setonItemClickListener(new OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    mStorePosition = position;
                    try
                    {
                        JSONObject jsonObject = mStoreData.getJSONObject(position);
                        mStoreTv.setText(jsonObject.getString("StoreName"));
                        mStroeName = jsonObject.getString("StoreName");
                        mReceiveStoreId = jsonObject.getInt("ID");
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
            mShelfSelectPop.setonItemClickListener(new OnItemClickListener()
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

    private void back()
    {
        if (mReadBtn.getText().toString().equals(getString(R.string.stop)))
        {
            return;
        }
        finish();
    }

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
        }
    }

    @Override
    public void updateList(ArrayList<ClothesData> list)
    {

    }

    @Override
    public void setId(int id)
    {

    }

    @Override
    public void setWareData(JSONObject object)
    {
        try
        {
            mWarehouseData = object.getJSONArray("ListWh");
            mStoreData = object.getJSONArray("ListStore");
            //initSp();
        } catch (JSONException e)
        {
            e.printStackTrace();
            finish();
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

    private class MyAdapter extends BaseAdapter
    {

        private ArrayList<EpcInOutData> mDataList;

        public MyAdapter()
        {
            mDataList = new ArrayList<EpcInOutData>();
        }

        public void update(ArrayList<EPCModel> dataList)
        {
            if (dataList.size() == 0)
            {
                return;
            }
            for (EPCModel epcModel : dataList)
            {
                EpcInOutData epcInData = new EpcInOutData();
                epcInData.InWhID = mWarehouseId;
                epcInData.InWhSiteID = mShelfId;
                epcInData.InUserID = 2;
                epcInData.EPC = epcModel._EPC;
                mDataList.add(epcInData);
            }
            dataList.clear();
            notifyDataSetChanged();
        }

        public ArrayList<EpcInOutData> getDataList()
        {
            return mDataList;
        }

        @Override
        public int getCount()
        {
            return mDataList.size();
        }

        @Override
        public EpcInOutData getItem(int position)
        {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.listitem_epc_inout, parent, false);
                holder = new ViewHolder();
                holder.numTv = (TextView) convertView.findViewById(R.id.num_tv);
                holder.epcTv = (TextView) convertView.findViewById(R.id.epc_tv);
                holder.shelfTv = (TextView) convertView.findViewById(R.id.shelf_tv);
                holder.divideV = convertView.findViewById(R.id.divide_v);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            EpcInOutData item = getItem(position);
            holder.numTv.setText(position + 1 + "");
            holder.epcTv.setText(item.EPC + "");
            holder.shelfTv.setText(item.InWhSiteID + "");
            if (position == getCount() - 1)
            {
                holder.divideV.setVisibility(View.GONE);
            } else
            {
                holder.divideV.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        private class ViewHolder
        {
            TextView numTv;
            TextView epcTv;
            TextView shelfTv;
            View divideV;
        }

    }

    /**
     * 获取仓库及货架数据
     */
    private void takeWarehouseData()
    {
        mPresenter.getWarehouseData(2);
    }

    /**
     * 从SharedPreferences中获取之前设置的仓库和货架id
     */
    private void initSp()
    {
        int warehouseId = SettingSPUtil.getInt(SettingSPUtil.Warehouse_Id + "_" + mType);
        int shelfId = SettingSPUtil.getInt(SettingSPUtil.Shelf_Id + "_" + mType);
        try
        {
            for (int i = 0; i < mWarehouseData.length(); i++)
            {
                JSONObject object = mWarehouseData.getJSONObject(i);
                int wareID = object.getInt("ID");
                if (wareID == warehouseId)
                {
                    mWarehouseId = wareID;
                    mWarehouseTv.setText(object.getString("WhName"));
                    JSONArray shelfArray = object.getJSONArray("ListWhSite");
                    for (int j = 0; j < shelfArray.length(); j++)
                    {
                        JSONObject shelfObject = shelfArray.getJSONObject(j);
                        int id = shelfObject.getInt("ID");
                        if (id == shelfId)
                        {
                            mShelfId = shelfId;
                            mShelfTv.setText(shelfObject.getString("WhSiteName"));
                            break;
                        }
                    }
                    break;
                }

            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 提交入库EPC数据
     */
    private void submitEpcList()
    {
        ArrayList<EpcInOutData> dataList = mAdapter.getDataList();
        if (dataList.size() == 0)
        {
            ToastUtil.toast("没有可提交的数据");
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++)
        {
            EpcInOutData epcInOutData = dataList.get(i);
            list.add(epcInOutData.EPC);
        }
        mPresenter.submitWarehouseEpcList(AppApplication.getGson().toJson(list), mWarehouseId, mShelfId, "trackingNo", mReceiveStoreId, 2, mType);
    }

}
