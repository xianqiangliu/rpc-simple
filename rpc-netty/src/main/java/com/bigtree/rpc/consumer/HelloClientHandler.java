package com.bigtree.rpc.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

/**
 * @author xianqiangliu
 */
public class HelloClientHandler extends ChannelInboundHandlerAdapter implements Callable {
    private ChannelHandlerContext context;
    private String result;
    private String para;

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        context = ctx;
    }

    /**
     * 收到服务端响应数据，唤醒等待线程
     * @param ctx
     * @param msg
     */
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx,Object msg){
        result = msg.toString();
        notify();
    }

    /**
     * 写出数据，开始等待线程
     * @return
     * @throws InterruptedException
     */
    @Override
    public synchronized Object call() throws InterruptedException {
        context.writeAndFlush(para);
        wait();
        return result;
    }

    void setPara(String para){
        this.para = para;
    }
}
