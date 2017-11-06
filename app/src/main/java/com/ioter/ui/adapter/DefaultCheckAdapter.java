package com.ioter.ui.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.clouiotech.pda.rfid.EPCModel;
import com.ioter.R;
import com.ioter.common.sqlite.ClothesData;
import com.ioter.hopeland.EpcBeen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2017/10/25.
 */

public class DefaultCheckAdapter extends BaseQuickAdapter<ClothesData, BaseViewHolder>
{


    public DefaultCheckAdapter()
    {
        super(R.layout.listitem_default_epc);
    }


    @Override
    protected void convert(BaseViewHolder helper, ClothesData item)
    {
        ImageView checkIv = (ImageView) helper.getView(R.id.check_iv);
        if (item.mIsCheck)
        {
            checkIv.setVisibility(View.VISIBLE);
        } else
        {
            checkIv.setVisibility(View.GONE);
        }
        helper.setText(R.id.epc_tv, item.mEpc);
    }

    public interface IEpcCheck
    {
        void checkedCount(int totalCount,int count);
    }

    private IEpcCheck mIEpcCheck;
    private int mCheckedCount;
    private Comparator<ClothesData> mComparator;

    public void setCheckedCountListener(IEpcCheck listener)
    {
        mIEpcCheck = listener;
    }


    public void updateModel(ArrayList<EPCModel> dataList)
    {
        if (dataList.size() > 0)
        {
            boolean hadChange = false;
            for (EPCModel epcModel : dataList)
            {
                for (int i = 0; i < mData.size(); i++)
                {
                    ClothesData clothesData = mData.get(i);
                    if (clothesData.mEpc.equals(epcModel._EPC))
                    {
                        if (!clothesData.mIsCheck)
                        {
                            hadChange = true;
                            clothesData.mIsCheck = true;
                            mCheckedCount++;
                        }
                        break;
                    }
                }
            }
            dataList.clear();
            if (hadChange)
            {
                if (mIEpcCheck != null)
                {
                    mIEpcCheck.checkedCount(mData.size(),mCheckedCount);
                }
                notifyDataSetChanged();
            }
        }
    }

    public void updateBeen(ArrayList<EpcBeen> dataList)
    {
        if (dataList.size() > 0)
        {
            boolean hadChange = false;
            for (EpcBeen epcBeen : dataList)
            {
                for (int i = 0; i < mData.size(); i++)
                {
                    ClothesData clothesData = mData.get(i);
                    if (clothesData.mEpc.equals(epcBeen.epcValue))
                    {
                        if (!clothesData.mIsCheck)
                        {
                            hadChange = true;
                            clothesData.mIsCheck = true;
                            mCheckedCount++;
                        }
                        break;
                    }
                }
            }
            dataList.clear();
            if (hadChange)
            {
                if (mIEpcCheck != null)
                {
                    mIEpcCheck.checkedCount(mData.size(),mCheckedCount);
                }
                notifyDataSetChanged();
            }
        }
    }


    /**
     * 排序将已扫到的数据排到前面
     */
    public void sortDataList()
    {
        if(mData.size() <= 1)
        {
            return;
        }
        if (mComparator == null)
        {
            mComparator = new Comparator<ClothesData>()
            {

                @Override
                public int compare(ClothesData lhs, ClothesData rhs)
                {
                    if (lhs.mIsCheck)
                    {
                        return -1;
                    }
                    else if (rhs.mIsCheck)
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            };
        }
        Collections.sort(mData, mComparator);
        notifyDataSetChanged();
    }


    /**
     * 获取已盘点的EPC列表
     *
     * @return
     */
    public ArrayList<String> getCheckedEpcList()
    {
        ArrayList<String> epcList = new ArrayList<String>();
        for (int i = 0; i < mData.size(); i++)
        {
            ClothesData data = mData.get(i);
            if (data.mIsCheck)
            {
                epcList.add(data.mEpc);
            }
        }
        return epcList;
    }


}
