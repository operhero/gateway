package com.gateway.handler;

import com.gateway.message.TimerMessage;
import com.gateway.server.GatewayServerBootstrap;
import com.gateway.server.MessageQueue;

import java.util.TimerTask;

public class MyTimer {
    class MyTask extends TimerTask {
        private String m_str;
        public MyTask(String str){
            m_str = str;
        }

        @Override
        public void run() {
            TimerMessage sm = new TimerMessage(m_str);
            m_msgQueue.offerMessage(sm);
        }
    }

    private final static MessageQueue m_msgQueue = new MessageQueue();

    public MyTimer(String str, int millscd, int t){
        if(t==0){
            GatewayServerBootstrap.getTimer().schedule(new MyTask(str), millscd);
        }else{
            GatewayServerBootstrap.getTimer().schedule(new MyTask(str), millscd, t);
        }
    }

    public static boolean Process(MainLoopHandler mainHandler){
        return m_msgQueue.Process(mainHandler);
    }
}
