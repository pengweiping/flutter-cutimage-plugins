import 'package:flutter/material.dart';
import 'dart:async';
import 'package:permission_handler/permission_handler.dart';
import 'package:flutter/services.dart';
import 'package:flutter_cutimage/flutter_cutimage.dart';
import 'package:flutter_cutimage_example/preview_player.dart';
import 'package:flutter_cutimage_example/makecut_manager.dart';

//void main() => runApp(MyApp());

Future main() async {
  if (!await _requestPermissions()) {
    await AppUtils.popApp();
  } else {
    runApp(new MyApp());
  }
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  int mode;
  @override
  void initState() {
    super.initState();

    initPlatformState();

    MakeCutManager.instance.startCut();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterCutimage.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  List<String> getDataList() {
    mode = 8;
    List<String> list = [
      "0",
      "1",
      "2",
      "3",
      "4",
      "5",
      "6",
      "7",
      "8",
      "9",
      "10",
      "11",
      "12",
      "13",
      "14",
      "15",
      "16",
      "17",
      "18",
      "19",
      "20",
      "21",
      "22",
      "23",
      "24",
      "25",
      "26",
      "27",
      "28",
      "29",
      "30",
      "31",
      "32",
      "33",
      "34",
      "35",
      "36",
      "37",
      "38",
      "39",
      "40",
      "41",
      "42",
      "43",
      "44",
      "45",
      "46",
      "47",
      "48",
      "49",
      "50",
      "51",
      "52",
      "53",
      "54",
      "55",
      "56",
      "57",
      "58",
      "59",
      "60",
      "61",
      "62",
      "63",
    ];
    return list;
  }

  /*
  List<String> getDataList() {
    mode = 2;
    List<String> list = ["0", "1", "2", "3"];
    return list;
  }
 */

  List<Widget> getWidgetList() {
    return getDataList().map((item) => getItemContainer(item)).toList();
  }

  Widget getItemContainer(String item) {
    return Container(
      alignment: Alignment.center,
      child: PreviewPlayer(
        channelId: item,
      ),
      color: Colors.white,
    );
  }

  @override
  Widget build(BuildContext context) {
    List<String> datas = getDataList();
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: GridView.builder(
            itemCount: datas.length,
            gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: mode, mainAxisSpacing: 2.0, crossAxisSpacing: 2.0, childAspectRatio: 1.0),
            itemBuilder: (BuildContext context, int index) {
              return getItemContainer(datas[index]);
            },
          ),
        ),
      ),
    );
  }
}

Future<bool> _requestPermissions() async {
  Map<PermissionGroup, PermissionStatus> permissions = await PermissionHandler().requestPermissions([
    PermissionGroup.storage,
    //   PermissionGroup.location,
  ]);

  List<bool> results = permissions.values.toList().map((status) {
    return status == PermissionStatus.granted;
  }).toList();

  return !results.contains(false);
}

class ApplicationMethodChannel {
  static MethodChannel main = MethodChannel('main');
}

class AppUtils {
  static Future<void> popApp() async {
    await SystemChannels.platform.invokeMethod('SystemNavigator.pop');
  }

  static Future checkUpgrade() async {
    try {
      final bool result = await ApplicationMethodChannel.main.invokeMethod('checkUpgrade');
      print('result=$result');
    } on PlatformException {
      print('faied');
    }
  }
}
