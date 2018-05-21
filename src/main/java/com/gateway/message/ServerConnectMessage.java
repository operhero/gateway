package com.gateway.message;

import com.gateway.center.GateCenter;

public class ServerConnectMessage extends Message {
    @Override
    public void processMessage() {
        GateCenter.Instance().m_gameserver = getChannel();
    }
}
