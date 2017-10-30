package com.ioter.presenter;


import android.content.Intent;
import android.text.TextUtils;

import com.ioter.common.sqlite.ClothesData;
import com.ioter.common.util.ToastUtil;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.presenter.contract.CheckContract;
import com.ioter.presenter.contract.MainContract;
import com.ioter.ui.activity.BaseActivity;
import com.ioter.ui.activity.ScanResultActivity;

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
        mModel.getCheckList(new WebserviceRequest.WebCallback()
        {
            @Override
            public void onStart()
            {

            }

            @Override
            public void onNext(String result)
            {
                if (TextUtils.isEmpty(result))
                {
                    return;
                }
                try
                {
                    JSONObject object = new JSONObject(result);
                    mView.setId(object.getInt("ID"));
                    ArrayList<ClothesData> dataList = new ArrayList<ClothesData>();
                    JSONArray jsonArray = object.getJSONArray("ListDetail");
                    if (jsonArray != null && jsonArray.length() > 0)
                    {
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ClothesData data = new ClothesData();
                            data.mEpc = jsonObject.getString("EPC");
                            JSONObject clothesObject = jsonObject.getJSONObject("Clothing");
                            data.mName = clothesObject.getString("Name");
                            data.mStyleNum = clothesObject.getString("StyleNo");
                            data.mColour = clothesObject.getString("Color");
                            data.mSize = clothesObject.getString("Size");
                            data.mPrice = String.format("%.2f", clothesObject.getDouble("Price"));
                            dataList.add(data);
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
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    ToastUtil.toast(result + "");
                    ((BaseActivity) mContext).finish();
                }
            }

            @Override
            public void onError(String error)
            {

            }

            @Override
            public void onComplete()
            {

            }
        });
    }


    public void submitEpcList(ArrayList<String> checkedEpcList, int mID)
    {
        mModel.submitEpcList(checkedEpcList, mID, new WebserviceRequest.WebCallback()
        {
            @Override
            public void onStart()
            {

            }

            @Override
            public void onNext(String result)
            {
                if (TextUtils.isEmpty(result))
                {
                    return;
                }
                if (result.equals("true"))
                {
                    ToastUtil.toast("数据提交成功");
                    ((BaseActivity)mContext).finish();
                } else
                {
                    ToastUtil.toast("数据提交失败：" + result);
                }
            }

            @Override
            public void onError(String error)
            {

            }

            @Override
            public void onComplete()
            {

            }
        });
    }

}
