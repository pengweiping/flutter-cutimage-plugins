import 'package:event_bus/event_bus.dart';

///事件总线管理类
class EventBusManager{
  // 工厂模式
  factory EventBusManager() => _getInstance();

  static EventBusManager get instance => _getInstance();
  static EventBusManager _instance;

  static EventBus _eventBus;

  EventBusManager._internal() {
    _init();
  }

  static EventBusManager _getInstance() {
    if (_instance == null) {
      _instance = new EventBusManager._internal();
    }
    return _instance;
  }


  void _init() async {
    _eventBus = EventBus();
  }



  Stream on<T>(Function callback){
    _eventBus.on<T>().listen((_){
      callback(_);
    });
  }


  void emit(dynamic event) {
    _eventBus.fire(event);
  }

}