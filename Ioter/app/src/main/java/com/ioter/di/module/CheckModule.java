package com.ioter.di.module;




import com.ioter.common.util.WebserviceRequest;
import com.ioter.data.CheckModel;
import com.ioter.data.MainModel;
import com.ioter.presenter.contract.CheckContract;
import com.ioter.presenter.contract.MainContract;

import dagger.Module;
import dagger.Provides;


@Module
public class CheckModule
{

    private CheckContract.CheckView mView;


    public CheckModule(CheckContract.CheckView view){

        this.mView = view;
    }

    @Provides
    public CheckContract.CheckView provideView(){

        return  mView;
    }

    @Provides
    public CheckContract.ICheckModel privodeModel(WebserviceRequest webserviceRequest)
    {

        return new CheckModel(webserviceRequest);
    }


}
