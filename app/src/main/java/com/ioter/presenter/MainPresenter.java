package com.ioter.presenter;


import android.content.Intent;
import android.util.Log;

import com.ioter.bean.ScanInfoData;
import com.ioter.common.rx.ErrorHandlerWebCallBack;
import com.ioter.presenter.contract.MainContract;
import com.ioter.ui.activity.ScanResultActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

public class MainPresenter extends BasePresenter<MainContract.IMainModel, MainContract.MainView>
{

    @Inject
    public MainPresenter(MainContract.IMainModel iMainModel, MainContract.MainView mainView)
    {
        super(iMainModel, mainView);
    }

    public void getProductInfo(final String epc)
    {

        mModel.getProductInfo(epc, new ErrorHandlerWebCallBack(mContext, mView)
        {

            @Override
            public void onResultData(int status, JSONObject object, String errMsg)
            {
                if (status == 0)
                {
                    try
                    {
                        ArrayList<ScanInfoData> dataList = new ArrayList<ScanInfoData>();
                        Iterator ite = object.keys();
                        // 遍历jsonObject数据,添加到Map对象
                        while (ite.hasNext())
                        {
                            String key = ite.next().toString();
                            String value = object.get(key).toString();
                            ScanInfoData data = new ScanInfoData();
                            data.time = key.replace("\n", "").trim();
                            data.content = value.replace("\n", "").trim();
                            dataList.add(data);
                        }
                        if (dataList.size() > 0)
                        {
                            Intent intent = new Intent();
                            intent.putExtra("result", dataList);
                            intent.setClass(mContext, ScanResultActivity.class);
                            mContext.startActivity(intent);
                        }
                    } catch (JSONException e)
                    {
                        onError(e);
                    }
                } else
                {
                    Log.e("errMsg", errMsg);
                }
            }

        });
    }


}
