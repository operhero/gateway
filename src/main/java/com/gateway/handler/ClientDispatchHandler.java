package com.gateway.handler;

import com.gateway.center.GameCache;
import com.gateway.message.ClientConnectMessage;
import com.gateway.message.ClientMessage;
import com.gateway.message.ClientDisconnectMessage;
import com.gateway.server.GatewayServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDispatchHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ChannelInboundHandlerAdapter.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ClientMessage clientMessage = (ClientMessage)msg;
        clientMessage.setChannel(ctx.channel());
        logger.debug("receive client msg: " + clientMessage.getMesssage());

        GatewayServerBootstrap.getNioThreadQueueMap().get(Thread.currentThread().getName()).offerMessage(clientMessage);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(GameCache.IsLogicServerConnected()){
            ClientConnectMessage cm = new ClientConnectMessage();
            cm.setChannel(ctx.channel());
            GatewayServerBootstrap.getNioThreadQueueMap().get(Thread.currentThread().getName()).offerMessage(cm);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ClientDisconnectMessage clientDisconnectMessage = new ClientDisconnectMessage();
        clientDisconnectMessage.setChannel(ctx.channel());
        GatewayServerBootstrap.getNioThreadQueueMap().get(Thread.currentThread().getName()).offerMessage(clientDisconnectMessage);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }
}
