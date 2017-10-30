package com.ioter.di.module;


import android.app.Activity;

import com.ioter.common.util.WebserviceRequest;
import com.ioter.data.LoginModel;
import com.ioter.di.ActivityScope;
import com.ioter.presenter.contract.LoginContract;
import com.ioter.ui.activity.LoginActivity;

import dagger.Module;
import dagger.Provides;


@Module
public class LoginModule
{
    private LoginContract.LoginView mView;

    public LoginModule(LoginContract.LoginView view)
    {

        this.mView = view;
    }

    @Provides
    public LoginContract.LoginView provideView()
    {

        return mView;
    }


    @Provides
    public LoginContract.ILoginModel privodeModel(WebserviceRequest webserviceRequest)
    {

        return new LoginModel(webserviceRequest);
    }


}
