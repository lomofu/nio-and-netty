package com.lomofu.learning.netty.netty.fileupload.server;

import com.lomofu.learning.netty.netty.fileupload.server.handler.UploadFileHandler;
import com.lomofu.learning.netty.netty.fileupload.server.protocal.FileUploadDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 文件上传 服务端配置
 *
 * @author fujq13
 * @date 2023-08-14
 */
public class FileUploadServer implements Runnable {
  private final int port;

  public FileUploadServer(int port) {
    this.port = port;
  }

  @Override
  public void run() {
    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workGroup = new NioEventLoopGroup();

    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap
        .group(bossGroup, workGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(
            new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new FileUploadDecoder());
                ch.pipeline().addLast(new UploadFileHandler());
              }
            })
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true);

    ChannelFuture channelFuture = serverBootstrap.bind(port);

    try {
      System.out.println("Server started at port " + port + "...");
      channelFuture.sync();

      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      bossGroup.shutdownGracefully();
      workGroup.shutdownGracefully();
    }
  }
}
