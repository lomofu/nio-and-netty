package com.lomofu.learning.netty.netty.fileupload.server.protocal;

import com.lomofu.learning.netty.netty.fileupload.server.entity.File;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * 解码文件上传
 *
 * @author fujq13
 * @date 2023-08-14
 */
public class FileUploadDecoder extends ByteToMessageDecoder {
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    System.out.println("decode 开始");

    // 组成 command(是创建文件，还是写文件) 4字节， fileName 4字节 总共8字节
    if (in.readableBytes() < 8) {
      return;
    }

    int command = in.readInt();
    int fileNameLen = in.readInt();

    if (in.readableBytes() < fileNameLen) {
      in.resetReaderIndex();
      return;
    }

    byte[] data = new byte[fileNameLen];
    in.readBytes(data);
    String fileName = new String(data);

    File file = new File();
    file.setFileName(fileName);
    file.setCommand(command);

    // 2时 为读数据
    if (command == 2) {
      // 读取文件数据长度
      int dataLen = in.readInt();

      if (in.readableBytes() < dataLen) {
        in.resetReaderIndex();
        return;
      }

      // 读取文件数据
      byte[] fileData = new byte[fileNameLen];
      in.readBytes(fileData);

      file.setData(fileData);
    }
    // 读取完数据，将下标设置到此，下面未读的数据从这个下标开始
    in.markReaderIndex();
    // 抛到下一个handler处理
    out.add(file);
  }
}
