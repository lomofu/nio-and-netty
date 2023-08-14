package com.lomofu.learning.netty.netty.heart_beat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 心跳机制
 *
 * @author fujq13
 * @date 2023-08-08
 */
public class NettyServer implements Runnable {
  private final int port;

  public NettyServer(int port) {
    this.port = port;
  }

  @Override
  public void run() {
    System.out.println("[Server] Boot Now on Thread" + Thread.currentThread().getName());
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    ServerBootstrap serverBootstrap = new ServerBootstrap();

    serverBootstrap
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(
            new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                // 官方提供的心跳机制
                ch.pipeline().addLast(new IdleStateHandler(2, 2, 5));
                ch.pipeline().addLast(new HeartBeatHandler());
              }
            })
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true);

    try {
      ChannelFuture channelFuture = serverBootstrap.bind(port);

      channelFuture.sync();

      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      System.out.println("[Server] started failed!");
      throw new RuntimeException(e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
