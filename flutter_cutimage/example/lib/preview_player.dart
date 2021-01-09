import 'dart:async';
import 'dart:io';
import 'package:flutter_cutimage_example/event.dart';
import 'package:flutter_cutimage_example/event_manager.dart';
import 'package:flutter_cutimage_example/preview.dart';
import 'package:flutter/material.dart';
import 'dart:ui' as dui;

///预监播放器
///能够自动更新画面的
class PreviewPlayer extends StatefulWidget {
  final String channelId;

  PreviewPlayer({
    Key key,
    @required this.channelId,
  })  : assert(channelId != null),
        super(key: key);

  @override
  State<StatefulWidget> createState() {
    return PreviewPlayerState();
  }
}

class PreviewPlayerState extends State<PreviewPlayer> {
  ///子通道key，用来获取切割后子通道的图像
  ///
  String _channelId;
  String _sdcardDir = "/sdcard/CutImage";

  @override
  void initState() {
    super.initState();
    _channelId = widget.channelId;
    _initEventBus();
  }

  void _initEventBus() {
    EventBusManager.instance.on<ReceivePreviewImageEvent>((event) {
      if (this.mounted) {
        setState(() {});
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    dui.Image image = Preview.instance.getMap(_channelId);
    return image != null
        ? ConstrainedBox(
            child: RawImage(
              image: image,
              fit: BoxFit.fill,
            ),
            constraints: new BoxConstraints.expand(),
          )
        : Container(
            color: Colors.black,
          );

    /*
    String _filePath = '$_sdcardDir/$_channelId.jpg';
    int t1 = new DateTime.now().millisecondsSinceEpoch;
    Image image = Image.file(File(_filePath), fit: BoxFit.cover);
    int t2 = new DateTime.now().millisecondsSinceEpoch;
    print("Load ${_filePath} Image Widgt ====================>Cast:" +
        (t2 - t1).toString());
    return image != null
        ? ConstrainedBox(
            child: image,
            constraints: new BoxConstraints.expand(),
          )
        : Container(
            color: Colors.black,
          );
   */
  }

  @override
  void dispose() {
    super.dispose();
    Preview.instance.remove(_channelId);
  }
}
