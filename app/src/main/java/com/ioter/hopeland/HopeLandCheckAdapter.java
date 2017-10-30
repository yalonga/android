package com.ioter.hopeland;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ioter.R;

/**
 * Created by Administrator on 2017/10/25.
 */

public class HopeLandCheckAdapter extends BaseQuickAdapter<EpcBeen,BaseViewHolder>
{


    public HopeLandCheckAdapter() {
        super(R.layout.listitem_default_epc);
    }

    @Override
    protected void convert(BaseViewHolder helper, EpcBeen item)
    {
        helper.setText(R.id.epc_tv,item.epcValue);
        helper.setText(R.id.count_tv,item.count);
    }


}
