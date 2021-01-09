import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

class CutTaskContext {
  //切割的原始图片
  Uint8List img;
  //子切割的原始图片对应的通道ID
  String chanid;
  //通道的分屏模式 2/4/6/9
  int vmode;
  int hmode;
  // 分屏模式,切片索引（1开始）
  int index;
  //构建当前切割事务的时间戳
  int timestamp;
  //切割类型:0:独立通道，1：子通道,2:切割特定区域
  int type;
  //独立通道，对应通道集合,按切割顺序（0,1.....）
  List<String> chanS;
  //切割特定区域
  Rect rect;

}
