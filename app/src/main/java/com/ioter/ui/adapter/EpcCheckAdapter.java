package com.ioter.ui.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clouiotech.pda.rfid.EPCModel;
import com.ioter.R;
import com.ioter.common.sqlite.ClothesData;

public class EpcCheckAdapter extends BaseAdapter
{
    public interface IEpcCheck
    {
        void checkedCount(int count);
    }
    
    private int mCheckedCount;
    private IEpcCheck mIEpcCheck;
    private LayoutInflater mInflater;
    private ArrayList<ClothesData> mDataList;
    private Comparator<ClothesData> mComparator;

    public EpcCheckAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
        mDataList = new ArrayList<ClothesData>();
    }
    
    public void setCheckedCountListener(IEpcCheck listener)
    {
        mIEpcCheck = listener;
    }

    public void updateList(ArrayList<ClothesData> dataList)
    {
        if (dataList == null)
        {
            return;
        }
        mDataList.clear();
        mDataList.addAll(dataList);
        mCheckedCount = 0;
        dataList.clear();
        notifyDataSetChanged();
    }

    public void update(ArrayList<EPCModel> dataList)
    {
        if (dataList.size() > 0)
        {
            boolean hadChange = false;
            for (EPCModel epcModel : dataList)
            {
                for (int i = 0; i < mDataList.size(); i++)
                {
                    ClothesData clothesData = mDataList.get(i);
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
                if(mIEpcCheck != null)
                {
                    mIEpcCheck.checkedCount(mCheckedCount);
                }
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 获取已盘点的EPC列表
     * 
     * @return
     */
    public ArrayList<String> getCheckedEpcList()
    {
        ArrayList<String> epcList = new ArrayList<String>();
        for (int i = 0; i < mDataList.size(); i++)
        {
            ClothesData data = mDataList.get(i);
            if (data.mIsCheck)
            {
                epcList.add(data.mEpc);
            }
        }
        return epcList;
    }

    /**
     * 排序将已扫到的数据排到前面
     */
    public void sortDataList()
    {
        if(mDataList.size() <= 1)
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
        Collections.sort(mDataList, mComparator);
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mDataList.size();
    }

    @Override
    public ClothesData getItem(int position)
    {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.listitem_epc, parent, false);
            holder = new ViewHolder();
            holder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
            holder.sizeTv = (TextView) convertView.findViewById(R.id.size_tv);
            holder.colorTv = (TextView) convertView.findViewById(R.id.color_tv);
            holder.priceTv = (TextView) convertView.findViewById(R.id.price_tv);
            holder.checkIv = (ImageView) convertView.findViewById(R.id.check_iv);
            holder.divideV = convertView.findViewById(R.id.divide_v);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ClothesData item = getItem(position);
        holder.nameTv.setText("名字："+item.mName);
        holder.sizeTv.setText("尺寸："+item.mSize);
        holder.colorTv.setText("颜色："+item.mColour);
        holder.priceTv.setText("价格："+item.mPrice);
        if (item.mIsCheck)
        {
            holder.checkIv.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.checkIv.setVisibility(View.GONE);
        }
        if (position == getCount() - 1)
        {
            holder.divideV.setVisibility(View.GONE);
        }
        else
        {
            holder.divideV.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class ViewHolder
    {
        TextView nameTv;
        TextView sizeTv;
        TextView colorTv;
        TextView priceTv;;
        ImageView checkIv;
        View divideV;
    }

}
