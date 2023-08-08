package com.lomofu.learning.netty.netty.chatRoom;
/**
 * 实现一个简单的聊天室
 *
 * @author fujq13
 * @date 2023-08-04
 */
public class ChatRoomMain {

  public static void main(String[] args) {
    NettyServer nettyServer = new NettyServer(8080);
    nettyServer.run();
  }
}
