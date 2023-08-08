package com.lomofu.learning.netty.netty.chatRoom;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.nio.charset.Charset;

/**
 * 服务端
 *
 * @author fujq13
 * @date 2023-08-04
 */
public class NettyServer {
  private final int port;

  public NettyServer(int port) {
    this.port = port;
  }

  public void run() {

    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    ServerBootstrap serverBootstrap = new ServerBootstrap();

    serverBootstrap
        .group(bossGroup, workerGroup) // 主从reactor模型
        .channel(NioServerSocketChannel.class) // 每一个连接建立一个channel
        .childHandler(
            new ChannelInitializer<SocketChannel>() {

              @Override
              protected void initChannel(SocketChannel ch) throws Exception { // 责任链模式
                // 增加字符串编解码器
                ch.pipeline().addLast("encoder", new StringEncoder(Charset.defaultCharset()));
                ch.pipeline().addLast("decoder", new StringDecoder(Charset.defaultCharset()));
                ch.pipeline().addLast(new ChatRoomHandler());
              }
            })
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true);

    try {
      ChannelFuture future = serverBootstrap.bind(port);
      System.out.println("[Server] started at port " + port + "...");
      future.sync();

      future.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      System.out.println("[Server] started failed!");
      throw new RuntimeException(e);
    } finally {
      // 管理线程池
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
