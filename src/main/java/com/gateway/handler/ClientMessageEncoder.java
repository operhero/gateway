package com.gateway.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ClientMessageEncoder extends MessageToMessageEncoder<String> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, List<Object> list) throws Exception {
        byte[] msgBytes = s.getBytes();
        ByteBuf byteBuff = Unpooled.wrappedBuffer(msgBytes);
        list.add(byteBuff);
    }
}
