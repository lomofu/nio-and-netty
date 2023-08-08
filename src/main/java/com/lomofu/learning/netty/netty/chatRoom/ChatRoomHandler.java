package com.lomofu.learning.netty.netty.chatRoom;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author fujq13
 * @date 2023-08-04
 */
public class ChatRoomHandler extends ChannelInboundHandlerAdapter {
  static final Set<Channel> CHANNELS = new CopyOnWriteArraySet<>();

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("[Server] " + ctx.channel().remoteAddress() + " is online...");
    if (!CHANNELS.isEmpty()) {
      // 广播通知大家上线
      for (Channel channel : CHANNELS) {
        channel.writeAndFlush("[Server] " + ctx.channel().remoteAddress() + " is online...");
        System.out.println(
            "[Server] send message to " + channel.remoteAddress() + " successfully...");
      }
    }

    // 将自己加入到在线CHANNEL中
    CHANNELS.add(ctx.channel());
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (CHANNELS.isEmpty()) {
      return;
    }

    String user = ctx.channel().remoteAddress().toString();
    String message = (String) msg;

    System.out.println("[Server] Received from" + user + "'s message: " + message);

    // 广播给聊天室其他人
    CHANNELS.stream()
        .filter(e -> !e.equals(ctx.channel()))
        .forEach(
            e -> {
              e.writeAndFlush("[" + user + "] " + message + "\n");
              System.out.println(
                  "[Server] send message to " + e.remoteAddress() + " successfully...");
            });
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("[Server] " + ctx.channel().remoteAddress() + " is offline...");

    // 广播通知大家下线
    for (Channel channel : CHANNELS) {
      channel.writeAndFlush("[Server] " + ctx.channel().remoteAddress() + " is offline...");
    }

    // 将自己从在线CHANNEL中移除
    CHANNELS.removeIf(channel -> channel.equals(ctx.channel()));
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    System.out.println("[Server] " + ctx.channel().remoteAddress() + " is offline...");
  }
}
