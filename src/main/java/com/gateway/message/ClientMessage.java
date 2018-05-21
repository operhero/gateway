package com.gateway.message;

import com.gateway.center.GateCenter;
import com.gateway.center.User;

public class ClientMessage extends Message {
    private String messsage;
    public String getMesssage(){return messsage;}
    public void setMessage(String message){this.messsage = message;}

    @Override
    public void processMessage(){
        User usr = GateCenter.Instance().GetUserByChannel(getChannel());
        if(usr!=null){
            usr.HandleClientMsg(this);
        }
    }
}
