package com.ioter.supoin;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ioter.R;
import com.ioter.di.component.AppComponent;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static com.ioter.supoin.Comm.Awl;

/**
 * 盘点
 *
 * @author hzj
 */
public class SupoinEpcReadActivity extends SupoinUHFBaseActivity
{
    private int readTime = 0;
    private int lastReadCount = 0;
    private int totalReadCount = 0; // 总读取次数
    private int speed = 0; // 读取速度

    private Button btn_Read;
    private TextView lb_ReadTime = null;
    private TextView lb_ReadSpeed = null;
    private TextView lb_TagCount = null;
    private ListView listView;
    private SimpleAdapter sa;
    private Object hmList_Lock = new Object();
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_epc_read;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
    }

    @Override
    public void init()
    {
        super.init();
        initTitle();
        initView();
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
        btn_Read = (Button) findViewById(R.id.btn_Read);
        btn_Read.setText(getString(R.string.start));
        lb_ReadTime = (TextView) findViewById(R.id.lb_ReadTime);
        lb_ReadSpeed = (TextView) findViewById(R.id.lb_ReadSpeed);
        lb_TagCount = (TextView) findViewById(R.id.lb_TagCount);

        listView = (ListView) this.findViewById(R.id.lv_Main);

    }


    @Override
    protected void showlist(ArrayList<EpcBeen> epcList)
    {
        sa = new SimpleAdapter(this, GetData(epcList), R.layout.epclist_item,
                new String[]{"EPC", "ReadCount"}, new int[]{
                R.id.EPCList_TagID, R.id.EPCList_ReadCount});
        listView.setAdapter(sa);
        listView.invalidate();
        if (lb_TagCount != null)
        { // 刷新标签总数
            lb_TagCount.setText("Total:" + epcList.size());
        }
    }

    protected List<Map<String, Object>> GetData(ArrayList<EpcBeen> epcList)
    {
        List<Map<String, Object>> rt = new ArrayList<Map<String, Object>>();
        synchronized (hmList_Lock)
        {
            for (int i = 0; i < epcList.size(); i++)
            {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("EPC", epcList.get(i).epcValue);
                map.put("ReadCount", epcList.get(i).count);
                rt.add(map);
            }
        }
        return rt;
    }

    public void Read(View v)
    {
        readEpc();
    }

    @Override
    protected void readEpc()
    {
        String controlText = btn_Read.getText().toString();
        if (controlText.equals(getString(R.string.start)))
        {
            try
            {
                Awl.WakeLock();
                Comm.startScan();
            } catch (Exception ex)
            {
                Toast.makeText(this,
                        "ERR：" + ex.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
            btn_Read.setText(getString(R.string.stop));
        } else
        {
            Awl.ReleaseWakeLock();
            Comm.stopScan();
            btn_Read.setText(getString(R.string.start));
        }
    }


    public void Back(View v)
    {
        if (btn_Read.getText().toString().equals(getString(R.string.stop)))
        {
            //ShowMsg(getString(R.string.uhf_please_stop), null);
            return;
        }
        finish();
    }


    @Override
    public void onClick(View view)
    {

    }
}
