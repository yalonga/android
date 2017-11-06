package com.ioter.presenter;


import com.ioter.common.rx.ErrorHandlerWebCallBack;
import com.ioter.common.sqlite.ClothesData;
import com.ioter.common.util.ToastUtil;
import com.ioter.presenter.contract.CheckContract;
import com.ioter.ui.activity.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

public class CheckPresenter extends BasePresenter<CheckContract.ICheckModel, CheckContract.CheckView>
{

    @Inject
    public CheckPresenter(CheckContract.ICheckModel iMainModel, CheckContract.CheckView mainView)
    {
        super(iMainModel, mainView);
    }

    public void getCheckList()
    {
        mModel.getCheckList(new ErrorHandlerWebCallBack(mContext, mView)
        {
            @Override
            public void onResultData(int status, JSONObject object, String errMsg)
            {
                try
                {
                    if (status == 0 && object != null)
                    {
                        mView.setId(object.getInt("ID"));
                        ArrayList<ClothesData> dataList = new ArrayList<ClothesData>();
                        JSONArray jsonArray = object.getJSONArray("ListDetail");
                        if (jsonArray != null && jsonArray.length() > 0)
                        {
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (jsonObject != null)
                                {
                                    ClothesData data = new ClothesData();
                                    data.mEpc = jsonObject.getString("EPC");
                                    Object clothing = jsonObject.get("Clothing");
                                    //JSONObject clothesObject = jsonObject.getJSONObject("Clothing");
                                    if (clothing != null && clothing instanceof JSONObject)
                                    {
                                        JSONObject clothesObject = (JSONObject) clothing;
                                        data.mName = clothesObject.getString("Name");
                                        data.mStyleNum = clothesObject.getString("StyleNo");
                                        data.mColour = clothesObject.getString("Color");
                                        data.mSize = clothesObject.getString("Size");
                                        data.mPrice = String.format("%.2f", clothesObject.getDouble("Price"));
                                    }
                                    dataList.add(data);
                                }
                            }
                        }
                        if (dataList != null && dataList.size() > 0)
                        {
                            mView.updateList(dataList);
                        } else
                        {
                            ToastUtil.toast("没有需要盘点的数据");
                            ((BaseActivity) mContext).finish();
                        }
                    }
                } catch (
                        JSONException e)

                {
                    e.printStackTrace();
                    onError(e);
                }
            }

        });
    }


    public void submitEpcList(ArrayList<String> checkedEpcList, int mID)
    {
        mModel.submitEpcList(checkedEpcList, mID, new ErrorHandlerWebCallBack(mContext, mView)
        {

            @Override
            public void onResultData(int status, JSONObject object, String errMsg)
            {
                if (status == 0)
                {
                    ToastUtil.toast("数据提交成功");
                    ((BaseActivity) mContext).finish();
                } else
                {
                    ToastUtil.toast("数据提交失败");
                }
            }


        });
    }


    public void submitWarehouseEpcList(String jsonArray, int whId, int whSiteId, String trackingNo, int receiveStoreId, int userId, final int type)
    {
        mModel.submitWarehouseEpcList(jsonArray, whId, whSiteId, trackingNo, receiveStoreId, userId, type, new ErrorHandlerWebCallBack(mContext, mView)
        {

            @Override
            public void onResultData(int status, JSONObject object, String errMsg)
            {

                if (status == 0)
                {
                    if (type == 0)
                    {
                        ToastUtil.toast("出库成功");
                    } else if (type == 1)
                    {
                        ToastUtil.toast("入库成功");
                    } else
                    {
                        ToastUtil.toast("入店成功");
                    }
                    ((BaseActivity) mContext).finish();
                } else
                {
                    if (type == 0)
                    {
                        ToastUtil.toast("出库失败:" + errMsg);
                    } else if (type == 1)
                    {
                        ToastUtil.toast("入库失败:" + errMsg);
                    } else
                    {
                        ToastUtil.toast("入店失败：" + errMsg);
                    }
                }
            }

        });
    }

    public void getWarehouseData(int id)
    {
        mModel.getWarehouseData(id, new ErrorHandlerWebCallBack(mContext, mView)
        {
            @Override
            public void onResultData(int status, JSONObject object, String errMsg)
            {
                if (status == 0)
                {
                    mView.setWareData(object);
                } else
                {
                    ToastUtil.toast(errMsg);
                }
            }
        });

    }


}
