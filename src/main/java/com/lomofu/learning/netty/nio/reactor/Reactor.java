package com.lomofu.learning.netty.nio.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Optional;
import java.util.Set;

/**
 * @author fujq13
 * @date 2023-08-08
 */
public class Reactor implements Runnable {
  private final Selector selector;

  private final ServerSocketChannel serverSocketChannel;

  public Reactor(int port) {
    try {
      serverSocketChannel = ServerSocketChannel.open();
    } catch (IOException e) {
      System.out.println("[Reactor] ServerSocketChannel open error");
      throw new RuntimeException(e);
    }

    try {
      serverSocketChannel.bind(new InetSocketAddress(port));
    } catch (IOException e) {
      System.out.println("[Reactor] ServerSocketChannel bind error");
      throw new RuntimeException(e);
    }

    try {
      serverSocketChannel.configureBlocking(false);
    } catch (IOException e) {
      System.out.println("[Reactor] ServerSocketChannel configureBlocking error");
      throw new RuntimeException(e);
    }

    SelectorProvider selectorProvider = SelectorProvider.provider();
    try {
      selector = selectorProvider.openSelector();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
      // 将事件绑定处理对象
      selectionKey.attach(new Acceptor(serverSocketChannel, selector));
    } catch (ClosedChannelException e) {
      System.out.println("[Reactor] ServerSocketChannel register error");
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    try {
      while (!Thread.interrupted()) {
        selector.select();
        Set<SelectionKey> selectionKeys = selector.selectedKeys();

        for (SelectionKey selectionKey : selectionKeys) {
          dispatch(selectionKey);
        }

        selectionKeys.clear();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // reactor将事件进行分发
  private void dispatch(SelectionKey selectionKey) {
    // 获得每一个事件的处理对象
    Runnable runnable = (Runnable) selectionKey.attachment();
    Optional.ofNullable(runnable).ifPresent(Runnable::run);
  }
}
