package com.gateway.center;

import com.gateway.message.ClientMessage;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

import static com.gateway.center.GameCache.SPLIT_SHARP_STRING;

public class User {
    public static final Logger logger = LoggerFactory.getLogger(User.class);

    public Channel channel;
    public LinkedList<String> m_lkdString = new LinkedList<String>();
    public long m_idlogin;
    public String m_strIp;


    public void OnFlushMsgTimer() {
        StringBuilder sbl = new StringBuilder();
        while (m_lkdString.size() > 0) {
            if (channel.isActive() && channel.isWritable()) {
                String str = m_lkdString.pop();
                if (str.length() > 0) {
                    if (sbl.length() > 0) {
                        sbl.append(SPLIT_SHARP_STRING);
                    }
                    sbl.append(str);
                }
                if (sbl.length() > 16 * 1024) {
                    channel.writeAndFlush(sbl.toString());
                    sbl.setLength(0);
                }
            } else {
                break;
            }
        }
        if (sbl.length() > 0) {
            channel.writeAndFlush(sbl.toString());
            sbl = null;
        }
    }

    public boolean SendEncodeMsg(String msg) {
        if (channel.isActive()) {
            m_lkdString.add(msg);
            return true;
        }
        return false;
    }

    public void HandleClientMsg(ClientMessage clientMessage) {
        GateCenter.Instance().SendMsgToLogicServer(m_idlogin, clientMessage.getMesssage());
    }
}
