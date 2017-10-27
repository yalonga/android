package com.ioter.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ioter.R;

public class SelectPop extends PopupWindow
{
    private LayoutInflater mInflater;
    private ListView mListView;
    private MyAdapter mAdapter;

    public SelectPop(Context context)
    {
        mInflater = LayoutInflater.from(context);
        View mainView = mInflater.inflate(R.layout.popwin_select, null);
        setContentView(mainView);
        mainView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
        mListView = (ListView) mainView.findViewById(R.id.listView);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setOutsideTouchable(false);
    }

    public void updateData(ArrayList<String> dataList)
    {
        if (mAdapter != null)
        {
            mAdapter.updateList(dataList);
        }
    }

    public void setonItemClickListener(OnItemClickListener listener)
    {
        mListView.setOnItemClickListener(listener);
    }

    private class MyAdapter extends BaseAdapter
    {
        private ArrayList<String> mDataList;

        public MyAdapter()
        {
            mDataList = new ArrayList<String>();
        }

        public void updateList(ArrayList<String> dataList)
        {
            mDataList.clear();
            mDataList.addAll(dataList);
            dataList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return mDataList.size();
        }

        @Override
        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.listitem_select, parent, false);
                holder = new ViewHolder();
                holder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.divideV = convertView.findViewById(R.id.divide_v);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.nameTv.setText(mDataList.get(position));
            if (position == getCount() - 1)
            {
                holder.divideV.setVisibility(View.GONE);
            }else
            {
                holder.divideV.setVisibility(View.VISIBLE);                
            }
            return convertView;
        }

        private class ViewHolder
        {
            TextView nameTv;
            View divideV;
        }

    }

}
