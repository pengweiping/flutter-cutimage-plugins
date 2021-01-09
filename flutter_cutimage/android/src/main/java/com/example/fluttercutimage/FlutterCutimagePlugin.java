package com.example.fluttercutimage;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.EventChannel;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * FlutterCutimagePlugin
 */
public class FlutterCutimagePlugin implements MethodCallHandler {
    public final static String defaultImagePath = "/sdcard/logo.png";
    public final static String TAG = "FlutterCutimagePlugin";


    private final HashMap<String, CutImage> cutimageMap;

    private final Registrar registrar;
    private static int MAXLOSEFRAME = 4;

    public FlutterCutimagePlugin(HashMap<String, CutImage> cutimageMap, Registrar registrar) {
        this.cutimageMap = cutimageMap;
        this.registrar = registrar;
    }

    class CutImage implements ICutTaskContext {

        private FileUtils fileutils = null;
        private byte[] imagedata = null;
        private EventChannel eventChannel = null;
        private String key;

        private QueuingEventSink eventSink = null;
        private ImageCuter cutter = null;
        private CutTaskExecutor executor = null;

        private CutTaskQueue tasks = null;

        //当前设备通道布局关系
        private ArrayList<Channel> curChanList;
        HashMap<String, Channel> chanelMap;
        private ReentrantReadWriteLock RWlock = new ReentrantReadWriteLock();

        private int frameIndex;


        public CutImage(String key, EventChannel eventChannel) {
      /*
      fileutils = new FileUtils();
      try {
        System.out.print("StartCut==================>:"+key);
        Log.e(TAG,"StartCut==================>:"+key);
        imagedata = fileutils.getContent(key);

      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        Log.e(TAG,"StartCut==================>:"+e.toString());
      }
      */
            this.eventSink = new QueuingEventSink();
            this.key = key;
            this.eventChannel = eventChannel;
            frameIndex = 0;
            eventChannel.setStreamHandler(
                    new EventChannel.StreamHandler() {
                        @Override
                        public void onListen(Object o, EventChannel.EventSink sink) {
                            Log.e(TAG, " eventSink.setDelegate==================>:");
                            eventSink.setDelegate(sink);
                        }

                        @Override
                        public void onCancel(Object o) {
                            eventSink.setDelegate(null);
                        }
                    });

        }

        public void Init() {
            HashMap<String, Object> event = new HashMap<String, Object>();
            Log.e(TAG, " CutImage.Init==================>:Start");
            event.put("event", "initialized");
            eventSink.success(event);
            tasks = new CutTaskQueue(60, 2000);
            cutter = new ImageCuter(this, eventSink);
            executor = new CutTaskExecutor(5, 10, 100, tasks, cutter);
            executor.Start();
            Log.e(TAG, " CutImage.Init==================>:End");
        }

        public void UnInit() {
            Log.e(TAG, " CutImage.UnInit==================>:Start");
            HashMap<String, Object> event = new HashMap<String, Object>();
            event.put("event", "uninitialized");
            eventSink.success(event);
            executor.Stop();
            tasks.removeAll();
            eventSink.endOfStream();
            Log.e(TAG, " CutImage.UnInit==================>:End");
        }

        public void StartCut(int vmode, int hmode) {
            if (imagedata != null) {
                Bitmap allImage = BitmapUtils.Bytes2Bimap(imagedata);
                if (allImage != null) {
                    for (int index = 0; index < vmode * hmode; index++) {
                        long t1 = System.currentTimeMillis();
                        Bitmap chil = getNYXPreviewImage(vmode, hmode, index + 1, allImage);
                        long t2 = System.currentTimeMillis();
                        if (chil != null) {
                            Log.e(TAG, "StartCut=====================================================================>Cut Cust:" + (t2 - t1));
                            long t3 = System.currentTimeMillis();
                            BitmapUtils.saveBitmap(chil, Integer.toString(index));
                            long t4 = System.currentTimeMillis();
                            Log.e(TAG, "StartCut======================================================================>Save Cust:" + (t4 - t3));
              /*
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              chil.compress(Bitmap.CompressFormat.JPEG, 100, baos);
              byte[] datas = baos.toByteArray();
              */
                        }
                    }
                }
            }
        }


        public Bitmap getNYXPreviewImage(int vmode, int hmode, int index, Bitmap fatherImage) {
            if (hmode == 0 || vmode == 0 || fatherImage == null) {
                return null;
            }
            int rows = (int) ((index - 1) / hmode);
            int cows = (int) ((index - 1) % hmode);
            if (hmode == 1) {
                cows = 0;
            }

            int rowCell = ((int) (fatherImage.getHeight() / (vmode * 2))) * 2;
            int colCell = ((int) (fatherImage.getWidth() / (hmode * 2))) * 2;
            Rect subRect = new Rect(cows * colCell, rows * rowCell, (cows + 1) * colCell, (rows + 1) * rowCell);
            return getSubImageFromImage(fatherImage, subRect);
        }

        private Bitmap getCMProPreviewImage(int left, int top, int right, int bottom, Bitmap fatherImage) {
            Rect subRect = new Rect(left, top, right, bottom);
            Log.e(TAG, "getCMProPreviewImage==================>subRect:" + subRect.toString());
            return getSubImageFromImage(fatherImage, subRect);
        }

        private Bitmap getSubImageFromImage(Bitmap superImage, Rect rect) {
            Log.e(TAG, "getSubImageFromImage==================>subRect:" + rect.toString());
            Bitmap bitmapClipBitmap = Bitmap.createBitmap(superImage, rect.left, rect.top, rect.right - rect.left,
                    rect.bottom - rect.top);
            return bitmapClipBitmap;
        }


        public void updateImage(String _key, byte[] imgdatas) {
            Log.e(TAG, "updateImage==================>:");
            HashMap<String, Object> event = new HashMap<String, Object>();
            event.put("event", "update");
            event.put("Key", _key);
            event.put("img", imgdatas);
            eventSink.success(event);
        }

        private boolean loseFrame(int activecnt) {
            if (activecnt > 40) {
                if ((frameIndex % (MAXLOSEFRAME + 3)) != 0) {
                    return true;
                }
            }
            if (activecnt > 30) {
                if ((frameIndex % (MAXLOSEFRAME + 2)) != 0) {
                    return true;
                }
            }
            if (activecnt > 20) {
                if ((frameIndex % (MAXLOSEFRAME + 1)) != 0) {
                    return true;
                }
            }
            if (activecnt > 15) {
                if ((frameIndex % MAXLOSEFRAME) != 0) {
                    return true;
                }
            }
            return false;
        }

        public void pushCutAction(CutTaskContext ctx) {
            //Log.e(TAG,"pushCutAction=====================>:"+tasks.size());
            //分割为子任务序列
            if (ctx.type == 0) {
                frameIndex++;
        /*
        if(loseFrame(ctx.indexSet.size())){
          ctx = null;
          return;
        }
        */
                if (ctx.indexSet.size() > 0) {
                    for (int i = 0; i < ctx.indexSet.size(); i++) {
                        CutTaskContext newCtx = new CutTaskContext();
                        newCtx.type = ctx.type;
                        newCtx.img = ctx.img;
                        newCtx.chanSet = ctx.chanSet;
                        newCtx.index = (int) ctx.indexSet.get(i);
                        newCtx.timestamp = ctx.timestamp;
                        newCtx.hmode = ctx.hmode;
                        newCtx.vmode = ctx.vmode;
                        newCtx.srcflag = ctx.srcflag;
                        newCtx.srcindex = ctx.srcindex;
                        newCtx.srcVmode = ctx.srcVmode;
                        newCtx.srcHmode = ctx.srcHmode;
                        ///主通道切割参数
                        newCtx.mainCutArea = ctx.mainCutArea;
                        tasks.enqueue(newCtx);
                    }
                }
                ctx = null;
            } else {
                tasks.enqueue(ctx);
            }

        }

        public void updateChanalArea(String mode, ArrayList<Channel> _chanList) {
            Log.e(TAG, " CutImage.updateChanalArea==================>mode:" + mode + ",chanList:" + _chanList.toString());
            RWlock.writeLock().lock();
            try {
                curChanList = _chanList;
                HashMap<String, Channel> _chanelMap = new HashMap<String, Channel>();
                for (Channel ch : curChanList) {
                    _chanelMap.put(ch.id, ch);
                }
                chanelMap = _chanelMap;
                if (mode == "1") {
                    eventSink.pause();
                } else if (mode == "0") {
                    eventSink.restart();
                }
            } finally {
                RWlock.writeLock().unlock();
            }

        }

        @Override
        public Channel getChanelArea(int chanid) {
            Channel chan = null;
            RWlock.readLock().lock();
            if (chanelMap != null) {
                chan = chanelMap.get(chanid);
            }
            RWlock.readLock().unlock();
            return chan;
        }


        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            Log.e(TAG, " CutImage.finalize==================>:" + key);
        }


    }


    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_cutimage");
        FlutterCutimagePlugin plugin = new FlutterCutimagePlugin(new HashMap<String, CutImage>(), registrar);
        channel.setMethodCallHandler(plugin);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else {
            switch (call.method) {
                case "create": {
                    String key = (String) call.argument("key");
                    CutImage cutimgs = cutimageMap.get(key);
                    if (cutimgs == null) {
                        Log.e(TAG, "create====================================================================================================>Start:" + key);
                        EventChannel _eventChannel = new EventChannel(registrar.messenger(), "rgb.com/FlutterCutimagePlugin/CutEvents" + key);
                        cutimgs = new CutImage(key, _eventChannel);
                        cutimgs.Init();
                        cutimageMap.put(key, cutimgs);
                        result.success(null);
                        Log.e(TAG, "create====================================================================================================>End:" + key);
                    }
                    break;
                }
                case "destroy": {
                    String key = (String) call.argument("key");
                    Log.e(TAG, "destroy====================================================================================================>:Start" + key);
                    CutImage cutimgs = cutimageMap.get(key);
                    cutimgs.UnInit();
                    cutimageMap.remove(key);
                    result.success(null);
                    Log.e(TAG, "destroy====================================================================================================>:End" + key);
                    break;
                }
                default: {
                    // _onMethodCall(call, result);
                    _onAsynMethodCall(call, result);
                    break;
                }
            }
        }
    }

    private void _onAsynMethodCall(MethodCall call, Result result) {
        String _key = (String) call.argument("key");
        CutImage ci = cutimageMap.get(_key);
        if (ci != null) {
            if (call.method.equals("doCutImage")) {
                int type = (int) call.argument("type");
                if (type == 1) {
                    byte[] imagedata = (byte[]) call.argument("data");
                    String chanStr = (String) call.argument("chanid");
                    String hmode1Str = (String) call.argument("hmode");
                    String vmode1Str = (String) call.argument("vmode");
                    int index = (int) call.argument("index");
                    CutTaskContext ctx = new CutTaskContext();
                    ctx.type = type;
                    ctx.chanid = chanStr;
                    ctx.vmode = Integer.parseInt(vmode1Str);
                    ctx.hmode = Integer.parseInt(hmode1Str);
                    ctx.index = index;
                    ctx.img = imagedata;
                    ctx.timestamp = System.currentTimeMillis();
                    ci.pushCutAction(ctx);
                } else if (type == 0) {
                    byte[] imagedata = (byte[]) call.argument("data");
                    String hmode1Str = (String) call.argument("hmode");
                    String vmode1Str = (String) call.argument("vmode");
                    HashMap<String, String> chanids = (HashMap<String, String>) call.argument("chanids");
                    ArrayList<Object> indexs = (ArrayList<Object>) call.argument("indexs");
                    HashMap<String,HashMap<String,String>> mainCutAreaMaps = (HashMap<String,HashMap<String,String>>) call.argument("mainCutAreas");

                    CutTaskContext ctx = new CutTaskContext();
                    ctx.type = type;
                    ctx.img = imagedata;
                    ctx.vmode = Integer.parseInt(vmode1Str);
                    ctx.hmode = Integer.parseInt(hmode1Str);
                    ctx.chanSet = chanids;
                    ctx.indexSet = indexs;
                    ctx.timestamp = System.currentTimeMillis();
                    ctx.mainCutArea = mainCutAreaMaps;
                    int srcflag = (int) call.argument("srcflag");
                    //切割源为部分区域
                    if (srcflag == 1) {
                        String srchmode1Str = (String) call.argument("srchmode");
                        String srcvmode1Str = (String) call.argument("srcvmode");
                        int srcindex = (int) call.argument("srcindex");
                        ctx.srcHmode = Integer.parseInt(srchmode1Str);
                        ctx.srcVmode = Integer.parseInt(srcvmode1Str);
                        ctx.srcindex = srcindex;
                    } else {
                        ctx.srcHmode = 0;
                        ctx.srcVmode = 0;
                        ctx.srcindex = 0;
                    }
                    ctx.srcflag = srcflag;
                    ci.pushCutAction(ctx);
                } else if (type == 4) {
                    //Log.e("XIAOPING", "====================================================================================================>:TAG1");
                    byte[] imagedata = (byte[]) call.argument("data");
                    String chanStr = (String) call.argument("chanid");
                    String x = (String) call.argument("x");
                    String y = (String) call.argument("y");
                    String w = (String) call.argument("w");
                    String h = (String) call.argument("h");

                    String hsize = (String) call.argument("hsize");
                    String vsize = (String) call.argument("vsize");

                    CutTaskContext ctx = new CutTaskContext();
                    ctx.type = type;
                    ctx.img = imagedata;
                    ctx.chanid = chanStr;
                    ctx.x = Integer.parseInt(x);
                    ctx.y = Integer.parseInt(y);
                    ctx.w = Integer.parseInt(w);
                    ctx.h = Integer.parseInt(h);
                    ctx.timestamp = System.currentTimeMillis();
                    ctx.vsize = Integer.parseInt(vsize);
                    ctx.hsize = Integer.parseInt(hsize);
                    ci.pushCutAction(ctx);
                    //Log.e("XIAOPING", "====================================================================================================>:TAG2:"+ctx.toString());
                } else if (type == 5) {
                    byte[] imagedata = (byte[]) call.argument("data");
                    String chanStr = (String) call.argument("chanid");
                    int index = (int) call.argument("index");
                    String x = (String) call.argument("x");
                    String y = (String) call.argument("y");
                    String w = (String) call.argument("w");
                    String h = (String) call.argument("h");

                    String hsize = (String) call.argument("hsize");
                    String vsize = (String) call.argument("vsize");

                    CutTaskContext ctx = new CutTaskContext();
                    ctx.type = type;
                    ctx.img = imagedata;
                    ctx.chanid = chanStr;

                    ctx.srcindex = index;
                    ctx.x = Integer.parseInt(x);
                    ctx.y = Integer.parseInt(y);
                    ctx.w = Integer.parseInt(w);
                    ctx.h = Integer.parseInt(h);
                    ctx.timestamp = System.currentTimeMillis();
                    ctx.vsize = Integer.parseInt(vsize);
                    ctx.hsize = Integer.parseInt(hsize);
                    ci.pushCutAction(ctx);
                }
                result.success(null);

            } else if (call.method.equals("updateChanalArea")) {
                String mode = (String) call.argument("mode");
        /*
        ArrayList<HashMap<String, String>> chanMapList = (ArrayList<HashMap<String, String>>)call.argument("chanMapList");
        ArrayList<Channel> chanList = new  ArrayList<Channel>();
        for(HashMap<String, String> item :chanMapList){
          Channel chan = new Channel();
          chan.id = item.get("id");
          chan.ench = item.get("ench");
          chan.x = item.get("x");
          chan.y = item.get("y");
          chan.w = item.get("w");
          chan.h = item.get("h");
          chanList.add(chan);
        }
        */
                ArrayList<Channel> chanList = new ArrayList<Channel>();
                ci.updateChanalArea(mode, chanList);
                result.success(null);

            }
        }
    }

    private void _onMethodCall(MethodCall call, Result result) {
        String _key = (String) call.argument("key");
        CutImage ci = cutimageMap.get(_key);
        if (ci != null) {
            if (call.method.equals("dartSendMessageToIOS")) {
                byte[] imagedata = (byte[]) call.argument("data");
                String indexStr = (String) call.argument("index");
                String hmode1Str = (String) call.argument("hmode");
                String vmode1Str = (String) call.argument("vmode");
                Bitmap allImage = BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length);
                // BitmapUtils.saveBitmap(allImage,indexStr+"-"+hmode1Str+"-"+vmode1Str);
                imagedata = null;
                try {
                    int index = Integer.parseInt(indexStr);
                    int hmode1 = Integer.parseInt(hmode1Str);
                    int vmode1 = Integer.parseInt(vmode1Str);

                    long t3 = System.currentTimeMillis();
                    Bitmap bt = ci.getNYXPreviewImage(vmode1, hmode1, index, allImage);
                    //BitmapUtils.saveBitmap(bt,indexStr);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bt.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] datas = baos.toByteArray();
                    long t4 = System.currentTimeMillis();
                    Log.e(TAG, "StartCut======================================================================>Cut Image Cust:" + (t4 - t3));
                    ci.updateImage(String.valueOf(index - 1), datas);
                    //result.success(datas);
                    result.success(null);

//        allImage.recycle();
//        allImage = null;
//        bt.recycle();
//        bt = null;
//        datas = null;

        /*
        Bitmap bt = ci.getNYXPreviewImage(vmode1, hmode1, index, allImage);
        allImage.recycle();
        allImage = null;
        BitmapUtils.saveBitmap(bt,Integer.toString(index-1));
        bt.recycle();
        bt = null;
        result.success(null);
        */

                } catch (Exception e) {
                    Log.e(TAG, "dartSendMessageToIOS==================>:" + e.toString());
                    result.success(null);
                }
            } else if (call.method.equals("getCMProPreviewImage")) {
                byte[] imagedata = (byte[]) call.argument("data");
                int left = (int) call.argument("left");
                int top = (int) call.argument("top");
                int right = (int) call.argument("right");
                int bottom = (int) call.argument("bottom");
                Bitmap allImage = BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length);
                imagedata = null;
                Bitmap bt = ci.getCMProPreviewImage(left, top, right, bottom, allImage);
                allImage = null;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bt.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] datas = baos.toByteArray();
                result.success(datas);
                bt.recycle();
                bt = null;
                datas = null;
                baos = null;
            } else if (call.method.equals("doStartCutImage")) {
                int vmode = (int) call.argument("vmode");
                int hmode = (int) call.argument("hmode");
                ci.StartCut(vmode, hmode);
                result.success("true");
            } else {
                result.notImplemented();
            }
        }

    }
}
