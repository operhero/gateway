package com.gateway.factory;

import com.gateway.handler.ClientDispatchHandler;
import com.gateway.handler.ClientMessageDecoder;
import com.gateway.handler.ClientMessageEncoder;
import com.gateway.handler.ExceptionHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

public class GatewayClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private int readTimeOutSeconds = 15;
    protected void initChannel(SocketChannel socketChannel) throws Exception{
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 65535, 0, 2, 0,0,true));
        pipeline.addLast("clientDecoder", new ClientMessageDecoder());

        pipeline.addLast("frameEncoder", new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 2,0,false));
        pipeline.addLast("clientEncoder", new ClientMessageEncoder());

        pipeline.addLast(new IdleStateHandler(readTimeOutSeconds, 0,0, TimeUnit.SECONDS));
        pipeline.addLast(new ClientDispatchHandler());
        pipeline.addLast(new ExceptionHandler());
    }
}
