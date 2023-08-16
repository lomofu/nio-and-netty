package com.lomofu.learning.netty.netty.fileupload.server;

import com.lomofu.learning.netty.netty.fileupload.server.FileUploadServer;

/**
 * 文件上传测试
 *
 * @author fujq13
 * @date 2023-08-14
 */
public class UploadFileMain {

  /** 文件上传的步骤 1. 请求上传 2. 创建文件 3. 将客户端数据写入本地磁盘 */
  public static void main(String[] args) {
    FileUploadServer server = new FileUploadServer(8080);
    server.run();
  }
}
