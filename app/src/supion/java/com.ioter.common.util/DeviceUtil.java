package com.ioter.common.util;

import com.clouiotech.port.Adapt;

public class DeviceUtil
{


    public static final int SUPOIN = 0;//肖邦
    public static final int HOPELAND = 1;//

    public static int getDeviceId()
    {
        return deviceId;
    }

    public static void setDeviceId(int deviceId)
    {
        DeviceUtil.deviceId = deviceId;
    }

    public static int deviceId = SUPOIN;

    public static String getDeviceName(int deviceId)
    {
        switch (deviceId)
        {
            case 0:
                return "销邦";
            case 1:
                return "鸿陆";
            default:
                return "";
        }
    }


    /**
     * 判断副电电量
     *
     * @return
     */
    public static Boolean canUsingBackBattery()
    {
        if (Adapt.getPowermanagerInstance().getBackupPowerSOC() < UIConstant.low_power_soc)
        {
            return false;
        }
        return true;
    }

}
