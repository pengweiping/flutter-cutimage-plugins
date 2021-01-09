import 'dart:ui' as dui;

import 'package:flutter_cutimage_example/global.dart';

///预监大图片
class Preview {
  //预监图片
  // Map<String, dui.Image> _map; //拼接屏大屏信息
  Map<String, dui.Image> _map; 
  // 工厂模式
  factory Preview() => _getInstance();

  static Preview get instance => _getInstance();
  static Preview _instance;

  Preview._internal() {
    _map = Map();
  }

  static Preview _getInstance() {
    if (_instance == null) {
      _instance = new Preview._internal();
    }
    return _instance;
  }

  void setMap(String key, dui.Image value) {
    //获取保存的前一帧图片，避免内存泄漏

    _map[key] = value;
  }

  dui.Image getMap(String key) {
    Global.gImagePreviewSet.add(key);

    if (_map != null) {
      return _map[key];
    }
    return null;
  }

  void remove(String key) {
    Global.gImagePreviewSet.remove(key);
  }

  void clear() {
    _map.clear();
  }
}
