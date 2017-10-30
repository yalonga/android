package com.ioter.ui.activity;

import android.os.Bundle;
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
import com.ioter.common.util.SettingSPUtil;
import com.ioter.common.util.ToastUtil;
import com.ioter.common.util.UIConstant;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.di.component.AppComponent;
import com.ioter.ui.adapter.SelectPop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 出入库
 * 
 * @author Administrator
 * 
 */
public class EpcInOutActivity extends UHFBaseActivity
{
    /** 0 出库 1 入库 **/
    private int mType;
    private int mWarehouseId; // 仓库id;
    private int mShelfId; // 货架id;
    private int mWarehousePosition;
    private int mShelfPosition;
    private JSONArray mWarehouseData;

    private TextView mWarehouseTv;
    private TextView mShelfTv;
    private Button mReadBtn;
    private MyAdapter mAdapter;
    private Object hmList_Lock = new Object();
    private HashMap<String, EPCModel> mCacheList = new HashMap<String, EPCModel>();
    private ArrayList<EPCModel> mEpcList = new ArrayList<>();

    @Override
    protected void onDestroy()
    {
        SettingSPUtil.putInt(SettingSPUtil.Warehouse_Id + "_" + mType, mWarehouseId);
        SettingSPUtil.putInt(SettingSPUtil.Shelf_Id + "_" + mType, mShelfId);
        super.onDestroy();
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_epc_inout;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {

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
        findViewById(R.id.app_common_bar_left_iv).setOnClickListener(this);
        String title = mType == 1 ? "入库" : "出库";
        ((TextView) findViewById(R.id.app_common_bar_title_tv)).setText(title);
        TextView rightTv = (TextView) findViewById(R.id.app_common_bar_right_tv);
        rightTv.setText("提交");
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setOnClickListener(this);
    }

    private void initView()
    {
        mWarehouseTv = (TextView) findViewById(R.id.warehouse_tv);
        mShelfTv = (TextView) findViewById(R.id.shelf_tv);
        findViewById(R.id.warehouse_llyt).setOnClickListener(this);
        findViewById(R.id.shelf_llyt).setOnClickListener(this);
        mReadBtn = (Button) findViewById(R.id.read_btn);
        mReadBtn.setOnClickListener(this);
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

    @Override
    protected void keyDown()
    {
        // TODO Auto-generated method stub
        super.keyDown();
        mReadBtn.setText(getString(R.string.stop));
        mReadBtn.setClickable(false);
    }

    @Override
    protected void keyUp()
    {
        // TODO Auto-generated method stub
        super.keyUp();
        mReadBtn.setText(getString(R.string.start));
        mReadBtn.setClickable(true);
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
                    }
                    catch (JSONException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mWarehouseSelectPop.dismiss();
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        mWarehouseSelectPop.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    private void showShelfSelect(View v)
    {
        final SelectPop mShelfSelectPop = new SelectPop(this);
        try
        {
            JSONObject jsonObject = mWarehouseData.getJSONObject(mWarehousePosition);
            JSONArray shelfArray = jsonObject.getJSONArray("listWhSite");
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
                        JSONArray shelfArray = jsonObject.getJSONArray("listWhSite");
                        JSONObject shelfObject = shelfArray.getJSONObject(mShelfPosition);
                        mShelfTv.setText(shelfObject.getString("WhSiteName"));
                        mShelfId = shelfObject.getInt("ID");
                    }
                    catch (JSONException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mShelfSelectPop.dismiss();
                }
            });
        }
        catch (JSONException e)
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
        case R.id.app_common_bar_left_iv:
            back();
            break;
        case R.id.app_common_bar_right_tv:
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
        case R.id.read_btn:
            if (mWarehouseId == 0 || mShelfId == 0)
            {
                ToastUtil.toast("请先设置仓库及货架");
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
        }
        else
        {
            Pingpong_Stop();
            btnRead.setText(getString(R.string.start));
        }
    }

    @Override
    public void OutPutEPC(EPCModel model)
    {
        // TODO Auto-generated method stub
        super.OutPutEPC(model);
        try
        {
            synchronized (hmList_Lock)
            {
                if (mCacheList.containsKey(model._EPC + model._TID))
                {
                    EPCModel tModel = mCacheList.get(model._EPC + model._TID);
                    tModel._TotalCount++;
                }
                else
                {
                    mCacheList.put(model._EPC + model._TID, model);
                    mEpcList.add(model);
                }
            }
        }
        catch (Exception ex)
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
            // TODO Auto-generated method stub
            return mDataList.size();
        }

        @Override
        public EpcInOutData getItem(int position)
        {
            // TODO Auto-generated method stub
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
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
            }
            else
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
            }
            else
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
/*        SoapObject request = new SoapObject("http://tempuri.org/", "GetAll");
        request.addProperty("userId", 2);
        AppApplication.getWebserviceRequest().submit(request, UIConstant.getWarehouseUrl(), UIConstant.getWarehouseSoapAction(),
                new WebserviceRequest.WebCallback()
                {

                    @Override
                    public void callbackResult(String result)
                    {
                        if (TextUtils.isEmpty(result))
                        {
                            return;
                        }
                        try
                        {
                            mWarehouseData = new JSONArray(result);
                            initSp();
                        }
                        catch (JSONException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            ToastUtil.toast("数据解析失败：" + result);
                            finish();
                        }
                    }
                });*/
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
                    JSONArray shelfArray = object.getJSONArray("listWhSite");
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
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
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
        JSONArray jsonArray = null;
        try
        {
            jsonArray = new JSONArray();
            for (int i = 0; i < dataList.size(); i++)
            {
                EpcInOutData epcInOutData = dataList.get(i);
                JSONObject object = new JSONObject();
                object.put("InWhID", epcInOutData.InWhID);
                object.put("InWhSiteID", epcInOutData.InWhSiteID);
                object.put("EPC", epcInOutData.EPC);
                object.put("InUserID", epcInOutData.InUserID);
                jsonArray.put(object);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        if (jsonArray == null)
        {
            ToastUtil.toast("数据封装失败");
            return;
        }
        String name = mType == 1 ? "StockIn" : "StockOut";
        SoapObject request = new SoapObject("http://tempuri.org/", name);
        request.addProperty("listStockInJson", jsonArray.toString());
        String soapAction = mType == 1 ? UIConstant.getEpcInSoapAction() : UIConstant.getEpcOutSoapAction();
       /* AppApplication.getWebserviceRequest().submit(request, UIConstant.getEpcInOutUrl(), soapAction, new WebserviceRequest.WebCallback()
        {

            @Override
            public void callbackResult(String result)
            {
                if (TextUtils.isEmpty(result))
                {
                    return;
                }
                if (result.equals("true"))
                {
                    if (mType == 0)
                    {
                        ToastUtil.toast("出库成功");
                    }
                    else
                    {
                        ToastUtil.toast("入库成功");
                    }
                    finish();
                }
                else
                {
                    if (mType == 0)
                    {
                        ToastUtil.toast("出库失败" + result);
                    }
                    else
                    {
                        ToastUtil.toast("入库失败" + result);
                    }
                }
            }
        });*/
    }

}
