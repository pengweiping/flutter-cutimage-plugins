import 'dart:async';

import 'package:flutter/services.dart';

class FlutterCutimage {
  static const MethodChannel _channel = const MethodChannel('flutter_cutimage');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<dynamic> CreateCutimage(Map<String, dynamic> args) {
    return _channel.invokeMethod("create", args);
  }

  static void DestroyCutimage(Map<String, dynamic> args) {
    _channel.invokeMethod("destroy", args);
  }

  static void DoCutImage(Map<String, dynamic> args) async {
    _channel.invokeMethod("doCutImage", args);
  }

  static void UpDateChanalArea(Map<String, dynamic> args) async {
    return _channel.invokeMethod("updateChanalArea", args);
  }

  /***********************************************************************************************/

  static Future<dynamic> dartSendMessageToIOS(Map<String, dynamic> args) {
    return _channel.invokeMethod("dartSendMessageToIOS", args);
  }

  static Future<dynamic> getCMProPreviewImage(Map<String, dynamic> args) {
    return _channel.invokeMethod("getCMProPreviewImage", args);
  }

  static Future<String> doStartCutImage(Map<String, dynamic> args) async {
    final String retState =
        await _channel.invokeMethod("doStartCutImage", args);
    return retState;
  }
}
