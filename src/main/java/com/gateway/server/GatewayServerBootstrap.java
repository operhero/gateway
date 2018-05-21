package com.gateway.server;

import com.gateway.center.GameCache;
import com.gateway.factory.GameServerChannelInitializer;
import com.gateway.factory.GatewayClientChannelInitializer;
import com.gateway.factory.NamedThreadFactory;
import com.gateway.handler.MainLoopHandler;
import com.gateway.handler.MessageLoopHandler;
import com.gateway.handler.MyTimer;
import com.gateway.handler.ServerDispatchHandle;
import com.gateway.thread.MessageHandlerThread;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GatewayServerBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(GatewayServerBootstrap.class);
    private static final Timer timer = new Timer(true);
    private static Properties prop = new Properties();
    private static Channel gameServerChannel = null;
    private ExecutorService es = Executors.newSingleThreadExecutor(new NamedThreadFactory("Gateway-Message-Loop"));
    private static Runnable messageLoopThread = null;

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1, new NamedThreadFactory("Gateway-Nio-Boss"));
    private EventLoopGroup workerGroup = new NioEventLoopGroup(0, new NamedThreadFactory("Gateway-Nio-Worker"));
    private EventLoopGroup gameServerWorkerGroup = new NioEventLoopGroup(1, new NamedThreadFactory("Gateway-Nio-Worker", Thread.MAX_PRIORITY - 1));

    private static final Map<String, MessageQueue> nioThreadQueueMap = new HashMap<String, MessageQueue>();
    public GatewayServerBootstrap(String[] args) throws Exception {
        loadProperty(args);
        startMessageLoop();
        startGatewayServer();
        startGameClient(gameServerWorkerGroup);
    }

    public static Map<String, MessageQueue> getNioThreadQueueMap() {
        return nioThreadQueueMap;
    }

    public static Timer getTimer() {
        return timer;
    }

    private void loadProperty(String[] args) throws IOException {
        try {
            if (args != null && args.length > 0) {
                Reader reader = new FileReader(args[0]);
                prop.load(reader);
                reader.close();
                if (args.length > 1) {
                    prop.setProperty("server.port", args[1]);
                    if (args.length > 2) {
                        prop.setProperty("game.port", args[2]);
                    }
                }
            } else {
                InputStream in = GatewayServerBootstrap.class.getResourceAsStream("/system.properties");
                prop.load(in);
                in.close();
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void startMessageLoop() {
        final MainLoopHandler aMainLoopHandler = new MainLoopHandler();
        es.execute(new MessageHandlerThread() {
            public void run() {
                messageLoopThread = Thread.currentThread();
                while (true) {
                    try {
                        ServerDispatchHandle.Process(aMainLoopHandler);
                        processClientMessage(aMainLoopHandler);
                        MyTimer.Process(aMainLoopHandler);
                        Thread.sleep(5);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            // Ignor
                        }
                    }
                }
            }

            @Override
            public MessageLoopHandler getMessageLoopHandler() {
                return aMainLoopHandler;
            }
        });
    }

    private boolean processClientMessage(MainLoopHandler mainHandler){
        boolean hasMsg =false;
        Iterator<Map.Entry<String, MessageQueue>> it = nioThreadQueueMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, MessageQueue> entry = it.next();
            if(!entry.getValue().Process(mainHandler)){
                hasMsg = true;
            }
        }
        return hasMsg;
    }

    private void startGatewayServer() throws InterruptedException {
        int port = Integer.parseInt(prop.getProperty("server.port"));
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class);
        b.handler(new LoggingHandler(LogLevel.DEBUG));
        b.childHandler(new GatewayClientChannelInitializer());
        b.option(ChannelOption.SO_BACKLOG, 2048);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        b.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024));
        ChannelFuture f = b.bind(port).sync();
        if (f.isSuccess()) {
            logger.error("Gateway server start success on port : " + port);
        } else {
            throw new RuntimeException("Gateway start error");
        }
    }

    public void startGameClient(EventLoopGroup eventLoopGroup) {
        String host = prop.getProperty("game.host");
        int port = Integer.parseInt(prop.getProperty("game.port"));
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.SO_SNDBUF, 1024 * 1024 * 50);
        b.option(ChannelOption.SO_RCVBUF, 1024 * 1024 * 50);
        b.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536));
        b.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1024 * 1024 * 40, 1024 * 1024 * 50));

        b.handler(new GameServerChannelInitializer(this));
        b.connect(host, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    logger.error("Gateway connect to gameserver success");
                    GameCache.setLogicServerIsReady(true);
                    setGameServerChannel(channelFuture.channel());
                } else {
                    logger.info("Gateway reconnect to gameserver at 5 sec..");
                    final EventLoop eventLoop = channelFuture.channel().eventLoop();
                    eventLoop.schedule(new Runnable() {
                        @Override
                        public void run() {
                            startGameClient(eventLoop);
                        }
                    }, 5, TimeUnit.SECONDS);
                }
            }
        });
    }

    public static void setGameServerChannel(Channel gameClientChannel) {
        GatewayServerBootstrap.gameServerChannel = gameClientChannel;
    }
}
