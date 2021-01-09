
import 'package:flutter/material.dart';


///存储一些必须的全局变量
class Global {
  static GlobalKey<NavigatorState> gNavigatorKey =
      new GlobalKey<NavigatorState>();

  //只有需要展示的图片才需要编码及展示，避免造成资源浪费
  //这里是考虑到性能问题的一种优化方式
  static Set<String> gImagePreviewSet = Set();

  static void clear() {
    gImagePreviewSet.clear();
  }
}
