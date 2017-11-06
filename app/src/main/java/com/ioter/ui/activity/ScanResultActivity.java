package com.ioter.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ioter.R;
import com.ioter.bean.ScanInfoData;
import com.ioter.di.component.AppComponent;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import java.util.ArrayList;


public class ScanResultActivity extends BaseActivity
{
    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;
    /**
     * 选择系统图片Request Code
     */
    public static final int REQUEST_IMAGE = 112;

    private Button button;

    private MyAdapter mAdapter;

    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_scan;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {

    }

    @Override
    public void init()
    {
        Intent intent = getIntent();
        if (intent == null)
        {
            finish();
        }
        ArrayList<ScanInfoData> dataList = (ArrayList<ScanInfoData>) intent.getSerializableExtra("result");
        /**
         * 初始化组件
         */
        initTitle();
        initView();

        mAdapter.updateList(dataList);
    }

    private void initTitle()
    {
        mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setNavigationIcon(
                new IconicsDrawable(this)
                        .icon(Ionicons.Icon.ion_ios_arrow_back)
                        .sizeDp(16)
                        .color(getResources().getColor(R.color.md_white_1000)
                        )
        );
        mToolBar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initView()
    {

        ListView epcLv = (ListView) findViewById(R.id.epc_lv);
        mAdapter = new MyAdapter();
        epcLv.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v)
    {

    }

    public class MyAdapter extends BaseAdapter
    {

        private ArrayList<ScanInfoData> mDataList;

        public MyAdapter()
        {
            mDataList = new ArrayList<ScanInfoData>();
        }

        public void updateList(ArrayList<ScanInfoData> dataList)
        {
            if (dataList == null || dataList.size() == 0)
            {
                return;
            }
            for (int i = dataList.size() - 1; i > -1; i--)
            {
                mDataList.add(dataList.get(i));
            }
            dataList.clear();
            notifyDataSetChanged();
        }

        public ArrayList<ScanInfoData> getDataList()
        {
            return mDataList;
        }

        @Override
        public int getCount()
        {
            return mDataList.size();
        }

        @Override
        public ScanInfoData getItem(int position)
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
                convertView = getLayoutInflater().inflate(R.layout.listitem_epc_scan_result, parent, false);
                holder = new ViewHolder();
                holder.contentTv = (TextView) convertView.findViewById(R.id.content_tv);
                holder.timeTv = (TextView) convertView.findViewById(R.id.time_tv);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            ScanInfoData item = getItem(position);
            holder.contentTv.setText(item.content);
            holder.timeTv.setText(item.time);
            return convertView;
        }


        private class ViewHolder
        {
            TextView contentTv;
            TextView timeTv;
        }

    }


}
