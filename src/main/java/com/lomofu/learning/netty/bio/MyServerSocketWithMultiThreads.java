package com.lomofu.learning.netty.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server Socket 2.0 BIO 使用 多线程处理每一个请求
 *
 * <p>缺点：多少个请求就会创建多少个线程
 *
 * @author fujq13
 * @date 2023-08-03
 */
public class MyServerSocketWithMultiThreads {
  private static ServerSocket serverSocket;

  public static MyServerSocketWithMultiThreads createServerSocket(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("[初始Socket完成] Socket 服务器运行在: " + port + " 端口");
      return new MyServerSocketWithMultiThreads();
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

        HandlerTask handlerTask = new HandlerTask(fromClientSocket);
        handlerTask.start();
      } catch (IOException e) {
        System.out.println("[阻塞等待异常] 异常信息: " + e.getLocalizedMessage());
      }
    }
  }

  /** 处理每一个请求的线程 */
  private final class HandlerTask extends Thread {
    private final Socket socket;

    public HandlerTask(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
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
  }

  public static void main(String[] args) {
    MyServerSocketWithMultiThreads serverSocketWithMultiThreads =
        MyServerSocketWithMultiThreads.createServerSocket(8080);
    serverSocketWithMultiThreads.run();
  }
}
