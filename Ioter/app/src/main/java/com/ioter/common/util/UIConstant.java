package com.ioter.common.util;

public class UIConstant
{
    public static final int DEBUGMODE = 0x01;// 大于0 开发模式
    
    /**消息**/
    public static final int MSG_SHOW_WAIT = 1;
    public static final int MSG_HIDE_WAIT = 2;
    public static final int MSG_SHOW_Toast = 3;
    public static final int MSG_WELCOME_Delay = 4;
    public static final int MSG_BANNER_Delay = 5;
    public static final int MSG_USER_BEG = 100;
    public static final int MSG_RESULT_READ = MSG_USER_BEG + 1; // 常量读
    public static final int MSG_FLUSH_READTIME = MSG_USER_BEG + 2;
    public static final int MSG_UHF_POWERLOW = MSG_USER_BEG + 3;
    
    public static int low_power_soc = 10;
    public static int _NowAntennaNo = 1; // 读写器天线编号
    public static int _UpDataTime = 0; // 重复标签上传时间，控制标签上传速度不要太快
    public static int _Max_Power = 30; // 读写器最大发射功率
    public static int _Min_Power = 0; // 读写器最小发射功率
    public static String _NowReadParam = _NowAntennaNo + "|1"; // 读标签参数
    
    
    
    public static String getLoginUrl()
    {
        return "http://192.168.31.24/UserService.svc?wsdl";
    }
    
    public static String getLoginSoapAction()
    {
        return "http://tempuri.org/IUserService/Login";
    }
    
    /**
     *  获取盘点url
     * @return
     */
    public static String getCheckUrl()
    {
        return "http://192.168.31.24/TakeStockService.svc?wsdl";
    }
    
    public static String getGetTakeStockSoapAction()
    {
        return "http://tempuri.org/ITakeStockService/GetTakeStock";
    }
    
    public static String getWriteTakeStockSoapAction()
    {
        return "http://tempuri.org/ITakeStockService/WriteTakeStock";
    }
    
    /**
     *  获取仓库及货架数据url
     * @return
     */
    public static String getWarehouseUrl()
    {
        return "http://192.168.31.24/WarehouseService.svc?wsdl";
    }
    
    public static String getWarehouseSoapAction()
    {
        return "http://tempuri.org/IWarehouseService/GetAll";
    }
    
    /**
     *  获取出入库url
     * @return
     */
    public static String getEpcInOutUrl()
    {
        return "http://192.168.31.24/StockService.svc?wsdl";
    }
    
    public static String getEpcInSoapAction()
    {
        return "http://tempuri.org/IStockService/StockIn";
    }
    
    public static String getEpcOutSoapAction()
    {
        return "http://tempuri.org/IStockService/StockOut";
    }

    /**
     * 获取产品数据
     * @return
     */
    public static String getProductUrl()
    {
        return "http://192.168.31.24/ProductService.svc?wsdl";
    }

    public static String getProductSoapAction()
    {
        return "http://tempuri.org/IProductService/GetInfo";
    }
}
