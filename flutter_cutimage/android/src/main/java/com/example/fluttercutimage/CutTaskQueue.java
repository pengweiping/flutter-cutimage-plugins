package com.example.fluttercutimage;


import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created Date: 2020/7/14
 * Description:
 */
public class CutTaskQueue {

    /// 等待测试的最大任务数，当CPU过载时，清除无效任务
    private int maxWaitTask;

    /// 有效延时(ms)，任务请求的时间戳超过maxDelayThreshold，可以丢弃，不进行分割任务处理
    private int maxDelayThreshold;


    private LinkedBlockingQueue<CutTaskContext> cutTasks;

    public CutTaskQueue(int maxWaitTask, int maxDelayThreshold) {
        this.maxWaitTask = maxWaitTask;
        this.maxDelayThreshold = maxDelayThreshold;
        this.cutTasks = new LinkedBlockingQueue<CutTaskContext>();
    }

    public void  enqueue(CutTaskContext taskContext){
        this.cutTasks.add(taskContext);

    }

    public  int size(){
        return  this.cutTasks.size();
    }
    public CutTaskContext dequeue(){
        try{
            CutTaskContext task = this.cutTasks.take();
            if((System.currentTimeMillis()-task.timestamp)>maxDelayThreshold){
                return  null;
            }
            return  task;
        }catch (InterruptedException e){
            return  null;
        }
    }

    /// 清除无效任务项
    public void  clear(){

    }
    /// 清除无效任务项
    public void  removeAll(){

    }
}
