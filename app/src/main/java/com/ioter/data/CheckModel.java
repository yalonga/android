package com.ioter.data;


import com.ioter.common.util.UIConstant;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.presenter.contract.CheckContract;

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
    public void submitEpcList(ArrayList<String> checkedEpcList, int mID, WebserviceRequest.WebCallback mWebCallback)
    {
        JSONArray jsonArray = new JSONArray(checkedEpcList);
        SoapObject request = new SoapObject("http://tempuri.org/", "WriteTakeStock");
        request.addProperty("takeStockId", mID);
        request.addProperty("listEpcJson", jsonArray.toString());
        mWebserviceRequest.submit(request, UIConstant.getCheckUrl(), UIConstant.getWriteTakeStockSoapAction(), mWebCallback);
    }

    //入参讲究顺序
    @Override
    public void submitWarehouseEpcList(String jsonArray, int whId, int whSiteId, String trackingNo, int receiveStoreId, int userId, int type, WebserviceRequest.WebCallback mWebCallback)
    {
        String name = null;
        String soapAction = null;
        String url = null;
        if (type == 1)
        {
            name = "StockIn";
            soapAction = UIConstant.getEpcInSoapAction();
            url = UIConstant.getEpcInOutUrl();
        } else if (type == 0)
        {
            name = "StockOut";
            soapAction = UIConstant.getEpcOutSoapAction();
            url = UIConstant.getEpcInOutUrl();
        } else if (type == 2)
        {
            name = "StoreIn";
            soapAction = UIConstant.getStoreInSoapAction();
            url = UIConstant.getStoreInUrl();
        }
        SoapObject request = new SoapObject("http://tempuri.org/", name);
        request.addProperty("listEpcJson", jsonArray.toString());
        if (type == 1)
        {
            request.addProperty("whId", whId);
            request.addProperty("whSiteId", whSiteId);
            request.addProperty("userId", userId);
        } else if (type == 0)
        {
            request.addProperty("userId", userId);
            request.addProperty("trackingNo", trackingNo);
            request.addProperty("receiveStoreId", receiveStoreId);
        } else if (type == 2)
        {
            request.addProperty("storeId", receiveStoreId);
            request.addProperty("userId", userId);
        }
        mWebserviceRequest.submit(request, url, soapAction, mWebCallback);
    }

    @Override
    public void getWarehouseData(int id, WebserviceRequest.WebCallback mWebCallback)
    {
        SoapObject request = new SoapObject("http://tempuri.org/", "GetAllWhAndStore");
        request.addProperty("userId", id);
        mWebserviceRequest.submit(request, UIConstant.getAllWhAndStore(), UIConstant.getAllWhAndStoreSoapAction(),
                mWebCallback);

    }

}
