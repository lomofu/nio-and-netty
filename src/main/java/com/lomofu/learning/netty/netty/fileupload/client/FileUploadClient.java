package com.lomofu.learning.netty.netty.fileupload.client;

import com.lomofu.learning.netty.netty.fileupload.client.handler.FileUploadClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author fujq13
 * @date 2023-08-14
 */
public class FileUploadClient {
  public static void main(String[] args) {
    NioEventLoopGroup group = new NioEventLoopGroup(1);

    Bootstrap bootstrap = new Bootstrap();
    bootstrap
        .group(group)
        .channel(NioSocketChannel.class)
        .handler(
            new ChannelInitializer<Channel>() {
              @Override
              protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new FileUploadClientHandler());
              }
            })
        .option(ChannelOption.SO_BACKLOG, 128);

    ChannelFuture channelFuture = bootstrap.connect("localhost", 8080);

    try {
      channelFuture.sync();
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      group.shutdownGracefully();
    }
  }
}
