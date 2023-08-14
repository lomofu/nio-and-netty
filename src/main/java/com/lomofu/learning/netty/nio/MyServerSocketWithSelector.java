package com.lomofu.learning.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO 2.0
 *
 * <p>客户端连接请求都会注册到selector上，selector多路复用器轮询到连接有IO请求就进行处
 *
 * @author fujq13
 * @date 2023-08-04
 */
public class MyServerSocketWithSelector {
  private static ServerSocketChannel serverSocketChannel;
  private static Selector selector; // 多路复用器

  public static MyServerSocketWithSelector createServerSocket(int port) {

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

    try {
      serverSocketChannel.configureBlocking(false);
    } catch (IOException e) {
      System.out.println("[ServerSocket设置非阻塞模式失败] 异常信息: " + e.getLocalizedMessage());
      throw new RuntimeException(e);
    }

    try {
      // 打开Selector处理Channel, 即创建epoll
      selector = Selector.open();
    } catch (IOException e) {
      System.out.println("[Selector打开异常] 异常信息: " + e.getLocalizedMessage());
      throw new RuntimeException(e);
    }

    try {
      // 使用显式 SPI 提供程序：
      // SelectorProvider selectorProvider = SelectorProvider.provider();
      // selectorProvider.openSelector();
      // 将ServerSocket注册到Selector上, 并且selector对客户端连接请求感兴趣
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    } catch (ClosedChannelException e) {
      System.out.println("[ServerSocket注册到Selector失败] 异常信息: " + e.getLocalizedMessage());
      throw new RuntimeException(e);
    }

    System.out.println("[初始Socket完成] Socket 服务器运行在: " + port + " 端口");

    return new MyServerSocketWithSelector();
  }

  public void run() {

    while (true) {
      // 阻塞等待需要处理的事件发生，已注册事件发生后，会执行后面的逻辑
      try {
        selector.select();

        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();

        while (selectionKeyIterator.hasNext()) {
          SelectionKey selectionKey = selectionKeyIterator.next();

          if (selectionKey.isAcceptable()) {
            // 如果是连接事件
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            // 获取客户端连接
            SocketChannel socketChannel = serverSocketChannel.accept();
            // 设置非阻塞
            socketChannel.configureBlocking(false);
            // 这里只注册读事件, 如果需要给客户端发送数据可以注册写事件
            socketChannel.register(selector, SelectionKey.OP_READ);
          } else if (selectionKey.isReadable()) { // 如果是读事件
            SocketChannel channel = (SocketChannel) selectionKey.channel();
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
            } else if (readSize == -1) {
              System.out.println("[客户端断开连接] 客户端: " + channel.getRemoteAddress());
              channel.close();
            }
          }

          // 从事件集合中删除背刺处理的key，防止下次select重复处理
          selectionKeyIterator.remove();
        }

      } catch (IOException e) {
        System.out.println("[Selector 异常] 异常信息: " + e.getLocalizedMessage());
        throw new RuntimeException(e);
      }
    }
  }

  public static void main(String[] args) {
    MyServerSocketWithSelector serverSocket = MyServerSocketWithSelector.createServerSocket(8080);
    serverSocket.run();
  }
}
