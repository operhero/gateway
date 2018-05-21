package com.gateway.factory;

import com.gateway.handler.ExceptionHandler;
import com.gateway.handler.ServerMessageDecoder;
import com.gateway.handler.ServerMessageEncoder;
import com.gateway.handler.ServerDispatchHandle;
import com.gateway.server.GatewayServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.nio.ByteOrder;

public class GameServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private GatewayServerBootstrap gatewayServerBootstrap;
    public GameServerChannelInitializer(GatewayServerBootstrap gatewayServerBootstrap){
        this.gatewayServerBootstrap = gatewayServerBootstrap;
    }
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 65535,0,2,0,0,true));
        pipeline.addLast("serverDecoder", new ServerMessageDecoder());

        pipeline.addLast("frameEncoder", new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 2, 0, false));
        pipeline.addLast("serverEncoder", new ServerMessageEncoder());

        pipeline.addLast(new ServerDispatchHandle(gatewayServerBootstrap));
        pipeline.addLast(new ExceptionHandler());
    }
}
