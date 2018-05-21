package com.gateway.handler;

import com.gateway.center.GameCache;
import com.gateway.message.ServerConnectMessage;
import com.gateway.message.ServerDisconnectMessage;
import com.gateway.message.ServerMessage;
import com.gateway.server.GatewayServerBootstrap;
import com.gateway.server.MessageQueue;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ServerDispatchHandle extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerMessageEncoder.class);
    private final static MessageQueue m_servermgrqueue = new MessageQueue();

    private GatewayServerBootstrap gatewayServerBootstrap;
    public ServerDispatchHandle(GatewayServerBootstrap gatewayServerBootstrap){
        this.gatewayServerBootstrap = gatewayServerBootstrap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ServerConnectMessage sc = new ServerConnectMessage();
        sc.setChannel(ctx.channel());
        m_servermgrqueue.offerMessage(sc);
    }

    public static boolean Process(MainLoopHandler mainHandler){
        return m_servermgrqueue.Process(mainHandler);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ServerMessage smsg = (ServerMessage)msg;
        smsg.setChannel(ctx.channel());
        logger.debug("receive gameserver msg : "+ smsg.getMessage());
        m_servermgrqueue.offerMessage(smsg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.error("reconnect to gameserver at 5 sec...");
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                gatewayServerBootstrap.startGameClient(eventLoop);
            }
        }, 5, TimeUnit.SECONDS);
        super.channelInactive(ctx);
        logger.error("socket close : "+ ctx.channel().remoteAddress());
        ServerDisconnectMessage disconnectMessage = new ServerDisconnectMessage();
        disconnectMessage.setChannel(ctx.channel());
        m_servermgrqueue.offerMessage(disconnectMessage);
        GameCache.setLogicServerIsReady(false);
    }
}
