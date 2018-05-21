package com.gateway.server;

import com.gateway.handler.MainLoopHandler;
import com.gateway.message.Message;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessageQueue {
    private ReadWriteLock m_lock = new ReentrantReadWriteLock();
    private Queue<Message> m_msgQueue = new LinkedList<Message>();

    public boolean Process(MainLoopHandler mainHandler){
        Queue<Message> que = getMsgQueue();
        if(que == null || que.isEmpty()){
            return false;
        }

        while(!que.isEmpty()){
            Message msg = que.poll();
            try{
                mainHandler.handler(msg);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return true;
    }

    private Queue<Message> getMsgQueue(){
        if(m_msgQueue.isEmpty()){
            return null;
        }

        Queue<Message> msgQueue = new LinkedList<Message>();
        Lock l = m_lock.writeLock();
        l.lock();
        try{
            msgQueue.addAll(m_msgQueue);
            m_msgQueue.clear();
        }finally {
            l.unlock();
        }
        return msgQueue;
    }

    public boolean offerMessage(Message message){
        Lock l = m_lock.writeLock();
        l.lock();
        try{
            m_msgQueue.offer(message);
        }finally {
            l.unlock();
        }
        return true;
    }
}
