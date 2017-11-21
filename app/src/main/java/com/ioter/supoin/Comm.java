package com.ioter.supoin;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TabHost;

import com.ioter.supoin.uhf.UHF1.UHF001;
import com.ioter.supoin.uhf.UHF1.UHF1Application;
import com.ioter.supoin.uhf.UHF1Function.AndroidWakeLock;
import com.ioter.supoin.uhf.UHF1Function.SPconfig;
import com.ioter.supoin.uhf.UHF5.RFIDOperate;
import com.ioter.supoin.uhf.UHF5helper.InventoryBuffer;
import com.ioter.supoin.uhf.UHF5helper.ReaderHelper;
import com.ioter.supoin.uhf.UHF6.UHF006;
import com.uhf.api.cls.Reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.ioter.supoin.Comm.DeviceType.supion_M_3;
import static com.ioter.supoin.Comm.DeviceType.supion_M_4;
import static com.ioter.supoin.Comm.DeviceType.supion_S_3;
import static com.ioter.supoin.Comm.DeviceType.supion_S_4;
import static com.ioter.supoin.Comm.DeviceType.supion_Y_4;
import static com.ioter.supoin.Comm.Module.UHF005;
import static com.ioter.supoin.Comm.operateType.lockOpe;
import static com.ioter.supoin.Comm.operateType.readOpe;
import static com.ioter.supoin.Comm.operateType.setPower;
import static com.ioter.supoin.Comm.operateType.writeOpe;
import static com.ioter.supoin.uhf.UHF1.UHF001.Devaddrs;
import static com.ioter.supoin.uhf.UHF1.UHF001.er;
import static com.ioter.supoin.uhf.UHF1.UHF001.getAntPowerConf;
import static com.ioter.supoin.uhf.UHF1.UHF001.getGen2Session;
import static com.ioter.supoin.uhf.UHF1.UHF001.getRegion_Conf;
import static com.ioter.supoin.uhf.UHF1.UHF001.getgen2q;
import static com.ioter.supoin.uhf.UHF1.UHF001.setAntCheck;
import static com.ioter.supoin.uhf.UHF1.UHF001.setAntPowerConf;
import static com.ioter.supoin.uhf.UHF1.UHF001.setReg;
import static com.ioter.supoin.uhf.UHF1.UHF001.setRegion_Conf;
import static com.ioter.supoin.uhf.UHF1.UHF001.setUHF1Parameters;
import static com.ioter.supoin.uhf.UHF1.UHF001.uhf1lockOp;
import static com.ioter.supoin.uhf.UHF1.UHF001.uhf1readOp;
import static com.ioter.supoin.uhf.UHF5.RFIDOperate.mReader;
import static com.ioter.supoin.uhf.UHF5.RFIDOperate.m_curInventoryBuffer;
import static com.ioter.supoin.uhf.UHF5.RFIDOperate.m_curReaderSetting;
import static com.ioter.supoin.uhf.UHF5.RFIDOperate.setUHF5Parameters;
import static com.ioter.supoin.uhf.UHF5.RFIDOperate.uhf5lockOp;
import static com.ioter.supoin.uhf.UHF5.RFIDOperate.uhf5readOp;
import static com.ioter.supoin.uhf.UHF5.RFIDOperate.uhf5writeOp;


/**
 * Created by ThinKPad on 2017/3/21.
 */

public class Comm
{

    public static Application app;//Application
    public static UHF1Application myapp;//UHF1Application
    public static SPconfig spConfig;//模块配置
    public static Comm.Module moduleType = null;//模块类型
    public static AndroidWakeLock Awl;//AndroidWakeLock
    public static SoundPool soundPool; //SoundPool
    public static String COM = "/dev/ttyHSL0";//串口
    public static int Baudrate = 115200;//波特率
    public static TabHost supoinTabHost;//tabHost
    public static int rfidSleep = 50;//扫描间隔
    public static int rfidRunTime = 100;//扫描间隔
    public static int session = 0;//session
    public static int btTarget = 0;//存盘模式 0:A,1:B
    public static RFIDOperate rfidOperate;//UHF005 rfidOperate
    public static IntentFilter itent = new IntentFilter();
    public static List<InventoryBuffer.InventoryTagMap> lsTagList = new ArrayList<InventoryBuffer.InventoryTagMap>();
    public static int tagListSize = 0;//获取不同标签的个数
    public static int ReadCnt = 0;//每次读到的个数
    public static operateType opeT;
    public static int mPos1 = -1, mPos2 = -1;
    public static String strPwd = "";
    public static int uhf5outpouwer;
    public static boolean isQuick = false;
    public static final int OPERATE_TEXT = 1;
    public static final int UHF5MESSAGE_TEXT = 2;
    public static final int UHF1MESSAGE_TEXT = 3;
    public static Handler mRWLHandler;//读写锁数据异步操作
    public static Handler mOtherHandler;//其他参数设置数据异步操作
    public static boolean isrun;
    public static String[] tag = null;//每次读到的数据
    public static int[] tagcnt = new int[1];//每次读到标签的个数
    public static List<Byte> LB = new ArrayList();//
    public static Reader.TAGINFO[] Ltis = null;//数据列表
    public static Reader.TAGINFO tfs;//数据
    public static boolean runCycleRead;//是否循环读取
    public static String tagUHF1 = "tagUHF1";
    public static String tagUHF5 = "tagUHF5";
    public static int version = 0;//UHF5 version
    public static int ant = 0;//天线号
    public static LocalBroadcastManager lbm;//LocalBroadcastManager
    public static Handler connecthandler;//连接
    public static Context context;
    private static UHF006 mUHF006 = new UHF006();

    //region MainOp
    public interface SerialPortCallback {
        void onReceive(String strVar, int var2);
    }

    public static final String CTRL_FILE_YF = "/sys/devices/platform/psam/psam_state";
    public static final String CTRL_FILE_G3G = "/sys/devices/platform/psam/psam_state";
    public static final String CTRL_FILE_MTK = "/sys/devices/platform/psam_dev/psam_state";
    private static final String CTRL_FILE_G4G = "/sys/devices/soc.0/78b0000.serial/uart_switch";
    private static void writeToCtrlFile(String data) {
        try {
            if (dt == supion_S_3) {
                FileOutputStream fps = new FileOutputStream(new File(CTRL_FILE_G3G));
                fps.write(data.getBytes());
                fps.close();
            } else if( dt == supion_M_3 || dt == supion_M_4)  {
                FileOutputStream fps = new FileOutputStream(new File(CTRL_FILE_MTK));
                fps.write(data.getBytes());
                fps.close();
            }else if( dt == supion_Y_4 ) {
                FileOutputStream fps = new FileOutputStream(new File(CTRL_FILE_YF));
                fps.write(data.getBytes());
                fps.close();
            }else{
                FileOutputStream fps = new FileOutputStream(new File(CTRL_FILE_G4G));
                fps.write(data.getBytes());
                fps.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    enum DeviceType {
        supion_S_3,
        supion_S_4,
        supion_M_3,
        supion_M_4,
        supion_Y_4
    }

    public static DeviceType dt;

    public static void checkDevice() {
        String strM = Build.MODEL;
        int SDK = Build.VERSION.SDK_INT;
        try {
            if (strM.equals("SHT3X")) {
                if (SDK==23) {//YF
                    dt = supion_Y_4;
                    COM = "dev/ttyMT0";// YF 4G
                }else {//MTK 3G
                    dt = supion_M_3;
                    COM = "dev/ttyMT2";// MTK 3G
                }
            } else if (strM.equals("SHT3X-4G")) {// MTK 4G
                dt = supion_M_4;
                COM = "dev/ttyMT3";
            } else if (strM.equals("SHT0X")) {//JT 4G
                dt = supion_S_4;
                COM = "/dev/ttyHSL1";//JT 4G
            }else if (SDK == 18) {
                dt = supion_S_3;
                COM = "/dev/ttyHSL2";//3G
            } else if (SDK == 22) {
                dt = supion_S_4;
                COM = "/dev/ttyHSL0";//4G
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "err:" + e.getMessage());
        }
    }

    public static boolean powerUp() {
        try {
            if (dt == supion_S_3 || dt == supion_M_3 || dt == supion_M_4)
                writeToCtrlFile("2");
            else if (dt == supion_S_4||dt ==supion_Y_4) {
                writeToCtrlFile("uart3");//上电
            } else{
                writeToCtrlFile("2");
                writeToCtrlFile("uart3");//上电
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean powerDown() {
        try {
            if (dt == supion_S_3 || dt == supion_M_3 || dt == supion_M_4)
                writeToCtrlFile("3");
            else if (dt == supion_S_4||dt ==supion_Y_4) {
                writeToCtrlFile("disable");//下电
                writeToCtrlFile("uart2");//切换复用
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void Connect() {
        Log.d( "TAG",COM);
        if (UHF001.connect()) {
            Message m1 = connecthandler.obtainMessage();
            Bundle b1 = new Bundle();
            if (moduleType != null) {
                b1.putString("Msg", moduleType.toString() + "模块打开成功");
                moduleType = Comm.Module.UHF001;
            }
            m1.setData(b1);
            connecthandler.sendMessage(m1);
        }else {
            UHF005connect(new Comm.SerialPortCallback() {
                @Override
                public void onReceive(String strVersion, int i) {
                    Message m2 = connecthandler.obtainMessage();
                    Bundle b2 = new Bundle();
                    if (!strVersion.equals("0")) {
                        moduleType = UHF005;
                        b2.putString("Msg", moduleType.toString() + "模块打开成功");
                        m2.setData(b2);
                        connecthandler.sendMessage(m2);
                    }
                }
            });
        }
    }

    public static void startScan() {
        Comm.isrun = true;
        if (dt == supion_S_3 || dt == supion_M_3 || dt == supion_M_4)
            writeToCtrlFile("startrfid");
        else if (dt == supion_S_4)
            writeToCtrlFile(new String("startrfid"));

        if (moduleType == Comm.Module.UHF001 && UHF001.CheckToScan()) {
            if (myapp.ThreadMODE == 0)
                UHF001.UHF1startScan();
            myapp.Devaddrs.clear();
        } else if (moduleType == UHF005) {
            rfidOperate.startScan(session, btTarget, ant);
        } else {
            if (dt == supion_S_3 || dt == supion_M_3 || dt == supion_M_4)
                writeToCtrlFile("stoprfid");
            else if (dt == supion_S_4)
                writeToCtrlFile(new String("stoprfid"));
        }

    }

    public static void stopScan() {
        Comm.isrun = false;
        if (moduleType == Comm.Module.UHF001) {
            if (myapp.nostop) {
                er = myapp.Mreader.AsyncStopReading();
                if (er != Reader.READER_ERR.MT_OK_ERR) {
                    return;
                }
            }
            if (myapp.ThreadMODE == 0)
                UHF001.UHF1stopScan();
            else {
                try {
                    UHF001.runThread.join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            myapp.Devaddrs.putAll(Devaddrs);
            // 读大量标签停止显示
            if (myapp.nostop) {
                UHF001.UHF1sendMes();
            }
        } else if (moduleType == UHF005) {
            rfidOperate.stop();
        }

        if (dt == supion_S_3 || dt == supion_M_3 || dt == supion_M_4)
            writeToCtrlFile("stoprfid");
        else if (dt == supion_S_4)
            writeToCtrlFile(new String("stoprfid"));
    }

    public static void clean() {
        if (moduleType == Comm.Module.UHF001 && !isrun) {
            Devaddrs.clear();
            myapp.Devaddrs.clear();
            myapp.Curepc = "";
        } else if (moduleType == UHF005 && !isrun) {

            m_curInventoryBuffer.clearInventoryPar();
            m_curInventoryBuffer.clearInventoryRealResult();
            tagListSize = 0;
            lsTagList.clear();
        }
    }

    public static boolean setParameters() {

        if (moduleType == Comm.Module.UHF001 && setUHF1Parameters()) {
            return true;
        } else if (moduleType == UHF005 && setUHF5Parameters()) {
            Comm.rfidSleep = 50;
            return true;
        } else
            return false;
    }
    //endregion

    public enum operateType {
        nullOperate,
        getAntCheck,
        setAntCheck,
        getPower,
        setPower,
        getReg,
        setReg,
        getFre,
        setFre,
        getQ,
        setQ,
        getSession,
        setSession,
        readOpe,
        writeOpe,
        writeepcOpe,
        lockOpe
    }

    public static void readTag(int ant, int tagBank, String opCount, String startAdd, int datatype) {
        Message m = new Message();
        Bundle b = new Bundle();
        try {
            if (Comm.moduleType == Module.UHF001) {
                String val = uhf1readOp(ant, tagBank, opCount, startAdd, datatype);
                b.putString("readData", val);
                m.setData(b);
                Comm.mRWLHandler.sendMessage(m);
            } else if (Comm.moduleType == UHF005) {
                Comm.opeT = readOpe;
                String val = uhf5readOp(ant, tagBank, opCount, startAdd, datatype);
                if (val != "") {
                    b.putString("Err", val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("readTag", e.getMessage());
            b.putString("Err", e.getMessage());
        }

    }

    public static void writeTag(int ant, int tagBank, String opCount, String startAdd, int datatype, String strWriteData) {
        Message m = new Message();
        Bundle b = new Bundle();
        try {
            if (moduleType == Module.UHF001) {
                Boolean writeSuc = false;
                writeSuc = UHF001.uhf1writeOp(datatype, ant, tagBank, opCount, startAdd, strWriteData);
                if (writeSuc)
                    b.putBoolean("isWriteSucceed", true);
                else
                    b.putBoolean("isWriteSucceed", false);
                m.setData(b);
                Comm.mRWLHandler.sendMessage(m);
            } else if (moduleType == UHF005) {
                Comm.opeT = writeOpe;
                String strRet = uhf5writeOp(ant, tagBank, opCount, startAdd, datatype, strWriteData);
                if (strRet != "") {
                    Log.d("wirteTag", strRet);
                    b.putBoolean("isWriteSucceed", false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("wirteTag", e.getMessage());
            b.putBoolean("isWriteSucceed", false);
        }

    }

    public static void lockTag(int ant, int lbank, int ltype) {
        Message m = new Message();
        Bundle b = new Bundle();
        try {
            if (moduleType == Module.UHF001) {
                boolean lock = uhf1lockOp(ant, lbank, ltype);
                if (lock)
                    b.putBoolean("isLockSucceed", true);
                else
                    b.putBoolean("isLockSucceed", false);
                m.setData(b);
                Comm.mRWLHandler.sendMessage(m);
            } else if (moduleType == UHF005) {
                Comm.opeT = lockOpe;
                String strRet = uhf5lockOp(ant, lbank, ltype);
                if (strRet != "") {
                    Log.d("lockTag", strRet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("lockTag", e.getMessage());
        }

    }

    //region other
    public static void getAntCheck() {
        Message m = new Message();
        Bundle b = new Bundle();
        if (moduleType == Comm.Module.UHF001) {
            int antIndex = UHF001.getAntCheck();
            b.putInt("antIndex", antIndex);
        }
        m.setData(b);
        Comm.mOtherHandler.sendMessage(m);
    }

    public static void setANTCheck(int antCheckEnable) {
        Message m = new Message();
        Bundle b = new Bundle();
        if (moduleType == Comm.Module.UHF001) {
            boolean antCheck = setAntCheck(antCheckEnable);
            b.putBoolean("isSetAntCheck", antCheck);
        }
        m.setData(b);
        Comm.mOtherHandler.sendMessage(m);
    }

    public static void getAntPower() {
        if (moduleType == Comm.Module.UHF001) {
            Message m = new Message();
            Bundle b = new Bundle();
            getAntPowerConf = myapp.Mreader.new AntPowerConf();
            UHF001.getPower();
            if (getAntPowerConf != null) {
                for (int i = 0; i < getAntPowerConf.antcnt; i++) {
                    if (i == 0) {
                        int ant1Power = (getAntPowerConf.Powers[i].readPower - 500) / 100;
                        b.putInt("ant1Power", ant1Power);
                    } else if (i == 1) {
                        int ant2Power = (getAntPowerConf.Powers[i].readPower - 500) / 100;
                        b.putInt("ant2Power", ant2Power);
                    } else if (i == 2) {
                        int ant3Power = (getAntPowerConf.Powers[i].readPower - 500) / 100;
                        b.putInt("ant3Power", ant3Power);
                    } else if (i == 3) {
                        int ant4Power = (getAntPowerConf.Powers[i].readPower - 500) / 100;
                        b.putInt("ant4Power", ant4Power);
                    }
                }
            }
            m.setData(b);
            Comm.mOtherHandler.sendMessage(m);
        } else if (moduleType == UHF005) {
            mReader.getOutputPower(m_curReaderSetting.btReadId);
        }

    }

    public static void setAntPower(int ant1pow, int ant2pow, int ant3pow, int ant4pow) {
        Message m = new Message();
        Bundle b = new Bundle();
        boolean isSuc = false;
        if (moduleType == Comm.Module.UHF001) {
            setAntPowerConf = myapp.Mreader.new AntPowerConf();

            int[] rp = new int[4];
            int[] wp = new int[4];

            rp[0] = ant1pow;
            rp[1] = ant2pow;
            rp[2] = ant3pow;
            rp[3] = ant4pow;
            wp[0] = rp[0];
            wp[1] = rp[1];
            wp[2] = rp[2];
            wp[3] = rp[3];

            setAntPowerConf.antcnt = myapp.antportc;
            int[] rpow = new int[setAntPowerConf.antcnt];
            int[] wpow = new int[setAntPowerConf.antcnt];
            for (int i = 0; i < setAntPowerConf.antcnt; i++) {
                Reader.AntPower jaap = myapp.Mreader.new AntPower();
                jaap.antid = i + 1;
                jaap.readPower = (short) (500 + 100 * rp[i]);
                rpow[i] = jaap.readPower;

                jaap.writePower = (short) (500 + 100 * wp[i]);
                wpow[i] = jaap.writePower;
                setAntPowerConf.Powers[i] = jaap;
            }
            boolean setPower = UHF001.setPower(rpow, wpow);
            if (setPower)
                isSuc = true;
            else
                isSuc = false;
            b.putBoolean("isSetPower", isSuc);
            m.setData(b);
            Comm.mOtherHandler.sendMessage(m);
        } else if (moduleType == UHF005) {
            Comm.opeT = setPower;
            uhf5outpouwer = (short) ((500 + 100 * ant1pow) / 100);

            byte btOutputPower = (byte) uhf5outpouwer;
            mReader.setOutputPower(m_curReaderSetting.btReadId, btOutputPower);
            m_curReaderSetting.btAryOutputPower = new byte[]{btOutputPower};
        }

    }

    public static void getAntReg() {
        Message m = new Message();
        Bundle b = new Bundle();

        if (moduleType == Comm.Module.UHF001) {
            UHF001.getReg();
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                switch (getRegion_Conf[0]) {
                    case RG_PRC:
                        b.putInt("getReg", 0);
                        break;
                    case RG_EU:
                        b.putInt("getReg", 4);
                        break;
                    case RG_EU2:
                        b.putInt("getReg", 5);
                        break;
                    case RG_EU3:
                        b.putInt("getReg", 6);
                        break;
                    case RG_KR:
                        b.putInt("getReg", 3);
                        break;
                    case RG_NA:
                        b.putInt("getReg", 1);
                        break;
                    default:
                        b.putInt("getReg", 7);
                        break;
                }
            }
            m.setData(b);
            Comm.mOtherHandler.sendMessage(m);
        } else if (moduleType == UHF005) {
            mReader.getFrequencyRegion(m_curReaderSetting.btReadId);
        }

    }

    public static void setAntReg(int Region) {

        if (moduleType == Comm.Module.UHF001) {
            Message m = new Message();
            Bundle b = new Bundle();
            switch (Region) {
                case 0:
                    setRegion_Conf = Reader.Region_Conf.RG_PRC;
                    break;
                case 1:
                    setRegion_Conf = Reader.Region_Conf.RG_NA;
                    break;
                case 2:
                    setRegion_Conf = Reader.Region_Conf.RG_NONE;
                    break;
                case 3:
                    setRegion_Conf = Reader.Region_Conf.RG_KR;
                    break;
                case 4:
                    setRegion_Conf = Reader.Region_Conf.RG_EU;
                    break;
                case 5:
                    setRegion_Conf = Reader.Region_Conf.RG_EU2;
                    break;
                case 6:
                    setRegion_Conf = Reader.Region_Conf.RG_EU3;
                    break;
                case 7:
                case 8:
                case 9:
                case 10:
                default:
                    setRegion_Conf = Reader.Region_Conf.RG_NONE;
                    break;
            }
            boolean setFre = setReg(setRegion_Conf);
            if (setFre) {
                myapp.Rparams.region = Region;
                myapp.Rparams.frelen = 0;
                b.putBoolean("isSetReg", true);
            } else
                b.putBoolean("isSetReg", false);

            m.setData(b);
            Comm.mOtherHandler.sendMessage(m);
        } else if (moduleType == UHF005) {
            byte btRegion = 0x00, btStartFreq = 0x00, btEndFreq = 0x00;
            switch (Region) {
                case 0:
                    btRegion = 0x03;
                    btStartFreq = (byte) 43;
                    btEndFreq = (byte) 53;
                    break;
                case 1:
                    btRegion = 0x01;
                    btStartFreq = (byte) 7;
                    btEndFreq = (byte) 59;
                    break;
                case 2:
                    btRegion = 0x02;
                    btStartFreq = (byte) 0;
                    btEndFreq = (byte) 6;
                    break;
                case 3:
                    break;
            }
            Log.d("UHF005", " btRegion = 0x01;");
            mReader.setFrequencyRegion(m_curReaderSetting.btReadId, btRegion, btStartFreq, btEndFreq);
            Log.d("UHF005", "btStartFreq:" + String.valueOf(btStartFreq) + " btEndFreq:" + String.valueOf(btEndFreq));
            m_curReaderSetting.btRegion = btRegion;
            m_curReaderSetting.btFrequencyStart = btStartFreq;
            m_curReaderSetting.btFrequencyEnd = btEndFreq;
        }

    }

    public static String[] getAntFre(int region) {
        String[] ssf = {""};
        if (moduleType == Comm.Module.UHF001) {
            Reader.HoptableData_ST hdst2 = myapp.Mreader.new HoptableData_ST();
            er = myapp.Mreader.ParamGet(Reader.Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);

            int[] tablefre;
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                tablefre = Sort(hdst2.htb, hdst2.lenhtb);
                ssf = new String[hdst2.lenhtb];
                for (int i = 0; i < hdst2.lenhtb; i++) {
                    ssf[i] = String.valueOf(tablefre[i]);
                }
            }
        } else if (moduleType == UHF005) {
            ssf = RFIDOperate.getuhf5Fre(region);
        }
        return ssf;
    }

    public static void getSes() {
        Message m = new Message();
        Bundle b = new Bundle();
        if (moduleType == Comm.Module.UHF001) {
            int[] val2 = new int[]{-1};
            getGen2Session(val2);

            if (val2[0] != -1)
                b.putInt("session", val2[0]);
        } else if (moduleType == UHF005) {
            switch (Comm.session) {
                case 0:
                    b.putInt("session", 0);
                    break;
                case 1:
                    b.putInt("session", 1);
                    break;
                case 2:
                    b.putInt("session", 2);
                    break;
                case 3:
                    b.putInt("session", 3);
                    break;
            }
        }
        m.setData(b);
        Comm.mOtherHandler.sendMessage(m);
    }

    public static void setSes(int ses) {
        Message m = new Message();
        Bundle b = new Bundle();
        if (moduleType == Comm.Module.UHF001) {
            int[] val = new int[]{-1};
            val[0] = ses;
            er = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, val);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                myapp.Rparams.session = val[0];
                b.putBoolean("isSetSes", true);
            } else
                b.putBoolean("isSetSes", false);
        } else if (moduleType == UHF005) {
            session = ses;
            b.putBoolean("isSetSes", true);
        }
        m.setData(b);
        Comm.mOtherHandler.sendMessage(m);
    }

    public static void getQValue() {
        Message m = new Message();
        Bundle b = new Bundle();
        int[] val = new int[]{-1};
        if (moduleType == Comm.Module.UHF001) {

            if (myapp.Rparams.qv < 0) {
                getgen2q(val);
                b.putInt("Q", val[0]);
            } else
                b.putInt("Q", myapp.Rparams.qv);
        } else if (moduleType == UHF005) {
            b.putInt("Q", val[0]);
        }
        m.setData(b);
        Comm.mOtherHandler.sendMessage(m);
    }

    public static void setQValue(int Q) {
        Message m = new Message();
        Bundle b = new Bundle();
        if (moduleType == Comm.Module.UHF001) {
            int[] val = new int[]{-1};
            val[0] = Q;
            // boolean setG = setgen2q(val[0]);
            er = myapp.Mreader.ParamSet(
                    Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_Q, val);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                myapp.Rparams.qv = val[0];
                b.putBoolean("isSetQ", true);
            } else
                b.putBoolean("isSetQ", true);
        }
        m.setData(b);
        Comm.mOtherHandler.sendMessage(m);
    }

    public static Comm.SerialPortCallback comm_callback;

    public static boolean UHF005connect(Comm.SerialPortCallback callback) {
        try {
            try {
                rfidOperate = new RFIDOperate();
                rfidOperate.onCreate(context, "30");//30为功率
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            Log.d(tagUHF5, "connect");

            comm_callback = callback;
            ReaderHelper.mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
            itent.addAction(ReaderHelper.BROADCAST_WRITE_LOG);
            itent.addAction(ReaderHelper.BROADCAST_REFRESH_READER_SETTING);
            itent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL);
            itent.addAction(ReaderHelper.BROADCAST_REFRESH_FAST_SWITCH);
            itent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY);
            itent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL);
            itent.addAction(ReaderHelper.BROADCAST_REFRESH_ISO18000_6B);
            itent.addAction(ReaderHelper.BROADCAST_REFRESH_OPERATE_TAG);
            itent.addAction(ReaderHelper.BROADCAST_REFRESH_READER_SETTING);
            itent.addAction(ReaderHelper.BROADCAST_WRITE_DATA);
            ReaderHelper.mLocalBroadcastManager.registerReceiver(rfidOperate.mRecv, itent);
            version = mReader.getFirmwareVersion(m_curReaderSetting.btReadId);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tagUHF5, "Erro:" + e.toString());
            return false;
        }
        return true;
    }


    public static enum Module {
        UHF001,
        UHF002,
        UHF003,
        UHF005
    }

    public enum runType {
        MT_HARDWARE_TOO_MANY_RESET,
        MT_HARDWARE_SUCCEED,
        MT_HARDWARE_FAILS
    }

    public static int SortGroup(RadioGroup rg) {
        int check1 = rg.getCheckedRadioButtonId();
        if (check1 != -1) {
            for (int i = 0; i < rg.getChildCount(); i++) {
                View vi = rg.getChildAt(i);
                int vv = vi.getId();
                if (check1 == vv) {
                    return i;
                }
            }
            return -1;
        } else
            return check1;
    }


    public static int[] Sort(int[] array, int len) {
        int tmpIntValue = 0;
        for (int xIndex = 0; xIndex < len; xIndex++) {
            for (int yIndex = 0; yIndex < len; yIndex++) {
                if (array[xIndex] < array[yIndex]) {
                    tmpIntValue = (Integer) array[xIndex];
                    array[xIndex] = array[yIndex];
                    array[yIndex] = tmpIntValue;
                }
            }
        }

        return array;
    }

    public static int[] CollectionTointArray(
            @SuppressWarnings("rawtypes") List list) {
        @SuppressWarnings("rawtypes")
        Iterator itor = list.iterator();
        int[] backdata = new int[list.size()];
        int i = 0;
        while (itor.hasNext()) {
            backdata[i++] = (int) (Integer) itor.next();
        }
        return backdata;
    }

    //buf:str  len:str的长度 hexbuf：返回的数据
    public static void Str2Hex(String buf, int len, byte[] hexbuf) {
        String chex = "0123456789ABCDEF";
        if (len % 2 == 0) {
            for (int i = 0; i < len; i += 2) {
                byte hnx = (byte) chex.indexOf(buf.toUpperCase().substring(i, i + 1));
                byte lnx = (byte) chex.indexOf(buf.toUpperCase().substring(i + 1, i + 2));
                hexbuf[i / 2] = (byte) (hnx << 4 & 255 | lnx & 255);
            }

        }
    }

    public static void Hex2Str(byte[] buf, int len, char[] out) {
        char[] hexc = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        for (int i = 0; i < len; ++i) {
            out[i * 2] = hexc[(buf[i] & 255) / 16];
            out[i * 2 + 1] = hexc[(buf[i] & 255) % 16];
        }
    }

    public static void playSound() {
        soundPool.play(1, 1, 1, 0, 0, 1);
    }
    //endregion
}
