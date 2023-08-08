package com.lomofu.learning.netty.netty.firstNetty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Netty服务器 Handler
 *
 * <p>DiscardServerHandler extends ChannelInboundHandlerAdapter, which is an implementation of
 * ChannelInboundHandler. ChannelInboundHandler provides various event handler methods that you can
 * override.
 *
 * <p>For now, it is just enough to extend ChannelInboundHandlerAdapter rather than to implement the
 * handler interface by yourself.
 *
 * @author fujq13
 * @date 2023-08-04
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
  /**
   * We override the channelRead() event handler method here. This method is called with the
   * received message, whenever new data is received from a client.
   *
   * <p>In this example, the type of the received message is ByteBuf.
   *
   * <p>Usually, channelRead() handler method is implemented like the following:
   *
   * <p>@Override
   *
   * <p>public void channelRead(ChannelHandlerContext ctx, Object msg)
   *
   * <p>{
   *
   * <p>try {
   *
   * <p>// Do something with msg
   *
   * <p>} finally { ReferenceCountUtil.release(msg); } }
   *
   * @param ctx
   * @param msg
   * @throws Exception
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf in = (ByteBuf) msg;

    try {
      System.out.println("[接收到客户端消息] " + in.toString(io.netty.util.CharsetUtil.US_ASCII));
    } finally {
      in.release();
    }
  }

  /**
   * The exceptionCaught() event handler method is called with a Throwable when an exception was
   * raised by Netty due to an I/O error or by a handler implementation due to the exception thrown
   * while processing events. In most cases, the caught exception should be logged and its
   * associated channel should be closed here, although the implementation of this method can be
   * different depending on what you want to do to deal with an exceptional situation. For example,
   * you might want to send a response message with an error code before closing the connection.
   *
   * @param ctx
   * @param cause
   * @throws Exception
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
