package com.ioter.common.rx;

import android.content.Context;
import android.widget.Toast;

import com.ioter.common.exception.BaseException;
import com.ioter.common.exception.ErrorMessageFactory;

import java.net.SocketException;


public class RxErrorHandler
{


    private Context mContext;

    public RxErrorHandler(Context context)
    {

        this.mContext = context;
    }

    public BaseException handleError(Throwable e)
    {

        BaseException exception = new BaseException();

        if (e instanceof SocketException)
        {

        } else
        {
            exception.setCode(BaseException.UNKNOWN_ERROR);
        }

        exception.setDisplayMessage(ErrorMessageFactory.create(mContext, exception.getCode()));

        return exception;
    }

    public void showErrorMessage(BaseException e)
    {
        Toast.makeText(mContext, e.getDisplayMessage(), Toast.LENGTH_LONG).show();
    }
}
