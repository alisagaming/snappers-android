package com.emerginggames.snappers2.utils;

import android.os.Handler;
import android.os.HandlerThread;
import com.badlogic.gdx.utils.Array;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 18.06.12
 * Time: 13:18
 */
public class WorkerThreads {
    private static final int MAX_THREADS = 3;
    private static WorkerThreads instance;

    private Array<HandlerWrapper> activeHandlers;
    //private Pool<HandlerWrapper> handlerPool;

    public WorkerThreads() {
        activeHandlers = new Array<HandlerWrapper>(MAX_THREADS);
    }

    static HandlerWrapper getFreeHandler(){
        if (instance == null)
            instance = new WorkerThreads();

        return instance.getHandler();
    }

    HandlerWrapper getHandler(){
        synchronized (activeHandlers){
            if (activeHandlers.size < MAX_THREADS){
                HandlerThread thread = new HandlerThread("");
                thread.start();
                HandlerWrapper wrapper =  new HandlerWrapper(new Handler(thread.getLooper()), thread);
                activeHandlers.add(wrapper);
                return wrapper;
            }
            else {
                HandlerWrapper cur, min = activeHandlers.get(0);
                int minQueue = min.queueLength;

                for (int i=1; i< MAX_THREADS; i++){
                    cur = activeHandlers.get(i);
                    if (cur.queueLength < minQueue)
                        min = cur;
                }
                return min;
            }
        }
    }

    void killHandler(HandlerWrapper handler){
        synchronized (activeHandlers){
            activeHandlers.removeValue(handler, true);
        }
    }

    public static void run (Runnable runnable){
        getFreeHandler().run(runnable);
    }

    public static void run (final Runnable[] runnables){
        getFreeHandler().run(new Runnable() {
            @Override
            public void run() {
                for (Runnable runnable: runnables)
                    getFreeHandler().run(runnable);
            }
        });
    }

    class HandlerWrapper{
        Handler handler;
        HandlerThread thread;
        public int queueLength = 0;

        HandlerWrapper(Handler handler, HandlerThread thread) {
            this.handler = handler;
            this.thread = thread;
        }

        public void run(final Runnable r){
            queueLength++;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    r.run();
                    queueLength--;
                    if (queueLength == 0){
                        killHandler(HandlerWrapper.this);
                        thread.quit();
                    }
                }
            });
        }
    }
}
