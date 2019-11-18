package com.bigtree.rpc.dubbo.demo;

/**
 * @author xianqiangliu
 */
public class DubboServiceImpl implements DubboService {
    @Override
    public String hello(String name) {
        return "Hello " + name;
    }
}
