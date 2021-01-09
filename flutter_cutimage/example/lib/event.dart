import 'dart:ui';

///跳转页面
class JumpPageEvent {
  String docID;
  JumpPageEvent(this.docID);
}

///关闭页面
class ClosePageEvent {
  String docID;
  ClosePageEvent(this.docID);
}

///打开页面
class OpenPageEvent {
  String docID;
  OpenPageEvent(this.docID);
}

///隐藏页面
class HidePageEvent {
  String docID;
  HidePageEvent(this.docID);
}

///显示页面
class ShowPageEvent {
  String docID;
  ShowPageEvent(this.docID);
}

///解压文件
class ZipEvent {
  bool success;

  ZipEvent(this.success);
}

///预案更新事件
class PresetUpdateEvent {
  String screenId;
  PresetUpdateEvent(this.screenId);
}

///屏幕名称更新事件
class ScreenNameUpdateEvent {
  String screenId;
  ScreenNameUpdateEvent(this.screenId);
}

///拼接屏幕更新事件
class StitchScreenUpdateEvent {
  String screenId;
  StitchScreenUpdateEvent(this.screenId);
}

///窗口列表更新事件
class WindowsUpdateEvent {
  String screenId;
  WindowsUpdateEvent(this.screenId);
}

///全屏事件
class FullScreenEvent {
  String itemId;
  FullScreenEvent(this.itemId);
}

///置顶事件
class TopEvent {
  String itemId;
  TopEvent(this.itemId);
}

///置底事件
class BottomEvent {
  String itemId;
  BottomEvent(this.itemId);
}

///窗口上移
class UpEvent {
  String itemId;
  UpEvent(this.itemId);
}

///窗口下移
class DownEvent {
  String itemId;
  DownEvent(this.itemId);
}

///开窗
class OpenEvent {
  String itemId;
  Window window;
  dynamic signal;
  String deviceId; //deviceId,物理信号源用来做权限判断
  OpenEvent(this.itemId, this.window, this.signal, {this.deviceId});
}

///切换信号源
class SwitchInputEvent {
  String itemId;
  Window window;
  dynamic signal;
  String deviceId; //deviceId,物理信号源用来做权限判断
  SwitchInputEvent(this.itemId, this.window, this.signal, {this.deviceId});
}

///关闭单个窗口
class CloseEvent {
  String itemId;
  CloseEvent(this.itemId);
}

///关闭所有窗口事件
class CloseAllWindowEvent {
  String itemId;
  CloseAllWindowEvent(this.itemId);
}

///锁定屏幕
class LockEvent {
  String itemId;
  LockEvent(this.itemId);
}

///展示信息
class ShowInfoEvent {
  String itemId;
  ShowInfoEvent(this.itemId);
}

///关闭信息展示
class HideInfoEvent {
  String itemId;
  HideInfoEvent(this.itemId);
}

///展示隐藏辅助线
class GuideLinesEvent {
  String itemId;
  String param;
  GuideLinesEvent(this.itemId, this.param);
}

///回显
class ItemEchoEvent {
  String itemId;
  bool on;
  ItemEchoEvent(this.itemId, this.on);
}

///回显,整个doc页
class DocEchoEvent {
  String docId;
  bool on;
  DocEchoEvent(this.docId, this.on);
}

///收到预监流事件
class ReceivePreviewEvent {
  ReceivePreviewEvent();
}

///拼接屏幕更新事件
class MultiScreenUpdateEvent {
  String screenId;
  MultiScreenUpdateEvent(this.screenId);
}

///输入通道列表
///物理通道
class InputChannelUpdateEvent {
  String screenId;
  String deviceId;
  InputChannelUpdateEvent(this.screenId, this.deviceId);
}

///单屏信号源
class MultiSubScreenInputEvent {
  String itemId;
  int index;
  var signal;
  Offset current;
  MultiSubScreenInputEvent(this.itemId, this.index, this.signal, this.current);
}

///切换屏组
class SwitchStitchScreenEvent {
  String screenId;
  String itemId;
  SwitchStitchScreenEvent(this.itemId, this.screenId);
}

///切换屏组
class SwitchStitchScreenEventInner {
  String screenId;
  String itemId;
  SwitchStitchScreenEventInner(this.itemId, this.screenId);
}

///关闭播放器
class PlayerStopEvent {
  String magic;
  PlayerStopEvent(this.magic);
}

///重新开启播放器
class PlayerReStartEvent {
  String magic;
  PlayerReStartEvent(this.magic);
}

///播放器改变事件
class PlayerChangedEvent {
  PlayerChangedEvent();
}

///IPC播放器改变事件
class IPCPlayerChangedEvent {
  IPCPlayerChangedEvent();
}

///切换预案事件
class PresetListSwitchEvent {
  String screenId;
  String itemId;
  PresetListSwitchEvent(this.itemId, this.screenId);
}

///切换预监模式
class SwitchPreviewModeEvent {
  String itemId;
  String mode;
  SwitchPreviewModeEvent(this.itemId, this.mode);
}

///收到预鉴服务器流
class ReceivePreviewImageEvent {
  ReceivePreviewImageEvent();
}

///预监模式变动
class ReceivePreviewModeChangeEvent {
  String mode;

  ReceivePreviewModeChangeEvent(this.mode);
}

///收到接入服务器断开消息
class ReceiveAccessSocketDisconnectEvent {
  ReceiveAccessSocketDisconnectEvent();
}

///收到预监服务器断开消息
class ReceivePreviewSocketDisconnectEvent {
  ReceivePreviewSocketDisconnectEvent();
}

class ReceiveCrashEvent {
  dynamic error;
  dynamic stackTrace;

  ReceiveCrashEvent(this.error, this.stackTrace);
}

///设置IP解码卡的URL
class SetIPDecoderURLEvent {
  String itemId;
  String screenId;
  String ch; //主通道号
  String subch; //子通道号
  String rtsp; //设置的URL
  SetIPDecoderURLEvent(
      this.itemId, this.screenId, this.ch, this.subch, this.rtsp);
}

///屏幕窗口更新绑定
class ScreenWindowChangeReBindEvent {
  ScreenWindowChangeReBindEvent();
}

///更新日历事件
class RefrashCalentarEvnetsEvent {
  String itemId;
  String userID;
  String year;
  String month;
  RefrashCalentarEvnetsEvent(this.itemId, this.userID, this.year, this.month);
}

/// 更新日历事件编辑对话框的预案列表
class RefrashCalentarPresetList {
  RefrashCalentarPresetList();
}

/// 更新日历
class RefrashCalentar {
  RefrashCalentar();
}
