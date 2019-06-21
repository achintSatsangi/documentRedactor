package com.altran.documentRedactor.controller;

import com.altran.documentRedactor.payload.Response;
import com.altran.documentRedactor.service.TextExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static org.springframework.util.StringUtils.cleanPath;

@RestController
public class UploadController {

    private TextExtractorService textExtractorService;

    @Autowired
    public UploadController(TextExtractorService textExtractorService) {
        this.textExtractorService = textExtractorService;
    }

    @PostMapping("/upload")
    public Response uploadFile(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        String textInFile = textExtractorService.getTextInFile(file);
        return new Response(cleanPath(file.getOriginalFilename()),
                file.getContentType(), file.getSize(), textInFile);
    }

    @ExceptionHandler(MultipartException.class)
    public void handleException(MultipartException e) {
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public void handleException(HttpClientErrorException e) {
        throw e;
    }

    @ExceptionHandler(RuntimeException.class)
    public void handleException() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public void validateFile(MultipartFile file) {
        if(Objects.isNull(file)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "No file");
        }
        if(!file.getContentType().startsWith("image")) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Not an image file");
        }
    }
}
