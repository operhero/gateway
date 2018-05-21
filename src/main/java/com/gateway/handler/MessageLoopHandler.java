package com.gateway.handler;

import com.gateway.message.Message;

public abstract class MessageLoopHandler {
    public volatile int msgNum = 0;
    public volatile int loopNum = 0;

    public void clear(){
        msgNum = 0;
        loopNum = 0;
    }

    public abstract void handler(Message msg);
}
