package com.ioter.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hwangjr.rxbus.RxBus;
import com.ioter.di.component.AppComponent;
import com.ioter.di.component.DaggerLoginComponent;
import com.ioter.di.module.LoginModule;
import com.ioter.presenter.LoginPresenter;
import com.ioter.presenter.contract.LoginContract;
import com.ioter.ui.widget.LoadingButton;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.mikepenz.iconics.IconicsDrawable;
import com.ioter.R;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.LoginView
{


    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.txt_mobi)
    EditText mTxtMobi;
    @BindView(R.id.view_mobi_wrapper)
    TextInputLayout mViewMobiWrapper;
    @BindView(R.id.txt_password)
    EditText mTxtPassword;
    @BindView(R.id.view_password_wrapper)
    TextInputLayout mViewPasswordWrapper;
    @BindView(R.id.btn_login)
    LoadingButton mBtnLogin;


    @Override
    public int setLayout()
    {
        return R.layout.activity_login;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
        DaggerLoginComponent.builder().appComponent(appComponent).loginModule(new LoginModule(this))
                .build().inject(this);
    }

    @Override
    public void init()
    {
        initView();
    }

    private void initView()
    {


        mToolBar.setNavigationIcon(
                new IconicsDrawable(this)
                        .icon(Ionicons.Icon.ion_ios_arrow_back)
                        .sizeDp(16)
                        .color(getResources().getColor(R.color.md_white_1000)
                        )
        );


        Observable<CharSequence> obMobi = RxTextView.textChanges(mTxtMobi);
        Observable<CharSequence> obPassword = RxTextView.textChanges(mTxtPassword);

        Observable.combineLatest(obMobi, obPassword, new Func2<CharSequence, CharSequence, Boolean>()
        {
            @Override
            public Boolean call(CharSequence mobi, CharSequence pwd)
            {
                return isPhoneValid(mobi.toString()) && isPasswordValid(pwd.toString());
            }
        }).subscribe(new Action1<Boolean>()
        {
            @Override
            public void call(Boolean aBoolean)
            {

                RxView.enabled(mBtnLogin).call(aBoolean);
            }
        });


        RxView.clicks(mBtnLogin).subscribe(new Action1<Void>()
        {
            @Override
            public void call(Void aVoid)
            {

                mPresenter.login(mTxtMobi.getText().toString().trim(), mTxtPassword.getText().toString().trim());

            }
        });


    }


    private boolean isPhoneValid(String phone)
    {
        //return phone.length() == 11;
        return true;
    }

    private boolean isPasswordValid(String password)
    {
       /* return password.length() >= 6;*/
        return true;
    }


    @Override
    public void checkPhoneError()
    {
        mViewMobiWrapper.setError("手机号格式不正确");
    }

    @Override
    public void checkPhoneSuccess()
    {
        mViewMobiWrapper.setError("");
        mViewMobiWrapper.setErrorEnabled(false);
    }

    @Override
    public void showLoading()
    {

        mBtnLogin.showLoading();
    }

    @Override
    public void showError(String msg)
    {

        mBtnLogin.showButtonText();
    }

    @Override
    public void dismissLoading()
    {

        mBtnLogin.showButtonText();
    }

    @Override
    public void onClick(View view)
    {

    }
}
