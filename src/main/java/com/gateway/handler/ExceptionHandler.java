package com.gateway.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class ExceptionHandler extends ChannelDuplexHandler {
    private Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise future) {
        final SocketAddress remoteAdd = remoteAddress;
        final SocketAddress localAdd = localAddress;
        ctx.connect(remoteAddress, localAddress, future.addListener(new FutureListener<Void>() {
            public void operationComplete(Future<Void> f) {
                if (!f.isSuccess()) {
                    logger.info(String.format("connect failed, remote address %s , local address %s",
                            remoteAdd == null ? "" : remoteAdd.toString(), localAdd == null ? "" : localAdd.toString()));
                }
            }
        }));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        final Object m = msg;
        ctx.write(msg, promise.addListener(new FutureListener<Void>() {
            public void operationComplete(Future<Void> f) {
                if (!f.isSuccess()) {
                    Throwable t = f.cause();
                    logger.warn(String.format("write failed, msg %s, cause %s", m.toString(), t == null ? "null" : t.getMessage()));
                }
            }
        }));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    }
}
