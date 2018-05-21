package com.gateway.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.gateway.server.GatewayServerBootstrap;
import com.gateway.server.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamedThreadFactory implements ThreadFactory {
    private Logger logger = LoggerFactory.getLogger(NamedThreadFactory.class);
    private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);
    private final AtomicInteger mThreadNum = new AtomicInteger(1);
    private final String mPredix;
    private final boolean mDaemo;
    private final ThreadGroup mGroup;
    private int priority = 0;
    public NamedThreadFactory(){
        this("pool-"+POOL_SEQ.getAndIncrement(), false);
    }

    public NamedThreadFactory(String prefix){
        this(prefix, false);
    }

    public NamedThreadFactory(String prefix, boolean daemo){
        this(prefix, daemo, 0);
    }

    public NamedThreadFactory(String prefix, int priority){
        this(prefix, false, priority);
    }

    public NamedThreadFactory(String predix, boolean daemo, int priority){
        this.mPredix = predix+"-thread-";
        this.mDaemo = daemo;
        SecurityManager s = System.getSecurityManager();
        this.mGroup = (s==null)?Thread.currentThread().getThreadGroup():s.getThreadGroup();
        this.priority = priority;
    }

    public Thread newThread(Runnable runnable){
        String name = mPredix+ mThreadNum.getAndIncrement();
        Thread ret = new Thread(mGroup, runnable, name, 0);
        GatewayServerBootstrap.getNioThreadQueueMap().put(name, new MessageQueue());
        ret.setDaemon(mDaemo);
        if(priority>0){
            ret.setPriority(priority);
        }
        ret.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error(String.format("thread : %s, throws s%", t.getName(), e.getMessage()), e);
            }
        });
        return ret;
    }
}
