package com.gateway.center;

import com.gateway.handler.MainLoopHandler;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.gateway.center.GameCache.TIME_FLUSH_MSG_TO_CLIENT;
import static com.gateway.center.GameCache.TIME_FLUSH_MSG_TO_GAME;

public class GateCenter {
    private static final Logger logger = LoggerFactory.getLogger(GateCenter.class);

    private static GateCenter s_instance = new GateCenter();

    public static GateCenter Instance() {
        return s_instance;
    }

    private HashMap<Channel, User> m_setAllconn = new HashMap<Channel, User>();
    private HashMap<Long, User> m_loginConn = new HashMap<Long, User>();
    private int msgLen = 0;
    public Channel m_gameserver = null;

    public void AddConn(Channel cnl, User usr) {
        m_setAllconn.put(cnl, usr);
    }

    public void RemoveConn(Channel cnl) {
        User usr = m_setAllconn.get(cnl);
        m_setAllconn.remove(cnl);
    }

    public User GetUserByAccount(long account) {
        return m_loginConn.get(account);
    }

    public User GetUserByChannel(Channel channel){
        return m_setAllconn.get(channel);
    }

    public void OnChannelDisconnect(Channel cnl) {
        User usr = m_setAllconn.get(cnl);
        if (usr == null) {
            return;
        }

        RemoveConn(cnl);
        usr.channel = null;
    }

    public void OnTimer(String message) {
        if (TIME_FLUSH_MSG_TO_GAME.equals(message)) {
            OnFlushGameMsg();
        } else if (TIME_FLUSH_MSG_TO_CLIENT.equals(message)) {
            OnFlushClientMsg();
        }
    }

    private void OnFlushGameMsg() {
        if (m_gameserver == null) {
            return;
        }

        if (msgLen > 0) {
            m_gameserver.flush();
            msgLen = 0;
        }
    }

    private void OnFlushClientMsg() {
        Iterator<Map.Entry<Channel, User>> iter = this.m_setAllconn.entrySet().iterator();
        int send = 0;
        int total = 0;
        while (iter.hasNext()) {
            Map.Entry<Channel, User> entry = iter.next();
            User u = entry.getValue();
            int size = u.m_lkdString.size();
            u.OnFlushMsgTimer();
            int remain = u.m_lkdString.size();
            send += size - remain;
            total += remain;
        }
        if (send > 0 || total > 0) {
            logger.debug("msg send : " + send + ", remain: " + total);
        }
    }

    public void SendMsgToClient(long idUser, String msg) {
        SendMsgToClientByAccount(idUser / MainLoopHandler.ACCOUNT_ID_BASE, msg);
    }

    public boolean SendMsgToClientByAccount(long idAccount, String msg) {
        User u = GetUserByAccount(idAccount);
        return u != null && u.SendEncodeMsg(msg);
    }

    public void SendMsgToLogicServer(long idAccount, String msg){
        if(!m_gameserver.isActive()){
            logger.warn("WARNING!!!! m_gameserver channel isActive : false");
            return;
        }
        if(!m_gameserver.isWritable()){
            logger.warn("WARNING!!!! m_gameserver channel isWritable : false");
            return;
        }

        m_gameserver.write(msg);
                msgLen += msg.length();
        if(msgLen >1024){
            OnFlushGameMsg();
        }
    }

    public void DisconnectAllClient() {
        Iterator<Map.Entry<Channel, User>> iter = this.m_setAllconn.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<Channel, User> entry = iter.next();
            Channel channel = entry.getKey();
            channel.close();
        }
    }
}
