package com.altran.documentRedactor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class GcpAuthCreator {

    public static void main(String[] args) throws IOException {
        Path gcp_key_file = Path.of(System.getenv("GCP_KEY_FILE"));
        Files.deleteIfExists(gcp_key_file);
        Files.createFile(gcp_key_file);
        Files.writeString(gcp_key_file, System.getenv("GCP_CRED"), StandardOpenOption.WRITE);
    }
}
