package com.altran.documentRedactor.payload;

public class Response {

    private final String fileName;
    private final String fileType;
    private final long size;

    public Response(String fileName, String fileType, long size) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public long getSize() {
        return size;
    }
}