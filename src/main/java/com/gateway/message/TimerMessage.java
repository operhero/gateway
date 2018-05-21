package com.gateway.message;

import com.gateway.center.GateCenter;

public class TimerMessage extends Message {
    private String message;
    public TimerMessage(String msg){
        message = msg;
    }

    @Override
    public void processMessage() {
        GateCenter.Instance().OnTimer(message);
    }
}
