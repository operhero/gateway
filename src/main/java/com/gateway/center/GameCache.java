package com.gateway.center;

public class GameCache {
    public static final String TIME_FLUSH_MSG_TO_GAME = "flush_msg_2_game";
    public static final String TIME_FLUSH_MSG_TO_CLIENT = "flush_msg_2_client";

    public static final String SPLIT_SHARP_STRING = "#";
    public static final String SPLIT_EMPTY = " ";

    private static boolean logicServerIsReady;
    public static void setLogicServerIsReady(boolean logicServerIsReady){
        GameCache.logicServerIsReady = logicServerIsReady;
    }
    public static boolean IsLogicServerConnected() {
        return logicServerIsReady;
    }
}
