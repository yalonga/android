package com.ioter.presenter.contract;


import com.ioter.common.sqlite.ClothesData;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.ui.BaseView;

import org.json.JSONObject;

import java.util.ArrayList;


public class CheckContract
{


    public interface CheckView extends BaseView
    {
        void updateList(ArrayList<ClothesData> list);

        void setId(int id);

        void setWareData(JSONObject object);
    }

    public interface ICheckModel
    {
        void getCheckList(WebserviceRequest.WebCallback mWebCallback);

        void submitEpcList(ArrayList<String> checkedEpcList, int id, WebserviceRequest.WebCallback mWebCallback);

        void submitWarehouseEpcList(String jsonArray, int whId, int whSiteId, String trackingNo, int receiveStoreId, int userId, int type, WebserviceRequest.WebCallback mWebCallback);

        void getWarehouseData(int id, WebserviceRequest.WebCallback mWebCallback);
    }

}
