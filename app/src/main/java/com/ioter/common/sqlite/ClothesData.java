package com.ioter.common.sqlite;

import java.io.Serializable;

public class ClothesData implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -3055030985087768871L;
    
    public String mEpc; // EPC编号
    public String mTotalCount; //
    public String mStyleNum; // 款号
    public String mName; // 品名
    public String mModel; // 规格型号
    public String mColour; // 颜色
    public String mSize; // 尺寸
    public String mPrice; // 单价
    public String mDesign; // 设计理念
    public boolean mIsCheck; // 已盘点

}
