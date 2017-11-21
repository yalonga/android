package com.ioter.di.component;

import com.ioter.di.ActivityScope;
import com.ioter.di.module.CheckModule;
import com.ioter.supoin.SupoinEpcCheckActivity;
import com.ioter.supoin.SupoinEpcInOutActivity;
import com.ioter.swingu.SwinguEpcCheckActivity;
import com.ioter.swingu.SwinguEpcInOutActivity;
import com.ioter.ui.activity.EpcCheckActivity;
import com.ioter.ui.activity.EpcInOutActivity;

import dagger.Component;


@ActivityScope
@Component(modules = CheckModule.class, dependencies = AppComponent.class)
public interface CheckComponent
{
    void inject(EpcCheckActivity activity);

    void inject(EpcInOutActivity activity);

    void inject(SupoinEpcCheckActivity activity);

    void inject(SupoinEpcInOutActivity activity);

    void inject(SwinguEpcInOutActivity activity);

    void inject(SwinguEpcCheckActivity activity);

}
