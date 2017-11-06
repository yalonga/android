package com.ioter.common.rx;

import android.content.Context;

import com.ioter.common.exception.BaseException;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.ui.BaseView;

import org.json.JSONException;
import org.json.JSONObject;


public abstract class ErrorHandlerWebCallBack extends WebserviceRequest.WebCallback
{

    private BaseView mView;

    protected RxErrorHandler mErrorHandler = null;

    protected Context mContext;

    public ErrorHandlerWebCallBack(Context context, BaseView view)
    {
        this.mContext = context;
        mErrorHandler = new RxErrorHandler(mContext);
        this.mView = view;
    }

    public boolean isShowProgress()
    {
        return true;
    }

    @Override
    public void onStart()
    {
        if (isShowProgress())
        {
            mView.showLoading();
        }
    }

    public abstract void onResultData(int status, JSONObject object, String errMsg);

    @Override
    public void onNext(String result)
    {
        int status = 0;
        String errorMsg = "";
        JSONObject object = null;
        try
        {
            JSONObject resultObject = new JSONObject(result);
            status = resultObject.getInt("Status");
            errorMsg = resultObject.getString("Message");
            object = resultObject.getJSONObject("Data");
        } catch (JSONException e)
        {
            //服务器异常
            errorMsg = errorMsg != null ? errorMsg : e.toString();
        } finally
        {
            onResultData(status, object, errorMsg);
        }
    }

    @Override
    public void onComplete()
    {
        mView.dismissLoading();
    }

    @Override
    public void onError(Throwable e)
    {
        e.printStackTrace();
        BaseException baseException = mErrorHandler.handleError(e);
        mView.showError(baseException.getDisplayMessage() + e.toString());
    }

}
