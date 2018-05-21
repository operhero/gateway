package com.gateway.handler;

import com.gateway.message.ServerMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

public class ServerMessageDecoder extends MessageToMessageDecoder<ByteBuffer> {
    private Logger logger = LoggerFactory.getLogger(ClientMessageDecoder.class);

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuffer byteBuffer, List<Object> list) throws Exception {
        String tmp = byteBuffer.toString();

        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setMessage(tmp);
        list.add(serverMessage);
    }
}
