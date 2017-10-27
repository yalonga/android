package com.ioter.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ioter.AppApplication;
import com.ioter.R;
import com.ioter.di.component.AppComponent;
import com.ioter.presenter.BasePresenter;
import com.ioter.ui.BaseView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView, View.OnClickListener
{

    private Unbinder mUnbinder;

    protected AppApplication mApplication;

    private Toast mToast = null;
    private ProgressDialog waitDialog = null;


    @Inject
    T mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        setContentView(setLayout());

        mUnbinder = ButterKnife.bind(this);
        this.mApplication = (AppApplication) getApplication();

        setupAcitivtyComponent(mApplication.getAppComponent());

        init();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mUnbinder != Unbinder.EMPTY)
        {

            mUnbinder.unbind();
        }
        if (waitDialog != null)
        {
            waitDialog = null;
        }
    }


//    protected  void  startActivity(Class c){
//
//        this.startActivity(new Intent(this,c));
//    }
//
//


    public abstract int setLayout();

    public abstract void setupAcitivtyComponent(AppComponent appComponent);


    public abstract void init();


    @Override
    public void showLoading()
    {
        if (waitDialog == null)
        {
            waitDialog = new ProgressDialog(this);
        }
        waitDialog.setMessage("加载中...");
        waitDialog.show();
    }

    @Override
    public void showError(String msg)
    {
        if (waitDialog != null)
        {
            waitDialog.setMessage(msg);
            waitDialog.show();
        }
    }

    @Override
    public void dismissLoading()
    {
        if (waitDialog != null && waitDialog.isShowing())
        {
            waitDialog.dismiss();
        }
    }


    /**
     * 显示提示消息
     *
     * @param msg
     * @param listener
     */
    protected void ShowMsg(String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info).setTitle("Tooltip")
                .setMessage(msg)
                .setPositiveButton(getString(R.string.ok), listener)
                .create().show();
    }



}
