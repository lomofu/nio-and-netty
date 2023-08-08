package com.lomofu.learning.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 同步非阻塞 实现一个线程可以处理多个请求
 *
 * <p>一个线程无限循环list轮询，检查是否有读写操作，如果有就处理，没有就继续轮询
 *
 * @author fujq13
 * @date 2023-08-04
 */
public class MyServerSocketWithNIO {
  // nio 服务端
  private static ServerSocketChannel serverSocketChannel;

  private BlockingQueue<SocketChannel> queue;

  public static MyServerSocketWithNIO createServerSocket(int port) {
    try {
      serverSocketChannel = ServerSocketChannel.open();
    } catch (IOException e) {
      System.out.println("[ServerSocket打开异常] 异常信息: " + e.getLocalizedMessage());
      throw new RuntimeException(e);
    }

    try {
      serverSocketChannel.socket().bind(new InetSocketAddress(port));
    } catch (IOException e) {
      System.out.println("[ServerSocket绑定端口异常] 异常信息: " + e.getLocalizedMessage());
      throw new RuntimeException(e);
    }

    // 设置非阻塞
    try {
      serverSocketChannel.configureBlocking(false);
    } catch (IOException e) {
      System.out.println("[ServerSocket设置非阻塞模式失败] 异常信息: " + e.getLocalizedMessage());
      throw new RuntimeException(e);
    }

    System.out.println("[初始Socket完成] Socket 服务器运行在: " + port + " 端口");

    return new MyServerSocketWithNIO();
  }

  public void run() {
    queue = new LinkedBlockingQueue<>();

    while (true) {

      try {
        // 非阻塞模式不会阻塞
        // NIO的非阻塞是由操作系统内部实现的，底层调用linux内核的accept函数
        SocketChannel socketChannel = serverSocketChannel.accept();

        if (Objects.nonNull(socketChannel)) {
          System.out.println("[客户端接入] 客户端信息: " + socketChannel.getRemoteAddress());

          // 设置SocketChannel为非阻塞
          socketChannel.configureBlocking(false);

          // 保存客户端到队列中
          if (queue.offer(socketChannel)) {

            System.out.println("[客户端加入队列成功]");
          }
        }
      } catch (IOException e) {
        System.out.println("[非阻塞等待异常] 异常信息: " + e.getLocalizedMessage());
        throw new RuntimeException(e);
      }

      Iterator<SocketChannel> iterator = queue.iterator();

      while (iterator.hasNext()) {
        SocketChannel channel = iterator.next();
        try {
          ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

          int readSize = channel.read(byteBuffer);

          if (readSize > 0) {
            System.out.println(
                "[读取客户端 "
                    + channel.getRemoteAddress()
                    + " 的内容][线程ID: "
                    + Thread.currentThread().getName()
                    + "] 内容: "
                    + new String(byteBuffer.array(), 0, readSize));
          } else if (readSize == -1) { // 客户端断开连接
            System.out.println("[客户端断开连接] 客户端信息: " + channel.getRemoteAddress());
            iterator.remove();
          }

        } catch (IOException e) {
          System.out.println("[读取客户端内容失败], 异常信息 " + e.getLocalizedMessage());
        }
      }
    }
  }

  public static void main(String[] args) {
    MyServerSocketWithNIO serverSocketWithNIO = MyServerSocketWithNIO.createServerSocket(8080);
    serverSocketWithNIO.run();
  }
}
