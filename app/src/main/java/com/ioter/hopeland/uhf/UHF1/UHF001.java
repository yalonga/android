package com.ioter.hopeland.uhf.UHF1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ioter.hopeland.Comm;
import com.pow.api.cls.RfidPower;
import com.uhf.api.cls.Reader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.ioter.hopeland.Comm.COM;
import static com.ioter.hopeland.Comm.LB;
import static com.ioter.hopeland.Comm.UHF1MESSAGE_TEXT;
import static com.ioter.hopeland.Comm.app;
import static com.ioter.hopeland.Comm.myapp;
import static com.ioter.hopeland.Comm.playSound;
import static com.ioter.hopeland.Comm.runType.MT_HARDWARE_FAILS;
import static com.ioter.hopeland.Comm.runType.MT_HARDWARE_SUCCEED;
import static com.ioter.hopeland.Comm.runType.MT_HARDWARE_TOO_MANY_RESET;
import static com.ioter.hopeland.Comm.soundPool;
import static com.ioter.hopeland.Comm.spConfig;
import static com.ioter.hopeland.Comm.strPwd;
import static com.ioter.hopeland.Comm.tagUHF1;
import static com.ioter.hopeland.Comm.tagcnt;
import static com.ioter.hopeland.Comm.tfs;
import static com.ioter.hopeland.Comm.moduleType;

/**
 * Created by ThinKPad on 2017/3/22.
 */

public class UHF001 {
    public static RfidPower.PDATYPE PT;
    public static Reader.READER_ERR er;
    public static Reader.Region_Conf rre;
    public static Map<String, Reader.TAGINFO> Devaddrs = new LinkedHashMap<String, Reader.TAGINFO>();// 有序
    public static Reader.TAGINFO tf;
    public static int trycount = 3;

    public static Thread runThread;

    public static Reader.AntPowerConf setAntPowerConf;
    public static Reader.AntPowerConf getAntPowerConf;

    public static Reader.Region_Conf[] getRegion_Conf = new Reader.Region_Conf[1];
    public static Reader.Region_Conf setRegion_Conf;

    public static Reader.HoptableData_ST getFreHoptableData;
    public static Reader.HoptableData_ST setFreHoptableData;
    public static Handler mhandler;//扫描数据异步操作
    public static Handler UHF1handler = new Handler();
    static String strReadR = "";
    static int readCount = 0;
    public static boolean isWriteEpc = false;

    //region mainActivity function
    public static boolean connect() {
        try {
            Log.d( "TAG",COM+"come");
            myapp = (UHF1Application) app;
            myapp.Mreader = new Reader();
            myapp.spf = spConfig;
            myapp.Rparams = myapp.new ReaderParams();
            myapp.nostop = false;

            PT = RfidPower.PDATYPE.valueOf(17);
            PT = RfidPower.PDATYPE.XBANG;

            Log.d( "TAG",COM+"come 1111111");

            er = myapp.Mreader.InitReader_Notype(COM, 1);

            Log.d( "TAG",COM+"er3:"+er);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                Log.d( "TAG",COM);

                myapp.needreconnect = false;

                myapp.spf.SaveString("PDATYPE", String.valueOf(0));
                myapp.spf.SaveString("ADDRESS", COM);
                myapp.spf.SaveString("ANTPORT", String.valueOf(0));
                myapp.antportc = 1;
                ConnectHandleUI();
                myapp.Address = COM;

                Reader.HardwareDetails val = myapp.Mreader.new HardwareDetails();
                myapp.Mreader.GetHardwareDetails(val);
                if (Reader.Module_Type.MODOULE_SLR5100 == val.module)
                    moduleType = Comm.Module.UHF001;
                else if (Reader.Module_Type.MODOULE_SLR1200 == val.module)
                    moduleType = Comm.Module.UHF002;
                else
                    return false;
            } else
                return false;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean reconnect() {
        er = myapp.Mreader.InitReader_Notype(myapp.Address,
                myapp.antportc);
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            ConnectHandleUI();

        } else
            return false;

        return true;
    }

    public static boolean CheckToScan() {
        if (myapp.needreconnect) {
            boolean bl = reconnect();
            if (!bl)
                return false;
        }
        return true;

    }

    private static void ConnectHandleUI() {
        try {
            Reader.READER_ERR er;
            myapp.Rparams = myapp.spf.ReadReaderParams();

            if (myapp.Rparams.invpro.size() < 1)
                myapp.Rparams.invpro.add("GEN2");

            List<Reader.SL_TagProtocol> ltp = new ArrayList<Reader.SL_TagProtocol>();
            for (int i = 0; i < myapp.Rparams.invpro.size(); i++) {
                if (myapp.Rparams.invpro.get(i).equals("GEN2")) {
                    ltp.add(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_GEN2);
                } else if (myapp.Rparams.invpro.get(i).equals("6B")) {
                    ltp.add(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B);
                } else if (myapp.Rparams.invpro.get(i).equals("IPX64")) {
                    ltp.add(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_IPX64);
                } else if (myapp.Rparams.invpro.get(i).equals("IPX256")) {
                    ltp.add(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_IPX256);
                }
            }

            Reader.Inv_Potls_ST ipst = myapp.Mreader.new Inv_Potls_ST();
            ipst.potlcnt = ltp.size();
            ipst.potls = new Reader.Inv_Potl[ipst.potlcnt];
            Reader.SL_TagProtocol[] stp = ltp
                    .toArray(new Reader.SL_TagProtocol[ipst.potlcnt]);
            for (int i = 0; i < ipst.potlcnt; i++) {
                Reader.Inv_Potl ipl = myapp.Mreader.new Inv_Potl();
                ipl.weight = 30;
                ipl.potl = stp[i];
                ipst.potls[0] = ipl;
            }

            er = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst);
            Log.d(tagUHF1, "Connected set pro:" + er.toString());

            er = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT,
                    new int[]{myapp.Rparams.checkant});
            Log.d(tagUHF1, "Connected set checkant:" + er.toString());

            //功率设置成最大
            Reader.AntPowerConf apcf = myapp.Mreader.new AntPowerConf();
            apcf.antcnt = myapp.antportc;
            int[] rpow = new int[1];
            int[] wpow = new int[1];
            Reader.AntPower jaap = myapp.Mreader.new AntPower();
            jaap.antid = 1;
            jaap.readPower = (short) (500 + 100 * 25);// 3000
            rpow[0] = jaap.readPower;
            jaap.writePower = (short) (500 + 100 * 25);// 3000
            wpow[0] = jaap.writePower;
            apcf.Powers[0] = jaap;

            myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);

//            setRegion_Conf = Reader.Region_Conf.RG_PRC;
//            boolean setFre = setReg(setRegion_Conf);

            if (myapp.Rparams.frelen > 0) {
                Reader.HoptableData_ST hdst = myapp.Mreader.new HoptableData_ST();
                hdst.lenhtb = myapp.Rparams.frelen;
                hdst.htb = myapp.Rparams.frecys;
                er = myapp.Mreader.ParamSet(
                        Reader.Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst);
            }

            er = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION,
                    new int[]{myapp.Rparams.session});
            er = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_Q,
                    new int[]{myapp.Rparams.qv});
            er = myapp.Mreader.ParamSet(
                    Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE,
                    new int[]{myapp.Rparams.wmode});
            er = myapp.Mreader.ParamSet(
                    Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN,
                    new int[]{myapp.Rparams.maxlen});
            er = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET,
                    new int[]{myapp.Rparams.target});

            if (myapp.Rparams.filenable == 1) {// meijin
                Reader.TagFilter_ST tfst = myapp.Mreader.new TagFilter_ST();
                tfst.bank = myapp.Rparams.filbank;
                tfst.fdata = new byte[myapp.Rparams.fildata.length() / 2];
                myapp.Mreader.Str2Hex(myapp.Rparams.fildata,
                        myapp.Rparams.fildata.length(), tfst.fdata);
                tfst.flen = tfst.fdata.length * 8;
                tfst.startaddr = myapp.Rparams.filadr;
                tfst.isInvert = myapp.Rparams.filisinver;

                myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, tfst);
            }

            if (myapp.Rparams.emdenable == 1) {
                Reader.EmbededData_ST edst = myapp.Mreader.new EmbededData_ST();

                edst.accesspwd = null;
                edst.bank = myapp.Rparams.emdbank;
                edst.startaddr = myapp.Rparams.emdadr;
                edst.bytecnt = myapp.Rparams.emdbytec;
                edst.accesspwd = null;

                er = myapp.Mreader.ParamSet(
                        Reader.Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
            }
            er = myapp.Mreader.ParamSet(
                    Reader.Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA,
                    new int[]{myapp.Rparams.adataq});
            er = myapp.Mreader.ParamSet(
                    Reader.Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI,
                    new int[]{myapp.Rparams.rhssi});
            er = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_SEARCH_MODE,
                    new int[]{myapp.Rparams.invw});

            myapp.Rparams.readtime = 50;
            Comm.rfidSleep = 0;
        } catch (Exception ex) {
            Log.d(tagUHF1,
                    ex.getMessage() + ex.toString() + ex.getStackTrace());
        }
    }

    public static boolean setUHF1Parameters() {
        try {
            // 读写功率都设成3000
            Reader.AntPowerConf apcf = myapp.Mreader.new AntPowerConf();
            apcf.antcnt = myapp.antportc;
            int[] rpow = new int[1];
            int[] wpow = new int[1];
            Reader.AntPower jaap = myapp.Mreader.new AntPower();
            jaap.antid = 1;
            jaap.readPower = (short) (500 + 100 * 25);// 3000
            rpow[0] = jaap.readPower;
            jaap.writePower = (short) (500 + 100 * 25);// 3000
            wpow[0] = jaap.writePower;
            apcf.Powers[0] = jaap;
            er = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                myapp.Rparams.rpow = rpow;
                myapp.Rparams.wpow = wpow;
            }
            // 设置成session1
            int[] val = new int[]{1};

            er = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION,
                    val);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                myapp.Rparams.session = val[0];

            }

            //频率设成中国
//            Reader.Region_Conf[] rcf2 = new Reader.Region_Conf[1];
//            er = myapp.Mreader.ParamGet(Reader.Mtr_Param.MTR_PARAM_FREQUENCY_REGION,
//                    rcf2);

//            Reader.Region_Conf rre;
//            rre = Reader.Region_Conf.RG_PRC;

//            er = myapp.Mreader.ParamSet(Reader.Mtr_Param.MTR_PARAM_FREQUENCY_REGION,
//                    rre);
//            if (er == Reader.READER_ERR.MT_OK_ERR) {
//                myapp.Rparams.region = 0;
//                myapp.Rparams.frelen = 0;
//            }

            Reader.HardwareDetails hd = myapp.Mreader.new HardwareDetails();
            myapp.Mreader.GetHardwareDetails(hd);
            if (Reader.Module_Type.MODOULE_SLR1200 == hd.module) {
                if (myapp.nostop)
                    myapp.nostop = false;
                else
                    myapp.nostop = true;
            }else
                myapp.nostop = false;

            // 读取时长,单位ms
            myapp.Rparams.readtime = 50;
            Comm.rfidSleep = 0;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static enum RegionConf {
        RG_NONE(0x0), RG_NA(0x01), RG_EU(0x02), RG_EU2(0X07), RG_EU3(0x08), RG_KR(
                0x03), RG_PRC(0x06), RG_PRC2(0x0A), RG_OPEN(0xFF);

        int p_v;

        RegionConf(int v) {
            p_v = v;
        }

        public int value() {
            return this.p_v;
        }

        public static RegionConf valueOf(int value) { // 手写的从int到enum的转换函数
            switch (value) {
                case 0:
                    return RG_NONE;
                case 1:
                    return RG_NA;
                case 2:
                    return RG_EU;
                case 7:
                    return RG_EU2;
                case 8:
                    return RG_EU3;
                case 3:
                    return RG_KR;
                case 6:
                    return RG_PRC;
                case 0x0A:
                    return RG_PRC2;
                case 0xff:
                    return RG_OPEN;
            }
            return null;
        }
    }

    public static void UHF1startScan() {
        if (myapp.nostop) {
            er = myapp.Mreader.AsyncStartReading(myapp.Rparams.uants,
                    myapp.Rparams.uants.length, 0);
            if (er != Reader.READER_ERR.MT_OK_ERR)
                return;
        }

        UHF1handler.postDelayed(runnable_cycleRead, 0);
        UHF1handler.postDelayed(runnable_refreshlist, 0);
    }

    private static Runnable runnable_refreshlist = new Runnable() {
        public void run() {
            UHF1sendMes();
            UHF1handler.postDelayed(this, 0);
        }
    };

    public static void UHF1sendMes() {
        Message m = Message.obtain(mhandler, UHF1MESSAGE_TEXT);
        Bundle bundle = new Bundle();
        bundle.putString("Result", strReadR);
        bundle.putInt("readCount", readCount);
        m.setData(bundle);
        mhandler.sendMessage(m);
    }

    public static void UHF1stopScan() {
        UHF1handler.removeCallbacks(runnable_cycleRead);
        UHF1handler.removeCallbacks(runnable_refreshlist);
    }

    //region runnable_cycleRead
    private static Runnable runnable_cycleRead = new Runnable() {
        public void run() {
            synchronized (this) {
                strReadR = "";
                //readCount=0;
                if (Comm.isrun) {
                    Comm.runType rt = MT_HARDWARE_SUCCEED;
                    try {
                        int[] tagcnt = new int[1];
                        tagcnt[0] = 0;
                        if (myapp.nostop) {
                            er = myapp.Mreader.AsyncGetTagCount(tagcnt);
                        } else {
                            er = myapp.Mreader.TagInventory_Raw(myapp.Rparams.uants,
                                    myapp.Rparams.uants.length,
                                    (short) myapp.Rparams.readtime, tagcnt);
                        }
                        readCount = tagcnt[0];
                        if (er == Reader.READER_ERR.MT_OK_ERR) {
                            if (tagcnt[0] > 0) {

                                playSound();
                                Comm.tag = new String[tagcnt[0]];
                                for (int i = 0; i < tagcnt[0]; i++) {
                                    tfs = myapp.Mreader.new TAGINFO();
                                    if (myapp.nostop)
                                        er = myapp.Mreader.AsyncGetNextTag(tfs);
                                    else
                                        er = myapp.Mreader.GetNextTag(tfs);
                                    if (er == Reader.READER_ERR.MT_HARDWARE_ALERT_ERR_BY_TOO_MANY_RESET) {
                                        rt = MT_HARDWARE_TOO_MANY_RESET;
                                        Comm.isrun = false;
                                    }
                                    if (er == Reader.READER_ERR.MT_OK_ERR) {
                                        String strE = Reader.bytes_Hexstr(tfs.EpcId);
                                        Comm.tag[i] = strE;
                                        if (!Devaddrs.containsKey(Comm.tag[i]))
                                            Devaddrs.put(Comm.tag[i], tfs);
                                        else {
                                            tf = Devaddrs.get(Comm.tag[i]);
                                            tf.ReadCnt += tfs.ReadCnt;
                                            tf.RSSI = tfs.RSSI;
                                            tf.Frequency = tfs.Frequency;
                                        }
                                    } else
                                        break;
                                }
                            } else {
                                rt = MT_HARDWARE_FAILS;
                                strReadR = "0";
                            }
                        } else
                            strReadR = "Fail_Err ";
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(tagUHF1, e.getMessage());
                        strReadR = e.getMessage();
                        rt = MT_HARDWARE_FAILS;
                    }

                    switch (rt) {
                        case MT_HARDWARE_TOO_MANY_RESET:
                            UHF1handler.removeCallbacks(runnable_cycleRead);
                            strReadR = "Reader is stop " + String.valueOf(er.value()) + er.toString();
                            break;
                        case MT_HARDWARE_SUCCEED:
                            strReadR = "SUCCEED";
                            break;
                        case MT_HARDWARE_FAILS:
                            strReadR = strReadR + " " + er.toString();
                            break;
                    }
                    if (Comm.tag == null) {
                        Comm.tag = new String[0];
                    }
                    int cll = Devaddrs.size();
                    if (cll < 0)
                        cll++;

                    UHF1handler.postDelayed(this, Comm.rfidSleep);
                }
            }
        }
    };
    //endregion


    public static Comm.runType runCycleRead() {
        try {
            int[] tagcnt = new int[1];
            tagcnt[0] = 0;
            if (myapp.nostop) {
                er = myapp.Mreader.AsyncGetTagCount(tagcnt);
            } else {
                er = myapp.Mreader.TagInventory_Raw(myapp.Rparams.uants,
                        myapp.Rparams.uants.length,
                        (short) myapp.Rparams.readtime, tagcnt);
            }
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                if (tagcnt[0] > 0) {
                    playSound();
                    Comm.tag = new String[tagcnt[0]];
                    for (int i = 0; i < tagcnt[0]; i++) {
                        tfs = myapp.Mreader.new TAGINFO();
                        if (myapp.nostop)
                            er = myapp.Mreader.AsyncGetNextTag(tfs);
                        else
                            er = myapp.Mreader.GetNextTag(tfs);
                        if (er == Reader.READER_ERR.MT_HARDWARE_ALERT_ERR_BY_TOO_MANY_RESET) {
                            return MT_HARDWARE_TOO_MANY_RESET;
                        }
                        if (er == Reader.READER_ERR.MT_OK_ERR) {
                            String strE = Reader.bytes_Hexstr(tfs.EpcId);
                            Comm.tag[i] = strE;
                            if (!Devaddrs.containsKey(Comm.tag[i]))
                                Devaddrs.put(Comm.tag[i], tfs);
                            else {
                                tf = Devaddrs.get(Comm.tag[i]);
                                tf.ReadCnt += tfs.ReadCnt;
                                tf.RSSI = tfs.RSSI;
                                tf.Frequency = tfs.Frequency;
                            }
                        } else
                            break;
                    }
                } else
                    return MT_HARDWARE_FAILS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tagUHF1, e.getMessage());
            return MT_HARDWARE_FAILS;
        }
        return MT_HARDWARE_SUCCEED;
    }

    public static Comm.runType runAloneRead() {
        try {
            er = myapp.Mreader.TagInventory_Raw(
                    myapp.Rparams.uants, myapp.Rparams.uants.length,
                    (short) myapp.Rparams.readtime, tagcnt);
            Log.d(tagUHF1,
                    "runAloneRead " + er.toString() + " cnt:"
                            + String.valueOf(tagcnt[0]));

            if (er == Reader.READER_ERR.MT_OK_ERR && tagcnt[0] > 0) {
                soundPool.play(1, 1, 1, 0, 0, 1);
                Comm.tag = new String[tagcnt[0]];

                for (int i = 0; i < tagcnt[0]; i++) {
                    Reader.TAGINFO tfs = myapp.Mreader.new TAGINFO();
                    er = myapp.Mreader.GetNextTag(tfs);
                    // runCycleRead = GetNexttag(tfs);
                    Log.d(tagUHF1, "get tag index:" + String.valueOf(i) + " er:" + er.toString());

                    if (er == Reader.READER_ERR.MT_HARDWARE_ALERT_ERR_BY_TOO_MANY_RESET) {
                        return MT_HARDWARE_TOO_MANY_RESET;
                    } else if (er == Reader.READER_ERR.MT_OK_ERR) {
                        Comm.tag[i] = Reader.bytes_Hexstr(tfs.EpcId);

                        if (!Devaddrs.containsKey(Comm.tag[i]))
                            Devaddrs.put(Comm.tag[i], tfs);
                        else {
                            tf = Devaddrs.get(Comm.tag[i]);
                            tf.ReadCnt += tfs.ReadCnt;
                            tf.RSSI = tfs.RSSI;
                            tf.Frequency = tfs.Frequency;
                        }
                    } else
                        return MT_HARDWARE_FAILS;
                }
            } else {
                return MT_HARDWARE_FAILS;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tagUHF1, e.getMessage());
            return MT_HARDWARE_FAILS;
        }
        return MT_HARDWARE_SUCCEED;
    }

    private static Reader.TAGINFO[] ParseTags(byte[] data, int len) {
        List<Reader.TAGINFO> Lti = new ArrayList<Reader.TAGINFO>();
        for (int i = 0; i < len; i++) {
            LB.add(data[i]);
        }
        int pos;
        try {
            do {
                int st_ff = LB.indexOf((Byte) (byte) 0xff);
                if (st_ff == -1)
                    break;

                if (st_ff - 1 > 0)
                    LB_del(0, st_ff);

                if (LB.get(1) + 7 > LB.size())
                    break;

                Reader.TAGINFO tf = myapp.Mreader.new TAGINFO();
                tf.AntennaID = 1;
                pos = 0;

                pos += 7;
                int epclen = LB.get(pos) - 4;
                tf.Epclen = (short) epclen;

                pos += 1;
                tf.PC[0] = LB.get(pos);
                pos += 1;
                tf.PC[1] = LB.get(pos);
                pos += 1;

                for (int i = pos; i < pos + epclen; i++)
                    tf.EpcId[i - pos] = LB.get(i);

                pos += epclen;

                tf.CRC[0] = LB.get(pos);
                pos += 1;
                tf.CRC[1] = LB.get(pos);

                pos += 2;// crc
                LB_del(0, pos + 1);
                Lti.add(tf);
            } while (true);
        } catch (Exception ex) {
            Log.d(tagUHF1, ex.getMessage());
        }
        return Lti.toArray(new Reader.TAGINFO[Lti.size()]);

    }

    private static boolean LB_del(int st, int ed) {
        boolean bl = true;
        for (int i = st; i < ed; i++) {
            if (LB.size() > 0)
                LB.remove(0);
            else {
                bl = false;
                break;
            }
        }
        return bl;
    }
    //endregion


    public static String uhf1readOp(int ant, int tagBank, String opCount, String startAdd, int datatype) {
        String val = "";
        myapp.Rparams.opant = ant + 1;
        byte[] rdata = new byte[Integer.valueOf(opCount) * 2];
        byte[] rpaswd = new byte[4];
        myapp.Rparams.password = strPwd;
        if (!myapp.Rparams.password.equals("")) {
            myapp.Mreader.Str2Hex(myapp.Rparams.password, myapp.Rparams.password.length(), rpaswd);
        }
        do {
            er = myapp.Mreader.GetTagData(myapp.Rparams.opant,
                    (char) tagBank,
                    Integer.valueOf(startAdd),
                    Integer.valueOf(opCount),
                    rdata, rpaswd, (short) myapp.Rparams.optime);

            trycount--;
            if (trycount < 1)
                break;
        } while (er != Reader.READER_ERR.MT_OK_ERR);

        if (er == Reader.READER_ERR.MT_OK_ERR) {
            char[] out = null;
            if (datatype == 0) {
                out = new char[rdata.length * 2];
                myapp.Mreader.Hex2Str(rdata, rdata.length, out);
                val = String.valueOf(out);
            } else if (datatype == 1) {
                out = new char[rdata.length];
                for (int i = 0; i < rdata.length; i++)
                    out[i] = (char) rdata[i];
                val = String.valueOf(out);
            } else if (datatype == 2) {
                try {
                    val = new String(rdata, "gbk");
                } catch (UnsupportedEncodingException e) {
                    Log.d(tagUHF1, "readOP err:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return val;
    }

    public static Boolean uhf1writeOp(int datatype, int ant, int tagBank, String opCount, String startAdd, String data) {
        byte[] dataW = null;
        myapp.Rparams.opant = ant + 1;
        er = null;

        if (datatype == 0) {
            dataW = new byte[data.length() / 2];
            myapp.Mreader.Str2Hex(data,
                    data.length(), dataW);
        } else if (datatype == 1) {
            String ascstr = data;
            if (ascstr.length() % 2 != 0)
                ascstr += "0";
            dataW = ascstr.getBytes();

        } else if (datatype == 2) {
            try {
                String ascstr = data;
                dataW = ascstr.getBytes("gbk");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        myapp.Rparams.password = strPwd;
        byte[] rpaswd = new byte[4];
        if (!myapp.Rparams.password.equals("")) {
            myapp.Mreader.Str2Hex(myapp.Rparams.password, myapp.Rparams.password.length(), rpaswd);
        }

        do {
            if (UHF001.isWriteEpc) {
                er = myapp.Mreader.WriteTagEpcEx(myapp.Rparams.opant,
                        dataW, dataW.length, rpaswd,
                        (short) myapp.Rparams.optime);
                UHF001.isWriteEpc=false;
            }
            else
                er = myapp.Mreader.WriteTagData(myapp.Rparams.opant,
                        (char) tagBank,
                        Integer.valueOf(startAdd),
                        dataW, dataW.length, rpaswd,
                        (short) myapp.Rparams.optime);

            trycount--;
            if (trycount < 1)
                break;
        } while (er != Reader.READER_ERR.MT_OK_ERR);

        if (er == Reader.READER_ERR.MT_OK_ERR)
            return true;
        else
            return false;
    }

    public static boolean uhf1lockOp(int ant, int lbank, int ltype) {
        Reader.Lock_Obj lobj = null;
        Reader.Lock_Type ltyp = null;

        if (lbank == 0) {
            lobj = Reader.Lock_Obj.LOCK_OBJECT_ACCESS_PASSWD;
            if (ltype == 0)
                ltyp = Reader.Lock_Type.ACCESS_PASSWD_UNLOCK;
            else if (ltype == 1)
                ltyp = Reader.Lock_Type.ACCESS_PASSWD_LOCK;
            else if (ltype == 2)
                ltyp = Reader.Lock_Type.ACCESS_PASSWD_PERM_LOCK;

        } else if (lbank == 1) {
            lobj = Reader.Lock_Obj.LOCK_OBJECT_KILL_PASSWORD;
            if (ltype == 0)
                ltyp = Reader.Lock_Type.KILL_PASSWORD_UNLOCK;
            else if (ltype == 1)
                ltyp = Reader.Lock_Type.KILL_PASSWORD_LOCK;
            else if (ltype == 2)
                ltyp = Reader.Lock_Type.KILL_PASSWORD_PERM_LOCK;
        } else if (lbank == 2) {
            lobj = Reader.Lock_Obj.LOCK_OBJECT_BANK1;
            if (ltype == 0)
                ltyp = Reader.Lock_Type.BANK1_UNLOCK;
            else if (ltype == 1)
                ltyp = Reader.Lock_Type.BANK1_LOCK;
            else if (ltype == 2)
                ltyp = Reader.Lock_Type.BANK1_PERM_LOCK;
        } else if (lbank == 3) {
            lobj = Reader.Lock_Obj.LOCK_OBJECT_BANK2;
            if (ltype == 0)
                ltyp = Reader.Lock_Type.BANK2_UNLOCK;
            else if (ltype == 1)
                ltyp = Reader.Lock_Type.BANK2_LOCK;
            else if (ltype == 2)
                ltyp = Reader.Lock_Type.BANK2_PERM_LOCK;
        } else if (lbank == 4) {
            lobj = Reader.Lock_Obj.LOCK_OBJECT_BANK3;
            if (ltype == 0)
                ltyp = Reader.Lock_Type.BANK3_UNLOCK;
            else if (ltype == 1)
                ltyp = Reader.Lock_Type.BANK3_LOCK;
            else if (ltype == 2)
                ltyp = Reader.Lock_Type.BANK3_PERM_LOCK;
        }

        myapp.Rparams.password = strPwd;
        byte[] rpaswd = new byte[4];
        if (!myapp.Rparams.password.equals("")) {
            myapp.Mreader.Str2Hex(myapp.Rparams.password, myapp.Rparams.password.length(), rpaswd);
        }

        er = myapp.Mreader.LockTag(myapp.Rparams.opant,
                (byte) lobj.value(), (short) ltyp.value(),
                rpaswd, (short) myapp.Rparams.optime);
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            return true;
        } else {
            return false;
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int getAntCheck() {
        int checkIndex = -1;
        int[] val2 = new int[]{-1};
        er = myapp.Mreader.ParamGet(
                Reader.Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT, val2);

        if (er == Reader.READER_ERR.MT_OK_ERR)
            checkIndex = val2[0];
        return checkIndex;
    }

    public static boolean setAntCheck(int antCheckEnable) {
        if (antCheckEnable == 0)
            er = myapp.Mreader.ParamSet(
                    Reader.Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT,
                    new int[]{0});
        else
            er = myapp.Mreader.ParamSet(
                    Reader.Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT,
                    new int[]{1});
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            myapp.Rparams.checkant = antCheckEnable;
            return true;
        } else
            return false;

    }


    public static Reader.AntPowerConf getPower() {
        er = myapp.Mreader.ParamGet(
                Reader.Mtr_Param.MTR_PARAM_RF_ANTPOWER, getAntPowerConf);
        return getAntPowerConf;
    }

    public static boolean setPower(int[] rpow, int[] wpow) {
        er = myapp.Mreader.ParamSet(
                Reader.Mtr_Param.MTR_PARAM_RF_ANTPOWER, setAntPowerConf);
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            myapp.Rparams.rpow = rpow;
            myapp.Rparams.wpow = wpow;
            return true;
        } else
            return false;
    }

    public static Reader.Region_Conf[] getReg() {
        er = myapp.Mreader.ParamGet(
                Reader.Mtr_Param.MTR_PARAM_FREQUENCY_REGION, getRegion_Conf);
        return getRegion_Conf;
    }

    public static boolean setReg(Reader.Region_Conf setRegion_Conf) {
        er = myapp.Mreader.ParamSet(
                Reader.Mtr_Param.MTR_PARAM_FREQUENCY_REGION, setRegion_Conf);
        if (er == Reader.READER_ERR.MT_OK_ERR)
            return true;
        else
            return false;
    }

    public static Reader.HoptableData_ST getFre() {
        er = myapp.Mreader.ParamGet(
                Reader.Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, getFreHoptableData);
        return getFreHoptableData;
    }

    public static String[] getUhf1Fre(Reader.HoptableData_ST getFreHoptableData) {
        int[] tablefre;
        int dataL = getFreHoptableData.lenhtb;
        String[] ssf = null;
        if (getFreHoptableData != null) {
            tablefre = Comm.Sort(getFreHoptableData.htb, dataL);
            ssf = new String[getFreHoptableData.lenhtb];
            for (int i = 0; i < dataL; i++) {
                ssf[i] = String.valueOf(tablefre[i]);
            }
        }
        return ssf;
    }

    public static boolean setFre(Reader.HoptableData_ST setFreHoptableData) {
        er = myapp.Mreader.ParamSet(
                Reader.Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, setFreHoptableData);
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            myapp.Rparams.frecys = setFreHoptableData.htb;
            myapp.Rparams.frelen = setFreHoptableData.lenhtb;
            return true;
        } else
            return false;
    }

    public static int[] getGen2Session(int[] val) {
        er = myapp.Mreader.ParamGet(
                Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, val);
        return val;
    }

    public static boolean setGen2Session(int val) {
        er = myapp.Mreader.ParamSet(
                Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, val);
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            myapp.Rparams.session = val;
            return true;
        } else
            return false;
    }

    public static int[] getgen2q(int[] val) {
        er = myapp.Mreader.ParamGet(
                Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_Q, val);
        return val;

    }

    public static boolean setgen2q(int val) {
        er = myapp.Mreader.ParamSet(
                Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_Q, val);
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            myapp.Rparams.qv = val;
            return true;
        } else
            return false;
    }


}

