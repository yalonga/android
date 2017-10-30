package com.ioter.presenter;


import android.content.Intent;
import android.text.TextUtils;

import com.ioter.common.util.SettingSPUtil;
import com.ioter.common.util.ToastUtil;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.common.util.WebserviceRequest.WebCallback;
import com.ioter.presenter.contract.LoginContract;
import com.ioter.ui.activity.BaseActivity;
import com.ioter.ui.activity.LoginActivity;
import com.ioter.ui.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;


public class LoginPresenter extends BasePresenter<LoginContract.ILoginModel, LoginContract.LoginView>
{

    @Inject
    public LoginPresenter(LoginContract.ILoginModel iLoginModel, LoginContract.LoginView loginView)
    {
        super(iLoginModel, loginView);
    }

    public void login(String phone, String pwd)
    {

        mModel.login("kuguanyuan", "123", new WebserviceRequest.WebCallback()
        {
            @Override
            public void onStart()
            {
                mView.showLoading();
            }

            @Override
            public void onNext(String result)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(result);
                    int id = jsonObject.getInt("ID");
                    if (id <= 0)
                    {
                        ToastUtil.toast("账号id错误");
                        return;
                    }
                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                    ((BaseActivity) mContext).finish();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    ToastUtil.toast(result + "");
                }
            }

            @Override
            public void onError(String error)
            {
                mView.showError(error);
            }

            @Override
            public void onComplete()
            {
                mView.dismissLoading();
            }
        });
    }

}
