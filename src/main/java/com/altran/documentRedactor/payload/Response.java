package com.altran.documentRedactor.payload;

public class Response {

    private final String fileName;
    private final String fileType;
    private final long size;
    private final String textInFile;

    public Response(String fileName, String fileType, long size, String textInFile) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
        this.textInFile = textInFile;
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

    public String getTextInFile() {
        return textInFile;
    }
}