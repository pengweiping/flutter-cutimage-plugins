package com.example.fluttercutimage;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created Date: 2020/7/14
 * Description:
 */
public class CutTaskContext {
    public  final static  String TAG = "CutTask";
    //切割的原始图片
    public byte[] img;
    //子切割的原始图片对应的通道ID
    public String chanid;
    //通道的分屏模式 2/4/6/9
    public int vmode;
    public int hmode;
    public int index;
    //构建当前切割事务的时间戳
    public  long timestamp;
    //切割类型:0:预监图片切割为独立通道，1：根据索引切割子通道,2:切割特定区域
    public  int type;
    //独立通道，index对应通道的映射关系,
    public HashMap<String,String> chanSet;
    //要切割区域的索引集合
    public ArrayList<Object> indexSet;

    //4个主通道要切割主通道区域的切割集合
    public HashMap<String,HashMap<String,String>>  mainCutArea;
    //切割特定区域
    public Rect rect;
    //切割特定区域分屏模式
    public int srcVmode;
    public int srcHmode;
    public int srcindex;
    //是为切割原生图片的部分区域：1：表示为部分区域，0：全部区域
    public int srcflag;

    //物理通道子通道，参考原始流坐标，
    int x;
    int y;
    int w;
    int h;
    //物理通道，参考原始流宽，高
    int hsize;
    int vsize;

    public CutTaskContext() {
        rect = new Rect();
        chanSet = new HashMap<String,String>();
        indexSet = new ArrayList<Object>();
    }

    @Override
    public String toString() {
        return "CutTaskContext{" +
                "chanid='" + chanid + '\'' +
                ", vmode=" + vmode +
                ", hmode=" + hmode +
                ", index=" + index +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", chanSet=" + chanSet +
                ", indexSet=" + indexSet +
                ", rect=" + rect +
                ", srcVmode=" + srcVmode +
                ", srcHmode=" + srcHmode +
                ", srcindex=" + srcindex +
                ", srcflag=" + srcflag +
                ", x=" + x +
                ", y=" + y +
                ", w=" + w +
                ", h=" + h +
                ", hsize=" + hsize +
                ", vsize=" + vsize +
                '}';
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        img = null;
        rect = null;
        //chanSet.clear();
        chanSet = null;
        //indexSet.clear();
        indexSet = null;
       // Log.e(TAG,"CutTaskContext--->finalize==================>:");
    }
}
