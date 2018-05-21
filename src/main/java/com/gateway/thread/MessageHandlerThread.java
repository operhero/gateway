package com.gateway.thread;

import com.gateway.handler.MessageLoopHandler;

public abstract class MessageHandlerThread implements Runnable {
    public abstract MessageLoopHandler getMessageLoopHandler();
}
