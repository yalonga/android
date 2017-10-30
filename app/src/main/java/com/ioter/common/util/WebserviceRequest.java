package com.ioter.common.util;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.ioter.AppApplication;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WebserviceRequest
{
    public static abstract class WebCallback
    {
        public abstract void onStart();

        public abstract void onNext(String result);

        public abstract void onError(String error);

        public abstract void onComplete();
    }

    public static final int RESULT_OK = 0;// 执行正常
    public static final int RESULT_NO_NET = -1;// 无网络
    public static final int RESULT_EXCEPTION = -2;// 执行异常
    public static final int RESULT_DESTROY = -3;// 操作已被取消

    private boolean mIsDestroy;
    private Handler mHandler;

    /**
     * 销毁，取消操作时执行
     */
    public void destroy()
    {
        mIsDestroy = true;
    }

    public void submit(final SoapObject request, final String url, final String soapAction, final WebCallback callback)
    {
        callback.onStart();

        AppApplication.getExecutorService().submit(new Runnable()
        {
            public void run()
            {
                try
                {
                    if (mIsDestroy)
                    {
                        postUI(RESULT_DESTROY, null, callback);
                        return;
                    }
                    String result = "";
                    // 创建soap 数据
                    SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    soapEnvelope.bodyOut = request;
                    soapEnvelope.dotNet = true;
                    HttpTransportSE transport = new HttpTransportSE(url);
                    // soap 协议发送
                    transport.call(soapAction, soapEnvelope);
                    if (soapEnvelope.getResponse() != null)
                    {
                        SoapObject object = (SoapObject) soapEnvelope.bodyIn;
                        result = object.getProperty(0).toString();
                    }
                    if (mIsDestroy)
                    {
                        postUI(RESULT_DESTROY, null, callback);
                        return;
                    }
                    postUI(RESULT_OK, result, callback);
                } catch (Exception e) // 执行异常的情况
                {
                    if (mIsDestroy)
                    {
                        postUI(RESULT_DESTROY, null, callback);
                        return;
                    }
                    postUI(RESULT_EXCEPTION, null, callback);
                }
            }
        });
    }

    /**
     * 更新UI界面
     *
     * @param resultStatus 执行状态
     * @param result
     * @param callback
     */
    private void postUI(final int resultStatus, final String result, final WebCallback callback)
    {
        if (mHandler == null)
        {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (mIsDestroy)
                    {
                        callback.onError("无数据");
                        return;
                    }
                    Log.e("WebserviceResult", result);
                    if (TextUtils.isEmpty(result))
                    {
                        callback.onError("无数据");
                        callback.onComplete();
                        return;
                    }
                    callback.onNext(result);
                    callback.onComplete();
                } catch (Exception e)
                {
                    Log.d("request", "postUI:" + resultStatus + e);
                }
            }
        });
    }


}
