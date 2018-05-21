package com.gateway.message;

import com.gateway.center.GateCenter;

public class ClientDisconnectMessage extends Message {
    @Override
    public void processMessage() {
        GateCenter.Instance().OnChannelDisconnect(getChannel());
    }
}
