package com.lomofu.learning.netty.netty.fileupload.server.entity;

/**
 * 文件报文类
 *
 * @author fujq13
 * @date 2023-08-14
 */
public class File {
  private String fileName;
  private Integer command;
  private byte[] data;

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public Integer getCommand() {
    return command;
  }

  public void setCommand(Integer command) {
    this.command = command;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }
}
