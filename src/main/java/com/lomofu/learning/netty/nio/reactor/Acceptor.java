package com.lomofu.learning.netty.nio.reactor;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Reactor 模型中，Acceptor 用于处理客户端连接请求
 *
 * @author fujq13
 * @date 2023-08-08
 */
public class Acceptor implements Runnable {

  private final ServerSocketChannel serverSocketChannel;
  private final Selector selector;

  public Acceptor(ServerSocketChannel serverSocketChannel, Selector selector) {
    this.serverSocketChannel = serverSocketChannel;
    this.selector = selector;
  }

  @Override
  public void run() {

    // 获取客户端连接
    try {
      SocketChannel socketChannel = serverSocketChannel.accept();

      if (socketChannel !=null){
        new Handler(socketChannel, selector);
      }

    } catch (IOException e) {
      System.out.println("[Acceptor] ServerSocketChannel accept error");
      throw new RuntimeException(e);
    }
  }
}
