package com.example.fluttercutimage;

import android.util.Log;

/**
 * Created Date: 2020/7/14
 * Description:
 */
public class CutTask implements  Runnable, Comparable<CutTask>{

    public  final static  String TAG = "CutTask";


    private CutTaskContext taskContext;

    public CutTaskContext getTaskContext() {
        return taskContext;
    }

    private ImageCuter cuter;
    public CutTask(CutTaskContext taskContext,ImageCuter cuter) {
        super();
        this.taskContext = taskContext;
        this.cuter = cuter;
    }

    @Override
    public int compareTo(CutTask cutTask) {
        return  this.getTaskContext().timestamp < cutTask.getTaskContext().timestamp?-1:1;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (taskContext != null){
            taskContext = null;
        }
    }

    @Override
    public void run() {
        if(taskContext != null){
            // 分割任务时间戳过期1s钟，丢弃当前分割任务
            if((System.currentTimeMillis()- taskContext.timestamp)> 1000){
                Log.e(TAG,"CutTask ==================>delete this cut request!!!!!!!!!!!");
                taskContext = null;
            }else{
                cuter.doImageCut(taskContext);
                taskContext = null;
            }
        }
    }
}
