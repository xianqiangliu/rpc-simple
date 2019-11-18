package com.bigtree.rpc.provider;

/**
 * @author xianqiangliu
 */
public class ServerBootstrap {
    public static void main(String[] args){
        NettyServer.startServer("127.0.0.1",8090);
    }
}
