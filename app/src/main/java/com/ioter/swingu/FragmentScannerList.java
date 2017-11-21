package com.ioter.swingu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ioter.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

//import com.google.android.gms.location.LocationClient;

public class FragmentScannerList extends Fragment implements OnClickListener
{
    public static final String ARG_TAP_NUMBER = "tap_number";
    private final String TAG = "connected_check";
    private View rootView;
    public BluetoothAdapter mBTadapter;
    private Set<BluetoothDevice> mBTpaired;
    private SwingAPI mSwing = null;
    private ListView DeviceListView;
    private ArrayList<ClassScanner> DeviceList;
    private AdapterScanner ListAdapter;
    String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        IntentFilter action_found = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, action_found);

        IntentFilter action_bond = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, action_bond);

        IntentFilter action_disconnected = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        getActivity().registerReceiver(mReceiver, action_disconnected);

        IntentFilter action_discover_end = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, action_discover_end);

        IntentFilter action_pairing_request = new IntentFilter(ACTION_PAIRING_REQUEST);
        getActivity().registerReceiver(mReceiver, action_pairing_request);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_scanner_list, container, false);
        DeviceListView = (ListView) rootView.findViewById(R.id.lv_BT_paired);
        DeviceListView.setAdapter(ListAdapter);
        DeviceListView.setOnItemClickListener(mDeviceClickListener);

        mBTadapter = BluetoothAdapter.getDefaultAdapter();

        return rootView;
    }


    @Override
    public void onStart()
    {
        super.onStart();
        Intent BT_turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(BT_turnOn, 7001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 7001:
                PairedListUpdate();
                break;

            default:
                break;
        }
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

        getActivity().unregisterReceiver(mReceiver);
    }

    public void PairedListUpdate()
    {
        mBTpaired = mBTadapter.getBondedDevices();
        DeviceList = new ArrayList<ClassScanner>();
        for (BluetoothDevice btd : mBTpaired)
        {
            int icon = R.mipmap.ic_action_bluetooth;
            addDevice(btd, icon);
        }
        ListAdapter = new AdapterScanner(getActivity(), DeviceList);
        DeviceListView.setAdapter(ListAdapter);

        try
        {
            if (mSwing != null)
            {
                if (mSwing.getState() == SwingAPI.STATE_NONE)
                {
                    mSwing.start();
                }
            }
        } catch (Exception e)
        {
            Log.e("dsm362", "MainActivity - onResume: " + e.getMessage());
        }
    }

    private void addDevice(BluetoothDevice btd, int type)
    {
        if (btd.getName() == null) return;

        String name = btd.getName().toUpperCase();
        int index = 0;

        for (index = 0; index < DeviceList.size(); index++)
        {
            ClassScanner temp = DeviceList.get(index);
            if (temp.getName().equals(btd.getName())) break;
        }

        if (index != DeviceList.size()) return;

        if (name.startsWith("SWINGU") || name.startsWith("WAVE"))
        {
            DeviceList.add(new ClassScanner(R.mipmap.swing_u, type, btd));
        } else if (name.startsWith("SWINGH"))
        {
            DeviceList.add(new ClassScanner(R.mipmap.swing_h, type, btd));
        } else if (name.startsWith("SWING"))
        {
            DeviceList.add(new ClassScanner(R.mipmap.swing, type, btd));
        }
    }

    public void setSwing(SwingAPI swing)
    {
        if (swing != null) mSwing = swing;
    }

    public void setConnectedScanner(String last_addr)
    {
        int index = 0;
        int selectedIndex = -1;

        for (index = 0; index < DeviceList.size(); index++)
        {
            ClassScanner temp = DeviceList.get(index);
            temp.setDisConnected(last_addr);
            if (temp.getMac().equals(last_addr)) selectedIndex = index;
        }

        if (index != DeviceList.size()) return;

        ClassScanner connected = DeviceList.get(selectedIndex);
        connected.setConnected();
        getActivity().runOnUiThread(updateListTable);
    }

    public void setDisconnected(String last_addr)
    {
        int index = 0;

        for (index = 0; index < DeviceList.size(); index++)
        {
            ClassScanner temp = DeviceList.get(index);
            temp.setDisConnected(last_addr);
        }
        getActivity().runOnUiThread(updateListTable);
    }


    public void pairDevice(BluetoothDevice device)
    {
        try
        {
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e)
        {
            Log.e("dsm362-pairDevice", e.getMessage());
        }
    }

    private void unpairDevice(BluetoothDevice device)
    {
        try
        {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e)
        {
            Log.e("dsm362-unpairDevice", e.getMessage());
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

	      /*  if(LocationClient.hasError(intent)) {
                int errorCode = LocationClient.getErrorCode(intent);
	            Log.e("dsm362", "Location Services error: " + Integer.toString(errorCode));
	            return;
	        }*/
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Get the BluetoothDevice object from the Intent
                // Add the name and address to an array adapter to show in a ListView
                addDevice(device, R.mipmap.ic_action_new);
                getActivity().runOnUiThread(updateListTable);
            } else if (ACTION_PAIRING_REQUEST.equals(action))
            {
                setBluetoothPairingPin(device);
            }
            // When the device bond state changed.
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
                int prevBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (bondState == BluetoothDevice.BOND_BONDING)
                {
                    Log.i("dsm362", "BOND_BONDING");
                    pairDevice(device);
                } else if (bondState == BluetoothDevice.BOND_BONDED)
                {
                    Log.i("dsm362", "BOND_BONDED");
                } else if (bondState == BluetoothDevice.BOND_NONE)
                {
                    Log.i("dsm362", "BOND_NONE");
                } else if (bondState == BluetoothDevice.ERROR)
                {
                    Log.i("dsm362", "ERROR");
                }

                if (prevBondState == BluetoothDevice.BOND_BONDED && bondState == BluetoothDevice.BOND_NONE)
                {
                    //BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //pairDevice(device);
                    //Log.i("dsm362", "pair start");
                } else if (prevBondState == BluetoothDevice.BOND_BONDING && bondState == BluetoothDevice.BOND_BONDED)
                {
                    //BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //Log.i("dsm362", "???");
                }
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
                Toast.makeText(FragmentScannerList.this.getActivity(), "Scanner is disconnected", Toast.LENGTH_LONG).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                Toast.makeText(getActivity(), "BT discovery finished", Toast.LENGTH_SHORT).show();
                if (discover_dialog != null)
                {
                    discover_dialog.cancel();
                    discover_dialog = null;
                }
            }
        }
    };

    public void setBluetoothPairingPin(BluetoothDevice device)
    {
        String string = "1234";

        byte[] pinBytes = string.getBytes();
        try
        {
            //Log.d(TAG, "Try to set the PIN");
            Method m = device.getClass().getMethod("setPin", byte[].class);
            m.invoke(device, pinBytes);
            Log.d("dsm362", "Success to add the PIN.");
            try
            {
                device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                Log.d("dsm362", "Success to setPairingConfirmation.");
            } catch (Exception e)
            {
                // TODO Auto-generated catch block
                //       Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e)
        {
            //  Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private ProgressDialog discover_dialog = null;

    public void discovery_start()
    {
        if (mBTadapter.isDiscovering() == false)
        {
            mBTadapter.startDiscovery();
            discover_dialog = new ProgressDialog(getActivity());
            discover_dialog.setMessage("Finding Scanner...");
            discover_dialog.setButton(Dialog.BUTTON_NEGATIVE, "Stop", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if (mBTadapter.isDiscovering()) mBTadapter.cancelDiscovery();
                }
            });
            discover_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            discover_dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    if (mBTadapter.isDiscovering()) mBTadapter.cancelDiscovery();
                }
            });
            discover_dialog.show();
        }
    }

    public Runnable updateListTable = new Runnable()
    {
        public void run()
        {
            FragmentScannerList.this.ListAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onClick(View v)
    {
    }

    boolean isOpen = false;

    private OnItemClickListener mDeviceClickListener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if (mSwing == null) return;

            final ClassScanner target = DeviceList.get(position);

            if (!isOpen)
            {
                if (target.getConnected())
                {
                    mSwing.stop();
                } else
                {
                    if (mSwing.isConnected() == true) mSwing.stop();
                    mSwing.connect(target.getDevice());
                }
            } else
            {
                mSwing.stop();
                mSwing.swing_clear_inventory();
            }
            isOpen = !isOpen;
        }
    };

    public void ConnectTo(BluetoothDevice scanner)
    {
        if (mSwing != null)
        {
            if (mSwing.isConnected() == true)
            {
                mSwing.stop();
            }
            mSwing.connect(scanner);
        }
    }
}
