package com.gateway.server;

import com.gateway.handler.MyTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gateway.center.GameCache.TIME_FLUSH_MSG_TO_CLIENT;
import static com.gateway.center.GameCache.TIME_FLUSH_MSG_TO_GAME;

public class GatewayServer {
    private static final Logger logger = LoggerFactory.getLogger(GatewayServer.class);

    public static void main(String[] args) {
        try {
            new GatewayServerBootstrap(args);
            new MyTimer(TIME_FLUSH_MSG_TO_GAME, 100, 100);
            new MyTimer(TIME_FLUSH_MSG_TO_CLIENT, 200, 200);
        } catch (Exception e) {
            logger.error("Gateway start error", e);
        }

    }
}
