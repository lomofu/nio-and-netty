package com.lomofu.learning.netty.netty.fileupload.client.entity;

import java.util.Optional;

/**
 * @author fujq13
 * @date 2023-08-14
 */
public class File {
  private Integer command;
  private Integer fileNameLength;
  private Integer dataLen;
  private byte[] data;

  public File(Integer command, Integer fileNameLength, Integer dataLen, byte[] data) {
    this.command = command;
    this.fileNameLength = fileNameLength;
    this.dataLen = dataLen;
    this.data = data;
  }

  public Integer getCommand() {
    return command;
  }

  public void setCommand(Integer command) {
    this.command = command;
  }

  public Integer getFileNameLength() {
    return fileNameLength;
  }

  public void setFileNameLength(Integer fileNameLength) {
    this.fileNameLength = fileNameLength;
  }

  public Integer getDataLen() {
    return dataLen;
  }

  public void setDataLen(Integer dataLen) {
    this.dataLen = dataLen;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return command
        + String.valueOf(+fileNameLength)
        + dataLen
        + Optional.ofNullable(data).map(Object::toString).orElse("");
  }
}
