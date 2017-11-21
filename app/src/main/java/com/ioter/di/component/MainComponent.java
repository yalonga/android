package com.ioter.di.component;

import com.ioter.di.ActivityScope;
import com.ioter.di.module.MainModule;
import com.ioter.ui.fragment.MainFragment;

import dagger.Component;


@ActivityScope
@Component(modules = MainModule.class ,dependencies = AppComponent.class)
public interface MainComponent
{
    void inject(MainFragment fragment);
}
