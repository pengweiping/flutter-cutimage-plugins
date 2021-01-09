import 'package:flutter_cutimage_example/cutimage_util.dart';
import 'dart:async';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
import 'package:flutter_cutimage_example/preview.dart';
import 'dart:ui' as ui;
import 'package:flutter/foundation.dart';
import 'package:flutter_cutimage_example/event.dart';
import 'package:flutter_cutimage_example/event_manager.dart';
import 'dart:typed_data';
import 'package:flutter_cutimage_example/cutimage.dart';
import 'dart:async';

import 'dart:typed_data';
import 'dart:ui';

class MakeCutManager {
  // 工厂模式
  factory MakeCutManager() => _getInstance();
  static MakeCutManager get instance => _getInstance();
  static MakeCutManager _instance;
  String _sdcardDir = "/sdcard/CutImage";
  Timer _timer;
  Timer _timer2;
  List<String> list = ["1", "2", "3", "4", "5", "6", "7", "8", "9"];

  List<String> inputlist = [
    "/sdcard/png/1.png",
    "/sdcard/png/2.png",
    "/sdcard/png/3.png",
    "/sdcard/png/4.png",
    "/sdcard/png/5.png",
    "/sdcard/png/6.png",
    "/sdcard/png/7.png",
    "/sdcard/png/8.png",
    "/sdcard/png/9.png",
  ];

  List<String> chanSet = ["0", "1", "2", "3"];

  Map<String, String> chanidMaps = Map<String, String>();

  List<int> indexSets = List<int>();

  MakeCutManager._internal() {
    _init();
  }
  static int index = 0;
  int vmode = 8;
  int hmode = 8;

  CutImage cuter;
  static MakeCutManager _getInstance() {
    if (_instance == null) {
      _instance = new MakeCutManager._internal();
    }
    return _instance;
  }

  void _init() async {
    print("_init=================:");
    cuter = CutImage("pwp0330");
    for (int i = 0; i < 64; i++) {
      chanidMaps[(i + 1).toString()] = i.toString();
      indexSets.add(i + 1);
    }
    //CutImageUtil.doStartCutImage(3, 3);
//    _timer2 = Timer.periodic(Duration(milliseconds: 2000), (timer) {
//      //print("doStartCutImage=================>${timer.tick}");
//      // CutImageUtil.doStartCutImage(vmode, hmode);
//
//      for (int i = 0; i < inputlist.length; i++) {
//        print("doStartCutImage=================>${timer.tick}:" +
//            inputlist[i].toString());
//        loadImageByFile(inputlist[i].toString()).then((imagedata) {
//          print("doStartCutImage=================:" +
//              inputlist[i].toString() +
//              ", file Load Success !!!!");
//          for (int index = 0; index < vmode * hmode; index++) {
//            print("doStartCutImage=================:" +
//                (index + 1).toString() +
//                ", Start to Cut !!!!");
//            imagedata.toByteData(format: ImageByteFormat.png).then((value) {
//              /*
//              CutImageUtil.getNYXPreviewImage(
//                      value.buffer.asUint8List(),
//                      (index + 1).toString(),
//                      vmode.toString(),
//                      hmode.toString())
//                  .then((data) {
//                Preview.instance.setMap((index).toString(), data);
//              });
//              */
//              CutImageUtil.getNYXPreviewImageEX(
//                      value.buffer.asUint8List(),
//                      (index + 1).toString(),
//                      vmode.toString(),
//                      hmode.toString())
//                  .then((dataList) {
//                _decodeImageFromListAsyncCallback(dataList, (image) async {
//                  Preview.instance.setMap((index).toString(), image);
//                });
//              });
//            });
//          }
//        });
//      }
//    });

//    _timer2 = Timer.periodic(Duration(milliseconds: 1000), (timer) {
//      int fileIndex = (index++) % (inputlist.length);
//      print("doStartCutImage:${index}=================>${timer.tick}:" +
//          inputlist[fileIndex].toString());
//      loadFile(inputlist[fileIndex].toString()).then((imagedata) {
//        for (int index = 0; index < vmode * hmode; index++) {
//          print("doStartCutImage=================:" +
//              inputlist[fileIndex].toString() +
//              "-" +
//              (index + 1).toString() +
//              ", Start to Cut !!!!");
//          cuter.getNYXPreviewImageEX(imagedata, (index + 1).toString(),
//                  vmode.toString(), hmode.toString())
//              .then((dataList) {
//                /*
//            _decodeImageFromListAsyncCallback(dataList, (image) async {
//              print("doStartCutImage=================:" +
//                  inputlist[fileIndex].toString() +
//                  "-" +
//                  (index + 1).toString() +
//                  ", Cut Sucess!!!!");
//              Preview.instance.setMap((index).toString(), image);
//              EventBusManager.instance.emit(ReceivePreviewImageEvent());
//            });
//            */
//          });
//        }
//      });
//    });

    _timer2 = Timer.periodic(Duration(milliseconds: 2000), (timer) {
      int fileIndex = (index++) % (inputlist.length);
      fileIndex = 0;
//      print("doStartCutImage:${index}=================>${timer.tick}:" +
//          inputlist[fileIndex].toString());
      loadFile(inputlist[fileIndex].toString()).then((imagedata) {
//        for (int index = 0; index < vmode * hmode; index++) {
//          cuter.getNYXPreviewImageAsync(
//              imagedata, index + 1, vmode.toString(), hmode.toString());
//        }

        cuter.getAllCMProCutMainPreImageAsync(imagedata, chanidMaps, indexSets, vmode.toString(), hmode.toString());

//        cuter.getAllCMProCutMainPartPreImageAsync(imagedata, chanidMaps, indexSets,
//            vmode.toString(), hmode.toString(),9,3.toString(),3.toString());
      });
    });

    _getLocalFile();
  }

  void _decodeImageFromListAsyncCallback(Uint8List list, ui.ImageDecoderCallback callback) async {
    ui.Codec codec = await ui.instantiateImageCodec(list);
    // ui.Codec codec = await ui.instantiateImageCodec(list, decodedCacheRatioCap: 1.0);
    ui.FrameInfo frameInfo = await codec.getNextFrame();
    callback(frameInfo.image);
    codec.dispose();
    codec = null;
    frameInfo = null;
  }

  Future<Uint8List> loadFile(String path) async {
    Uint8List list = await File(path).readAsBytes();
    return list;
  }

  //通过 文件读取Image
  Future<ui.Image> loadImageByFile(String path) async {
    var list = await File(path).readAsBytes();
    return loadImageByUint8List(list);
  }

  Future<ui.Image> loadImageByUint8List(Uint8List list) async {
    ui.Codec codec = await ui.instantiateImageCodec(list);
    ui.FrameInfo frame = await codec.getNextFrame();
    return frame.image;
  }

  void _getLocalFile() async {
    if (Platform.isAndroid) {
      _sdcardDir = (await getExternalStorageDirectory()).path;
    } else if (Platform.isIOS) {
      _sdcardDir = (await getApplicationDocumentsDirectory()).path;
    }
  }

  void startCut() {
    if (_timer == null) {
      _timer = Timer.periodic(Duration(milliseconds: 20), (timer) {
//        print(
//            "ReceivePreviewImageEvent tick ${timer.tick}, timer isActive ${timer.isActive}");
        /*
        for (String name in list) {
          String _filePath = '$_sdcardDir/$name.png';
          FileImage image = File(_filePath));
           dui.Image im;
           Preview.instance.setMap(name, im);
        }
        */
        EventBusManager.instance.emit(ReceivePreviewImageEvent());
      });
    }
  }

  void stopCut() {
    if (_timer != null) {
      _timer.cancel();
    }
    cuter.dispose();
  }
}
