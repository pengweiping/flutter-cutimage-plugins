import 'dart:async';
import 'dart:io';
import 'package:flutter_cutimage_example/preview.dart';
import 'package:flutter/services.dart';
import 'package:flutter_cutimage_example/event.dart';
import 'package:flutter_cutimage_example/event_manager.dart';
import 'package:flutter_cutimage/flutter_cutimage.dart';
import 'dart:typed_data';
import 'dart:ui' as ui;

class CutImage {
  String _key;
  StreamSubscription<dynamic> _eventSubscription;
  CutImage(String key) {
    this._key = key;
    _init();
  }

  void _init() async {
    print("_init ===================================================================>!!!!!TAG1");
    await FlutterCutimage.CreateCutimage({"key": _key}).then((dynamic) {
      print("_init ===================================================================>!!!!!");
      _eventSubscription =
          _eventChannelFor(_key).receiveBroadcastStream().listen(_eventListener, onError: _errorListener);
    });
  }

  void dispose() async {
    await FlutterCutimage.DestroyCutimage({"key": _key});
  }

  EventChannel _eventChannelFor(String key) {
    return EventChannel('rgb.com/FlutterCutimagePlugin/CutEvents$key');
  }

  void _errorListener(Object obj) {
    final PlatformException e = obj;
  }

  void _eventListener(dynamic event) {
    final Map<dynamic, dynamic> map = event;
    //print("get  a event ===================================================================>!!!!!"+map['event']);
    switch (map['event']) {
      case 'initialized':
        break;
      case 'uninitialized':
        break;
      case 'update':
        String key = map['Key'];
        // print("update a cut image ===================================================================>!!!!!:${key}");
        Uint8List imgdate = map['img'];
        _decodeImageFromListAsyncCallback(imgdate, (image) async {
          Preview.instance.setMap(key, image);
          // print("update a cut ReceivePreviewImageEvent..............");
          if (key == "63") {
            //  EventBusManager.instance.emit(ReceivePreviewImageEvent());
          }
        });
        break;
    }
  }

  /// 异步分割图片：从hmode*vmode的图片中，切割第index(1开始)图片，还回的异步消息中，携带上下文key索引：chanid +"_"+"index"
  /// ${_deviceId}_${channel.id}_${i + 1}
  /// chanid = ${_deviceId}_${channel.id}
  ///
  ///
  void getNYXPreviewImageAsync(Uint8List imagedata, int index, String hmode, String vmode) async {
    FlutterCutimage.DoCutImage({
      "key": _key,
      "type": 1,
      "data": imagedata,
      "chanid": "16",
      "hmode": hmode,
      "vmode": vmode,
      "index": index,
    });
  }

  ///将PreviewImage中分索引index切割为通道预监图片
  void getCMProCutMainPreImageAsync(Uint8List imagedata, List<String> chanids, int index, String hmode, String vmode) {
    FlutterCutimage.DoCutImage({
      "key": _key,
      "type": 3,
      "data": imagedata,
      "chanids": chanids,
      "hmode": hmode,
      "vmode": vmode,
      "index": index,
    });
  }

  ///非NYX中的，将PreviewImage中切割为四个主通道预监图片
  ///
  ///
  void getAllCMProCutMainPreImageAsync(
      Uint8List imagedata, Map<String, String> chanids, List<int> indexs, String hmode, String vmode) {
    FlutterCutimage.DoCutImage({
      "key": _key,
      "type": 0,
      "data": imagedata,
      "chanids": chanids,
      "indexs": indexs,
      "hmode": hmode,
      "vmode": vmode,
      "srcflag": 0,
    });
  }

  ///非NYX中的，将PreviewImage某个区域的图片，按indexs进行切割
  void getAllCMProCutMainPartPreImageAsync(
    Uint8List imagedata,
    Map<String, String> chanids,
    List<int> indexs,
    String hmode,
    String vmode,
    int srcIndex,
    String srchmode,
    String srcvmode,
  ) {
    FlutterCutimage.DoCutImage({
      "key": _key,
      "type": 0,
      "data": imagedata,
      "chanids": chanids,
      "indexs": indexs,
      "hmode": hmode,
      "vmode": vmode,
      "srcflag": 1,
      "srcindex": srcIndex,
      "srchmode": srchmode,
      "srcvmode": srcvmode,
    });
  }

  void _decodeImageFromListAsyncCallback(Uint8List list, ui.ImageDecoderCallback callback) async {
    ui.Codec codec = await ui.instantiateImageCodec(list);
    //final ui.Codec codec =await ui.instantiateImageCodec(list, decodedCacheRatioCap: 1.0);
    ui.FrameInfo frameInfo = await codec.getNextFrame();
    callback(frameInfo.image);
    codec.dispose();
    codec = null;
    frameInfo = null;
  }

  Future<ui.Image> _decodeImageFromListAsync(Uint8List list) async {
    //ui.Codec codec = await ui.instantiateImageCodec(list, decodedCacheRatioCap: 1.0);
    ui.Codec codec = await ui.instantiateImageCodec(list);
    ui.FrameInfo frameInfo = await codec.getNextFrame();
    return frameInfo.image;
  }

  Future<Uint8List> getNYXPreviewImageEX(Uint8List imagedata, String index, String hmode, String vmode) async {
    dynamic args = await FlutterCutimage.dartSendMessageToIOS({
      "key": _key,
      "data": imagedata,
      "index": index,
      "hmode": hmode,
      "vmode": vmode,
    });
    return args;
  }
}
