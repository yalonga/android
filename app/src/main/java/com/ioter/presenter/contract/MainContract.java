package com.ioter.presenter.contract;


import com.ioter.common.util.WebserviceRequest;
import com.ioter.ui.BaseView;

import java.util.List;


public class MainContract
{


    public interface MainView extends BaseView
    {


    }

    public interface IMainModel
    {
        void getProductInfo(String epc, WebserviceRequest.WebCallback mWebCallback);
    }

}
