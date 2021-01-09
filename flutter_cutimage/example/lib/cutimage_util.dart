import 'dart:async';

import 'dart:typed_data';
import 'dart:ui';
import 'dart:ui' as ui;

import 'package:flutter_cutimage/flutter_cutimage.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

import 'package:flutter_cutimage_example/cuttask_cxt.dart';

class CutImageUtil {
  static Future<void> initPlatformState() async {
    await FlutterCutimage.platformVersion;
  }

  static Future<ui.Image> getNYXPreviewImage(Uint8List imagedata, String index, String hmode, String vmode) async {
    dynamic args = await FlutterCutimage.dartSendMessageToIOS({
      "data": imagedata,
      "index": index,
      "hmode": hmode,
      "vmode": vmode,
    });
    ui.Image im = await _decodeImageFromListAsync(Uint8List.fromList(args));
    return im;
  }

  static Future<ui.Image> getCMProPreviewImage(ui.Image image, Rect src) async {
    ByteData stuff = await image.toByteData(format: ImageByteFormat.png);
    dynamic args = await FlutterCutimage.getCMProPreviewImage({
      "data": stuff.buffer.asUint8List(),
      "left": src.left.toInt(),
      "top": src.top.toInt(),
      "right": src.right.toInt(),
      "bottom": src.bottom.toInt(),
    });
    ui.Image im = await _decodeImageFromListAsync(Uint8List.fromList(args));
    return im;
  }

  static Future<ui.Image> _decodeImageFromListAsync(Uint8List list) async {
    ui.Codec codec = await ui.instantiateImageCodec(list);
    //ui.Codec codec = await ui.instantiateImageCodec(list, decodedCacheRatioCap: 1.0);

    ui.FrameInfo frameInfo = await codec.getNextFrame();
    return frameInfo.image;
  }

  static Future<String> doStartCutImage(int vmode, int hmode) async {
    String ret = await FlutterCutimage.doStartCutImage({
      "vmode": vmode,
      "hmode": hmode,
    });
    return ret;
  }

  static Future<Uint8List> getNYXPreviewImageEX(Uint8List imagedata, String index, String hmode, String vmode) async {
    dynamic args = await FlutterCutimage.dartSendMessageToIOS({
      "data": imagedata,
      "index": index,
      "hmode": hmode,
      "vmode": vmode,
    });
    return args;
  }
}
