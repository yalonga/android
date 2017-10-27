package com.ioter.di.module;

import android.app.Application;

import com.ioter.common.util.WebserviceRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule
{

    private Application mApplication;

    public AppModule(Application application)
    {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    public Application provideApplication()
    {
        return mApplication;
    }

/*    @Provides
    @Singleton
    public ExecutorService provideExecutorService()
    {
        return Executors.newFixedThreadPool(24);
    }*/

    @Provides
    @Singleton
    public WebserviceRequest provideWebserviceRequest()
    {
        return new WebserviceRequest();
    }


}
