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

import java.util.List;
import java.util.Objects;

import static org.springframework.util.StringUtils.cleanPath;

@RestController
public class FileHandlerController {

    private TextExtractorService textExtractorService;

    public static List<String> PATTERNS = List.of("\\d{11}", "\\d{6} \\d{5}", "\\d{6}_\\d{5}", "f.nr.\\d{5}", "f.nr. \\d{5}");

    @Autowired
    public FileHandlerController(TextExtractorService textExtractorService) {
        this.textExtractorService = textExtractorService;
    }

    @PostMapping("/extractText")
    public Response extractText(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        String textInFile = textExtractorService.getTextInFile(file);
        return new Response(cleanPath(file.getOriginalFilename()),
                file.getContentType(), file.getSize(), textInFile);
    }

    @PostMapping("/extractPersonNumber")
    public Response extractPersonNumber(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        String textInFile = textExtractorService.getTextInFile(file);
        StringBuilder sb = new StringBuilder();
        PATTERNS.forEach(p -> getMatchingPatterns(textInFile, sb, p));
        return new Response(cleanPath(file.getOriginalFilename()),
                file.getContentType(), file.getSize(), sb.toString());
    }

    private StringBuilder getMatchingPatterns(String textInFile, StringBuilder sb, String p) {
        sb.append("Matches for " + p);
        sb.append(textExtractorService.getMatchingPattern(textInFile, p)).append("\n");
        return sb;
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
