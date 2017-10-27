package com.ioter.data;


import android.content.Intent;
import android.text.TextUtils;

import com.ioter.AppApplication;
import com.ioter.common.util.SettingSPUtil;
import com.ioter.common.util.ToastUtil;
import com.ioter.common.util.UIConstant;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.presenter.contract.LoginContract;
import com.ioter.ui.activity.LoginActivity;
import com.ioter.ui.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

public class LoginModel implements LoginContract.ILoginModel
{

    private WebserviceRequest mWebserviceRequest;

    public LoginModel(WebserviceRequest webserviceRequest)
    {
        this.mWebserviceRequest = webserviceRequest;
    }

    @Override
    public void login(String name, String pwd,WebserviceRequest.WebCallback mWebCallback)
    {
        SoapObject request = new SoapObject("http://tempuri.org/", "Login");
        request.addProperty("userName", name);
        request.addProperty("password", pwd);
        mWebserviceRequest.submit(request, UIConstant.getLoginUrl(), UIConstant.getLoginSoapAction(), mWebCallback);
    }
}
