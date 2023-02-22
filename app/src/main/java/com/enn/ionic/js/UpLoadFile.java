package com.enn.ionic.js;

import java.io.Serializable;

public class UpLoadFile implements Serializable {
    private String fileName;
    private String fileUrl;
    private int size;
    private String suffix;
    private Object ossId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Object getOssId() {
        return ossId;
    }

    public void setOssId(Object ossId) {
        this.ossId = ossId;
    }
}
