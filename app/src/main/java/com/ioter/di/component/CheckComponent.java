package com.ioter.di.component;

import com.ioter.di.ActivityScope;
import com.ioter.di.module.CheckModule;
import com.ioter.di.module.MainModule;
import com.ioter.ui.activity.EpcCheckActivity;
import com.ioter.ui.activity.MainActivity;

import dagger.Component;


@ActivityScope
@Component(modules = CheckModule.class ,dependencies = AppComponent.class)
public interface CheckComponent
{
    void inject(EpcCheckActivity activity);
}
