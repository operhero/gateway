package com.gateway.message;

import com.gateway.center.GateCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gateway.center.GameCache.SPLIT_EMPTY;

public class ServerMessage extends Message {
    private static final Logger logger = LoggerFactory.getLogger(ServerMessage.class);

    private String message;
    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    @Override
    public void processMessage() {
        super.processMessage();

        int idx = message.lastIndexOf(SPLIT_EMPTY);
        if(idx == -1){
            return;
        }

        String sTarget = message.substring(idx+1);
        long idTarget = Long.parseLong(sTarget);

        GateCenter.Instance().SendMsgToClient(idTarget, message);
    }
}
