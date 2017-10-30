package com.ioter.presenter.contract;


import com.ioter.common.sqlite.ClothesData;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.ui.BaseView;

import java.util.ArrayList;


public class CheckContract
{


    public interface CheckView extends BaseView
    {
        void updateList(ArrayList<ClothesData> list);
        void setId(int id);
    }

    public interface ICheckModel
    {
        void getCheckList(WebserviceRequest.WebCallback mWebCallback);

        void submitEpcList(ArrayList<String> checkedEpcList,int mID,WebserviceRequest.WebCallback mWebCallback);
    }

}
