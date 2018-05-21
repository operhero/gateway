package com.gateway.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServerMessageEncoder extends MessageToMessageEncoder<String> {
    private Logger logger = LoggerFactory.getLogger(ServerMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, List<Object> list) throws Exception {
        ByteBuf bytebuf = Unpooled.wrappedBuffer(s.getBytes());
        list.add(bytebuf);
    }
}
