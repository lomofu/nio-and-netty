package com.lomofu.learning.netty.netty.firstNetty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty服务器
 *
 * @author fujq13
 * @date 2023-08-04
 */
public class DiscardServer extends ChannelInboundHandlerAdapter {
  private final int port;

  public DiscardServer(int port) {
    this.port = port;
  }

  public void run() {
    // 第一步： 可以暂时理解创建的线程池
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    ServerBootstrap serverBootstrap = new ServerBootstrap(); // 第二步

    serverBootstrap
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class) // 第三步
        .childHandler(
            new ChannelInitializer<SocketChannel>() { // 第四步
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new DiscardServerHandler());
              }
            })
        .option(ChannelOption.SO_BACKLOG, 128) // 第五步
        .childOption(ChannelOption.SO_KEEPALIVE, true); // 第六步

    try {
      // Bind and start to accept incoming connections.
      ChannelFuture future = serverBootstrap.bind(port);

      System.out.println("Server started at port " + port + "...");

      future.sync(); // 第七步

      // Wait until the server socket is closed.
      future.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
