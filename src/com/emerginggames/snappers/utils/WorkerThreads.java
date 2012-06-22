package com.emerginggames.snappers.utils;

import android.os.Handler;
import android.os.HandlerThread;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 18.06.12
 * Time: 13:18
 */
public class WorkerThreads {
    private static WorkerThreads instance;

    private Array<Handler> activeHandlers;
    private Pool<Handler> handlerPool;

    public WorkerThreads() {
        activeHandlers = new Array<Handler>();
        handlerPool = new Pool<Handler>() {
            @Override
            protected Handler newObject() {
                HandlerThread thread = new HandlerThread("");
                thread.start();
                return new Handler(thread.getLooper());
            }
        };
    }

    static Handler getFreeHandler(){
        if (instance == null)
            instance = new WorkerThreads();

        Handler handler = instance.handlerPool.obtain();
        instance.activeHandlers.add(handler);
        return handler;
    }

    static void freeHandler(Handler handler){
        instance.activeHandlers.removeValue(handler, true);
        instance.handlerPool.free(handler);
    }

    public static void run (Runnable runnable){
        /*Handler h = getFreeHandler();
        ClassToRunInThread cl = new ClassToRunInThread(h, runnable);
        h.post(cl);*/
        HandlerThread th = new HandlerThread("web worker thread");
        th.start();
        Handler h = new Handler(th.getLooper());
        ClassToRunInThread cl = new ClassToRunInThread(th, runnable);
        h.post(cl);
    }

    static class ClassToRunInThread implements Runnable{
        Runnable toRun;
//        Handler currentHandler;
        HandlerThread thread;

/*        ClassToRunInThread(Handler currentHandler, Runnable toRun) {
            this.currentHandler = currentHandler;
            this.toRun = toRun;
        }*/

        ClassToRunInThread(HandlerThread thread, Runnable toRun) {
            this.thread = thread;
            this.toRun = toRun;
        }

        @Override
        public void run() {
            toRun.run();
            //freeHandler(currentHandler);
            thread.quit();
        }
    }
}
