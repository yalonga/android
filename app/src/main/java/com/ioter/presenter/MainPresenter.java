package com.ioter.presenter;


import android.content.Intent;
import android.text.TextUtils;

import com.ioter.common.util.ToastUtil;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.presenter.contract.MainContract;
import com.ioter.ui.activity.ScanResultActivity;

import java.util.List;

import javax.inject.Inject;


import static android.Manifest.permission.READ_PHONE_STATE;

public class MainPresenter extends BasePresenter<MainContract.IMainModel, MainContract.MainView>
{

    @Inject
    public MainPresenter(MainContract.IMainModel iMainModel, MainContract.MainView mainView)
    {
        super(iMainModel, mainView);
    }

    public void getProductInfo(String epc){

        mModel.getProductInfo(epc, new WebserviceRequest.WebCallback()
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
                    ToastUtil.toast("数据异常");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("result", result);
                intent.setClass(mContext, ScanResultActivity.class);
                mContext.startActivity(intent);

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
