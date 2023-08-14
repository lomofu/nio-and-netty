package com.lomofu.learning.netty.netty.sticky_unsticky_packet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;

/**
 * 解决TCP 粘包、拆包问题
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
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    ServerBootstrap serverBootstrap = new ServerBootstrap();

    serverBootstrap
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(
            new ChannelInitializer<ServerChannel>() {
              @Override
              protected void initChannel(ServerChannel ch) throws Exception {

                // 1. 使用固定长度数据包：FixedLengthFrameDecoder
                ch.pipeline().addLast(new FixedLengthFrameDecoder(1024));

                // 2. 使用特殊字符进行分割：DelimiterBasedFrameDecoder 例如这里使用$
                ch.pipeline()
                    .addLast(
                        new DelimiterBasedFrameDecoder(
                            1024, Unpooled.copiedBuffer("$".getBytes())));
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
