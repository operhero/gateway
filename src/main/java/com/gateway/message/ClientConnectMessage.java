package com.gateway.message;

import com.gateway.center.GameCache;
import com.gateway.center.GateCenter;
import com.gateway.center.User;

import java.net.InetSocketAddress;

public class ClientConnectMessage extends Message {
    @Override
    public void processMessage() {
        if(GameCache.IsLogicServerConnected()){
            User newuser = new User();
            newuser.channel = getChannel();

            GateCenter.Instance().AddConn(getChannel(), newuser);
            try{
                newuser.m_strIp = ((InetSocketAddress)getChannel().remoteAddress()).getAddress().getHostAddress();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            getChannel().close();
        }
    }
}
