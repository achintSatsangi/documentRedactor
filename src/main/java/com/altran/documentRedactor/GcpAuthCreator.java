package com.altran.documentRedactor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class GcpAuthCreator {

    public static void main(String[] args) throws IOException {
        Files.createFile(Path.of(System.getenv("GCP_KEY_FILE")));
        Files.writeString(Path.of(System.getenv("GCP_KEY_FILE")), System.getenv("GCP_CRED"), StandardOpenOption.WRITE);
    }
}
