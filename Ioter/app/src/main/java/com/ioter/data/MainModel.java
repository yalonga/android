package com.ioter.data;


import com.ioter.common.util.UIConstant;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.presenter.contract.LoginContract;
import com.ioter.presenter.contract.MainContract;

import org.ksoap2.serialization.SoapObject;

public class MainModel implements MainContract.IMainModel
{

    private WebserviceRequest mWebserviceRequest;

    public MainModel(WebserviceRequest webserviceRequest)
    {
        this.mWebserviceRequest = webserviceRequest;
    }

    @Override
    public void getProductInfo(String epcCode, WebserviceRequest.WebCallback mWebCallback)
    {
        SoapObject request = new SoapObject("http://tempuri.org/", "GetInfo");
        request.addProperty("epc", epcCode);
        mWebserviceRequest.submit(request, UIConstant.getProductUrl(), UIConstant.getProductSoapAction(),
                mWebCallback);
    }
}
