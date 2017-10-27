package com.ioter.data;


import com.ioter.AppApplication;
import com.ioter.common.util.UIConstant;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.presenter.contract.CheckContract;
import com.ioter.presenter.contract.MainContract;

import org.json.JSONArray;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class CheckModel implements CheckContract.ICheckModel
{

    private WebserviceRequest mWebserviceRequest;

    public CheckModel(WebserviceRequest webserviceRequest)
    {
        this.mWebserviceRequest = webserviceRequest;
    }

    @Override
    public void getCheckList(WebserviceRequest.WebCallback mWebCallback)
    {
        SoapObject request = new SoapObject("http://tempuri.org/", "GetTakeStock");
        request.addProperty("userId", 2);
        mWebserviceRequest.submit(request, UIConstant.getCheckUrl(), UIConstant.getGetTakeStockSoapAction(),
                mWebCallback);
    }

    @Override
    public void submitEpcList(ArrayList<String> checkedEpcList,int mID, WebserviceRequest.WebCallback mWebCallback)
    {
        JSONArray jsonArray = new JSONArray(checkedEpcList);
        SoapObject request = new SoapObject("http://tempuri.org/", "WriteTakeStock");
        request.addProperty("takeStockId", mID);
        request.addProperty("listEpcJson", jsonArray.toString());
        mWebserviceRequest.submit(request, UIConstant.getCheckUrl(), UIConstant.getWriteTakeStockSoapAction(),mWebCallback);
    }

}
