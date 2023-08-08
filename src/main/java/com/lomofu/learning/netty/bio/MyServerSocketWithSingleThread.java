package com.lomofu.learning.netty.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO 使用ServerSocket实现网络编程, 最简单的单线程版本
 *
 * <p>缺点: 一次只能处理一个请求，多个请求进入会出现阻塞等待
 *
 * @author fujq13
 * @date 2023-08-02
 */
public class MyServerSocketWithSingleThread {
  private static ServerSocket serverSocket;

  public static MyServerSocketWithSingleThread createServerSocket(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("[初始Socket完成] Socket 服务器运行在: " + port + " 端口");
      return new MyServerSocketWithSingleThread();
    } catch (IOException e) {
      System.out.println("[初始化Socket异常] 异常信息: " + e.getLocalizedMessage());
      throw new RuntimeException(e);
    }
  }

  public void run() {
    // 循环一直等待客户端的链接
    while (true) {
      try {
        System.out.println("[服务器运行中] 等待请求...");
        Socket fromClientSocket = serverSocket.accept();
        this.handler(fromClientSocket);
      } catch (IOException e) {
        System.out.println("[阻塞等待异常] 异常信息: " + e.getLocalizedMessage());
      }
    }
  }

  public void handler(Socket socket) {
    byte[] readBytes = new byte[1024]; // 读取数据缓存 1MB
    System.out.println("[客户端接入] 客户端信息: " + socket.getInetAddress() + ":" + socket.getPort());
    try {
      int readSize = socket.getInputStream().read(readBytes);

      System.out.println(
          "[读取客户端内容][线程ID: "
              + Thread.currentThread().getName()
              + "] 内容: "
              + new String(readBytes, 0, readSize));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    MyServerSocketWithSingleThread serverSocketWithSingleThread =
        MyServerSocketWithSingleThread.createServerSocket(8080);
    serverSocketWithSingleThread.run();
  }
}
