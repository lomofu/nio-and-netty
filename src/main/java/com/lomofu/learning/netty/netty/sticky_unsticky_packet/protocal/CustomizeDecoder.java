package com.lomofu.learning.netty.netty.sticky_unsticky_packet.protocal;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * 自定义协议的解码方案
 *
 * <p>协议： 数据长度 + 数据
 *
 * @author fujq13
 * @date 2023-08-08
 */
public class CustomizeDecoder extends ByteToMessageDecoder {
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    // 已知第一位用来表示数据长度，是一个int 一个int为4个字节，因此
    if (in.readableBytes() < 4) {
      return;
    }

    // 1. 读取数据包的长度
    int length = in.readInt();

    // 剩余可读字节数小于数据包长度，说明数据包还没有读完，等待下一次读取
    if (in.readableBytes() < length) {
      // 重置读取位置 (因为read的方法都会更新读索引，所以需要恢复）
      in.resetReaderIndex();
      return;
    }

    // 2. 读取数据包的内容
    byte[] content = new byte[length];
    in.readBytes(content);
    System.out.println(new String(content));

    // 读取完数据，将下标设置到此，下面未读的数据从这个下标开始
    in.markReaderIndex();
  }
}
