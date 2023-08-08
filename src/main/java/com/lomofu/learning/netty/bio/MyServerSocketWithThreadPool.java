package com.lomofu.learning.netty.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author fujq13
 * @date 2023-08-03
 */
public class MyServerSocketWithThreadPool {
  private static ThreadPoolExecutor poolExecutor;
  private static ServerSocket serverSocket;

  public static MyServerSocketWithThreadPool createServerSocket(int port) {
    // 创建线程池
    poolExecutor =
        new ThreadPoolExecutor(
            5,
            10,
            60,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy());

    try {
      serverSocket = new ServerSocket(port);
      System.out.println("[初始Socket完成] Socket 服务器运行在: " + port + " 端口");
      return new MyServerSocketWithThreadPool();
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

        // 使用线程池处理每一个请求
        poolExecutor.execute(() -> handler(fromClientSocket));
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
    MyServerSocketWithThreadPool serverSocketWithThreadPool =
        MyServerSocketWithThreadPool.createServerSocket(8080);
    serverSocketWithThreadPool.run();
  }
}
