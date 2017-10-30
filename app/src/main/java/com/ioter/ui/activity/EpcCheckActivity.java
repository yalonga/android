package com.ioter.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.clouiotech.pda.rfid.EPCModel;
import com.ioter.R;
import com.ioter.common.sqlite.ClothesData;
import com.ioter.di.component.AppComponent;
import com.ioter.di.component.DaggerCheckComponent;
import com.ioter.di.module.CheckModule;
import com.ioter.presenter.contract.CheckContract;
import com.ioter.ui.adapter.DefaultCheckAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * 盘点
 *
 * @author hzj
 */
public class EpcCheckActivity extends UHFBaseActivity implements CheckContract.CheckView
{
    private int mID;
    private Object hmList_Lock = new Object();
    private HashMap<String, EPCModel> mCacheList = new HashMap<String, EPCModel>();
    private ArrayList<EPCModel> mEpcList = new ArrayList<>();
    private DefaultCheckAdapter mAdapter;
    @BindView(R.id.read_btn)
    Button mReadBtn;
    @BindView(R.id.total_num_tv)
    TextView mTotalNumTv;
    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.tool_bar)
    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_epc_check;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
        DaggerCheckComponent.builder().appComponent(appComponent).checkModule(new CheckModule(this)).build().inject(this);
    }

    @Override
    public void init()
    {
        initTitle();
        initView();
        takeTaskList();
    }

    private void initTitle()
    {
        mToolBar.setNavigationIcon(
                new IconicsDrawable(this)
                        .icon(Ionicons.Icon.ion_ios_arrow_back)
                        .sizeDp(16)
                        .color(getResources().getColor(R.color.md_white_1000)
                        )
        );
    }

    private void initView()
    {
        mReadBtn.setOnClickListener(this);

        //为RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void ShowList()
    {
        if (!isStartPingPong)
            return;
        synchronized (hmList_Lock)
        {
            //mAdapter.update(mEpcList);
        }
    }

    @Override
    protected void keyDown()
    {
        super.keyDown();
        mReadBtn.setText(getString(R.string.stop));
        mReadBtn.setClickable(false);
    }

    @Override
    protected void keyUp()
    {
        super.keyUp();
        mReadBtn.setText(getString(R.string.start));
        mReadBtn.setClickable(true);
        //mAdapter.sortDataList();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.app_common_bar_left_iv:
                back();
                break;
            case R.id.app_common_bar_right_tv:
                submitEpcList();
                break;
            case R.id.read_btn:
                readEpc(v);
                break;
            default:
                break;
        }
    }

    private void back()
    {
        if (mReadBtn.getText().toString().equals(getString(R.string.stop)))
        {
            return;
        }
        finish();
    }

    /**
     * 读功能
     *
     * @param v
     */
    private void readEpc(View v)
    {
        Button btnRead = (Button) v;
        String controlText = btnRead.getText().toString();
        if (controlText.equals(getString(R.string.start)))
        {
            Pingpong_Read();
            btnRead.setText(getString(R.string.stop));
        } else
        {
            Pingpong_Stop();
            //mAdapter.sortDataList();
            btnRead.setText(getString(R.string.start));
        }
    }

    @Override
    public void OutPutEPC(EPCModel model)
    {
        // TODO Auto-generated method stub
        super.OutPutEPC(model);
        try
        {
            synchronized (hmList_Lock)
            {
                if (mCacheList.containsKey(model._EPC + model._TID))
                {
                    EPCModel tModel = mCacheList.get(model._EPC + model._TID);
                    tModel._TotalCount++;
                } else
                {
                    mCacheList.put(model._EPC + model._TID, model);
                    mEpcList.add(model);
                }
            }
        } catch (Exception ex)
        {
            Log.e("Debug", "标签输出异常：" + ex.getMessage());
        }
    }

    /**
     * 获取要盘点的清单列表
     */
    private void takeTaskList()
    {
        mPresenter.getCheckList();
    }

    /**
     * 将已盘点的EPC列表提交到后台
     */
    private void submitEpcList()
    {
/*        ArrayList<String> checkedEpcList = mAdapter.getCheckedEpcList();
        if (checkedEpcList.size() == 0)
        {
            ToastUtil.toast("没有可提交数据");
            return;
        }

        mPresenter.submitEpcList(checkedEpcList, mID);*/
    }

    @Override
    public void updateList(ArrayList<ClothesData> list)
    {
        mTotalNumTv.setText("总数量：" + list.size());

        mAdapter = new DefaultCheckAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setNewData(list);

    }

    @Override
    public void setId(int id)
    {
        mID = id;
    }
}
