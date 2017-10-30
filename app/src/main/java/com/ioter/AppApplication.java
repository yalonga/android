package com.ioter;

import android.app.Application;

import com.ioter.common.util.WebserviceRequest;
import com.ioter.di.component.AppComponent;
import com.ioter.di.component.DaggerAppComponent;
import com.ioter.di.module.AppModule;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AppApplication extends Application
{

    private AppComponent mAppComponent;

    private static WebserviceRequest mWebServiceRequest;

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

    public static WebserviceRequest getWebserviceRequest()
    {
        return mWebServiceRequest;
    }

    public static ExecutorService getExecutorService()
    {
        if (mThreadPool == null || mThreadPool.isShutdown())
        {
            mThreadPool = Executors.newFixedThreadPool(24);
        }
        return mThreadPool;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
        mApplication = this;
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this))
                .build();
    }


}
