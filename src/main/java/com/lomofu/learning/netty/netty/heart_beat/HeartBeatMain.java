package com.lomofu.learning.netty.netty.heart_beat;
/**
 * @author fujq13
 * @date 2023-08-08
 */
public class HeartBeatMain {
  public static void main(String[] args) {
    NettyServer server = new NettyServer(8080);
    server.run();
  }
}
