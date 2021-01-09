package com.example.fluttercutimage;

import android.util.Log;

import io.flutter.plugin.common.EventChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * And implementation of {@link EventChannel.EventSink} which can wrap an underlying sink.
 *
 * <p>It delivers messages immediately when downstream is available, but it queues messages before
 * the delegate event sink is set with setDelegate.
 *
 * <p>This class is not thread-safe. All calls must be done on the same thread or synchronized
 * externally.
 * copy from video_player
 */
final class QueuingEventSink implements EventChannel.EventSink,Runnable {

  public  final static  String TAG = "QueuingEventSink";
  private EventChannel.EventSink delegate;
  private LinkedBlockingQueue<Object> eventQueue = new LinkedBlockingQueue<Object>();
  private boolean done = false;
  private Thread pusher = null;
  private boolean bpause = false;

  public QueuingEventSink(){
      pusher = new Thread(this);
      if(pusher != null ){
          pusher.start();
      }

  }

  public void setDelegate(EventChannel.EventSink delegate) {
    this.delegate = delegate;
     //maybeFlush();
  }

    @Override
    public void run() {
        while(!done){
            maybeFlush();
        }
    }

    @Override
  public void endOfStream() {
    enqueue(new EndOfStreamEvent());
    //maybeFlush();
    done = true;
  }

  @Override
  public void error(String code, String message, Object details) {
    enqueue(new ErrorEvent(code, message, details));
    //maybeFlush();
  }

  @Override
  public void success(Object event) {
    Log.e(TAG,"QueuingEventSink:"+eventQueue.size());
    enqueue(event);
    //maybeFlush();
  }

  private  void enqueue(Object event) {
    if (done) {
      return;
    }
    eventQueue.add(event);
  }
  public void pause(){
    bpause = true;
  }

  public void restart(){
    bpause = false;
  }
  private  void maybeFlush() {
    if (delegate == null) {
      eventQueue.clear();
      try {
          Thread.sleep(500);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      return;
    }

    /*
    for (Object event : eventQueue) {
      if (event instanceof EndOfStreamEvent) {
        delegate.endOfStream();
      } else if (event instanceof ErrorEvent) {
        ErrorEvent errorEvent = (ErrorEvent) event;
        delegate.error(errorEvent.code, errorEvent.message, errorEvent.details);
      } else {
        delegate.success(event);
      }
    }
   */
    Object event = null;
    try {
        event = eventQueue.take();
    } catch (InterruptedException e) {
        e.printStackTrace();
        return;
    }

    if (event instanceof EndOfStreamEvent) {
      delegate.endOfStream();
      done = true;
    } else if (event instanceof ErrorEvent) {
      ErrorEvent errorEvent = (ErrorEvent) event;
      delegate.error(errorEvent.code, errorEvent.message, errorEvent.details);
    } else {
     // Log.e(TAG,"QueuingEventSink:"+event.toString());
      if(!bpause){
        delegate.success(event);
      }else{
        event = null;
      }
   }

    /*
    while (eventQueue.size() >0 ){
      HashMap<String, Object> event = (HashMap<String, Object> )eventQueue.poll();
      event.put("img",  null);
      event = null;
    }
    */
    // eventQueue.clear();
  }

  private static class EndOfStreamEvent {

  }

  private static class ErrorEvent {
    String code;
    String message;
    Object details;

    ErrorEvent(String code, String message, Object details) {
      this.code = code;
      this.message = message;
      this.details = details;
    }
  }
}
