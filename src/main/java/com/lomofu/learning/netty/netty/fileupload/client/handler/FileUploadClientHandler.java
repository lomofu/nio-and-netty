package com.lomofu.learning.netty.netty.fileupload.client.handler;

import com.lomofu.learning.netty.netty.fileupload.client.entity.File;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

/**
 * @author fujq13
 * @date 2023-08-14
 */
public class FileUploadClientHandler extends SimpleChannelInboundHandler<File> {
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, File msg) throws Exception {
    System.out.println("收到服务端响应：" + msg);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("客户端连接成功");
    int count = 3;

    while (count > 1) {
      System.out.println(count + "秒后发送文件");
      count--;
      TimeUnit.SECONDS.sleep(1);
    }

    System.out.println("开始发送");
    java.io.File file =
        new java.io.File(
            "/Users/lomofu/Documents/Project/java/netty-learning/src/main/resources/test.text");
    int command = 1;
    int fileNameLen = "test.text".length();
    long dataLen = file.length();

    ctx.writeAndFlush(new File(command, fileNameLen, (int) dataLen, null).toString());

    ByteBuffer byteBuffer = ByteBuffer.allocate(10);
    FileChannel channel = FileChannel.open(file.toPath());

    while (channel.read(byteBuffer) != -1) {
      command = 2;
      ctx.writeAndFlush(
          new File(command, fileNameLen, (int) dataLen, byteBuffer.array()).toString());
      System.out.println("发送一次");
      byteBuffer.clear();
    }
    channel.close();
  }
}
