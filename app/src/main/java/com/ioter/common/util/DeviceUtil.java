package com.ioter.common.util;

import com.clouiotech.port.Adapt;

public class DeviceUtil
{


    public static final int SUPOIN = 0;//肖邦
    public static final int HOPELAND = 1;//


    public static int getDefaultDeviceId()
    {
        return SUPOIN;
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
