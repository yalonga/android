package com.ioter.di.component;

import com.ioter.di.ActivityScope;
import com.ioter.di.module.LoginModule;
import com.ioter.di.module.MainModule;
import com.ioter.ui.activity.LoginActivity;
import com.ioter.ui.activity.MainActivity;

import dagger.Component;


@ActivityScope
@Component(modules = MainModule.class ,dependencies = AppComponent.class)
public interface MainComponent
{
    void inject(MainActivity activity);
}
