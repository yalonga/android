package com.ioter.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.clouiotech.pda.rfid.EPCModel;
import com.ioter.R;
import com.ioter.common.util.UIConstant;
import com.ioter.di.component.AppComponent;

public class EpcReadActivity extends UHFBaseActivity
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
    private HashMap<String, EPCModel> hmList = new HashMap<String, EPCModel>();
    
    @Override
    protected void msgProcess(Message msg)
    {
        super.msgProcess(msg);
        switch (msg.what)
        {
        case UIConstant.MSG_FLUSH_READTIME:
            if (lb_ReadTime != null) { // 刷新读取时间
                readTime++;
                lb_ReadTime.setText("Time:" + readTime + "S");
            }
            break;
        default:
            super.msgProcess(msg);
            break;
        }
    }

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
        return R.layout.activity_epc_read;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {

    }

    @Override
    public void init()
    {
        initView();
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
    protected void ShowList()
    {
        if (!isStartPingPong)
            return;
        sa = new SimpleAdapter(this, GetData(), R.layout.epclist_item,
                new String[] { "EPC", "ReadCount" }, new int[] {
                        R.id.EPCList_TagID, R.id.EPCList_ReadCount });
        listView.setAdapter(sa);
        listView.invalidate();
        if (lb_ReadTime != null) { // 刷新读取时间
            readTime++;
            lb_ReadTime.setText("Time:" + readTime / 1 + "S");
        }
        if (lb_ReadSpeed != null) { // 刷新读取速度
            speed = totalReadCount - lastReadCount;
            if (speed < 0)
                speed = 0;
            lastReadCount = totalReadCount;
            if (lb_ReadSpeed != null) {
                lb_ReadSpeed.setText("SP:" + speed + "T/S");
            }
        }
        if (lb_TagCount != null) { // 刷新标签总数
            lb_TagCount.setText("Total:" + hmList.size());
        }
    }
    
 // 获得更新数据源
    @SuppressWarnings({ "rawtypes", "unused" })
    protected List<Map<String, Object>> GetData() {
        List<Map<String, Object>> rt = new ArrayList<Map<String, Object>>();
        synchronized (hmList_Lock) {
            // if(hmList.size() > 0){ //
            Iterator iter = hmList.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                EPCModel val = (EPCModel) entry.getValue();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("EPC", val._EPC);
                map.put("ReadCount", val._TotalCount);
                rt.add(map);
            }
            // }
        }
        return rt;
    }
    
    @Override
    protected void Clear()
    {
        // TODO Auto-generated method stub
        super.Clear();
        totalReadCount = 0;
        readTime = 0;
        hmList.clear();
        ShowList();
    }

    @Override
    protected void keyDown()
    {
        // TODO Auto-generated method stub
        super.keyDown();
        btn_Read.setText(getString(R.string.stop));
        btn_Read.setClickable(false);
    }

    @Override
    protected void keyUp()
    {
        // TODO Auto-generated method stub
        super.keyUp();
        btn_Read.setText(getString(R.string.start));
        btn_Read.setClickable(true);
    }
    
    public void Read(View v) {
        Button btnRead = (Button) v;
        String controlText = btnRead.getText().toString();
        if (controlText.equals(getString(R.string.start))) {
            Pingpong_Read();
            btnRead.setText(getString(R.string.stop));
        } else {
            Pingpong_Stop();
            btnRead.setText(getString(R.string.start));
        }
    }

    @Override
    public void OutPutEPC(EPCModel model)
    {
        // TODO Auto-generated method stub
        super.OutPutEPC(model);
        try {
            synchronized (hmList_Lock) {
                if (hmList.containsKey(model._EPC + model._TID)) {
                    EPCModel tModel = hmList.get(model._EPC + model._TID);
                    tModel._TotalCount++;
                } else {
                    hmList.put(model._EPC + model._TID, model);
                }
            }
            totalReadCount++;
        } catch (Exception ex) {
            Log.d("Debug", "标签输出异常：" + ex.getMessage());
        }
    }

    @Override
    public void onClick(View v)
    {

    }
}
