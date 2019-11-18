package com.bigtree.rpc.dubbo.demo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xianqiangliu
 */
public class RpcFramework {

    /**
     * 暴露服务
     * @param service 服务实现
     * @param port 服务端口
     * @throws Exception
     */
    public static void export(final Object service,int port) throws Exception {
        if(service == null) throw new IllegalArgumentException("service instance == null");
        if(port <= 0 || port > 65535) throw new IllegalArgumentException("Invalid port " + port);
        System.out.println("Export serice " +service.getClass().getName() + " on port "  + port);
        //服务端套接字
        ServerSocket server  = new ServerSocket(port);
        for(;;){
            try {
                final Socket socket = server.accept();
                //创建线程接收客户端请求
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            try{
                                //序列化
                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                                try{
                                    String methodName = input.readUTF();
                                    Class<?>[] parameterTypes = (Class<?>[])input.readObject();
                                    Object[] arguments = (Object[])input.readObject();
                                    //反序列化
                                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                                    try{
                                        Method method = service.getClass().getMethod(methodName,parameterTypes);
                                        Object result = method.invoke(server,arguments);
                                        output.writeObject(result);
                                    } catch(Throwable t){
                                        output.writeObject(t);
                                    }finally {
                                        output.close();
                                    }
                                }finally {
                                    input.close();
                                }
                            }finally {
                                socket.close();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 引用远程接口服务
     * @param interfaceClass 接口类型
     * @param host 远程服务器主机
     * @param port 远程服务器端口
     * @param <T> 接口泛型
     * @return 远程服务
     * @throws Exception
     */
    public static <T> T  refer(final Class<T> interfaceClass, final String host, final int port) throws Exception {
        if(interfaceClass == null) throw new IllegalArgumentException("Interface class == null");
        if(!interfaceClass.isInterface()) throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");
        if(host == null || host.length() == 0) throw new IllegalArgumentException("Host == null");
        if(port <= 0 || port > 65535) throw new IllegalArgumentException("Invalid port " + port);

        System.out.println("Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);
        //创建动态代理对象，利用动态代理，对接口类的方法调用进行的隐藏
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class<?>[]{interfaceClass},new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket(host,port);
                try {
                    //反序列化
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    try{
                        output.writeUTF(method.getName());
                        output.writeObject(method.getParameterTypes());
                        output.writeObject(args);

                        //序列化
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        try{
                            Object result = input.readObject();
                            if(result instanceof Throwable) throw (Throwable)result;
                            return result;
                        }finally {
                            input.close();
                        }
                    }finally{
                        output.close();
                    }
                }finally {
                    socket.close();
                }
            }
        });
    }
}
