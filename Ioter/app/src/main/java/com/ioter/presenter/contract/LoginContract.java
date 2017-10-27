package com.ioter.presenter.contract;


import com.ioter.bean.LoginBean;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.ui.BaseView;

public interface LoginContract
{


    public interface ILoginModel
    {

        void login(String phone, String pwd, WebserviceRequest.WebCallback mWebCallback);

    }


    public interface LoginView extends BaseView
    {

        void checkPhoneError();

        void checkPhoneSuccess();
    }
}
