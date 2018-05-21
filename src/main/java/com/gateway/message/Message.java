package com.gateway.message;

import io.netty.channel.Channel;

public class Message {
    private Channel channel;
    public Channel getChannel(){return channel;}
    public void setChannel(Channel channel){this.channel = channel;}
    public void processMessage(){

    }
}
