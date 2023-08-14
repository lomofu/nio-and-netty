package com.lomofu.learning.netty.nio.reactor;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * @author fujq13
 * @date 2023-08-08
 */
public class Handler implements Runnable {
  private final SocketChannel socketChannel;
  private final Selector selector;

  public Handler(SocketChannel socketChannel, Selector selector) {
    this.socketChannel = socketChannel;
    this.selector = selector;

    try {
      // 注册读事件
      SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
      selectionKey.attach(this); // 读事件处理对象为自己
      selector.wakeup();
    } catch (ClosedChannelException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    Set<SelectionKey> selectionKeys = selector.selectedKeys();

    for (SelectionKey selectionKey : selectionKeys) {
      Runnable handler = (Runnable) selectionKey.attachment();
      handler.run();
    }
    selectionKeys.clear();
  }
}
