package com.ioter.di.component;

import android.app.Application;

import com.google.gson.Gson;
import com.ioter.common.util.WebserviceRequest;
import com.ioter.di.module.AppModule;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Component;



@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

   public Application getApplication();

    public ExecutorService getExecutorService();

    public WebserviceRequest getWebserviceRequest();

    public  Gson getGson();

}
