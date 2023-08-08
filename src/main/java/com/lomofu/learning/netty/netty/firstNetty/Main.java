package com.lomofu.learning.netty.netty.firstNetty;
/**
 * @author fujq13
 * @date 2023-08-04
 */
public class Main {
  public static void main(String[] args) {
    DiscardServer discardServer = new DiscardServer(8080);
    discardServer.run();
  }
}
