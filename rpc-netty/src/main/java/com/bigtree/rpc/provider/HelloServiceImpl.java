package com.bigtree.rpc.provider;

import com.bigtree.rpc.publicInterface.HelloService;

/**
 * @author xianqiangliu
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String msg) {
        return msg != null ? msg +" I am fine." : "I am fine";
    }
}
