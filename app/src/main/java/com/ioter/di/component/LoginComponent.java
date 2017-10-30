package com.ioter.di.component;

import com.ioter.di.ActivityScope;
import com.ioter.di.module.LoginModule;
import com.ioter.ui.activity.LoginActivity;

import dagger.Component;


@ActivityScope
@Component(modules = LoginModule.class ,dependencies = AppComponent.class)
public interface LoginComponent
{
    void inject(LoginActivity activity);
}
