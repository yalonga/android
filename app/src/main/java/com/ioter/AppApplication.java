package com.ioter;

import android.app.Application;

import com.google.gson.Gson;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.di.component.AppComponent;
import com.ioter.di.component.DaggerAppComponent;
import com.ioter.di.module.AppModule;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.concurrent.ExecutorService;


public class AppApplication extends Application
{

    private AppComponent mAppComponent;

    private WebserviceRequest mWebServiceRequest;

    private static ExecutorService mThreadPool;

    private static AppApplication mApplication;


    public static AppApplication getApplication()
    {
        return mApplication;
    }

    public AppComponent getAppComponent()
    {
        return mAppComponent;
    }

    public WebserviceRequest getWebserviceRequest()
    {
        return mWebServiceRequest;
    }

    public static ExecutorService getExecutorService()
    {
        return mThreadPool;
    }

    public static Gson mGson;

    public static Gson getGson()
    {
        return mGson;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this))
                .build();
        mApplication = (AppApplication) mAppComponent.getApplication();
        mThreadPool = mAppComponent.getExecutorService();
        mWebServiceRequest = mAppComponent.getWebserviceRequest();
        mGson = mAppComponent.getGson();
    }


}
