package com.ioter.presenter;


import android.content.Intent;

import com.ioter.common.rx.ErrorHandlerWebCallBack;
import com.ioter.common.util.ToastUtil;
import com.ioter.presenter.contract.LoginContract;
import com.ioter.ui.activity.BaseActivity;
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

        mModel.login("kuguanyuan", "123", new ErrorHandlerWebCallBack(mContext, mView)
        {

            @Override
            public void onResultData(int status, JSONObject object, String errMsg)
            {
                try
                {
                    if (status == 0)
                    {
                        int id = object.getInt("ID");
                        if (id <= 0)
                        {
                            ToastUtil.toast("账号id错误");
                            return;
                        }
                        mContext.startActivity(new Intent(mContext, MainActivity.class));
                        ((BaseActivity) mContext).finish();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

        });
    }

}
