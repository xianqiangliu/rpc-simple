package com.bigtree.rpc.dubbo.demo;

/**
 * @author xianqiangliu
 */
public class RpcConsumer {
    public static void main(String[] args) throws Exception {
        DubboService service = RpcFramework.refer(DubboService.class,"127.0.0.1",1234);
        for(int i=0; i < Integer.MAX_VALUE; i++){
            String result = service.hello("World" + i);
            System.out.println(result);
            Thread.sleep(1000);
        }
    }
}
