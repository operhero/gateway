package com.gateway.message;

import com.gateway.center.GateCenter;

public class ServerDisconnectMessage extends Message {
    @Override
    public void processMessage() {
        GateCenter.Instance().m_gameserver = null;
        GateCenter.Instance().DisconnectAllClient();
    }
}
