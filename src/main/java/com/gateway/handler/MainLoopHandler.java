package com.gateway.handler;

import com.gateway.message.Message;

public class MainLoopHandler extends MessageLoopHandler {
    public static long s_index = 1;
    public final static long ACCOUNT_ID_BASE = 10000;

    public void handler(Message msg) {
        msg.processMessage();
    }
}
