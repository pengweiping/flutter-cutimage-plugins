package com.example.fluttercutimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.flutter.plugin.common.EventChannel;

/**
 * Created Date: 2020/7/14
 * Description:
 */
public class ImageCuter {

    public  final static  String TAG = "ImageCuter";


    private QueuingEventSink eventSink;
    private ICutTaskContext ctx;

    public ImageCuter(ICutTaskContext ctx, QueuingEventSink eventSink){
        this.ctx = ctx;
        this.eventSink = eventSink;
    }


    public void doImageCut(CutTaskContext ctx){
        Bitmap allImage = BitmapFactory.decodeByteArray(ctx.img, 0, ctx.img.length);
        //Log.e("XIAOPING","doImageCut======================================:"+ctx.type);

        if (allImage != null) {
            if (ctx.type == 0){
                if(ctx.srcflag == 1){
                    Rect src =  getRect(ctx.srcVmode,ctx.srcHmode,ctx.srcindex,allImage.getHeight(),allImage.getWidth());
                    Bitmap srcImage =  cutAreaImage(allImage, src);
                    if(srcImage != null){
                        cutMainImage(ctx.chanSet,ctx.vmode,ctx.hmode,ctx.index,srcImage);
                        //srcImage.recycle();
                        srcImage = null;
                    }
                }else{
                    cutMainImageEx(ctx.chanSet,ctx.mainCutArea,ctx.vmode,ctx.hmode,ctx.index,allImage);
                }
            }else if(ctx.type == 1) {
                cutSubImage(ctx.chanid,ctx.vmode,ctx.hmode,ctx.index,allImage);
            }else if(ctx.type == 2) {
                Bitmap img =  cutAreaImage(allImage,ctx.rect);
                if(img != null){
                    updateImage(ctx.chanid,img);
                    img = null;
                }
            }else if(ctx.type == 4) {
               // Log.e(TAG,"doImageCut======================================>4: ctx:"+ctx.toString());
                //x=0, y=0, w=1920, h=1080, hsize=3840, vsize=2160
                if(ctx.hsize >0 &&ctx.vsize>0){
                    Rect desRC = doCoordinateMap(ctx.hsize,ctx.vsize,ctx.x,ctx.y,ctx.w,ctx.h,allImage.getWidth(),allImage.getHeight());
                    //Log.e(TAG,"doImageCut======================================>4: desRC:"+desRC.toString());
                    Bitmap img =  cutAreaImage(allImage, desRC);
                    if(img != null){
                        updateImage(ctx.chanid,img);
                        img = null;
                    }
                }


            }else if(ctx.type == 5) {
                if(ctx.hsize >0 &&ctx.vsize>0){
                    Rect src =  getRect(2,2,ctx.srcindex,allImage.getHeight(),allImage.getWidth());
                    Rect desRC = doCoordinateMap(ctx.hsize,ctx.vsize,ctx.x,ctx.y,ctx.w,ctx.h,src.width(),src.height());
                    //Log.e(TAG,"doImageCut============================================>5: src:"+src.toString()+",desRC:"+desRC.toString());
                    Rect relDesRC = new Rect();
                    relDesRC.left = src.left + desRC.left;
                    relDesRC.top = src.top + desRC.top;
                    relDesRC.right = src.left + desRC.right;
                    relDesRC.bottom = src.top +desRC.bottom;
                    Bitmap img =  cutAreaImage(allImage, relDesRC);
                    if(img != null){
                        updateImage(ctx.chanid,img);
                        img = null;
                    }

                }

            }
        }
        allImage.recycle();
        allImage = null;
    }

    /**
     * @param srcWidth
     * @param srcHeight
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public Rect  doCoordinateMap(int srcWidth,int srcHeight,int x,int y,int w,int h,int desWidth,int desHeight){
        Rect rc = new Rect();
        float sclaleX = (float) desWidth/ (float) srcWidth;
        float sclaleY =(float) desHeight/ (float) srcHeight;
       // Log.e(TAG,"doImageCut======================================>4: sclaleX:"+sclaleX+",sclaleY:"+sclaleY);
        rc.left =(int) (x*sclaleX);
        rc.top =(int) (y*sclaleY);
        rc.right = rc.left + (int)(w*sclaleX);
        rc.bottom = rc.top + (int)(h*sclaleY);
        return  rc;
    }

    /***
     *
     * @param chanid:独立的通道前缀
     * @param vmode
     * @param hmode
     * @param index:1....开始，0表示主通道，自己本身
     * @param fatherImage
     */
    private void cutSubImage(String chanid,int vmode, int hmode, int index, Bitmap fatherImage) {
        long t1 = System.currentTimeMillis();
        int h = fatherImage.getHeight();
        int w= fatherImage.getWidth();
        Rect subRect = getRect(vmode, hmode, index,h,w);
        if (subRect == null) return;
        Bitmap childBitmap =  cutAreaImage(fatherImage, subRect);
        if(childBitmap != null){
            String childChanID = chanid +"_"+ (index);
            //String childChanID = String.valueOf(index-1);
            updateImage(childChanID,childBitmap);
            childBitmap.recycle();
            childBitmap = null;
            long t2 = System.currentTimeMillis();
            Log.e(TAG,"cutSubImage==================>: cust:"+(t2-t1));
        }
    }

    /**
     *
     * @param chanids:独立的主通道集合(key:1....开始)
     * @param vmode
     * @param hmode
     * @param index:1....开始，1表示第一个切割子区域
     * @param fatherImage
     */
    private void cutMainImage(HashMap<String,String> chanids,int vmode, int hmode, int index, Bitmap fatherImage){
        long t1 = System.currentTimeMillis();
        String mainChanID = chanids.get(String.valueOf(index));
        Log.e(TAG,"cutMainImage================================================>"+index+","+mainChanID);
        int h = fatherImage.getHeight();
        int w = fatherImage.getWidth();
        Rect subRect = getRect(vmode, hmode, index, h,w);
        if (subRect == null) return;
        Bitmap childBitmap =  cutAreaImage(fatherImage, subRect);
        if(childBitmap != null){
            //String mainChanID = chanid +"_0";
            updateImage(mainChanID,childBitmap);
            childBitmap.recycle();
            childBitmap = null;
            long t2 = System.currentTimeMillis();
            Log.e(TAG,"cutMainImage==================>: cust:"+(t2-t1));
            //chanids = null;
        }
        fatherImage.recycle();
        fatherImage = null;

    }


    private void cutMainImageEx(HashMap<String,String> chanids,HashMap<String,HashMap<String,String>>  mainCutArea,int vmode, int hmode, int index, Bitmap fatherImage){
        long t1 = System.currentTimeMillis();
        if(mainCutArea != null){
            HashMap<String,String> cutArea = mainCutArea.get(String.valueOf(index));
            ///不包含主通道切割参数，默认全部区域
            if(cutArea == null) {
                String mainChanID = chanids.get(String.valueOf(index));
                Log.e(TAG,"cutMainImageEx================================================>"+index+","+mainChanID);
                int h = fatherImage.getHeight();
                int w = fatherImage.getWidth();
                Rect subRect = getRect(vmode, hmode, index, h,w);
                if (subRect == null) return;
                Bitmap childBitmap =  cutAreaImage(fatherImage, subRect);
                if(childBitmap != null){
                    // String mainChanID = chanid +"_0";
                    updateImage(mainChanID,childBitmap);
                    childBitmap.recycle();
                    childBitmap = null;
                    long t2 = System.currentTimeMillis();
                    Log.e(TAG,"cutMainImageEx==================>: cust:"+(t2-t1));
                    // chanids = null;
                }
                fatherImage.recycle();
                fatherImage = null;
            } else {
                try {
                    Log.e(TAG,"cutMainImageEx============================================>4: cutArea:"+cutArea.toString()+",index:"+index);
                    int x = Integer.parseInt(cutArea.get("x"));
                    int y = Integer.parseInt(cutArea.get("y"));
                    int w = Integer.parseInt(cutArea.get("w"));
                    int h = Integer.parseInt(cutArea.get("h"));

                    int hsize = Integer.parseInt(cutArea.get("hsize"));
                    int vsize = Integer.parseInt(cutArea.get("vsize"));

                    String mainChanID = chanids.get(String.valueOf(index));

                    Rect src =  getRect(2,2,index,fatherImage.getHeight(),fatherImage.getWidth());
                    Rect desRC = doCoordinateMap(hsize,vsize,x,y,w,h,src.width(),src.height());
                    Log.e(TAG,"cutMainImageEx============================================>5: src:"+src.toString()+",desRC:"+desRC.toString()+",index:"+index);
                    Rect relDesRC = new Rect();
                    relDesRC.left = src.left + desRC.left;
                    relDesRC.top = src.top + desRC.top;
                    relDesRC.right = src.left + desRC.right;
                    relDesRC.bottom = src.top +desRC.bottom;
                    Bitmap img =  cutAreaImage(fatherImage, relDesRC);
                    if(img != null){
                        Log.e(TAG,"cutMainImageEx============================================>6: relDesRC:"+relDesRC.toString()+",mainChanID:"+mainChanID);
                        updateImage(mainChanID,img);
                        img = null;
                    }
                }catch (Exception e) {
                    fatherImage.recycle();
                    fatherImage = null;
                }
            }
        }else{
            String mainChanID = chanids.get(String.valueOf(index));
            Log.e(TAG,"cutMainImageEx================================================>"+index+","+mainChanID);
            int h = fatherImage.getHeight();
            int w = fatherImage.getWidth();
            Rect subRect = getRect(vmode, hmode, index, h,w);
            if (subRect == null) return;
            Bitmap childBitmap =  cutAreaImage(fatherImage, subRect);
            if(childBitmap != null){
                // String mainChanID = chanid +"_0";
                updateImage(mainChanID,childBitmap);
                childBitmap.recycle();
                childBitmap = null;
                long t2 = System.currentTimeMillis();
                Log.e(TAG,"cutMainImageEx==================>: cust:"+(t2-t1));
                // chanids = null;
            }
            fatherImage.recycle();
            fatherImage = null;
        }
    }




    private Rect getRect(int vmode, int hmode, int index,int h,int w) {
        if (hmode == 0 || vmode == 0 || h == 0 ||w ==0) {
            return null;
        }

        int rows = (int) ((index - 1) / hmode);
        int cows = (int) ((index - 1) % hmode);
        if (hmode == 1) {
            cows = 0;
        }

        int rowCell = ((int) (h / (vmode * 2))) * 2;
        int colCell = ((int) (w / (hmode * 2))) * 2;
        Rect subRect = new Rect(cows * colCell, rows * rowCell, (cows + 1) * colCell, (rows + 1) * rowCell);
        return subRect;
    }



    private Bitmap cutAreaImage(Bitmap superImage, Rect rect) {
        Log.e(TAG, "cutAreaImage==================>:11111:" + rect.toString()+",image:w:"+superImage.getWidth()+",image:h:"+superImage.getHeight());
        int w = rect.right - rect.left;
        int h = rect.bottom - rect.top;
        /// 非法切割区域
        if (w == 0 || h == 0) {
            Bitmap bitmapClipBitmap = Bitmap.createBitmap(superImage, 0, 0, superImage.getWidth(),
                    superImage.getHeight());
            return bitmapClipBitmap;
        }
        if ((rect.left + w) <= superImage.getWidth() && (rect.top + h) <= superImage.getHeight()) {
            Log.e(TAG, "cutAreaImage==================>:11111:TAG1");
            ///正常切割区域
            Bitmap bitmapClipBitmap = Bitmap.createBitmap(superImage, rect.left, rect.top, w,
                    h);
            return bitmapClipBitmap;
        } else {
            Log.e(TAG, "cutAreaImage==================>:11111:TAG2");
            ///子通道，切割区域越界，直接显示主通道
            {

                Bitmap bitmapClipBitmap = Bitmap.createBitmap(superImage, 0, 0, superImage.getWidth(),
                        superImage.getHeight());
                return bitmapClipBitmap;
                /*
                // 切割可切割的区域
                if ((rect.left + w) > superImage.getWidth() || (rect.top + h) > superImage.getHeight()) {
                    w = superImage.getWidth() - rect.left;
                    h = superImage.getHeight() - rect.top;
                Bitmap bitmapClipPartBitmap = Bitmap.createBitmap(superImage, rect.left, rect.top, w, h);
                return  bitmapClipPartBitmap;

                }
              */
            }

            /*
            if(w <=0 || h<=0){
                return  null;
            }
            Bitmap bitmapClipBitmap = Bitmap.createBitmap(superImage, rect.left, rect.top, w, h);
            return bitmapClipBitmap;
            */
        }
    }


    private  void  updateImage(String _key,Bitmap child){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        child.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imgdatas = baos.toByteArray();
        Log.e(TAG,"updateImage==================>:");
        HashMap<String, Object> event = new HashMap<String, Object>();
        event.put("event", "update");
        event.put("Key", _key);
        event.put("img",imgdatas);
        eventSink.success(event);
        event = null;
        child.recycle();
        child = null;
        imgdatas = null;
        baos = null;

    }
}
