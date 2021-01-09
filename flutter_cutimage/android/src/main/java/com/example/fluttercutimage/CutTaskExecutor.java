package com.example.fluttercutimage;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created Date: 2020/7/14
 * Description:
 */
public class CutTaskExecutor implements  Runnable{
    private int corePoolSize = 5;
    private int maximumPoolSize =10;
    private int maxtaskSize = 1000;
    private ThreadPoolExecutor executor;
    private CutTaskQueue tasks;
    private boolean bRun = false;
    private Thread dispatcher;
    private ImageCuter cuter;
    public  final static  String TAG = "CutTaskExecutor";
    static class NameTreadFactory implements ThreadFactory {
        private final AtomicInteger mThreadNum = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "my-thread-" + mThreadNum.getAndIncrement());
            System.out.println(t.getName() + " has been created");
            return t;
        }
    }

    public static class MyIgnorePolicy implements RejectedExecutionHandler {
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            doLog(r, e);
        }
        private void doLog(Runnable r, ThreadPoolExecutor e) {
            // 可做日志记录等
            System.err.println( r.toString() + " rejected");
            //System.out.println("completedTaskCount: " + e.getCompletedTaskCount());
        }
    }

    public CutTaskExecutor(int corePoolSize,int maximumPoolSize,int taskSize,CutTaskQueue tasks,ImageCuter cuter) {
        long keepAliveTime = 200;
        this.maxtaskSize = taskSize;
        this.tasks = tasks;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(maxtaskSize);
        //PriorityBlockingQueue<Runnable> workQueue = new PriorityBlockingQueue<Runnable>();
        ThreadFactory threadFactory = new NameTreadFactory();
        RejectedExecutionHandler handler = new MyIgnorePolicy();
        this.executor = new ThreadPoolExecutor(1, 1, 180*1000, unit,
                workQueue, threadFactory, handler);
        this.executor.prestartAllCoreThreads(); // 预启动所有核心线程
        this.dispatcher = new Thread(this);
        this.cuter = cuter;
    }

    public synchronized void  Start()  {
        if (!bRun){
            if(dispatcher != null){
                bRun = true;
                dispatcher.start();
            }
        }

    }
    public synchronized void  Stop()  {
        bRun = false;
        executor.shutdown();
    }

    @Override
    public void run() {
        while (bRun){
            if (tasks != null){
                CutTaskContext task =  tasks.dequeue();
                // CPU过载，休眠分派线程
                int active =executor.getActiveCount();
                int queuesize = executor.getQueue().size();
                if( queuesize >50 ){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }

                if(task != null){
                   // Log.e(TAG,"task:"+task.toString());
                    executor.execute(new CutTask(task,cuter));
                    Log.e(TAG,"1:"+executor.getActiveCount());
                    Log.e(TAG,"2:"+executor.getQueue().size());
                    Log.e(TAG,"3:"+executor.getTaskCount());
                    Log.e(TAG,"4:"+executor.getCompletedTaskCount());
                }
            }
        }

    }

}
