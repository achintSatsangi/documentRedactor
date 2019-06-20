package com.altran.documentRedactor.controller;

import com.altran.documentRedactor.payload.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.util.StringUtils.cleanPath;

@RestController
public class UploadController {

    @PostMapping("/upload")
    public Response uploadFile(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        return new Response(cleanPath(file.getOriginalFilename()),
                file.getContentType(), file.getSize());
    }

    public void validateFile(MultipartFile file) {
        if(!file.getContentType().startsWith("image")) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Not an image file");
        }
    }
}
