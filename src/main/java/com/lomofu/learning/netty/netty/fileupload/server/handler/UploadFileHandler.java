package com.lomofu.learning.netty.netty.fileupload.server.handler;

import com.lomofu.learning.netty.netty.fileupload.server.entity.File;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author fujq13
 * @date 2023-08-14
 */
public class UploadFileHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("客户端接入成功， 等待接收文件");
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof File file) {
      if (file.getCommand() == 1) {
        // 创建文件
        boolean exists = Files.exists(Path.of("./", file.getFileName()));
        if (!exists) {
          Files.createFile(Path.of("./files", file.getFileName()));
          System.out.println("创建文件：" + file.getFileName());
        }
      } else if (file.getCommand() == 2) {
        // 写文件
        FileChannel channel = FileChannel.open(Path.of("./files", file.getFileName()));
        channel.write(ByteBuffer.wrap(file.getData()));
        channel.close();
      }
    }
  }
}
