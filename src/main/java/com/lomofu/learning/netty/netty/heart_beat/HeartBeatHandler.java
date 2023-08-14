package com.lomofu.learning.netty.netty.heart_beat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义心跳机制
 *
 * @author fujq13
 * @date 2023-08-08
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
  AtomicInteger count = new AtomicInteger(0);

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    IdleStateEvent event = (IdleStateEvent) evt;
    int times = this.count.get();

    System.out.println("[Server] Trigger " + event.toString() + " current count is: " + times);

    // 读超时超过3次，关闭连接
    if (times >= 3) {
      System.out.println(
          "[Server] close client: " + ctx.channel().remoteAddress() + " cause it is idle!");
      ctx.channel().close();
    }

    if (IdleState.READER_IDLE == event.state()) {
      count.incrementAndGet();
    }
  }
}
