package com.ioter.di.module;




import com.ioter.common.util.WebserviceRequest;
import com.ioter.data.LoginModel;
import com.ioter.data.MainModel;
import com.ioter.di.ActivityScope;
import com.ioter.presenter.contract.LoginContract;
import com.ioter.presenter.contract.MainContract;

import dagger.Module;
import dagger.Provides;


@Module
public class MainModule
{

    private MainContract.MainView mView;


    public MainModule(MainContract.MainView view){

        this.mView = view;
    }

    @Provides
    public MainContract.MainView provideView(){

        return  mView;
    }

    @Provides
    public MainContract.IMainModel privodeModel(WebserviceRequest webserviceRequest)
    {

        return new MainModel(webserviceRequest);
    }


}
