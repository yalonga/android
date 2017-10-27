package com.ioter.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ioter.R;
import com.ioter.common.sqlite.ClothesData;

/**
 * Created by Administrator on 2017/10/25.
 */

public class DefaultCheckAdapter extends BaseQuickAdapter<ClothesData,BaseViewHolder>
{


    public DefaultCheckAdapter() {
        super(R.layout.listitem_default_epc);
    }

    @Override
    protected void convert(BaseViewHolder helper, ClothesData item)
    {
        helper.setText(R.id.epc_tv,item.mEpc);
        helper.setText(R.id.count_tv,item.mSize);
    }


}
