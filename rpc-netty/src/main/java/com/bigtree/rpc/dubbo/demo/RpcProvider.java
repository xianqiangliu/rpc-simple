package com.bigtree.rpc.dubbo.demo;

/**
 * @author xianqiangliu
 */
public class RpcProvider {
    public static void main(String[] args) throws Exception {
        DubboService service = new DubboServiceImpl();
        RpcFramework.export(service,1234);
    }
}
