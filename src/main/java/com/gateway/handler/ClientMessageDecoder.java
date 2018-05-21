package com.gateway.handler;

import com.gateway.message.ClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

public class ClientMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    private Logger logger = LoggerFactory.getLogger(ClientMessageDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuffer, List<Object> list) throws Exception {
        String tmp = byteBuffer.toString();

        ClientMessage clientMessage = new ClientMessage();
        clientMessage.setMessage(tmp);
        list.add(clientMessage);
    }
}
